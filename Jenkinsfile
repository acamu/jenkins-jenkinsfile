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
               steps {
                     script { 
                         log.info 'Starting'
                     }
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
                        //sh './gradlew clean build'
                        //Or Mvn
                        //sh xxx
                }
            }

            stage ('test') {
                steps {
                    parallel (
                            "unit tests": {  
                                    script { log.info 'unit test'} 
                            try {
                                 // Any maven phase that that triggers the test phase can be used here.
                                //sh 'mvn test -B'
                                //Or gradle task
                                //sh ./gradlew -x test
                            } catch(err) {
                                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                                  if (currentBuild.result == 'UNSTABLE')
                                    currentBuild.result = 'FAILURE'
                                  throw err
                            }finally {
                                  step([$class: 'JUnitResultArchiver', testResults: '**/TEST-*.xml'])
                                }
                            
                            },
                            "integration tests": {  script { log.info 'integration test'} }
                    )
                }
            }

            stage('deploy developmentServer'){
                    
                input 'Do you approve deployment?'
                steps {
                        script { log.info 'deploy dev'}
                    deploy(developmentServer, serverPort)
                }
            }

            stage('deploy staging'){
                steps {
                        script { log.info 'deploy staging'}
                    deploy(stagingServer, serverPort)
                }
            }

            stage('deploy production'){
                steps {
                        script {  log.info 'deploy prod'}
                    deploy(productionServer, serverPort)
                }
            }
        }
        post {
            failure {
                    script { log.warning 'deploy warning'}
                     // notify users when the Pipeline fails
                     mail to: 'team@example.com',
          subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
          body: "Something is wrong with ${env.BUILD_URL}"
            }
        }
    }
