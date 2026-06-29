# Sample Data Overview

This file summarizes the minimum dataset the graders expect (per submission instructions) and maps it to the actual rows inserted by the provided seed scripts.

---

## 1. Required Actors

| Requirement | Provided By | Details |
| --- | --- | --- |
| 1 Admin | `admin1` | Username/password: `admin1` / `password`. Created in `db/auth_seed.sql`. |
| 1 Instructor with sections | `inst1` | Assigned to `CS101-A` and `CS201-A` (see `db/erp_seed.sql`). |
| 2 Students with enrollments | `stu1`, `stu2` | Each registered for at least one section via `db/erp_seed.sql`. |

Extra sample users also exist (`inst2`, `stu3`, etc.) for flexibility during demos.

---

## 2. Courses and Sections

Seed data creates three baseline courses and multiple sections. Highlights:

| Course Code | Title | Sections | Instructor | Notes |
| --- | --- | --- | --- | --- |
| CS101 | Intro to CS | `CS101-A` | `inst1` | Scheduled Mon/Wed 09:00–10:15, capacity 40. |
| CS201 | Data Structures | `CS201-A` | `inst1` | Tue/Thu 11:00–12:15, capacity 35. |
| MATH150 | Calculus I | `MATH150-A` | `inst2` | Mon/Wed/Fri 08:00–08:50, capacity 45. |

You can view or modify these via **Admin → Courses/Sections**.

---

## 3. Enrollments & Grades

`db/erp_seed.sql` enrolls:

- `stu1` in `CS101-A` and `CS201-A`
- `stu2` in `CS101-A`

Grades table is populated with placeholder QUIZ/MIDTERM entries so Instructor → Grades & Statistics tabs have data to display immediately.

---

## 4. Maintenance & Settings Seed

`settings` table includes:

| Key | Value | Purpose |
| --- | --- | --- |
| `maintenance_mode` | `OFF` | Controls Maintenance banner (Admin → Settings toggles this). |

---

## 5. Regenerating the Dataset

To reset to the exact state described above:

```bash
mysql -u root -p < db/auth_schema.sql
mysql -u root -p < db/auth_seed.sql
mysql -u root -p < db/update_auth_schema.sql

mysql -u root -p < db/erp_schema.sql
mysql -u root -p < db/update_erp_schema.sql
mysql -u root -p < db/erp_seed.sql
```

> WARNING: Running these will drop/recreate the databases, erasing any local changes.

---

Use this document as the “small test dataset” reference in the Testing Pack. If you add custom demo data, append it here before submission. 


