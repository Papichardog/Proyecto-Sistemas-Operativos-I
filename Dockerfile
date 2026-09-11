# Etapa 1: compilación. Usa Maven + JDK completo, pero esta capa se
# descarta al final — no infla el tamaño de la imagen que se despliega.
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos primero solo el pom.xml y descargamos dependencias: si el
# código cambia pero el pom.xml no, Docker reutiliza esta capa cacheada
# en vez de volver a descargar todo Internet en cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen final. Solo el JRE (no el JDK completo) y el .jar ya
# compilado — mucho más liviana que arrastrar Maven a producción.
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
