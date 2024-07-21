# JDK 17
FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY target/cooperativa-votacao-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

EXPOSE 8080

ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "/app.jar"]