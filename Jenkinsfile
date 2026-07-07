pipeline {
    agent any
    environment {
        IMAGE_NAME = "cinemax"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'release'
                    expression { env.GIT_BRANCH == 'origin/release' }
                }
            }
            steps {
                sh 'docker build --no-cache --network host -t ${IMAGE_NAME}:latest .'
            }
        }
        stage('Deploy') {
            when {
                anyOf {
                    branch 'release'
                    expression { env.GIT_BRANCH == 'origin/release' }
                }
            }
            steps {
                sh 'mkdir -p /home/hyperwise98/ws/cinemax/uploads'
                sh 'chmod -R 777 /home/hyperwise98/ws/cinemax/uploads'
                sh 'docker stop ${IMAGE_NAME} || true'
                sh 'docker rm ${IMAGE_NAME} || true'
                // OPENAI_API_KEY 등 시크릿은 cinemax_ENV_FILE(.env)에서 --env-file로 주입
                withCredentials([
                    file(credentialsId: 'cinemax_ENV_FILE', variable: 'ENV_FILE')
                ]) {
                    sh '''
                        docker run -d \
                        --name ${IMAGE_NAME} \
                        -p 8083:8080 \
                        --network global-proxy \
                        --env-file ${ENV_FILE} \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e DB_URL="jdbc:mariadb://221.148.101.200:3306/cinemax?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Seoul" \
                        -e DB_USERNAME=root \
                        -e DB_PASSWORD=hw0908!@ \
                        -v /home/hyperwise98/ws/cinemax/uploads:/app/uploads \
                        -e UPLOAD_PATH=/app/uploads/ \
                        --restart unless-stopped \
                        ${IMAGE_NAME}:latest
                    '''
                }
            }
        }
    }
}
