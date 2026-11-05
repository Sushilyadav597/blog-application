-- ==============================================================================
-- RESTful Blog Application API - Seed Data
-- Database: blog_db
-- ==============================================================================

USE blog_db;

-- Initial users with BCrypt-hashed password for 'Password@123'
INSERT INTO users (id, username, email, password, role, created_at)
VALUES 
(1, 'sushil_admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
(2, 'john_doe', 'john@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ROLE_USER', CURRENT_TIMESTAMP),
(3, 'jane_smith', 'jane@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ROLE_USER', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO posts (id, title, content, author_id, created_at, updated_at)
VALUES 
(1, 'Getting Started with Spring Boot 3 and Java 21', 'Spring Boot 3 introduces support for Java 21, virtual threads, native compilation, and enhanced observability. In this blog post, we explore the essential features and modern best practices for building scalable REST APIs.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Building Secure REST APIs with Spring Security 6 and JWT', 'Stateless authentication using JSON Web Tokens (JWT) allows modern web applications to scale horizontally without maintaining session state on the server. Here is how you can implement modern SecurityFilterChain configurations.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE title = VALUES(title);

INSERT INTO comments (id, content, post_id, author_id, created_at, updated_at)
VALUES 
(1, 'Excellent introduction to Spring Boot 3 and Java 21! Looking forward to part 2.', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Clear explanation of JWT authentication filters and stateless sessions.', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE content = VALUES(content);
