# Use JDK 17 base image
FROM eclipse-temurin:17-jdk-jammy

# Install Tesseract OCR native packages
RUN apt-get update && apt-get install -y tesseract-ocr libtesseract-dev

WORKDIR /app

# Copy all files from the backend directory
COPY . .

# Build executable JAR using Maven Wrapper
RUN ./mvnw clean package -DskipTests

EXPOSE 8085

# Launch Spring Boot app
CMD ["sh", "-c", "java -jar target/*.jar"]