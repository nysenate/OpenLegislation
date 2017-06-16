pipeline {
  agent any
  tools {
    maven 'maven 3.5.0'
    jdk 'jdk8'
  }
  stages {
    stage('Test') {
      steps {
        configFileProvider([configFile(fileId: '229494d5-96f1-4f6a-8ac2-cbc5f8101e78', targetLocation: 'src/main/resources/test.app.properties')]) {
          bat 'mvn clean'
          bat 'mvn org.jacoco:jacoco-maven-plugin:prepare-agent'
          bat 'verify'
          withSonarQubeEnv('sonarqube') {
            bat 'mvn sonar:sonar'
          }
        }
      }
    }
  }
  post {
    success {
      junit 'target/*-reports/**/*.xml'
      jacoco execPattern: 'target/jacoco.exec'
    }
  }
}