pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat '.\\gradlew.bat clean build'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t max2ba/gateway-service:latest D:/JavaProjects/gateway-service'
            }
        }

        stage('Docker Restart') {
            steps {
                bat '''
                cd /d D:\\JavaProjects\\config-server
                docker compose up -d --build gateway-service
                '''
            }
        }
    }
}