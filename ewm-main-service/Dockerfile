FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# POM'ы
COPY pom.xml /app/pom.xml
COPY ewm-main-service/pom.xml /app/ewm-main-service/pom.xml
COPY stats/pom.xml /app/stats/pom.xml

# Исходники модулей, от которых зависит ewm-main-service
COPY ewm-main-service /app/ewm-main-service
COPY stats/stats-dto /app/stats/stats-dto
COPY stats/stats-client /app/stats/stats-client
COPY stats/stats-server /app/stats/stats-server

# Собираем только нужный модуль и его зависимости
RUN mvn -q -f /app/pom.xml -DskipTests -pl ewm-main-service -am package

# Покажем, что собралось
RUN ls -l /app/ewm-main-service/target

# Выбираем «правильный» boot-jar (исключаем original/источники), копируем под единое имя
RUN sh -c 'set -e; \
  JAR=$(ls -1 /app/ewm-main-service/target/*.jar | grep -Ev "(original|sources|javadoc)" | head -n1); \
  echo "Using JAR: $JAR"; \
  cp "$JAR" /app/app.jar'

# Страховка: валидируем, что файл точно есть
RUN test -f /app/app.jar

# ===== run stage =====
FROM eclipse-temurin:21-jre
WORKDIR /opt/app

# Копируем единый файл из build-стадии
COPY --from=build /app/app.jar /opt/app/app.jar

# Страховка №2: на рантайм-стадии файл тоже проверим
RUN ls -l /opt/app && test -f /opt/app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
