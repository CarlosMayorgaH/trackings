# Trackings Application Development Guidelines

## Project Overview
This is a Spring Boot 3.5.5 application using Java 21, designed for tracking functionality with PostgreSQL database integration. The application leverages both traditional Spring MVC and reactive WebFlux capabilities.

## Build/Configuration Instructions

### Prerequisites
- **Java 21+** (CRITICAL REQUIREMENT)
  - Spring Boot 3.x requires minimum Java 17
  - This project specifically targets Java 21 as defined in pom.xml
  - Verify Java version: `java -version`
  - If using older Java versions, you'll encounter compilation errors like "class file has wrong version"

### Maven Build
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package the application
./mvnw package

# Run the application
./mvnw spring-boot:run
```

### Key Dependencies
- **Spring Boot Starter Web**: Traditional MVC web applications
- **Spring Boot Starter WebFlux**: Reactive web applications
- **Spring Boot Starter Data JPA**: Database access with Hibernate
- **PostgreSQL Driver**: Database connectivity
- **Flyway**: Database migration management
- **Lombok**: Reduces boilerplate code (requires IDE plugin)
- **Spring Boot Docker Compose**: Automatic database container management

### Database Setup
The application uses automatic Docker Compose integration:

1. **PostgreSQL Configuration** (compose.yaml):
   - Database: `mydatabase`
   - Username: `myuser`
   - Password: `secret`
   - Port: 5432 (mapped dynamically)

2. **Automatic Startup**:
   - Spring Boot automatically starts PostgreSQL container when running the application
   - No manual Docker commands needed
   - Database connection is auto-configured

3. **Manual Docker Setup** (if needed):
   ```bash
   docker-compose up -d postgres
   ```

### Flyway Database Migrations
- Migration scripts should be placed in `src/main/resources/db/migration/`
- Naming convention: `V{version}__{description}.sql`
- Example: `V1__Create_user_table.sql`

## Testing Information

### Test Framework
- **JUnit 5**: Primary testing framework
- **Spring Boot Test**: Integration testing support
- **Reactor Test**: For reactive components testing

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TrackingsApplicationTests

# Run with specific profile
./mvnw test -Dspring.profiles.active=test
```

### Test Structure Examples
```java
@SpringBootTest
@ActiveProfiles("test")
class MyServiceTest {
    
    @Test
    void testServiceMethod() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Creating New Tests
1. Create test classes in `src/test/java/com/example/trackings/`
2. Use `@SpringBootTest` for integration tests
3. Use `@ActiveProfiles("test")` for test-specific configuration
4. Follow AAA pattern: Arrange, Act, Assert
5. Use descriptive test method names with `void testMethodName()` format

### Test Configuration
- Test profiles can be configured in `application-test.properties`
- Use `@TestPropertySource` for test-specific properties
- Mock external dependencies using `@MockBean`

## Code Style and Development Guidelines

### Project Structure
```
src/
├── main/
│   ├── java/com/example/trackings/
│   │   └── TrackingsApplication.java
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
    └── java/com/example/trackings/
        └── *Tests.java
```

### Lombok Usage
- Project includes Lombok for reducing boilerplate code
- Common annotations: `@Data`, `@Entity`, `@Builder`, `@Slf4j`
- Ensure your IDE has Lombok plugin installed
- Annotation processing is configured in Maven compiler plugin

### Configuration Management
- Minimal configuration in `application.properties`
- Spring Boot auto-configuration handles most setup
- Use profiles for environment-specific settings
- Database connection handled by Docker Compose integration

### Reactive vs Traditional Web
The project supports both paradigms:
- **Traditional MVC**: Use `@RestController`, `@Service`, blocking operations
- **Reactive WebFlux**: Use `@RestController` with `Mono<T>` and `Flux<T>`
- Choose based on use case - reactive for high concurrency, traditional for simplicity

## Troubleshooting

### Common Issues
1. **Java Version Mismatch**
   - Error: "class file has wrong version X.X, should be Y.Y"
   - Solution: Ensure Java 21+ is installed and set as JAVA_HOME
   - Verify: `java -version` and `javac -version`

2. **Database Connection Issues**
   - Ensure Docker is running
   - Check compose.yaml configuration
   - Verify PostgreSQL container is starting: `docker ps`

3. **Lombok Not Working**
   - Install Lombok plugin in your IDE
   - Enable annotation processing in IDE settings
   - Restart IDE after plugin installation

4. **Maven Wrapper Issues**
   - Ensure execute permissions: `chmod +x mvnw` (Linux/Mac)
   - Use `mvnw.cmd` on Windows: `./mvnw.cmd clean compile`

### Development Setup Verification
Run this checklist to ensure proper setup:
```bash
# 1. Check Java version (should be 21+)
java -version

# 2. Compile project
./mvnw clean compile

# 3. Run tests
./mvnw test

# 4. Start application (with database)
./mvnw spring-boot:run
```

## Additional Notes
- The application uses Spring Boot's auto-configuration extensively
- Database schema changes should use Flyway migrations
- Consider using `@Profile` annotations for environment-specific beans
- Monitor application with Spring Boot Actuator (add dependency if needed)
- Use `@ConfigurationProperties` for complex configuration binding

---
*Last updated: 2025-09-03*