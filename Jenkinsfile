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
                }
            }

            stage ('test') {
                steps {
                    parallel (
                            "unit tests": {  script { log.info 'unit test'} },
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
