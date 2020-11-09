FROM java:8
WORKDIR /
ADD AuthenticationService.jar AuthenticationService.jar
ENTRYPOINT ["java", "-Dprocess.name=AuthenticationService", "-jar", "AuthenticationService.jar"]
