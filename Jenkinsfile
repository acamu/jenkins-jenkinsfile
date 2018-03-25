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
               teps {
             script { 
                 log.info 'Starting'
             }
        }
            }

            stage('build') {
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
                steps {
                        script { log.info 'deploy dev'}
                    deploy(pipelineParams.developmentServer, pipelineParams.serverPort)
                }
            }

            stage('deploy staging'){
                steps {
                        script { log.info 'deploy staging'}
                    deploy(pipelineParams.stagingServer, pipelineParams.serverPort)
                }
            }

            stage('deploy production'){
                steps {
                        script {  log.info 'deploy prod'}
                    deploy(pipelineParams.productionServer, pipelineParams.serverPort)
                }
            }
        }
        post {
            failure {
                    script { log.warning 'deploy warning'}
            }
        }
    }
