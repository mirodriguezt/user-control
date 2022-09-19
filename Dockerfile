#FROM amazoncorretto:11-alpine-jdk
#ARG PROFILE
#ARG ADDITIONAL_OPTS
#ENV PROFILE=${PROFILE}
#ENV ADDITIONAL_OPTS=${ADDITIONAL_OPTS}
#MAINTAINER baeldung.com
#COPY target/user-control*.jar user-control.jar
#ENTRYPOINT ["java","-jar","/user-control.jar"]
#SHELL ["/bin/sh", "-c"]
#EXPOSE 5005
#EXPOSE 8080
#CMD java ${ADDITIONAL_OPTS} -jar user-control.jar --spring.profiles.active=${PROFILE}
FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]