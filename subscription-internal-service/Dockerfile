FROM openjdk:8-jdk-alpine
VOLUME /app
COPY target/subscription-internal-service.jar .
EXPOSE 8081
ENTRYPOINT exec java $JAVA_OPTS -jar /subscription-internal-service.jar