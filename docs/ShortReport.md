# Short Report (University ERP Modernization)

> Length guideline: ~5–7 pages equivalent. This markdown captures the requested narrative; copy into a PDF if needed.

---

## 1. Project Overview

- **Goal**: Modernize the legacy Java Swing ERP into a unified dark-mode dashboard with a professional look & feel, responsive layout, and consolidated navigation.
- **Scope**:
  - Re-themed Student, Instructor, and Admin dashboards to a consistent dark palette (FlatLaf Dark + custom styling).
  - Introduced role-based session handling, maintenance banner refresh, and improved account menu actions.
  - Added automated JUnit 5 regression tests for critical security helpers.
- **Tech Stack**: Java 11, Swing, FlatLaf, MySQL 8, Maven, BCrypt, Apache Commons CSV, OpenPDF.

---

## 2. UI Modernization Summary

| Screen | Improvements | Screenshot (capture at submission time) |
| --- | --- | --- |
| Login | Dark modal, clearer error dialogs, updated font | `screenshots/login.png` |
| Student Dashboard | Tabbed layout matches admin/instructor theme, tables styled, transcript CTA | `screenshots/student-dashboard.png` |
| Instructor Dashboard | Grade entry + statistics share dark cards, CTA buttons recolored | `screenshots/instructor-dashboard.png` |
| Admin Dashboard | Users/Courses/Sections/Settings tabs with consistent spacing and banners | `screenshots/admin-dashboard.png` |

> **Note:** Place PNGs under `screenshots/` and update the table above before generating the final PDF.

### Visual Design Principles Applied
1. **Unified palette**: `#121216` background, indigo accent, soft grays for text hierarchy.
2. **Rounded surfaces**: Buttons, cards, sidebar toggles use 12px radius w/ hover/active states.
3. **Typography**: Inter/Segoe UI-like fonts (FlatLaf default) with consistent 13–16 pt sizing.
4. **Spacing**: Panels padded (16 px) to avoid cramped forms; tables use 24 px row height.

---

## 3. Final Grade Weighting Rule

Instructor → Grade Entry tab exposes weight inputs with the **default formula**:

```
Final Score = 0.20 * QUIZ + 0.30 * MIDTERM + 0.50 * END_SEM
```

- Instructor may override weights (must total 100%).  
- `computeFinalGrades` (InstructorApi → service layer) stores the computed value in `grades.final_grade`.
- Student dashboard simply displays the stored final grade string; no client-side recalculation.

---

## 4. Role Enforcement & Maintenance

| Layer | Mechanism |
| --- | --- |
| **Login** | `AuthService` authenticates via `users_auth` table, issues `UserSession`. Session role drives MainFrame content selection. |
| **Admin Actions** | Only Admin dashboard exposes user/course/section management. Instructor/Student dashboards omit those APIs entirely. |
| **Maintenance Mode** | `settings.maintenance_mode` toggled via Admin → Settings. `MaintenanceBanner` polls every 5s; when ON, Student/Instructor actions that mutate data call APIs which reject writes server-side (read-only messaging shown). |
| **Logout / Change Password** | Added top-left **Account** menu for global accessibility. Logout clears `SessionManager`; password dialog enforces current-session checks. |

---

## 5. Database Table Lists

### Auth DB (`univ_auth`)

| Table | Columns (key ones) | Purpose |
| --- | --- | --- |
| `users_auth` | `user_id (PK)`, `username`, `role`, `password_hash`, `status`, `last_login`, `login_attempts`, `locked_until` | Stores credentials + lockout metadata. |

### ERP DB (`univ_erp`)

| Table | Highlights |
| --- | --- |
| `students` | `user_id (FK → users_auth)`, `roll_no`, `program`, `year` |
| `instructors` | `user_id (FK)`, `department` |
| `courses` | `course_id`, `code`, `title`, `credits` |
| `sections` | `section_id`, `course_id`, `instructor_id`, schedule info, `drop_deadline` |
| `enrollments` | `enrollment_id`, `student_id`, `section_id`, `status`, `enrollment_date` |
| `grades` | `grade_id`, `enrollment_id`, `component`, `score`, `final_grade` |
| `settings` | Key-value pairs (currently `maintenance_mode`) |

Refer to `db/erp_schema.sql` for full DDL.

---

## 6. Extras & Enhancements

- **Dark Theme Shell**: Replaced legacy panels with a cohesive design, including maintenance banner restyle and menu actions.
- **Collapsible Sidebar (optional branch)**: Earlier iteration added a right-side nav; final submission reverts to role dashboards with menu bar per user request.
- **Automated Tests**: Added `PasswordHasherTest` and `SessionManagerTest` (JUnit 5).
- **Docs Bundle**: `docs/` now hosts HowToRun, SampleData, ShortReport, TestPlan, TestSummary, and diagrams.

---

## 7. Implementation Notes for Graders

1. **Screenshots**: Capture final UI states (admin/users, instructor/grades, student/catalog) and include them when exporting this markdown to PDF.
2. **Backup/Restore**: Requires `mysqldump` and `mysql` CLI; mention this in the oral demo if those tools aren’t present.
3. **Accessibility**: All interactive widgets have hover states and adequate contrast for dark backgrounds.
4. **Known Issues**: None blocking. Documented in `docs/TestSummary.md` (Maintenance button depends on CLI availability).

---

## 8. Appendix – Submission Checklist Mapping

| Requirement | Location |
| --- | --- |
| Working app & How-to-run | `docs/HowToRun.md` |
| Sample data spec | `docs/SampleData.md` |
| Short report | **This file** |
| Testing pack | `docs/TestPlan.md`, `docs/TestSummary.md` + seed scripts |
| Diagrams | `docs/Diagrams.md` |

Export this file (and any screenshot assets) to PDF for the final bundle. 


