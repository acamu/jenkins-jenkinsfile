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
                    log.info("checkout")
                }
            }

            stage('build') {
                steps {
                   log.info("build")
                }
            }

            stage ('test') {
                steps {
                    parallel (
                        "unit tests": {  log.info("unit test") },
                        "integration tests": {  log.info("integration test") }
                    )
                }
            }

            stage('deploy developmentServer'){
                steps {
                 log.info("deploy dev")
                    deploy(pipelineParams.developmentServer, pipelineParams.serverPort)
                }
            }

            stage('deploy staging'){
                steps {
                 log.info("deploy staging")
                    deploy(pipelineParams.stagingServer, pipelineParams.serverPort)
                }
            }

            stage('deploy production'){
                steps {
                 log.info("deploy prod")
                    deploy(pipelineParams.productionServer, pipelineParams.serverPort)
                }
            }
        }
        post {
            failure {
                 log.warning("deploy warning")
            }
        }
    }
