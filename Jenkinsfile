pipeline {
    agent any
    tools {
        maven 'Maven 3.5.2'
        jdk 'OpenJDK 8'
    }

    stages {
        stage('Build') {
            steps {
                // Note: replace 'jenkins-branch' with whichever branch is needed.
                git branch: 'jenkins-integration', url: 'https://github.com/nysenate/OpenLegislation'
                // Run the maven build
                if (isUnix()) {
                    sh 'mvn clean compile'
                    sh 'mvn verify'
                } else {
                    bat 'mvn clean compile'
                    bat 'mvn verify'
                }
            }
        }

        stage('Results') {
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                archive 'target/*.jar'
            }
        }
   }
}