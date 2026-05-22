# ====== Stage 1: Build the JAR with Maven ======
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy everything and build in one step
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B

# ====== Stage 2: Run the JAR ======
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
