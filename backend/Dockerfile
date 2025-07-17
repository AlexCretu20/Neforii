#dockerfile folosit ca sa generam target/jar
#versiunea din pom.xml
FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY . .

RUN apt-get update && apt-get install -y maven && mvn clean package

#ENTRYPOINT si NU CMD, deoarece entrypoint defineste comanda de baza ce nu se schimba, iar CMD poate fi suprascris la docker run
ENTRYPOINT ["java", "-jar", "target/backend-1.0-SNAPSHOT-jar-with-dependencies.jar"]