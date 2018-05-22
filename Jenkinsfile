#!/bin/groovy
@Library('My-library') _

/* Only keep the 2 most recent builds. */
def projectProperties = [
    [$class: 'BuildDiscarderProperty',strategy: [$class: 'LogRotator', numToKeepStr: '2']],
]

//Artifactory variables
def server
def rtGradle
def buildInfo

//Docker variabes
def container

properties(projectProperties)

pipeline {
    agent any
    
    environment {
        branch = 'master'
        scmUrl = 'ssh://git@myScmServer.com/repos/myRepo.git'
        serverPort = '8080'
        developmentServer = 'dev-myproject.mycompany.com'
        stagingServer = 'staging-myproject.mycompany.com'
        productionServer = 'production-myproject.mycompany.com'
        SERVER_ID = 'artifactory6.0'
        GRADLE_TOOL = 'gradle-4.6'
    }

    tools {
     gradle "gradle-4.6"
    }

    stages {

            stage('checkout git') {
               // when {
               //     branch 'master'  //only run these steps on the master branch
               // }
                steps {
                    script {
                        log.info 'Starting'
                        //branch name from Jenkins environment variables
                        log.info "My branch is: ${env.BRANCH_NAME}"
                    }
                    cleanWs()

                    checkout scm
                }
            }

            stage ('Artifactory configuration') {

                steps {
                     script {

                        // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
                        server = Artifactory.server SERVER_ID

                        rtGradle = Artifactory.newGradleBuild()
                        rtGradle.tool = GRADLE_TOOL // Tool name from Jenkins configuration
                        rtGradle.deployer repo: 'libs-release-local', server: server
                        rtGradle.resolver repo: 'libs-release', server: server
                        rtGradle.deployer.deployArtifacts = false // Disable artifacts deployment during Gradle run

                        buildInfo = Artifactory.newBuildInfo()
                     }
                }
            }

            stage('build') {
                //when {
               //     branch 'master'  //only run these steps on the master branch
                //}
                steps {
                    script {
                        log.info 'build'

                        //Build
                        //Gradle
                        sh 'gradle clean build -x test'
                        /*
                        if (isUnix()) {
                           // sh './gradlew clean build -x test --info '
                             sh 'gradle clean build -x test'
                            // sh './gradlew clean build test'
                        } else {
                            bat 'gradle clean build -x test'
                             bat './gradlew.bat clean build -x test'
                        }
                        */

                       archiveArtifacts artifacts: '**/build/libs/**/*.jar', fingerprint: true , allowEmptyArchive: true
                      //  archiveArtifacts artifacts: '**/target/*.war', fingerprint: true , allowEmptyArchive: true
                    }
                    //Or Mvn
                    //sh xxx
                }
            }

        
            stage('unit test') {
                steps {
                    parallel(
                            "unit tests": {
                                script { 
                                        log.info 'unit test' 

                                        try {
                                            // Any maven phase that that triggers the test phase can be used here.
                                            //sh "mvn test -B"
                                            //Or gradle task
                                            //sh './gradlew test'
                                            // sh (script: './gradlew test', returnStatus: true)

                                             sh 'gradle test junit5CodeCoverageReport'
                                            /*
                                              if (isUnix()) {
                                               sh 'gradle test junit5CodeCoverageReport'
                                            } else {
                                                bat 'gradlew.bat test junit5CodeCoverageReport'
                                            }
                                            */

                                            //https://jenkins.io/blog/2016/07/01/html-publisher-plugin/
                                             try {
                                                publishHTML(target: [
                                                    allowMissing         : true,
                                                    keepAll              : true,
                                                    reportDir            : './build/reports/jacoco/html',
                                                    reportFiles          : 'index.html',
                                                    reportName           : "LCOV Report"
                                                ])
                                            } catch (e) {
                                              // ignore
                                            }

                                        } catch (err) {
                                            step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                                            if (currentBuild.result == 'UNSTABLE')
                                                currentBuild.result = 'FAILURE'
                                            throw err
                                        } finally {
                                           archiveUnitTestResults()
                                           // archiveCheckstyleResults()
                                        }
                                    }

                            },

                            "Other tests": { script { log.info 'Other test' } }
                    )
                }
            }

            //  "integration tests": {  script { log.info 'integration test'} }

            stage('Reserve binary') {
                steps {
                    //stash includes: 'all/target/*.war', name: 'war'
                    //stash includes: 'all/target/*.jar', name: 'jar'
                    script { 
                        log.info 'Reserver binary'
                    }
                }
            }
    /*
            //https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
            stage('SonarQube analysis') {
                steps {
                     script { 
                        log.info 'SonarQube analysis'

                    withSonarQubeEnv('SonarQubeServer') {
                        // requires SonarQube Scanner for Gradle 2.1+
                        // It's important to add --info because of SONARJNKNS-281
                       // sh './gradlew --info  sonarqube'
                         //sh './gradlew sonarqube'
                         sh 'gradle sonarqube'
                    }
                  }
                }
            }

            //https://jenkins.io/blog/2017/04/18/continuousdelivery-devops-sonarqube/
            https://stackoverflow.com/questions/43588403/why-sonar-fails-at-waitforqualitygate-with-error-401
            stage("SonarQube Quality Gate") {
                steps { 
                    script { 
                        timeout(time: 5, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout 
                            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv 
                            if (qg.status != 'OK') { 
                                error "Pipeline aborted due to quality gate failure: ${qg.status}"
                            }
                        }
                    }
                }
            } 
    */
/*
            //https://github.com/michaelhuettermann/sandbox/blob/master/pipeline/jenkins/MyDeliveryPipeline/pipeline.groovy      
            stage('Distribute artifact to repository') {
                steps {

                     script { 
                       log.info "Push Artifact to Artifactory Registry."
                    //sh "rm all/target/*.war"
                    //unstash 'war'
                    //unstash 'jar'
                    log.info "Deploy Deployment Unit to Artifactory or Nexus."
                    def uploadSpec = """
                               {
                                   "files": [
                                       {
                                           "pattern": "build/libs/*.jar",
                                           "target": "libs-snapshot-local/org/acam/jar/",
                                           "props":  "where=arnaud;owner=acamu" 
                                       } ]         
                                   }
                                   """
                    buildInfo = Artifactory.newBuildInfo()
                    buildInfo.env.capture = true
                    buildInfo = server.upload(uploadSpec)

                     //rtGradle.run rootDir: 'gradle-examples/gradle-example-ci-server/', buildFile: 'build.gradle', tasks: 'artifactoryPublish', buildInfo: buildInfo
                    //rtGradle.deployer.deployArtifacts buildInfo
                    server.publishBuildInfo buildInfo
                    }
                }
            }
*/

            stage('Build docker image'){
                steps{
                    script { 
                     // prepare docker build context
                    //sh "cp target/project.war ./tmp-docker-build-context"
                    container = docker.build("acamu/acamutest:${branch}", "--build-arg PACKAGE_VERSION=${branch } ./docker")
                    }
                }

            }
        
            stage('Publish Docker image to registry') {
                steps {
                    script { 
                    log.info "Push Docker image to Artifactory Docker Registry."
                   // def artDocker = Artifactory.docker("$DOCKER_UN_ADMIN", "$DOCKER_PW_ADMIN")
                   // def dockerInfo = artDocker.push("aaaaaaa:latest", "docker-dev-local")
                   // buildInfo.append(dockerInfo)
                   // server.publishBuildInfo(buildInfo)
                    container.push()

                    container.push('latest')
                        
                    }
                }
            }
   

          stage('Xray Quality Gate') {
              steps {
                  script { 
                    log.info "Xray Quality Gate"
              /*
                def scanConfig = [
                        'buildName'  : buildInfo.name,
                        'buildNumber': buildInfo.number,
                        'failBuild'  : false
                ]
                def scanResult = server.xrayScan scanConfig
                echo scanResult as String
            */
                  }
                  }
        }

          stage('check Plage de service to deliver app') {
                steps {
                    script {
                        log.info 'deploy dev'
                    }

                }
            }

            stage('deploy developmentServer') {
                 steps {
                     script {
                         /*
                        def doesJavaRock = input(message: 'Do you like Java?', ok: 'Yes', 
                                    parameters: [booleanParam(defaultValue: true, 
                                    description: 'If you like Java, just push the button',name: 'Yes?')])

                        echo ("Java rocks?:" + doesJavaRock)
                        */
                        log.info 'deploy dev'
                        deploy(developmentServer, serverPort)
                     }
                 }
            }

            stage('deploy staging') {
                steps {
                    script { 
                        log.info 'deploy staging'
                        deploy(stagingServer, serverPort)
                         }
                }
            }

            stage('deploy production') {
                steps {
                    script { 
                        log.info 'deploy prod' 
                        deploy(productionServer, serverPort)
                            }
                }
            }        
    }
    post {
        always {
            echo 'Finished!'
            //deleteDir()
        }
        success {
            echo 'Succeeeded.'
        }
        unstable {
            echo 'Unstable.'
        }
       failure {
            script { 
           log.warning 'deploy warning' 
            // notify users when the Pipeline fails
                /*
            mail to: 'team@example.com',
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
              */
              }
        }
        /*changed {
            echo 'Things in life change.'
        } */
    }
}

