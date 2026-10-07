FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY EcoMindApp.java .

RUN javac -d . EcoMindApp.java

CMD ["java", "EcoMind.EcoMindApp"]
