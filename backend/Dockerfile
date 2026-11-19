FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
# Build inside container or use prebuilt jar
RUN ./mvnw clean package -DskipTests || true

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
