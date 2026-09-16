# NovaBank Customer Registration API

A Spring Boot backend assignment that implements a secure customer registration endpoint for NovaBank.

The project focuses on building one endpoint correctly and safely:

```http
POST /api/v1/auth/register
```

The implementation covers layered architecture, DTO boundaries, backend validation, cross-field validation, BCrypt password hashing, duplicate prevention, mass-assignment protection, JPA auditing, Spring Security endpoint exposure, global exception handling, and safe logging.

> Current project note: the implementation is running on Java 17. The original assignment specification requests Java 21.

---

## 1. Technology Stack

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- BCrypt / Spring Security Crypto
- H2 Database
- Lombok
- Springdoc OpenAPI / Swagger UI
- Maven
- Postman
- SLF4J + Logback

---

## 2. Project Structure

```text
src/main/java/com/novabank/novabank_registration
│
├── customer
│   ├── controller
│   │   └── RegistrationController.java
│   ├── dto
│   │   ├── RegistrationRequestDto.java
│   │   └── RegistrationResponseDto.java
│   ├── entity
│   │   ├── Customer.java
│   │   ├── CustomerStatus.java
│   │   ├── Role.java
│   │   └── RoleName.java
│   ├── mapper
│   │   └── RegistrationMapper.java
│   ├── repository
│   │   ├── CustomerRepository.java
│   │   └── RoleRepository.java
│   ├── service
│   │   └── RegistrationService.java
│   └── validation
│       ├── PasswordsMatch.java
│       ├── PasswordsMatchValidator.java
│       ├── PasswordNotEmail.java
│       └── PasswordNotEmailValidator.java
│
├── common
│   ├── entity
│   │   └── BaseEntity.java
│   ├── error
│   │   ├── ApiErrorResponse.java
│   │   ├── FieldErrorDto.java
│   │   ├── DuplicateResourceException.java
│   │   └── GlobalExceptionHandler.java
│   └── logging
│       └── LogMasker.java
│
└── config
    ├── PasswordConfig.java
    ├── JpaAuditingConfig.java
    ├── SecurityConfig.java
    ├── DevSecurityConfig.java
    └── RoleDataInitializer.java
```

---

## 3. Setup

### Prerequisites

Install:

- JDK 21
- Maven
- Git
- Postman

Verify:

```bash
java -version
mvn -v
```

### Run the Application

From the project root:

```bash
mvn clean spring-boot:run
```

The application currently runs on:

```text
http://localhost:8081
```

The registration endpoint is:

```text
http://localhost:8081/api/v1/auth/register
```

---

## 4. Development Profile

The development profile uses H2 and exposes development tools.

Example configuration:

```properties
spring.datasource.url=jdbc:h2:file:./data/novabank
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

server.port=8081
```

H2 Console:

```text
http://localhost:8081/h2-console
```

The H2 console is intended for the `dev` profile only.

---

## 5. Swagger / OpenAPI

Swagger UI is available in the development profile at:

```text
http://localhost:8081/swagger-ui.html
```

The registration endpoint should document the success response and all supported error responses.

---

## 6. Registration Endpoint

### Request

```http
POST /api/v1/auth/register
Content-Type: application/json
```

Example:

```json
{
  "firstName": "Rola",
  "lastName": "Essam",
  "email": "rola@example.com",
  "mobileNumber": "0123456789",
  "password": "Password123",
  "confirmPassword": "Password123"
}
```

### Successful Response

```http
201 Created
```

A successful response contains only safe public data, for example:

```json
{
  "customerId": 1,
  "firstName": "Rola",
  "lastName": "Essam",
  "email": "rola@example.com",
  "status": "PENDING_VERIFICATION",
  "registeredAt": "2026-09-16T12:00:00"
}
```

The response also contains a `Location` header pointing to the newly created customer resource.

No password or password hash is returned.

---

## 7. Validation Rules

### First Name

- Required
- Maximum 50 characters

### Last Name

- Required

### Email

- Required
- Must be a valid email address
- Trimmed and normalized to lowercase before persistence
- Duplicate comparison is case-insensitive

### Mobile Number

- Required
- Exactly 10 digits
- Letters and spaces are rejected
- Must be unique

### Password

- Required
- Minimum 8 characters
- Must contain at least one letter
- Must contain at least one digit
- Must match `confirmPassword`

---

