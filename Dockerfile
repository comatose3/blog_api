# Stage 1: Build with JDK 24
FROM openjdk:24-ea-17-jdk-slim AS build
WORKDIR /app

# Копируем исходный код
COPY . .

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:24-ea-17-jdk-slim
WORKDIR /app

# Копируем собранный JAR
COPY --from=build /app/target/*.jar app.jar

# Создаем не-root пользователя для безопасности
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]