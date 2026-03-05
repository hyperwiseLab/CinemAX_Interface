#!/bin/bash

# 오류 발생 시 즉시 중단
set -e

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 Cinemax Production 배포 (포트 8080)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# ── 환경 설정 ──────────────────────────────────────────────
WORKSPACE="/var/jenkins_home/workspace/Cinemax"
IMAGE_NAME="cinemax-prod"
CONTAINER_NAME="cinemax"
HOST_PORT=8080
CONTAINER_PORT=8080
UPLOAD_HOST_PATH="/home/cinemax/upload"
UPLOAD_CONTAINER_PATH="/home/cinemax/upload"

cd "$WORKSPACE"

# ── Docker 접근 확인 ────────────────────────────────────────
echo "🔧 Docker 데몬 접근 확인..."
docker info >/dev/null 2>&1 || { echo "❌ Docker 접근 실패"; exit 1; }
echo "✅ Docker 접근 가능"

# ── Gradle 빌드 ─────────────────────────────────────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧱 Gradle 빌드 시작"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

chmod +x ./gradlew
./gradlew clean build -x test --no-daemon

# JAR 파일 확인
JAR_FILE="$WORKSPACE/build/libs/Cinemax-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR 파일 없음: $JAR_FILE"
    ls -lh "$WORKSPACE/build/libs/" || true
    exit 1
fi

echo "✅ JAR 파일 확인:"
ls -lh "$JAR_FILE"

# ── 포트 8080 충돌 검사 및 기존 컨테이너 정리 ──────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔍 포트 8080 충돌 검사"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 포트 8080을 사용하는 모든 컨테이너 찾기
PORT_IN_USE=$(docker ps --format '{{.Names}}' --filter "publish=8080" || true)

if [ -n "$PORT_IN_USE" ]; then
    echo "⚠️  포트 8080 사용 중인 컨테이너:"
    echo "$PORT_IN_USE"

    for container in $PORT_IN_USE; do
        echo "🛑 컨테이너 중지/삭제: $container"
        docker stop "$container" 2>/dev/null || true
        docker rm "$container" 2>/dev/null || true
    done
fi

# 동일한 이름의 컨테이너 정리
if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
    echo "🛑 기존 $CONTAINER_NAME 컨테이너 삭제"
    docker stop "$CONTAINER_NAME" 2>/dev/null || true
    docker rm "$CONTAINER_NAME" 2>/dev/null || true
fi

echo "✅ 포트 8080 사용 가능"

# ── Docker 이미지 빌드 ──────────────────────────────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🐳 Docker 이미지 빌드"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

docker build --pull \
  -f "$WORKSPACE/Dockerfile.prod" \
  -t "$IMAGE_NAME" \
  "$WORKSPACE"

echo "✅ 이미지 빌드 완료"

# ── Docker 컨테이너 실행 (포트 8080) ────────────────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 Docker 컨테이너 시작 (포트 8080)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

docker run -d \
  --name "$CONTAINER_NAME" \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE="prod" \
  -e DB_URL="jdbc:mariadb://175.126.37.175:3306/cinemax_dev?serverTimezone=Asia/Seoul&characterEncoding=UTF-8" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="cinemax!2025" \
  -e JWT_SECRET="your-secret-key-change-this-in-production-minimum-256-bits-required-for-hs256-algorithm" \
  -e JWT_ACCESS_TOKEN_EXPIRATION="10800000" \
  -e JWT_REFRESH_TOKEN_EXPIRATION="604800000" \
  -e JWT_ISSUER="cinemax" \
  -e EMAIL_FROM="iwings_noreply@hyperwise.co.kr" \
  -e EMAIL_FROM_NAME="Cinemax" \
  -e JENKINS_URL="http://localhost:8081" \
  -e JENKINS_USERNAME="admin" \
  -e JENKINS_TOKEN="your-jenkins-api-token" \
  -e JENKINS_CONNECTION_TIMEOUT="30000" \
  -e JENKINS_READ_TIMEOUT="60000" \
  -e GEMINI_API_KEY="AIzaSyCqLhtjreV3i5IGAKxG_LTcRZzYfOV2G7c" \
  -e GEMINI_PROJECT_ID="829402555214" \
  -e GEMINI_LOCATION="us-central1" \
  -e GEMINI_MODEL="gemini-1.5-pro" \
  -e GEMINI_TEMPERATURE="0.7" \
  -e GEMINI_MAX_TOKENS="2048" \
  -e GEMINI_TOP_P="0.95" \
  -e GEMINI_TOP_K="40" \
  -v ${UPLOAD_HOST_PATH}:${UPLOAD_CONTAINER_PATH} \
  "$IMAGE_NAME" \
  --file.upload-dir-mac=${UPLOAD_CONTAINER_PATH} \
  --file.upload-dir-linux=${UPLOAD_CONTAINER_PATH} \
  --file.upload-dir-win=${UPLOAD_CONTAINER_PATH}

echo "✅ 컨테이너 시작 완료"

# ── Docker 시스템 정리 ──────────────────────────────────────
echo "🧹 Docker 시스템 정리..."
docker system prune -a -f

# ── 배포 결과 확인 ──────────────────────────────────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 배포 결과 확인"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📦 실행 중인 컨테이너:"
docker ps | grep "$CONTAINER_NAME" || echo "⚠️ 컨테이너 없음"

echo ""
echo "📋 컨테이너 로그 (최근 30줄):"
docker logs --tail=30 "$CONTAINER_NAME" 2>&1

# 헬스 체크
echo ""
echo "🏥 헬스 체크 (10초 대기)..."
sleep 10

if docker ps | grep -q "$CONTAINER_NAME"; then
    echo "✅ 컨테이너 정상 실행 중"

    # 포트 확인
    echo ""
    echo "🌐 포트 8080 리스닝 확인:"
    docker port "$CONTAINER_NAME" || true
else
    echo "❌ 컨테이너가 실행되지 않았습니다"
    echo "📋 전체 로그:"
    docker logs "$CONTAINER_NAME" 2>&1
    exit 1
fi

# ── 배포 완료 ───────────────────────────────────────────────
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎉 배포 완료!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌐 애플리케이션: http://175.126.37.175:8080/api/v1"
echo "📊 Swagger UI: http://175.126.37.175:8080/api/v1/swagger-ui.html"
echo "📋 로그 확인: docker logs -f cinemax"
echo "🛑 중지 명령: docker stop cinemax"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"