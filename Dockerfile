FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /usr/app/

COPY . .

RUN chmod +x ./gradlew && ./gradlew build --exclude-task test --no-daemon

FROM eclipse-temurin:25-jre-alpine

COPY --from=builder /usr/app/build/libs/application.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

CMD java -jar /opt/app/application.jar
