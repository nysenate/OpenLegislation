pipeline {
  agent any
  tools {
    maven 'maven 3.5.0'
    jdk 'jdk8'
  }
  stages {
    stage('Test') {
      configFileProvider([configFile(fileId: '229494d5-96f1-4f6a-8ac2-cbc5f8101e78', targetLocation: 'test.app.properties')]) {
        steps {
          bat 'mvn clean verify'
        }
        post {
          success {
            junit 'target/surefire-reports/**/*.xml'
          }
        }
      }
    }
  }
}