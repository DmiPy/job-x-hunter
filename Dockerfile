# STAGE 1
# ------------------------------------
# USE Maven 3.0.11 and Java 21 as the base image. The whole step is called builder
FROM maven:3.9.11-eclipse-temurin-21 AS builder

# GO to work directory
WORKDIR /app

# COPY the file pom.xml into the container in /app directory
COPY pom.xml .

# DOWNLOAD all dependencies
RUN mvn dependency:go-offline

# COPY all the code from src (project) into src (container)
COPY . .

RUN mvn clean package -DskipTests


# STAGE 2
# ------------------------------------
# USE Java 21 as the base image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]