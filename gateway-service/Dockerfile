FROM openjdk:8-jdk-alpine
VOLUME /app
COPY target/gateway-service.jar .
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar /gateway-service.jar