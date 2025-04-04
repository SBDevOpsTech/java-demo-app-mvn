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
        stage('Deploy to Dev') {
            steps {
                echo 'Deploying to Dev environment...'
                // Add your deployment logic here
            }
        }
        stage('Deploy to QA') {
            steps {
                echo 'Deploying to QA environment...'
                // Add your deployment logic here
            }
        }
        stage('Deploy to Prod') {
            steps {
                echo 'Deploying to Prod environment...'
                // Add your deployment logic here
            }
        }
    }
}