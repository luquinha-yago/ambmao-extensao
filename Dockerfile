FROM node:24-bookworm-slim AS frontend
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY src/ ./src/
COPY scripts/exportar-spring.mjs ./scripts/exportar-spring.mjs
RUN npm run backend:prepare

FROM eclipse-temurin:26-jdk AS build
WORKDIR /app
COPY .mvn/ ./.mvn/
COPY mvnw ./mvnw
COPY backend/pom.xml ./backend/pom.xml
COPY backend/src/ ./backend/src/
COPY --from=frontend /app/backend/src/main/resources/ ./backend/src/main/resources/
RUN chmod +x mvnw && ./mvnw -B -f backend/pom.xml -DskipTests package

FROM eclipse-temurin:26-jdk AS runtime
WORKDIR /app
COPY --from=build --chown=10001:10001 /app/backend/target/ambmao-0.0.1-SNAPSHOT.jar ./app.jar
USER 10001:10001
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
