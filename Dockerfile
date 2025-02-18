FROM maven:3.9.7-eclipse-temurin-22 AS build
COPY . .
RUN mvn clean package -DGCLOUD_PROJECT=tax-calculator

FROM eclipse-temurin:22-jdk
EXPOSE 8080
WORKDIR /
COPY src/main/resources /app/resources
COPY --from=build /target/tax-calculator.jar tax-calculator.jar

ENTRYPOINT ["java","-jar","tax-calculator.jar"]
ENV TZ=Europe/Rome
