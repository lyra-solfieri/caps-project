FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget -q http://localhost:8080/ -O /dev/null || exit 1

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-jar", "app.jar"]
