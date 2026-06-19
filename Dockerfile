# ---- Estágio de build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Compila e empacota (sem rodar testes na imagem)
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Estágio de runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
