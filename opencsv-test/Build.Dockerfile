FROM azul/zulu-openjdk-alpine:21-latest AS build
ARG POSTGRES_HOST
ENV POSTGRES_HOST=$POSTGRES_HOST
WORKDIR /src
COPY . /src
RUN env
RUN ip a
RUN nc -zv -w 5 $POSTGRES_HOST 5432
RUN ./mvnw clean package

#FROM azul/zulu-openjdk-alpine:21-jre-headless-latest
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
