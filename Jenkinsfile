pipeline {
  agent any
  stages {
    stage('Unit test') {
      steps {
        bat 'mvn test'
      }
    }
    stage('Integration test') {
      steps {
        bat 'mvn integration-test'
      }
    }
  }
}