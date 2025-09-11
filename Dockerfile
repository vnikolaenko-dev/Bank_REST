# BUILD STAGE
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /home/app

# Очищаем кэш Maven перед началом
RUN rm -rf /root/.m2/repository

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# RUN STAGE
FROM openjdk:17-jdk-alpine
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "app.jar"]