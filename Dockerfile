# Use OpenJDK as the base image (Ensure it matches your Java version)
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the target folder (replace with your actual jar file)
COPY target/TodoApp-0.0.1-SNAPSHOT.jar /app/TodoApp.jar

# Expose the port (default for Spring Boot is 8080)
EXPOSE 8080

# Run the Spring Boot app
CMD ["java", "-jar", "/app/TodoApp.jar"]
