# Шаг 1: Сборка толстого JAR-архива с помощью sbt
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1 AS builder
WORKDIR /app
COPY . .
RUN sbt assembly

# Шаг 2: Запуск скомпилированного JAR на легковесной JRE Eclipse Temurin
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Копируем собранный jar-файл из предыдущего шага сборщика
COPY --from=builder /app/target/scala-3.3.3/fintech-backend-assembly-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

