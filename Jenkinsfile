@Library('My-library') _
pipeline {
    agent any
    environment {
        branch = 'master'
        scmUrl = 'ssh://git@myScmServer.com/repos/myRepo.git'
        serverPort = '8080'
        developmentServer = 'dev-myproject.mycompany.com'
        stagingServer = 'staging-myproject.mycompany.com'
        productionServer = 'production-myproject.mycompany.com'
    }
    stages {
        stage('checkout git') {
            when {
                branch 'master'  //only run these steps on the master branch
            }
            steps {
                script {
                    log.info 'Starting'
                }
                cleanWs()

                //checkout scm
            }
        }

        stage('build') {
            when {
                branch 'master'  //only run these steps on the master branch
            }
            steps {
                script {
                    log.info 'build'
                }
                //Build
                //Gradle
                //sh './gradlew clean build -x test
                //Or Mvn
                //sh xxx
            }
        }

        stage('unit test') {
            steps {
                parallel(
                        "unit tests": {
                            script { log.info 'unit test' }
                            try {
                                // Any maven phase that that triggers the test phase can be used here.
                                //sh "mvn test -B"
                                //Or gradle task
                                //sh './gradlew test'
                                // sh (script: './gradlew test', returnStatus: true)

                            } catch (err) {
                                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                                if (currentBuild.result == 'UNSTABLE')
                                    currentBuild.result = 'FAILURE'
                                throw err
                            } finally {
                                archiveUnitTestResults()
                                archiveCheckstyleResults()
                            }

                        },

                        "Other tests": { script { log.info 'Other test' } }
                )
            }
        }

        //  "integration tests": {  script { log.info 'integration test'} }

        stage('Reserve binary') {
           // stash includes: 'all/target/*.war', name: 'war'
        }

        //https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('My SonarQube Server') {
                    // requires SonarQube Scanner for Gradle 2.1+
                    // It's important to add --info because of SONARJNKNS-281
                    sh './gradlew --info sonarqube'
                }
            }
        }

        //https://jenkins.io/blog/2017/04/18/continuousdelivery-devops-sonarqube/
        stage("SonarQube Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    }
                }
            }
        }

        //https://github.com/michaelhuettermann/sandbox/blob/master/pipeline/jenkins/MyDeliveryPipeline/pipeline.groovy
        stage('Distribute WAR') {
            steps {
                //sh "rm all/target/*.war"
              //  unstash 'war'
                echo "Deploy Deployment Unit to Artifactory."
                def uploadSpec = """
                           {
                               "files": [
                                   {
                                       "pattern": "all/target/all-(*).war",
                                       "target": "libs-release-local/org/acam/web/{1}/",
                                       "props":  "where=arnaud;owner=acamu" 
                                   } ]         
                               }
                               """
                buildInfo = Artifactory.newBuildInfo()
                buildInfo.env.capture = true
                buildInfo = server.upload(uploadSpec)
            }
        }

        stage('Distribute Docker image') {
            steps {
                echo "Push Docker image to Artifactory Docker Registry."
                def artDocker = Artifactory.docker("$DOCKER_UN_ADMIN", "$DOCKER_PW_ADMIN")
                def dockerInfo = artDocker.push("aaaaaaa:latest", "docker-dev-local")
                buildInfo.append(dockerInfo)
                server.publishBuildInfo(buildInfo)
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

            input 'Do you approve deployment?'
            steps {
                script { log.info 'deploy dev' }
                deploy(developmentServer, serverPort)
            }
        }

        stage('deploy staging') {
            steps {
                script { log.info 'deploy staging' }
                deploy(stagingServer, serverPort)
            }
        }

        stage('deploy production') {
            steps {
                script { log.info 'deploy prod' }
                deploy(productionServer, serverPort)
            }
        }
    }
    post {
        failure {
            script { log.warning 'deploy warning' }
            // notify users when the Pipeline fails
            mail to: 'team@example.com',
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
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
