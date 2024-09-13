FROM gradle:7.6-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle

RUN gradle build --no-daemon

COPY build/libs/insurance-api-0.0.1.jar /app/insurance-api.jar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/insurance-api.jar /app/insurance-api.jar

EXPOSE 8080

CMD ["java", "-jar", "insurance-api.jar"]
