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
                sh 'docker build --network host -t ${IMAGE_NAME}:latest .'
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
                sh 'mkdir -p /home/hyperwise98/ws/cinemax/secrets'
                sh 'docker stop ${IMAGE_NAME} || true'
                sh 'docker rm ${IMAGE_NAME} || true'
                withCredentials([
                    file(credentialsId: 'cinemax_ENV_FILE', variable: 'ENV_FILE'),
                    file(credentialsId: 'cinemax-gcp-keys-json', variable: 'GCP_KEYS_FILE')
                ]) {
                    sh '''
                        rm -rf /home/hyperwise98/ws/cinemax/secrets/keys.json
                        cp "${GCP_KEYS_FILE}" /home/hyperwise98/ws/cinemax/secrets/keys.json
                        chmod 644 /home/hyperwise98/ws/cinemax/secrets/keys.json
                        echo "=== keys.json 상태 확인 ==="
                        ls -la /home/hyperwise98/ws/cinemax/secrets/keys.json
                        [ -f /home/hyperwise98/ws/cinemax/secrets/keys.json ] || (echo "ERROR: keys.json이 파일이 아닙니다" && exit 1)
                        echo "=== keys.json 정상 생성 완료 ==="
                    '''
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
                        -e GOOGLE_APPLICATION_CREDENTIALS=/app/secrets/keys.json \
                        -e GOOGLE_KEY_FILE=/app/secrets/keys.json \
                        -v /home/hyperwise98/ws/cinemax/uploads:/app/uploads \
                        -v /home/hyperwise98/ws/cinemax/secrets/keys.json:/app/secrets/keys.json:ro \
                        -e UPLOAD_PATH=/app/uploads/ \
                        --restart unless-stopped \
                        ${IMAGE_NAME}:latest
                    '''
                }
            }
        }
    }
}
