pipeline {
    agent any
    tools {
        maven 'Maven 3.5.2'
        jdk 'OpenJDK 8'
    }

    stages {
        stage('Build') {
            steps {
                echo 'Jenkins is building...'
                // Run the maven build

                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                        sh 'mvn verify'
                    } else {
                        bat 'mvn clean compile'
                        bat 'mvn verify'
                    }
                }

                echo 'Jenkins has finished building.'
            }
        }

        stage('Results') {
            echo 'Jenkins is returning results.'
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                archive 'target/*.jar'
            }
            echo 'Jenkins has returned results.'
        }
   }
}