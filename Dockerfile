FROM gradle:7.6.1-jdk17-alpine AS builder

WORKDIR /usr/app/

COPY . .

RUN gradle build --exclude-task test 

FROM eclipse-temurin:17.0.6_10-jre-alpine

COPY --from=builder /usr/app/build/libs/*.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

CMD java -jar /opt/app/application.jar