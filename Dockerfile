#
# Build stage
#
#FROM maven:3.8.5-openjdk-17 AS build
#COPY . .
#RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/book-store-be.jar book-store-be.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/book-store-be.jar"]