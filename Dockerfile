# Multi-module reactor build: parent pom + kos-domain + kos-application.
# The bootable jar (spring-boot-maven-plugin) is produced in kos-application.
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copy the reactor poms first so dependency resolution can be layer-cached.
COPY pom.xml .
COPY kos-domain/pom.xml kos-domain/
COPY kos-application/pom.xml kos-application/
# go-offline can't resolve the not-yet-built kos-domain artifact; tolerate that
# gap here and let the reactor build below resolve it in module order.
RUN mvn -B -q dependency:go-offline || true
COPY kos-domain/src kos-domain/src
COPY kos-application/src kos-application/src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/kos-application/target/kos-application-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
