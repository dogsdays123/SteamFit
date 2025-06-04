FROM openjdk:17-jdk
VOLUME /uploadtest
ARG JAR_FILE=build/libs/kdt.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
EXPOSE 80