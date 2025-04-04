pipeline {
    agent any
    stages {
        stage('build') {
            steps {
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
        stage('docker-build-and-push') {
            steps {
                script {
                    def imageName = "srinisbook/java-demoapp-maven:latest"
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker build -t $imageName .
                            docker push $imageName
                        """
                    }
                }
            }
        }
    }
}