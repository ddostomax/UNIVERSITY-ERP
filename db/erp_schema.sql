-- ERP DB schema (MySQL)

CREATE DATABASE IF NOT EXISTS univ_erp
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE univ_erp;

CREATE TABLE IF NOT EXISTS students (
  user_id  BIGINT PRIMARY KEY,
  roll_no  VARCHAR(20) NOT NULL UNIQUE,
  name     VARCHAR(100) NOT NULL,
  program  VARCHAR(50),
  year     INT,
  CONSTRAINT fk_students_user
    FOREIGN KEY (user_id) REFERENCES univ_auth.users_auth(user_id)
);

CREATE TABLE IF NOT EXISTS instructors (
  user_id    BIGINT PRIMARY KEY,
  name       VARCHAR(100) NOT NULL,
  department VARCHAR(100),
  CONSTRAINT fk_instructors_user
    FOREIGN KEY (user_id) REFERENCES univ_auth.users_auth(user_id)
);

CREATE TABLE IF NOT EXISTS courses (
  course_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code      VARCHAR(20) NOT NULL UNIQUE,
  title     VARCHAR(100) NOT NULL,
  credits   INT NOT NULL
);

CREATE TABLE IF NOT EXISTS sections (
  section_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id     BIGINT NOT NULL,
  instructor_id BIGINT,
  day_of_week   VARCHAR(10),
  start_time    TIME,
  end_time      TIME,
  room          VARCHAR(50),
  capacity      INT NOT NULL,
  semester      VARCHAR(10),
  year          INT,
  drop_deadline DATE,
  CONSTRAINT fk_sections_course
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
  CONSTRAINT fk_sections_instructor
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

CREATE TABLE IF NOT EXISTS enrollments (
  enrollment_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id     BIGINT NOT NULL,
  section_id     BIGINT NOT NULL,
  status         VARCHAR(20) NOT NULL,
  enrollment_date DATE DEFAULT (CURRENT_DATE),
  CONSTRAINT uq_enrollment UNIQUE (student_id, section_id),
  CONSTRAINT fk_enroll_student
    FOREIGN KEY (student_id) REFERENCES students(user_id),
  CONSTRAINT fk_enroll_section
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);

CREATE TABLE IF NOT EXISTS grades (
  grade_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  enrollment_id BIGINT NOT NULL,
  component     VARCHAR(50) NOT NULL, -- QUIZ / MIDTERM / END_SEM
  score         DECIMAL(5,2) NOT NULL,
  final_grade   VARCHAR(5),
  CONSTRAINT fk_grades_enrollment
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

CREATE TABLE IF NOT EXISTS settings (
  `key`   VARCHAR(50) PRIMARY KEY,
  `value` VARCHAR(255) NOT NULL
);


