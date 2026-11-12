# --- Stadio 1: build con Maven e JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# --- Stadio 2: immagine di esecuzione, solo JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/retail-analytics-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
