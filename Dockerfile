FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar order-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-Xmx350m", "-jar", "order-service.jar"]