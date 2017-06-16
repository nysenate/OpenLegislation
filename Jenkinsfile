pipeline {
    agent any
    tools {
        jdk 'jdk8'
        maven 'maven 3.5.0'
    }
    stages {
        stage('Test') {
            steps {
                configFileProvider([configFile(fileId: '229494d5-96f1-4f6a-8ac2-cbc5f8101e78', targetLocation: 'src/main/resources/test.app.properties')]) {
                    bat 'mvn clean'
                    bat 'mvn verify'
                }
            }
            post {
                always {
                    junit 'target/*-reports/*.xml'
                    jacoco execPattern: 'target/*.exec'
                }
            }
        }
        stage('Analyze') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    bat 'mvn sonar:sonar'
                }
            }
        }
    }
}