## 8. Error Contract

All handled 4xx and 5xx responses use a common error envelope:

```json
{
  "apiPath": "/api/v1/auth/register",
  "errorCode": "ERROR_CODE",
  "errorMessage": "Human readable message",
  "errorTime": "2026-09-16T12:00:00"
}
```

Validation responses additionally include `fieldErrors`.

| HTTP Status | Scenario | errorCode |
|---|---|---|
| 201 Created | Registration succeeded | — |
| 400 Bad Request | Field validation failed | `VALIDATION_FAILED` |
| 400 Bad Request | Passwords do not match | `PASSWORD_MISMATCH` |
| 400 Bad Request | Malformed JSON | `MALFORMED_REQUEST` |
| 409 Conflict | Email already registered | `EMAIL_ALREADY_REGISTERED` |
| 409 Conflict | Mobile already registered | `MOBILE_ALREADY_REGISTERED` |
| 500 Internal Server Error | Unexpected unhandled error | `INTERNAL_ERROR` |

The implementation also contains a defensive database-integrity fallback that returns HTTP 409 when a persistence constraint conflict cannot be safely identified as an email or mobile duplicate.

---

## 9. Password Security

Passwords are never stored in plaintext.

The registration flow is:

```text
raw password
    ↓
BCryptPasswordEncoder
    ↓
salted BCrypt hash
    ↓
customers.password_hash
```

The same password registered for two customers produces different hashes because BCrypt uses a per-password salt.

Passwords and password hashes must never appear in:

- API responses
- application logs
- exception messages
- committed files

---

## 10. Duplicate Prevention

Duplicate registration is protected at two levels.

### Application Layer

The service checks:

```java
existsByEmailIgnoreCase(...)
existsByMobileNumber(...)
```

This allows the API to return clear business errors.

### Database Layer

Database unique constraints provide the final correctness guarantee if concurrent requests pass the application check at the same time.

Duplicate email comparison is case-insensitive after normalization.

---

## 11. Mass-Assignment Protection

`RegistrationRequestDto` deliberately contains only:

```text
firstName
lastName
email
mobileNumber
password
confirmPassword
```

It does not contain:

```text
id
status
role
roles
createdAt
updatedAt
passwordHash
```

Therefore a payload such as:

```json
{
  "role": "ADMIN",
  "status": "ACTIVE",
  "id": 1
}
```

cannot grant privileges or override server-controlled values.

The server assigns:

```text
role   = CUSTOMER
status = PENDING_VERIFICATION
id     = database-generated
```

---

## 12. Roles

The role model contains:

```text
CUSTOMER
TELLER
ADMIN
```

Registration always assigns the `CUSTOMER` role.

The database uses:

```text
roles
customer_roles
```

to represent customer-role relationships.

---

## 13. Auditing

`Customer` inherits audit fields from `BaseEntity`.

JPA auditing automatically manages:

```text
createdAt
updatedAt
```

The API request cannot write these fields.

---

## 14. Security Configuration

The registration endpoint is public:

```text
/api/v1/auth/register
```

Other endpoints require authentication.

Default Spring Security form login and HTTP Basic are disabled.

Development-only access is configured separately for tools such as:

```text
/h2-console/**
/swagger-ui/**
```

---

## 15. Safe Logging

Successful registration is logged at `INFO`.

Example:

```text
Customer registered successfully: customerId=12, email=r***@example.com
```

Validation failures can be logged at `DEBUG`, but password fields are represented as:

```text
password=***
confirmPassword=***
```

Raw passwords and hashes are never intentionally logged.

Logback is configured for console and rolling-file output.

---

## 16. Postman

Use an environment variable:

```text
baseUrl = http://localhost:8081
```

Requests should use:

```text
{{baseUrl}}/api/v1/auth/register
```

The collection should include the success case and all acceptance scenarios, including validation failures, duplicates, malformed JSON, and mass-assignment attempts.

---



## 18. Testing Status

The current implementation has been manually exercised through Postman.

**Known gap:** the automated REG-304 test suite has not yet been implemented. The assignment requires unit tests, repository tests, validator tests, MockMvc integration tests, and at least 70% service-layer JaCoCo line coverage. Postman testing does not replace these automated requirements.

---

## 19. Build

Run:

```bash
mvn clean verify
```

The final assignment requires this command to succeed with no disabled or ignored tests.

---


