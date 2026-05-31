# Compilar el código usando Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copiamos todo el código fuente al contenedor
COPY . .
# Compilamos saltando los tests para ir más rápido
RUN mvn clean package -DskipTests

# Crear la imagen final superligera solo para ejecutar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia el arranque
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto de Spring Boot
EXPOSE 8080
# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
