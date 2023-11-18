FROM openjdk:17
ADD ./book-store-be.jar book-store-be.jar
ENTRYPOINT ["java", "-jar", "book-store-be.jar"]