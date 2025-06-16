# === Stage 1: Build với Maven + JDK 21 ===
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Thư mục làm việc
WORKDIR /app

# Copy pom.xml và wrapper để tận dụng cache khi download dependency
COPY pom.xml mvnw* ./
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Copy toàn bộ source và build jar (bỏ qua tests để nhanh hơn)
COPY src src
RUN mvn clean package -DskipTests

# === Stage 2: Runtime image trên OpenJDK 21 slim ===
FROM openjdk:21-jdk-slim

# Metadata (tuỳ chọn)
LABEL maintainer="you@example.com"

# Thư mục làm việc
WORKDIR /app

# Copy jar đã build từ builder stage
COPY --from=builder /app/target/DTAdemoTuan6-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (trùng server.port)
EXPOSE 8080

# Chạy ứng dụng với cấu hình memory tối thiểu/tối đa
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
