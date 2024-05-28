FROM openjdk

COPY target/*.jar todoItemApp.jar

CMD java -jar todoItemApp.jar