FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file (adjust name if needed)
COPY build/libs/EmployeeAPI-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
