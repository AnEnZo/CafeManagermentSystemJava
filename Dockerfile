# 1. Dùng image gốc là OpenJDK 21, phiên bản slim để nhẹ
FROM openjdk:21-jdk-slim

# 2. Thêm label metadata (tuỳ chọn)
LABEL maintainer="you@example.com"

# 3. Đặt thư mục làm việc trong container là /app
WORKDIR /app

# 4. Copy file .jar build từ thư mục target của Maven vào container
#COPY target/*.jar app.jar
COPY target/DTAdemoTuan6-0.0.1-SNAPSHOT.jar app.jar

# 5. Mở cổng 8080, trùng với cấu hình server.port trong Spring Boot
EXPOSE 8080

# 6. Câu lệnh để chạy app.jar
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
