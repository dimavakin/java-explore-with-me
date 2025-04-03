FROM maven:3.9.9-amazoncorretto-21 AS builder

WORKDIR /workspace
COPY . .

RUN mvn clean package -pl stats/server -am -DskipTests

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /workspace/stats/server/target/*.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]