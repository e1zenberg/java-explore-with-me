FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml /app/pom.xml
COPY ewm-main-service/pom.xml /app/ewm-main-service/pom.xml
COPY stats/pom.xml /app/stats/pom.xml

COPY ewm-main-service /app/ewm-main-service
COPY stats/stats-dto /app/stats/stats-dto
COPY stats/stats-client /app/stats/stats-client
COPY stats/stats-server /app/stats/stats-server

RUN mvn -q -f /app/pom.xml -DskipTests -pl ewm-main-service -am package

RUN ls -l /app/ewm-main-service/target

RUN sh -c 'set -e; \
  JAR=$(ls -1 /app/ewm-main-service/target/*.jar | grep -Ev "(original|sources|javadoc)" | head -n1); \
  echo "Using JAR: $JAR"; \
  cp "$JAR" /app/app.jar'

RUN test -f /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /opt/app

COPY --from=build /app/app.jar /opt/app/app.jar

RUN ls -l /opt/app && test -f /opt/app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
