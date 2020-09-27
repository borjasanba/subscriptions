FROM openjdk:8-jdk-alpine
VOLUME /app
COPY target/adidas-email-service.jar .
EXPOSE 8082
ENTRYPOINT exec java $JAVA_OPTS -jar /adidas-email-service.jar