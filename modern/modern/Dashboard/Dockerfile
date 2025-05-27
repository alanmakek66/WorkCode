FROM openjdk:17

WORKDIR /app

COPY target/demoapp123.jar app.jar

EXPOSE 61001

ENTRYPOINT ["java", "-jar", "app.jar"]