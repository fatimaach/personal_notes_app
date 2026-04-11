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

        stage('Stop Old Part 2') {
            steps {
                sh 'docker compose -f docker-compose.jenkins.yml down || true'
            }
        }

        stage('Start Part 2') {
            steps {
                sh 'docker compose -f docker-compose.jenkins.yml up -d'
            }
        }

        stage('Show Containers') {
            steps {
                sh 'docker ps'
            }
        }
    }
}