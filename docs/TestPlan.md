# Test Plan (University ERP)

> Covers acceptance tests, additional scenarios, and the “small test dataset” mapping referenced in the submission instructions.

---

## 1. Objectives

1. Verify each role (Admin / Instructor / Student) can execute its critical workflows without regression.
2. Validate the modernized UI (dark theme, spacing, menus) renders consistently across macOS/Windows.
3. Confirm maintenance mode, backup/restore, and security helpers behave correctly.

---

## 2. Test Environment

| Component | Spec |
| --- | --- |
| OS | macOS 14 (Sonoma) and Windows 11 (for cross-check) |
| Java | OpenJDK 11.0.x |
| Maven | 3.9.11 |
| Database | MySQL 8.0.36 local instance |
| Seed Data | As described in `docs/SampleData.md` |
| Tools | IntelliJ IDEA (for debugging), `mysql` CLI, `mysqldump` |

---

## 3. Test Data

Use the provided seed scripts to populate:

- `admin1/password`
- `inst1/password` with sections CS101-A, CS201-A
- `stu1/password`, `stu2/password` with enrollments

Additional entries may be created during test execution; reset the DB via the seed scripts as needed.

---

## 4. Acceptance Tests

### 4.1 Authentication & Sessions

| ID | Description | Steps | Expected |
| --- | --- | --- | --- |
| AUTH-01 | Valid admin login | Launch app → login `admin1/password`. | Admin dashboard opens. |
| AUTH-02 | Invalid password | Enter `admin1/badpass`. | Error dialog, no session set. |
| AUTH-03 | Logout flow | From any dashboard, Account → Logout. | Returns to login screen, session cleared. |
| AUTH-04 | Change password dialog | Account → Change Password, submit mismatched new passwords. | Validation error shown. |

### 4.2 Admin Dashboard

| ID | Description | Steps | Expected |
| --- | --- | --- | --- |
| ADM-01 | Create student | Users tab → enter data → Create User. | Success dialog, fields cleared. |
| ADM-02 | Create course | Courses tab → fill code/title/credits → Create. | Table refresh shows new row. |
| ADM-03 | Create section | Sections tab → select course/instructor, schedule, capacity → Create. | Section table updates. |
| ADM-04 | Assign instructor | Sections tab bottom panel → select section + instructor → Assign. | Instructor column updates. |
| ADM-05 | Maintenance toggle | Settings tab → enable toggle. | Maintenance banner appears on other dashboards. |
| ADM-06 | Backup DB | Settings tab → Backup → choose destination. | Success dialog (if `mysqldump` available). |
| ADM-07 | Restore DB | Settings tab → Restore → confirm + select file. | Success dialog; tables revert (requires CLI tools). |

### 4.3 Instructor Dashboard

| ID | Description | Steps | Expected |
| --- | --- | --- | --- |
| INS-01 | View sections | Login as inst1 → My Sections. | Seed sections listed. |
| INS-02 | Enter grade | Grade Entry tab → select section, choose student row, add QUIZ score. | Success dialog, table refresh includes new score. |
| INS-03 | Compute finals | Enter weights (20/30/50) → Compute. | Final grade column populated. |
| INS-04 | Export grades | Grade Entry → Export to CSV. | File saved to chosen location. |
| INS-05 | View statistics | Statistics tab → select section, Refresh. | Table shows average/min/max per component. |

### 4.4 Student Dashboard

| ID | Description | Steps | Expected |
| --- | --- | --- | --- |
| STU-01 | View catalog | Login as stu1 → Course Catalog. | All sections visible, sorted. |
| STU-02 | Register | Select available section → Register button → confirm. | Success dialog; My Registrations shows the new enrollment. |
| STU-03 | Drop | My Registrations → select row → Drop. | Row removed; Table refresh. |
| STU-04 | Timetable view | Timetable tab → Refresh. | Weekly schedule displays. |
| STU-05 | Transcript export | Transcript tab → Download CSV / PDF. | Files generated successfully. |

### 4.5 Maintenance Mode Validation

| ID | Description | Steps | Expected |
| --- | --- | --- | --- |
| MAINT-01 | Maintenance banner | Toggle ON via Admin. Login as student/instructor. | Yellow banner appears; actions that modify data show warning dialogs (server rejects). |

### 4.6 Dark Theme UI Checks

| ID | Description | Steps | Expected |
| --- | --- | --- | --- |
| UI-01 | Table styling | Inspect any table (courses, grades). | Row height 24 px, dark background, hover highlight. |
| UI-02 | Buttons | Hover primary/secondary buttons. | Accent/hyperlink states appear; focus rings disabled. |
| UI-03 | Menu bar | Account menu accessible from each screen. | Change Password/Logout items active. |

---

## 5. Negative & Edge Tests

| ID | Scenario | Expected |
| --- | --- | --- |
| NEG-01 | Create course with missing code | Error dialog (“Please fill in all required fields”). |
| NEG-02 | Create section with invalid time format | Error dialog referencing HH:mm requirement. |
| NEG-03 | Register for same section twice | API rejects duplicate enrollment; UI shows error message. |
| NEG-04 | Instructor enters non-numeric score | Error dialog (“Invalid score”). |
| NEG-05 | DB connection down | App shows “Login failed due to system error… Failed to initialize pool.” |

---

## 6. Automation Coverage

- `PasswordHasherTest` (JUnit 5): ensures hashing/verification logic is correct and null-safe.
- `SessionManagerTest` (JUnit 5): validates session set/get/clear semantics.

Run via:

```bash
mvn -Dmaven.repo.local=/Users/arham/Desktop/EERP/.m2repo test
```

---

## 7. Exit Criteria

- All acceptance tests pass on macOS.
- Critical paths (auth, CRUD, grading, registration) pass on Windows smoke test.
- Maintenance toggle verified both on/off.
- Automated JUnit tests green.
- No open Severity 1 bugs; known minor issues documented in `docs/TestSummary.md`.

---

## 8. References

- Seed data: `docs/SampleData.md`
- How to Run: `docs/HowToRun.md`
- Test Summary: `docs/TestSummary.md`
- Diagrams/flows: `docs/Diagrams.md`

This document plus the seed scripts constitutes the “Testing pack” deliverable. 


