# Blog API

A comprehensive blog API developed with Spring Boot.

## Features

- 🔐 JWT Authentication
- 📝 Blog post management (CRUD)
- 💬 Comment system
- 🔒 Spring Security integration
- 📊 API Documentation (Swagger)
- ✅ Unit & Integration Tests
- 🚀 Docker support
- 📈 Caching
- 🏥 Health checks

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- H2 Database
- JWT
- Swagger/OpenAPI
- JUnit 5
- Docker

## Installation and Setup

### Requirements
- Java 17+
- Maven 3.6+

### Steps

1. Clone the project
2. Install dependencies:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will run at http://localhost:8080

## API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` -  User login

### Posts
- `GET /posts` - List all posts
- `GET /posts/{id}` - Get post details + comments
- `POST /posts` - Create new post (auth required)
- `PUT /posts/{id}` - Update post (auth required)
- `DELETE /posts/{id}` - Delete post (auth required)

### Comments
- `POST /posts/{id}/comments` - Add comment (auth required)

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html

## Test

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn test -Dspring.profiles.active=test
```

### API Test Script
```bash
./test-api.sh
```

## Database

H2 Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (empty)

## Docker

```bash
# Build
mvn clean package
docker build -t blog-api .

# Run
docker run -p 8080:8080 blog-api

# Docker Compose
docker-compose up
```

## Health Check

http://localhost:8080/actuator/health

## Usage Examples

### 1. User Registration
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Create Post
```bash
curl -X POST http://localhost:8080/posts \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Blog Post",
    "content": "This is my first blog post..."
  }'
```

## Developer Notes

- JWT secret should be taken from environment variables in production
- PostgreSQL should be used in production
- Logging configuration should be adapted for production
- Rate limiting can be added
- Email verification system can be implemented


