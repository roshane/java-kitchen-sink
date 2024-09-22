FROM azul/zulu-openjdk-alpine:21-latest
#FROM azul/zulu-openjdk-alpine:21-jre-headless-latest
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
