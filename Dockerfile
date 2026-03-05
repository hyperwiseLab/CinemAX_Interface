# 1단계: Gradle로 빌드
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY . .

# Plain JAR 제거 후 빌드
RUN gradle clean build --no-daemon -x test && \
    rm -f build/libs/*-plain.jar

# 2단계: JRE로 실행용 이미지 구성 (JDK 불필요 → JRE로 경량화)
FROM eclipse-temurin:21-jdk-jammy

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/Cinemax-*.jar app.jar

# secrets 디렉토리 생성
RUN mkdir -p /app/secrets /app/uploads

# 프로덕션 프로필 설정
ENV SPRING_PROFILES_ACTIVE=prod

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
