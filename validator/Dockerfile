# Use a imagem oficial do Maven para construir a aplicação
FROM maven:3.8.4-openjdk-17 AS build

# Copie o código fonte e o arquivo pom.xml para o container
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

# Compile o projeto e crie o arquivo JAR
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

# Use uma imagem oficial do OpenJDK 17 como base para rodar a aplicação
FROM openjdk:17-jdk-slim

# Copie o arquivo JAR criado na fase de build para o container
COPY --from=build /usr/src/app/target/validator-0.0.1-SNAPSHOT.jar validator.jar

# Defina o comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "/validator.jar"]

EXPOSE 8081