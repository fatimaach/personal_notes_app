pipeline {
    agent any

    triggers {
        githubPush()
    }

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

        stage('Build & Deploy') {
            steps {
                sh '''
                docker compose -f docker-compose.jenkins.yml down || true
                docker compose -f docker-compose.jenkins.yml up -d --build
                '''
            }
        }
    }
}
