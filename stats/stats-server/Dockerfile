# ===== build =====
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Родительские POM'ы
COPY pom.xml /app/pom.xml
COPY stats/pom.xml /app/stats/pom.xml

# Исходники всех модулей, которые перечислены в stats/pom.xml
COPY stats/stats-dto /app/stats/stats-dto
COPY stats/stats-server /app/stats/stats-server
COPY stats/stats-client /app/stats/stats-client

RUN mvn -q -f /app/stats/pom.xml -DskipTests -pl stats-server -am package

# ===== run =====
FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY --from=build /app/stats/stats-server/target/stats-server-*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
