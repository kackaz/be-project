# Code Style / Project Conventions

This document summarizes coding conventions for the `be-project` (Java 21, Spring Boot 3.5).

Principles
- Aim for readable, maintainable, and secure code.
- Agree on conventions as a team and apply them consistently.

1) File format and encoding
- Use UTF-8 encoding.
- Line length: ~100–120 characters recommended.
- Use spaces (4 spaces) instead of tabs.
- Enable automatic formatting in your IDE (use a shared formatter such as google-java-format or an agreed IDE profile).

2) .editorconfig (recommended)
- Add a `.editorconfig` file at the repository root to enforce basic editor rules across IDEs.

3) Project/package structure
- Organize code by layer:
  - `com.example.beproject.controller`
  - `com.example.beproject.service`
  - `com.example.beproject.repository`
  - `com.example.beproject.model` (or `entity`)
  - `com.example.beproject.dto`
  - `com.example.beproject.config`
  - `com.example.beproject.util`
- Keep one top-level public class per `*.java` file.

4) Naming conventions
- Packages: lower-case.
- Classes / Interfaces: UpperCamelCase (e.g., `AuthService`, `UserRepository`).
- Methods, variables, fields: lowerCamelCase.
- Constants: UPPER_SNAKE_CASE.
- REST paths: kebab-case or semantic paths (e.g., `/users`, `/auth/login`).

5) Controllers
- Controllers should be thin: receive request -> validate -> call service -> return ResponseEntity.
- Avoid business logic in controllers.
- Use DTOs for request and response payloads.
- Use `@RestController` and `@RequestMapping` to group endpoints.

6) Services
- Contain business logic.
- Coordinate repositories and map between domain models and DTOs.
- Apply `@Transactional` only where needed.

7) Repository / Database
- Use `PreparedStatement`, `JdbcTemplate`, or an ORM to prevent SQL injection.
- Avoid string concatenation for SQL statements.
- For production, use schema migration tools (Flyway or Liquibase).
- Keep SQL in the repository layer.

8) Security
- Hash passwords with BCrypt (`spring-security-crypto`) before storing them.
- Store JWT secret and other secrets in environment variables or a secret store (do not commit secrets to the repository).
- Include and validate token expiration in JWTs.

9) Validation
- Use Jakarta Bean Validation (`jakarta.validation`) annotations such as `@NotNull`, `@Email`, `@Size` on DTOs.
- Use `@Valid` in controller method parameters and provide a global exception handler for binding errors.

10) Error handling
- Use a `@ControllerAdvice` to map exceptions to appropriate HTTP responses.
- Return appropriate HTTP status codes (400, 401, 403, 404, 500).

11) Logging
- Use SLF4J (Logback is the Spring Boot default).
- Never log sensitive data (passwords, secrets, full tokens).
- Use appropriate log levels: DEBUG, INFO, WARN, ERROR.

12) Tests
- Separate unit tests and integration tests.
- Unit tests: JUnit + Mockito (mock repositories in service tests).
- Controller tests: MockMvc (standaloneSetup) for unit tests; `@SpringBootTest` for integration tests.
- Integration tests against a database should use an isolated test database (SQLite temp file, Docker, or an embedded DB) and clean state between runs.

13) API documentation
- Use `springdoc-openapi` to generate Swagger UI automatically.
- Add Javadoc and OpenAPI annotations (`@Operation`, `@Schema`) for important endpoints.

14) Dependencies
- Manage versions explicitly in `pom.xml`.
- Avoid unused dependencies.

15) Git / Commits
- Commit message format: `<type>: <subject>` (e.g., `feat: add auth endpoints`).
  - Types: feat, fix, chore, docs, refactor, test
- Use feature branches (`feature/<name>`).
- Require tests to pass before merging pull requests.

16) CI/CD (recommended)
- Run `mvn -q -DskipTests=false test` in CI and include static analysis (SpotBugs / Checkstyle / PMD).
- Run build and integration tests in the pipeline.

17) Short examples
- DTO:
  ```java
  public class RegisterRequest { public String email; public String password; /* ... */ }
  ```
- Controller snippet:
  ```java
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) { ... }
  ```

18) Recommended tools
- google-java-format (formatter)
- Checkstyle or SpotBugs
- springdoc-openapi
- BCrypt (`spring-security-crypto`)

---

This is a starter guideline — adapt and extend it to match your team and production requirements.
