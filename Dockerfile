#
# Build stage
#
FROM maven:3.8.5-jdk-17 AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:17.0.7-jdk-slim
COPY --from=build /target/Web_BookStore_BE-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]