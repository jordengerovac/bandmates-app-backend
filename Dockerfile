FROM openjdk:11
ADD target/bandmates-0.0.1-SNAPSHOT.jar bandmates-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bandmates-0.0.1-SNAPSHOT.jar"]