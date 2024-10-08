FROM gradle:8.5.0-jdk21-alpine AS builder

WORKDIR /usr/app/

COPY . .

RUN gradle build --exclude-task test 

FROM eclipse-temurin:21.0.3_9-jdk-alpine

COPY --from=builder /usr/app/build/libs/*.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

CMD java -jar /opt/app/application.jar