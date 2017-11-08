FROM java:8
MAINTAINER John Kelly "jkelly@carsaver.com"
ADD target/banana-service.jar app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8080