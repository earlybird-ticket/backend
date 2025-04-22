FROM gradle:8.13-jdk17 AS builder

WORKDIR /app

ARG MODULE_NAME

COPY settings.gradle .
COPY ./${MODULE_NAME}/build.gradle .
COPY ./${MODULE_NAME}/gradle gradle
COPY gradlew .

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

COPY common common
COPY ${MODULE_NAME} ${MODULE_NAME}

RUN find /app -type d

RUN chmod +x ./gradlew
RUN ./gradlew :${MODULE_NAME}:clean :${MODULE_NAME}:build -x test

RUN find /app -name "*.jar"

FROM eclipse-temurin:17-jre

ARG MODULE_NAME

WORKDIR /app

COPY --from=builder /app/${MODULE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

