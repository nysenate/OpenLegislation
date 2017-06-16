pipeline {
  agent any
  tools {
    maven 'maven 3.5.0'
    jdk 'jdk8'
  }
  stages {
    stage('Test') {
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