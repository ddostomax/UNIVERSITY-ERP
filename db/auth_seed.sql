USE univ_auth;

-- Password hashes here are placeholders; replace them with real bcrypt hashes later.

INSERT INTO users_auth (username, role, password_hash, status)
VALUES
  ('admin1', 'ADMIN', '$2a$10$sWOpzTaB1ag73b6azVpikenUMrUCHfDe9yB.geJx6/I64WWi1A1xS', 'ACTIVE'),
  ('inst1',  'INSTRUCTOR', '$2a$10$bjgAXJeurzz7pGRxOABsgOLU8PWJDsf0qfIHpaxE8hbihzesDPkeS', 'ACTIVE'),
  ('stu1',   'STUDENT', '$2a$10$/yBzMpYY2ABRKCpsb12GOutwxoC7ce/.4kpfVwDO2zBB50tAhW0QC', 'ACTIVE'),
  ('stu2',   'STUDENT', '$2a$10$5keJuiDkp5lUggPtHF8Lw.YjmiY980Q8H/fCNxlqGM7nLyM5FuZFW', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  role = VALUES(role),
  password_hash = VALUES(password_hash),
  status = VALUES(status);


