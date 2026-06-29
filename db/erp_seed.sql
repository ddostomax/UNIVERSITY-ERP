USE univ_erp;

-- Link to users in univ_auth via user_id. Adjust IDs to match your actual inserted IDs.

-- Example assumption: admin1=1, inst1=2, stu1=3, stu2=4 in users_auth.

INSERT INTO students (user_id, roll_no, name, program, year) VALUES
  (3, 'CS2025001', 'Student One', 'B.Tech CSE', 2),
  (4, 'CS2025002', 'Student Two', 'B.Tech CSE', 2);

INSERT INTO instructors (user_id, name, department) VALUES
  (2, 'Instructor One', 'Computer Science');

INSERT INTO courses (code, title, credits) VALUES
  ('CS101', 'Introduction to Programming', 4),
  ('CS201', 'Data Structures', 4);

INSERT INTO sections (course_id, instructor_id, day_of_week, start_time, end_time, room, capacity, semester, year)
VALUES
  (1, 2, 'MON', '09:00:00', '10:30:00', 'R101', 40, 'Odd', 2025),
  (2, 2, 'WED', '11:00:00', '12:30:00', 'R102', 35, 'Odd', 2025);

INSERT INTO enrollments (student_id, section_id, status) VALUES
  (3, 1, 'ENROLLED'),
  (4, 1, 'ENROLLED');

INSERT INTO settings (`key`, `value`) VALUES
  ('maintenanceMode', 'false')
ON DUPLICATE KEY UPDATE `value` = VALUES(`value`);


