FROM eclipse-temurin:17-jre

COPY target/*.jar todoItemApp.jar

CMD java -jar todoItemApp.jar