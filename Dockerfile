FROM openjdk:17-jdk-slim

# Copy Files
WORKDIR /usr/src/app
COPY . .

# Install
RUN ./mvnw -Dmaven.test.skip=true package

ENV DJL_DEFAULT_ENGINE=PyTorch

# Docker Run Command
EXPOSE 8082
CMD ["java","-jar","/usr/src/app/target/project2-0.0.1-SNAPSHOT.jar"]