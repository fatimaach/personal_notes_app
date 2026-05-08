pipeline {
    agent any

    stages {
        stage('Create Env') {
            steps {
                sh '''
                cat > .env <<EOF
MONGO_URI=mongodb://mongo:27017/personal_notes_db
JWT_SECRET=myverysecretkey
PORT=5000
EOF
                '''
            }
        }

        stage('Stop Old Deployment') {
            steps {
                sh 'docker compose -f docker-compose.jenkins.yml down || true'
            }
        }

        stage('Start Deployment') {
            steps {
                sh 'docker compose -f docker-compose.jenkins.yml up -d'
            }
        }

        stage('Show Containers') {
            steps {
                sh 'docker ps'
            }
        }

                stage('Wait for App') {
                        steps {
                                sh '''
                                MAX=30
                                i=0
                                while [ $i -lt $MAX ]; do
                                    if curl -sSf http://localhost:5001 > /dev/null 2>&1; then
                                        echo "App is reachable at http://localhost:5001"
                                        exit 0
                                    fi
                                    if curl -sSf http://localhost:80 > /dev/null 2>&1; then
                                        echo "App is reachable at http://localhost:80"
                                        exit 0
                                    fi
                                    i=$((i+1))
                                    sleep 2
                                done
                                echo "App did not respond after $MAX attempts"
                                exit 1
                                '''
                        }
                }

        stage('Check Deployment') {
            steps {
                sh '''
                curl -sSf http://localhost:5001 >/dev/null 2>&1 || curl -sSf http://localhost:80 >/dev/null 2>&1
                '''
            }
        }

        stage('Run Selenium Tests in Docker') {
            steps {
                sh '''
                # Run the Selenium test suite inside the markhobson/maven-chrome container
                docker run --rm --network host -v "$PWD/selenium-tests":/workspace -w /workspace markhobson/maven-chrome mvn test
                '''
            }
        }
    }

    post {
        always {
            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                emailext(
                    subject: "${JOB_NAME} - Build #${BUILD_NUMBER} - ${currentBuild.currentResult}",
                    body: "Build URL: ${env.BUILD_URL}",
                    to: 'fatimaazam2111@gmail.com'
                )
            }
        }
    }
}