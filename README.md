# Advice API

A simple Spring Boot WebFlux API that provides random advice by consuming the [Advice Slip API](https://api.adviceslip.com/).

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+

### Run the Application

```bash
# Clone and navigate to project
git clone <repository-url>
cd advice-api

# Run with Maven
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Test the API

```bash
# Get random advice
curl http://localhost:8080/advice/random

# View API documentation
open http://localhost:8080/swagger-ui.html
```

### Expected Response

```json
{
  "id": 165,
  "advice": "Eliminate the unnecessary."
}

```

## Build & Package

```bash
# Run tests
mvn test

# Create JAR
mvn clean package

# Run JAR
java -jar target/advice-api-1.0.0.jar
```

## Configuration

Default configuration in `application.yml`:
- **Port**: 8080
- **External API**: https://api.adviceslip.com

## Troubleshooting

If the API returns error 503, verify external API is working:
```bash
curl https://api.adviceslip.com/advice
```