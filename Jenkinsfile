pipeline {
    agent any
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch name to build')
        string(name: 'DOCKER_IMAGE', defaultValue: 'srinisbook/java-demoapp-maven:latest', description: 'Docker image name with tag')
    }
    stages {
        stage('build') {
            steps {
                sh 'git checkout ${BRANCH_NAME}'
                sh 'mvn clean package'
            }
        }
        stage('test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('publish-test-results') {
            steps {
                junit 'target/surefire-reports/*.xml'
            }
        }
        stage('sonar-scan') {
            steps {
                withSonarQubeEnv('SonarQube') { // Replace 'SonarQube' with your SonarQube server name in Jenkins
                    sh 'mvn sonar:sonar -Dsonar.projectKey=java-demoapp-maven -Dsonar.projectName=DemoMavenApp'
                }
            }
        }
        stage('quality-gate') {
            steps {
                script {
                    def qualityGate = waitForQualityGate()
                    if (qualityGate.status != 'OK') {
                        error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
                    }
                }
            }
        }
        stage('docker-build-and-push') {
            steps {
                retry(3) { // Retry the stage up to 3 times if it fails
                    script {
                        withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            sh """
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                                docker build -t ${DOCKER_IMAGE} .
                                docker push ${DOCKER_IMAGE}
                            """
                        }
                    }
                }
            }
        }
        stage('Deploy to Environments') {
            parallel {
                stage('Deploy to Dev') {
                    steps {
                        script {
                            withCredentials([
                                file(credentialsId: 'kubeconfig-dev-credentials', variable: 'KUBECONFIG'),
                                file(credentialsId: 'dev-secrets-file', variable: 'DEV_SECRETS')
                            ]) {
                                sh """
                                    helm upgrade --install java-demoapp-dev ./helm-chart \
                                        --set image.repository=${DOCKER_IMAGE.split(':')[0]} \
                                        --set image.tag=${DOCKER_IMAGE.split(':')[1]} \
                                        --namespace dev \
                                        --values $DEV_SECRETS
                                """
                            }
                        }
                    }
                }
                stage('Deploy to QA') {
                    steps {
                        script {
                            withCredentials([
                                file(credentialsId: 'kubeconfig-qa-credentials', variable: 'KUBECONFIG'),
                                file(credentialsId: 'qa-secrets-file', variable: 'QA_SECRETS')
                            ]) {
                                sh """
                                    helm upgrade --install java-demoapp-qa ./helm-chart \
                                        --set image.repository=${DOCKER_IMAGE.split(':')[0]} \
                                        --set image.tag=${DOCKER_IMAGE.split(':')[1]} \
                                        --namespace qa \
                                        --values $QA_SECRETS
                                """
                            }
                        }
                    }
                }
                stage('Deploy to Prod') {
                    steps {
                        script {
                            withCredentials([
                                file(credentialsId: 'kubeconfig-prod-credentials', variable: 'KUBECONFIG'),
                                file(credentialsId: 'prod-secrets-file', variable: 'PROD_SECRETS')
                            ]) {
                                sh """
                                    helm upgrade --install java-demoapp-prod ./helm-chart \
                                        --set image.repository=${DOCKER_IMAGE.split(':')[0]} \
                                        --set image.tag=${DOCKER_IMAGE.split(':')[1]} \
                                        --namespace prod \
                                        --values $PROD_SECRETS
                                """
                            }
                        }
                    }
                }
            }
        }
    }
}