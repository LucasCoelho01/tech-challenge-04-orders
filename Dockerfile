# Use the official Maven image as a parent image
FROM maven:3.8.4-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download all Maven dependencies (dependencies will be cached if the pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy the source code into the container
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Start a new stage from the openjdk base image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file from the builder stage to the current directory
COPY --from=builder /app/target/*.jar ./app.jar

# Expose the port the application runs on
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]