def archiveUnitTestResults() {
    step([$class: "JUnitResultArchiver", testResults: "build/**/TEST-*.xml"])
}

def archiveCheckstyleResults() {
    step([$class         : "CheckStylePublisher",
          canComputeNew  : false,
          defaultEncoding: "",
          healthy        : "",
          pattern        : "build/reports/checkstyle/main.xml",
          unHealthy      : ""])
}

def switchSnapshotBuildToReleaseMvn() {
    def descriptor = Artifactory.mavenDescriptor()
    descriptor.version = '1.0.0'
    descriptor.pomFile = 'pom.xml'
    descriptor.transform()
}

def buildAndPublishToArtifactoryMvn() {       
        def rtMaven = Artifactory.newMavenBuild()
        rtMaven.tool = "Maven 3.x"
        rtMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
        rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server
        rtMaven.run pom: 'pom.xml', goals: 'install', buildInfo: buildInfo
        server.publishBuildInfo buildInfo
}

def promoteBuildInArtifactory() {
        def promotionConfig = [
            // Mandatory parameters
            'buildName'          : buildInfo.name,
            'buildNumber'        : buildInfo.number,
            'targetRepo'         : 'libs-prod-local',
 
            // Optional parameters
            'comment'            : 'deploying to production',
            'sourceRepo'         : 'libs-release-local',
            'status'             : 'Released',
            'includeDependencies': false,
            'copy'               : true,
            // 'failFast' is true by default.
            // Set it to false, if you don't want the promotion to abort upon receiving the first error.
            'failFast'           : true
        ]
 
        // Promote build
        server.promote promotionConfig
}

def distributeBuildToBinTray() {
        def distributionConfig = [
            // Mandatory parameters
            'buildName'             : buildInfo.name,
            'buildNumber'           : buildInfo.number,
            'targetRepo'            : 'reading-time-dist',  
            // Optional parameters
            //'publish'               : true, // Default: true. If true, artifacts are published when deployed to Bintray.
            'overrideExistingFiles' : true, // Default: false. If true, Artifactory overwrites builds already existing in the target path in Bintray.
            //'gpgPassphrase'         : 'passphrase', // If specified, Artifactory will GPG sign the build deployed to Bintray and apply the specified passphrase.
            //'async'                 : false, // Default: false. If true, the build will be distributed asynchronously. Errors and warnings may be viewed in the Artifactory log.
            //"sourceRepos"           : ["yum-local"], // An array of local repositories from which build artifacts should be collected.
            //'dryRun'                : false, // Default: false. If true, distribution is only simulated. No files are actually moved.
        ]
        server.distribute distributionConfig
}

def promoteInArtifactoryAndDistributeToBinTray() {
    stage ("Promote in Artifactory and Distribute to BinTray") {
        promoteBuildInArtifactory()
        distributeBuildToBinTray()
    }
}
