FROM openjdk:17-jdk-slim
WORKDIR /app
COPY EcoMindApp.java .
COPY ecomind_data.txt .
RUN javac EcoMindApp.java
CMD ["java", "EcoMindApp"]
