pipeline {
    agent any

    tools {
        maven "M3"
    }

    stages {
        stage('Run Test') {
            steps {
                sh "mvn clean test"
            }
        }
    }
}
