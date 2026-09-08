# ==========================================
# Stage 1: Build Stage
# ==========================================
FROM maven:3.9.7-eclipse-temurin-22 AS build
# WHEN READY FOR JAVA 21: FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /build

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application securely (skipping tests to speed up docker build)
RUN mvn clean package -DskipTests -DGCLOUD_PROJECT=tax-calculator

# ==========================================
# Stage 2: Hardened Runtime Stage
# ==========================================
FROM eclipse-temurin:22-jre-alpine
# WHEN READY FOR DHI JAVA 21: FROM dhi.io/eclipse-temurin:21-alpine3.22

# Set Timezone
ENV TZ=Europe/Rome

# Security: Create a dedicated non-root user and group
# (Alpine uses 'addgroup' and 'adduser')
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set the working directory
WORKDIR /app

# Copy resources (if explicitly needed outside the jar) and set ownership
COPY --chown=appuser:appgroup src/main/resources /app/resources
# WHEN READY FOR DHI JAVA 21: COPY --chown=nonroot:nonroot src/main/resources /app/resources

# Copy only the built JAR from the build stage to the runtime stage
COPY --from=build --chown=appuser:appgroup /build/target/tax-calculator.jar /app/tax-calculator.jar
# WHEN READY FOR DHI JAVA 21: COPY --from=build --chown=nonroot:nonroot /build/target/tax-calculator.jar /app/tax-calculator.jar

# Security: Switch to the non-root user to run the application
USER appuser:appgroup
# WHEN READY FOR DHI JAVA 21 (DHI images have 'nonroot' built-in): USER nonroot

# Expose the port the app runs on
EXPOSE 8080

# Security: Prevent JVM from using DNS caching issues and limit RAM footprint if necessary
# Add standard hardening JVM flags
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/tax-calculator.jar"]
