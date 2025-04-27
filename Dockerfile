FROM openjdk:17

WORKDIR /app

COPY ./target/app-0.0.1-SNAPSHOT.jar /app/
COPY src/main/resources/application.yml /app/application.yml
COPY src/main/resources/application-prod.yml /app/application-prod.yml

CMD ["java", "-jar", "app-0.0.1-SNAPSHOT.jar", "--spring.config.location=application.yml"]
