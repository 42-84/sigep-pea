// SIGEP - pipeline Jenkins (declaratif)
// Cree le 12/03/2018 (FD) suite a la migration Jenkins 1 -> 2 de la DSI.
// Job : http://jenkins.bas.intra/job/SIGEP/
pipeline {
    agent { label 'java8' }

    options {
        timeout(time: 45, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false -Dhttp.proxyHost=proxy.bas.intra -Dhttp.proxyPort=3128'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --pretty=format:"%h %an %s"'
            }
        }

        stage('Build') {
            steps {
                // -x test : les tests sont joues dans le stage suivant pour avoir un rapport separe
                sh './gradlew clean build -x test'
            }
        }

        stage('Tests unitaires') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        /*
        // Desactive le 04/06/2018 : les tests *IT ont besoin d'une base H2 initialisee dans l'ordre
        // referentiel -> comptes -> portefeuille, ce que l'agent ne fait pas. A reactiver (voir TODO.txt).
        stage('Tests integration') {
            steps {
                sh './gradlew integrationTest'
            }
        }
        */

        stage('Sonar') {
            steps {
                withSonarQubeEnv('sonar-bas') {
                    sh './gradlew sonarqube -Dsonar.projectVersion=$(grep version= gradle.properties | cut -d= -f2)'
                }
            }
        }

        stage('Archivage des jars') {
            steps {
                archiveArtifacts artifacts: 'sigep-referentiel/build/libs/*.jar, sigep-comptes/build/libs/*.jar, sigep-portefeuille/build/libs/*.jar', fingerprint: true
            }
        }

        stage('Deploiement recette') {
            when {
                branch 'develop'
            }
            steps {
                sh 'deploiement/deploy.sh recette'
            }
        }
    }

    post {
        failure {
            mail to: 'sigep-tma@bas.intra',
                 subject: "[SIGEP] Build ${env.JOB_NAME} #${env.BUILD_NUMBER} en echec",
                 body: "Voir ${env.BUILD_URL}console"
        }
    }
}
