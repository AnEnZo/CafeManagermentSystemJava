# Cafe Management System

## Project Description
This is a full-stack system (backend and frontend) for managing restaurant operations including orders, tables, menus, invoices, and user authentication.

## Features
- User authentication and role-based authorization
- Order management and table reservations
- Export and import reports in Word and Excel formats
- QR code generation for payment processing
- Revenue and sales statistics with interactive charts

## Technologies Used
- Java, Spring Boot, Spring Security (JWT, OAuth2)
- MySQL, JPA/Hibernate
- RESTful API
- React, Typescript

## How to Run Backend
1. Clone the repository
2. Configure your database in `application.properties`
3. Run the Spring Boot application
4. Access the API at `http://localhost:8080/api`

## How to Run Frontend
1. Open command prompt or terminal
2. Navigate to the frontend folder: `cd DtaAssignment/frontend`
3. Run the frontend app: `npm run dev`
4. Access the frondend at `http://localhost:5173/`

## Author
Dinh Tuan An


# Config your application.properties like 

spring.application.name=DTAdemoTuan6
#spring.datasource.url=jdbc:mysql://host.docker.internal:3306/namedatabase
spring.datasource.url=jdbc:mysql://localhost:3306/namedatabase

spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
server.port=8080
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect



logging.level.org.springdoc=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=DEBUG
logging.level.root=DEBUG
logging.level.org.springframework.security=DEBUG
springdoc.swagger-ui.path=/swagger-ui.html
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=always

# JWT Configuration
# secret key to sign JWT tokens
jwt.secret=your_jwt_secret_key
# token expiration time in milliseconds (1 day)
jwt.expiration=86400000

# mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


#twilio.account-sid=
#twilio.auth-token=
#twilio.from-phone=
#logging.level.com.twilio=DEBUG

vonage.api-key=your_api_key
vonage.api-secret=your_api_secret

spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m

logging.level.org.springframework.messaging.simp=DEBUG
logging.level.org.springframework.web.socket=DEBUG

spring.security.messaging.csrf.enabled=false

# OAuth2 Client registration
spring.security.oauth2.client.registration.google.client-id=your_google_client_id
spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret
spring.security.oauth2.client.registration.google.scope=email,profile

spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/oauth2/callback/{registrationId}

# OAuth2 Provider (Google)
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
spring.security.oauth2.client.provider.google.user-name-attribute=sub
#spring.security.oauth2.client.registration.google.scope=openid,profile,email,https://www.googleapis.com/auth/user.phonenumbers.read


spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Format log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
