pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building the application...'
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t my-app:latest .'
            }
        }
        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to registry...'
                sh 'docker tag my-app:latest my-dockerhub-username/my-app:latest'
                sh 'docker push my-dockerhub-username/my-app:latest'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying Docker container...'
                sh 'docker run -d --name my-app-container -p 8080:8080 my-app:latest'
            }
        }
    }
    post {
        success {
            echo 'Build and deployment succeeded!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}
