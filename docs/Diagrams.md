# Diagrams & Sketches

> Use these textual/mermaid diagrams as placeholders if you need to generate PDFs later. Replace with hand-drawn/Visio equivalents if preferred.

---

## 1. Use-Case Lists

### Admin
- Manage users (create student/instructor/admin accounts).
- Manage courses and sections (CRUD + instructor assignment).
- Toggle maintenance mode; trigger DB backup/restore.
- Change own password, log out.

### Instructor
- View assigned sections.
- Enter component scores (QUIZ/MIDTERM/END_SEM).
- Compute final grades; export grades.
- View class statistics.
- Change password / log out.

### Student
- Browse course catalog, register/drop sections.
- View timetable and grades.
- Export transcript (CSV/PDF).
- Change password / log out.

---

## 2. “Things” Sketch (Entities & Relations)

```mermaid
erDiagram
    USERS_AUTH ||--o{ STUDENTS : "role=STUDENT"
    USERS_AUTH ||--o{ INSTRUCTORS : "role=INSTRUCTOR"
    USERS_AUTH ||--o{ SECTIONS : "created by (admin actions)"

    COURSES ||--o{ SECTIONS : contains
    INSTRUCTORS ||--o{ SECTIONS : teaches
    STUDENTS ||--o{ ENROLLMENTS : registers
    SECTIONS ||--o{ ENROLLMENTS : hosts
    ENROLLMENTS ||--o{ GRADES : has

    SETTINGS {
        varchar key PK
        varchar value
    }
```

> The ERD covers both `univ_auth` (users_auth) and `univ_erp` schemas. Settings acts as a global config table (maintenance mode).

---

## 3. Flow Sketches

### 3.1 Student Enrollment Flow

```mermaid
sequenceDiagram
    participant StudentUI
    participant StudentApi
    participant EnrollmentRepo

    StudentUI->>StudentApi: register(sectionId)
    StudentApi->>EnrollmentRepo: insert enrollment
    EnrollmentRepo-->>StudentApi: success / constraint violation
    StudentApi-->>StudentUI: ApiResponse (success/failure message)
    StudentUI->>StudentUI: Refresh catalog + registrations tables
```

### 3.2 Instructor Grade Entry Flow

```mermaid
sequenceDiagram
    participant InstructorUI
    participant InstructorApi
    participant GradeRepo

    InstructorUI->>InstructorApi: enterScore(sectionId, enrollmentId, component, score)
    InstructorApi->>GradeRepo: upsert grade row
    GradeRepo-->>InstructorApi: success
    InstructorApi-->>InstructorUI: ApiResponse
    InstructorUI->>InstructorApi: computeFinalGrades(sectionId, weights)
    InstructorApi->>GradeRepo: recompute + persist final_grade
    GradeRepo-->>InstructorApi: success
    InstructorApi-->>InstructorUI: ApiResponse (dialog)
```

### 3.3 Maintenance Toggle Flow

```mermaid
sequenceDiagram
    participant AdminUI
    participant AdminApi
    participant SettingsRepo
    participant Banner

    AdminUI->>AdminApi: toggleMaintenance(on/off)
    AdminApi->>SettingsRepo: update key=maintenance_mode
    SettingsRepo-->>AdminApi: success
    AdminApi-->>AdminUI: message dialog
    loop Every 5s
        Banner->>SettingsRepo: read maintenance_mode
        SettingsRepo-->>Banner: current value
        Banner->>Banner: show warning if ON
    end
```

---

These diagrams satisfy the “use-case lists,” “things sketch,” and “2–3 flow sketches” requirements. Adjust labels or add screenshots before final submission if needed. 


