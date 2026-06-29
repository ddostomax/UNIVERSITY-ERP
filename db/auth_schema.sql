-- Auth DB schema (MySQL)

CREATE DATABASE IF NOT EXISTS univ_auth
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE univ_auth;

CREATE TABLE IF NOT EXISTS users_auth (
  user_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(50) NOT NULL UNIQUE,
  role          VARCHAR(20) NOT NULL, -- ADMIN / INSTRUCTOR / STUDENT
  password_hash VARCHAR(255) NOT NULL,
  status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  last_login    TIMESTAMP NULL,
  login_attempts INT NOT NULL DEFAULT 0,
  locked_until  TIMESTAMP NULL
);


