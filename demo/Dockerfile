# Use official OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run jar file
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
