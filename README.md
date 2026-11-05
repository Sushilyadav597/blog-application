# 🚀 RESTful Blog Application API

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6-green.svg)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-JJWT%200.12.6-blue.svg)](https://github.com/jwtk/jjwt)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger%20UI-success.svg)](http://localhost:8080/swagger-ui/index.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE)

An industry-standard, production-ready **RESTful Blog Application API** engineered with **Java 21**, **Spring Boot 3.3.5**, **Spring Security 6**, **JSON Web Tokens (JWT)**, **Spring Data JPA / Hibernate**, and **MySQL 8**.

Architected with strict adherence to **SOLID principles**, **Separation of Concerns**, **Layered Architecture**, and clean coding practices. Built without containerization dependencies (No Docker required) to run directly via **Eclipse**, **IntelliJ IDEA**, or **Maven CLI**.

---

## 📑 Table of Contents
1. [Core Features](#-core-features)
2. [Technology Stack](#-technology-stack)
3. [Project Folder Structure](#-project-folder-structure)
4. [Database Design & Schema](#-database-design--schema)
5. [Standardized API Response Envelopes](#-standardized-api-response-envelopes)
6. [API Endpoint Reference](#-api-endpoint-reference)
7. [Security & Authentication Architecture](#-security--authentication-architecture)
8. [MySQL Configuration & Setup (No Docker)](#-mysql-configuration--setup-no-docker)
9. [How to Run the Application](#-how-to-run-the-application)
10. [Swagger / OpenAPI Documentation](#-swagger--openapi-documentation)
11. [Unit & Integration Testing Suite](#-unit--integration-testing-suite)
12. [Postman Collection Guide](#-postman-collection-guide)
13. [25-Day Assignment Plan](#-25-day-assignment-plan)
14. [GitHub Submission Checklist](#-github-submission-checklist)

---

## 🌟 Core Features

- **User Authentication & Authorization**:
  - Secure account registration with input validation.
  - BCrypt password encryption (never stored or exposed in plaintext).
  - Stateless authentication with signed JWT tokens (HMAC-SHA256).
  - Role-based authorization (`ROLE_USER`, `ROLE_ADMIN`).
  - Current authenticated user profile retrieval (`GET /api/auth/me`).
- **Blog Post Management**:
  - Public read-only access for viewing published posts or single post details.
  - Authenticated post creation.
  - Server-side pagination and dynamic multi-field sorting.
  - **Search & Filter**: Search by title, search by content, and filter by author ID.
  - Strict ownership validation (only original authors or administrators may edit or delete a post).
- **Interactive Comment System**:
  - Authenticated comment submission on any published blog post.
  - Public retrieval of comments filtered by post ID.
  - Strict ownership validation for updating and deleting comments.
- **Enterprise Error & Exception Handling**:
  - Centralized `@RestControllerAdvice` handling custom exceptions (`400`, `401`, `403`, `404`, `409`, `500`).
  - Standardized JSON responses (`success: true/false`, `message`, `data`, `status`).
- **Interactive API Documentation**:
  - Embedded **Springdoc OpenAPI 3 / Swagger UI** at `http://localhost:8080/swagger-ui/index.html` with Bearer Token authorization.

---

## 🛠 Technology Stack

| Layer | Technology | Details / Version |
|---|---|---|
| **Language** | Java 21 LTS | Records, Pattern matching, virtual-thread compatibility |
| **Framework** | Spring Boot | 3.3.5 (Spring Framework 6.1) |
| **Security** | Spring Security 6 | Modern `SecurityFilterChain`, stateless `SessionCreationPolicy.STATELESS` |
| **Authentication** | JJWT | 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) |
| **Persistence** | Spring Data JPA / Hibernate | Hibernate ORM, Declarative `@Transactional` |
| **Database** | MySQL 8 / H2 Test | MySQL 8 (`blog_db`), in-memory H2 for isolated unit/integration tests |
| **Validation** | Jakarta Bean Validation | `@Valid`, `@NotBlank`, `@Email`, `@Size` |
| **Documentation** | Springdoc OpenAPI | 2.6.0 with Swagger UI (`/swagger-ui/index.html`) |
| **Build Tool** | Apache Maven | Maven 3.9+ |
| **Testing** | JUnit 5, Mockito, MockMvc | 44 comprehensive unit and web integration tests |
| **Development** | Eclipse / IntelliJ / VS Code | Native IDE compatibility (No Docker needed) |

---

## 🏗 Project Folder Structure

```
blog-application-api/
│
├── src/main/java/com/blog/
│   ├── config/
│   │   └── OpenApiConfig.java              # Swagger / OpenAPI Configuration
│   ├── controller/
│   │   ├── AuthController.java             # User Authentication & Profile
│   │   ├── PostController.java             # Post CRUD, Pagination, Search & Filter
│   │   └── CommentController.java          # Comment CRUD
│   ├── service/
│   │   ├── AuthService.java                # Authentication Contract
│   │   ├── PostService.java                # Post Business Logic Contract
│   │   ├── CommentService.java             # Comment Business Logic Contract
│   │   └── impl/
│   │       ├── AuthServiceImpl.java        # Registration, Login, Token Issuance
│   │       ├── PostServiceImpl.java        # Post Business Logic & Ownership Checks
│   │       └── CommentServiceImpl.java     # Comment Business Logic & Ownership Checks
│   ├── repository/
│   │   ├── UserRepository.java             # User Data Access
│   │   ├── PostRepository.java             # Post Data Access, Search Query & Paging
│   │   └── CommentRepository.java          # Comment Data Access & Counting
│   ├── entity/
│   │   ├── User.java                       # User Entity (One-to-Many Posts & Comments)
│   │   ├── Post.java                       # Post Entity (Many-to-One User, One-to-Many Comments)
│   │   ├── Comment.java                    # Comment Entity (Many-to-One User & Post)
│   │   └── Role.java                       # Security Role (ROLE_USER, ROLE_ADMIN)
│   ├── enums/
│   │   └── Role.java                       # Standard Enum Location
│   ├── dto/
│   │   ├── request/
│   │   │   ├── RegisterRequest.java        # Registration Payload
│   │   │   ├── LoginRequest.java           # Login Payload
│   │   │   ├── PostRequest.java            # Post Title & Content Payload
│   │   │   └── CommentRequest.java         # Comment Content Payload
│   │   └── response/
│   │       ├── ApiResponse.java            # Generic API Response Wrapper {success, message, data}
│   │       ├── AuthResponse.java           # Token & User Profile
│   │       ├── UserSummaryResponse.java    # Safe Profile (Zero password exposure)
│   │       ├── PostResponse.java           # Post Details with Author Summary
│   │       ├── CommentResponse.java        # Comment Details with Author Summary
│   │       ├── PagedResponse.java          # Paginated Metadata & Records
│   │       └── ErrorResponse.java          # Standard Error Payload {success: false, message, status}
│   ├── security/
│   │   ├── CustomUserDetailsService.java   # Spring Security UserDetails Loader
│   │   ├── UserPrincipal.java              # Authenticated User Adapter
│   │   ├── JwtTokenProvider.java           # JWT Generation & Cryptographic Validation
│   │   ├── JwtAuthenticationFilter.java    # OncePerRequestFilter for Bearer Tokens
│   │   ├── JwtAuthenticationEntryPoint.java# 401 Unauthorized JSON Handler
│   │   └── SecurityConfig.java             # SecurityFilterChain, CORS, BCrypt
│   ├── exception/
│   │   ├── ResourceNotFoundException.java   # 404 Not Found
│   │   ├── EmailAlreadyExistsException.java # 409 Conflict
│   │   ├── UnauthorizedException.java       # 401 Unauthorized
│   │   ├── BadRequestException.java         # 400 Bad Request
│   │   └── GlobalExceptionHandler.java      # Centralized @RestControllerAdvice
│   ├── mapper/
│   │   ├── UserMapper.java                 # Entity to DTO Mapper
│   │   ├── PostMapper.java                 # Entity to DTO Mapper
│   │   └── CommentMapper.java              # Entity to DTO Mapper
│   └── BlogApplication.java                # Spring Boot Entry Point
│
├── src/main/resources/
│   ├── application.properties              # MySQL 8 Configuration
│   └── application-dev.properties          # Standalone H2 Dev Profile
│
├── src/test/java/com/blog/                 # 44 Automated Unit & MockMvc Tests
│
├── database/
│   ├── schema.sql                          # MySQL 8 DDL Database Schema Script
│   └── data.sql                            # Initial Seed Data Script
│
├── postman/
│   └── Blog_Application_API.postman_collection.json # Complete Postman Collection
│
├── README.md                               # Project Documentation
├── pom.xml                                 # Maven Build Configuration
└── .gitignore                              # Git Exclusion Rules
```

---

## 🗄 Database Design & Schema

### Relational Schema (`blog_db`)

```
USERS
├── id (BIGINT, PK, AUTO_INCREMENT)
├── username (VARCHAR(50), NOT NULL)
├── email (VARCHAR(100), NOT NULL, UNIQUE)
├── password (VARCHAR(255), NOT NULL)
├── role (VARCHAR(20), NOT NULL)
└── created_at (TIMESTAMP, NOT NULL)

POSTS
├── id (BIGINT, PK, AUTO_INCREMENT)
├── title (VARCHAR(200), NOT NULL)
├── content (LONGTEXT, NOT NULL)
├── author_id (BIGINT, FK -> USERS.id, ON DELETE CASCADE)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)

COMMENTS
├── id (BIGINT, PK, AUTO_INCREMENT)
├── content (TEXT, NOT NULL)
├── post_id (BIGINT, FK -> POSTS.id, ON DELETE CASCADE)
├── author_id (BIGINT, FK -> USERS.id, ON DELETE CASCADE)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

- **Relationships**:
  - `User 1 ──── N Posts`
  - `User 1 ──── N Comments`
  - `Post 1 ──── N Comments`

---

## 📦 Standardized API Response Envelopes

### Successful Response Format (`ApiResponse<T>`)
```json
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "id": 1,
    "title": "Getting Started with Spring Boot 3",
    "content": "Spring Boot 3 introduces support for Java 21...",
    "author": {
      "id": 1,
      "username": "sushil",
      "email": "sushil@example.com",
      "role": "ROLE_USER"
    },
    "commentCount": 0,
    "createdAt": "2026-11-05T12:00:00"
  }
}
```

### Error Response Format (`ErrorResponse`)
```json
{
  "success": false,
  "message": "Post not found with id : '99'",
  "status": 404,
  "error": "Not Found",
  "timestamp": "2026-11-05T12:00:00",
  "path": "/api/posts/99"
}
```

---

## 📡 API Endpoint Reference

### 1. Authentication Endpoints (`/api/auth`)

| Method | Endpoint | Access | Description | Status |
|---|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new user account & return JWT | `201 Created` |
| `POST` | `/api/auth/login` | Public | Authenticate user & return JWT | `200 OK` |
| `GET` | `/api/auth/me` | Authenticated | Retrieve currently logged-in user profile | `200 OK` |

### 2. Blog Post Endpoints (`/api/posts`)

| Method | Endpoint | Access | Description | Status |
|---|---|---|---|---|
| `GET` | `/api/posts` | Public | List posts with pagination, sorting, search by title/content, and author filter | `200 OK` |
| `GET` | `/api/posts/{id}` | Public | Retrieve single post by ID | `200 OK` |
| `POST` | `/api/posts` | Authenticated | Create a new blog post | `201 Created` |
| `PUT` | `/api/posts/{id}` | Owner / Admin | Update post (Title & Content) | `200 OK`, `403 Forbidden` |
| `DELETE` | `/api/posts/{id}` | Owner / Admin | Delete post and associated comments | `200 OK`, `403 Forbidden` |

#### Query Parameters for `GET /api/posts`:
- `page`: Page index (default `0`)
- `size`: Page size (default `10`)
- `sortBy`: Property to sort by (`createdAt`, `title`, `id`, `updatedAt`)
- `sortDir`: Sort direction (`desc` or `asc`)
- `title`: Filter posts matching title keyword
- `content`: Filter posts matching content keyword
- `authorId`: Filter posts created by a specific user ID

### 3. Comment Endpoints (`/api/posts/{postId}/comments` & `/api/comments/{id}`)

| Method | Endpoint | Access | Description | Status |
|---|---|---|---|---|
| `GET` | `/api/posts/{postId}/comments` | Public | Get all comments for a post | `200 OK` |
| `GET` | `/api/comments/{id}` | Public | Get single comment by ID | `200 OK` |
| `POST` | `/api/posts/{postId}/comments` | Authenticated | Add a comment to a blog post | `201 Created` |
| `PUT` | `/api/comments/{id}` | Owner / Admin | Update comment content | `200 OK`, `403 Forbidden` |
| `DELETE` | `/api/comments/{id}` | Owner / Admin | Delete comment | `200 OK`, `403 Forbidden` |

---

## 🛢 MySQL Configuration & Setup (No Docker)

### 1. Database Connection Properties (`application.properties`)
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:blog_db}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 2. Execute SQL Schema Script
Using MySQL Command Line or MySQL Workbench:
```bash
mysql -u root -p < database/schema.sql
```
*(Optional) Seed initial users and posts:*
```bash
mysql -u root -p < database/data.sql
```

---

## 🚀 How to Run the Application

The project does **NOT** require Docker. It runs directly on any computer using Java 21 and Maven.

### Method 1: Run via Maven (Terminal)
```bash
mvn spring-boot:run
```

### Method 2: Run Executable JAR
```bash
mvn clean package -DskipTests
java -jar target/blog-application-api-1.0.0.jar
```

### Method 3: Run in Eclipse / IntelliJ IDEA
1. Open Eclipse or IntelliJ IDEA.
2. Select **File → Open Project / Import Existing Maven Projects**.
3. Choose the project directory: `blog-application-api`.
4. Locate `com.blog.BlogApplication.java`.
5. Right-click → **Run As → Java Application** (or Spring Boot App).

---

## 📖 Swagger / OpenAPI Documentation

Once running, access the interactive API documentation:

📍 **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
📍 **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### How to Authenticate in Swagger UI:
1. Open [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
2. Expand `Authentication Management` → `POST /api/auth/register` or `POST /api/auth/login`.
3. Submit request and copy the returned `token` from `data.token`.
4. Click the green **Authorize 🔓** button in the top-right corner.
5. Paste the token into the value field and click **Authorize**.

---

## 🧪 Unit & Integration Testing Suite

Execute the complete test suite of **44 automated tests**:
```bash
mvn clean test
```

### Test Coverage Highlights:
- **`JwtTokenProviderTest`**: Token generation, signature verification, expiration check, subject extraction.
- **`AuthServiceTest`**: User registration, duplicate email handling (`409 Conflict`), username validation, login authentication.
- **`PostServiceTest`**: Post creation, pagination, sorting, title search, content search, author update, non-author update denial (`403 Forbidden`), admin deletion rights.
- **`CommentServiceTest`**: Comment publishing, post existence check, ownership verification for updates and deletions (`403 Forbidden`).
- **`AuthControllerTest` (MockMvc)**: End-to-end registration (201), validation errors map (400), login (200), bad credentials (401).
- **`PostControllerTest` (MockMvc)**: Public post retrieval, secured post creation, ownership enforcement (403).
- **`CommentControllerTest` (MockMvc)**: Public comment retrieval, secured comment creation, ownership enforcement (403).

---

## 📬 Postman Collection Guide

A complete Postman collection is located at:
📂 [postman/Blog_Application_API.postman_collection.json](file:///C:/Users/Sushi/OneDrive/Attachments/Desktop/blog%20application/postman/Blog_Application_API.postman_collection.json)

- **Automated Token Capture**: Running `Register` or `Login` automatically saves the JWT token into the collection variable `{{jwt_token}}`.
- All requests automatically use the Bearer token without manual copying.

---

## 📅 25-Day Assignment Plan

- **Day 1–2**: Requirement analysis, layered architecture design, API contract planning.
- **Day 3–5**: Database modeling, MySQL schema (`database/schema.sql`), JPA entities (`User`, `Post`, `Comment`).
- **Day 6–10**: Spring Data repositories, Post and Comment CRUD APIs, pagination, sorting, search.
- **Day 11–13**: Spring Security 6, JWT generation/validation, custom filter, role-based authorization.
- **Day 14–16**: Unit and integration testing with JUnit 5, Mockito, and Spring Boot MockMvc (44 tests).
- **Day 17–18**: Springdoc OpenAPI / Swagger UI integration and Postman collection creation.
- **Day 19–21**: Code refactoring, SOLID principles adherence, zero sensitive data leakage checks.
- **Day 22–25**: Final build verification (`mvn clean test`), README documentation, GitHub commit and push.

---

## 👨‍💻 GitHub Submission Checklist

- [x] No Docker files present (pure local Java 21 / Maven / IDE project).
- [x] Clean `.gitignore` active (strictly ignores `target/`, `.env`, logs, and IDE caches).
- [x] MySQL database name set to `blog_db` (`jdbc:mysql://localhost:3306/blog_db`).
- [x] Database script placed in `database/schema.sql`.
- [x] Standard response format implemented: `{ "success": true, "message": "...", "data": {} }`.
- [x] Search by title, search by content, and filter by author implemented.
- [x] All 44 automated tests passing (`mvn clean test`).
- [x] Executable JAR built (`mvn clean package -DskipTests`).
- [x] Ready to push to GitHub.
