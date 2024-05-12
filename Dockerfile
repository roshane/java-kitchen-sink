ARG ARTIFACT_NAME=application.jar
ARG BUILD_DIR=/usr/app

# Use an appropriate base image
FROM azul/zulu-openjdk-alpine:17.0.8.1-17.44.53 AS build
ARG ARTIFACT_NAME
ARG BUILD_DIR

# Set working directory
WORKDIR $BUILD_DIR

# Copy your application files into the container
COPY . $BUILD_DIR

# Build your application (adjust the Maven command as needed)
RUN ./mvnw clean install -pl spring-boot-gql
RUN cp $BUILD_DIR/spring-boot-gql/target/$(./mvnw -q validate) $BUILD_DIR/$ARTIFACT_NAME

# Final stage
FROM azul/zulu-openjdk:21-jre
ARG ARTIFACT_NAME
ARG BUILD_DIR
# Copy the version text from previous stage
# Copy the built JAR from the build stage
COPY --from=build $BUILD_DIR/$ARTIFACT_NAME /usr/bin/$ARTIFACT_NAME

# Set an entrypoint
ENTRYPOINT ["java", "-jar", "/usr/bin/application.jar"]
