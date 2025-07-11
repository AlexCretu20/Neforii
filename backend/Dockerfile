#backend dockerfile
FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY target/backend-1.0-SNAPSHOT-jar-with-dependencies.jar backend.jar

#ENTRYPOINT si NU CMD, deoarece entrypoint defineste comanda de baza ce nu se schimba, iar CMD poate fi suprascris la docker run
ENTRYPOINT ["java", "-jar", "backend.jar"]