# Test Summary (One Page)

Date: 27 Nov 2025  
Build: `mvn clean package` @ commit/latest working tree  
Environment: macOS 14, JDK 11, MySQL 8.0.36

---

## 1. Execution Results

| Suite | Tests | Pass | Fail | Notes |
| --- | --- | --- | --- | --- |
| Manual Acceptance (AUTH, ADM, INS, STU, MAINT, UI) | 24 | 24 | 0 | See `docs/TestPlan.md` for details. |
| Negative/Edge | 5 | 5 | 0 | Validation dialogs behaved correctly. |
| Automated (JUnit 5) | 5 | 5 | 0 | `PasswordHasherTest`, `SessionManagerTest`. |

Command used for automation:

```bash
mvn -Dmaven.repo.local=/Users/arham/Desktop/EERP/.m2repo test
```

---

## 2. Known Issues / Risks

| ID | Description | Impact | Mitigation |
| --- | --- | --- | --- |
| KI-01 | Database Backup/Restore buttons require `mysqldump`/`mysql` in PATH. On systems without those binaries, dialogs show an error. | Medium (feature unavailable unless CLI installed). | Documented in HowToRun; instruct graders to skip or install MySQL CLI tools. |
| KI-02 | Maintenance Mode enforces read-only on the server. UI messaging assumes API will reject writes. | Low. | Verified via Admin toggle, but no offline mode. |
| KI-03 | Registering/dropping sections relies on server validation for deadlines/capacity. Client does not pre-validate. | Low. | API returns informative message; user sees dialog. |

No blocking defects remain.

---

## 3. Regression Evidence

- Admin: Created & deleted throwaway course/section → tables refreshed; no orphan data.
- Instructor: Entered new quiz scores → final grade recomputed as expected.
- Student: Registered & dropped sections → enrollment table updated, timetable refreshed.
- Maintenance toggle: ON state prevented Instructor grade entry (error message) and displayed banner; OFF restored normal behavior.
- Dark theme verified on both macOS (Retina) and Windows (via screenshot review).

---

## 4. Sign-off

| Role | Name | Status |
| --- | --- | --- |
| Developer | Goyam Jain (2024224) | ✅ |
| QA / Self-check | Arham Bothra (2024101) | ✅ |

With the above, the application is ready for submission alongside the required documents and diagrams. 


