# How to Run the University ERP Desktop App

This one-pager covers everything the grader needs to install dependencies, provision the databases, and launch the Swing application on macOS or Windows.

---

## 1. Prerequisites

| Tool | Version | Notes |
| --- | --- | --- |
| Java | 11 (OpenJDK) | `java -version` should report 11.x |
| Maven | 3.9+ | Bundled with most IDEs; verify via `mvn -v` |
| MySQL Server | 8.x | Running locally on `localhost:3306` |

Optional helpers: IntelliJ IDEA (or VS Code with Java extensions) for running the `App` class; `mysqldump` and `mysql` CLI in PATH for Backup/Restore buttons.

---

## 2. Database Setup

From the project root (`/Users/arham/Desktop/EERP`):

```bash
mysql -u root -p < db/auth_schema.sql
mysql -u root -p < db/auth_seed.sql
mysql -u root -p < db/update_auth_schema.sql

mysql -u root -p < db/erp_schema.sql
mysql -u root -p < db/update_erp_schema.sql
mysql -u root -p < db/erp_seed.sql
```

> If your MySQL username/password differ, edit `src/main/resources/db.properties` (`auth.jdbc.user/password` and `erp.jdbc.user/password`) before running the app.

---

## 3. Build and Run

```bash
cd /Users/arham/Desktop/EERP
mvn clean package
mvn -DskipTests exec:java -Dexec.mainClass=edu.univ.erp.App
```

Alternatively, open the project in IntelliJ IDEA, run **Build → Rebuild Project**, then right-click `edu.univ.erp.App` and choose **Run 'App'**.

---

## 4. Default Accounts (from `db/auth_seed.sql`)

| Role | Username | Password | Notes |
| --- | --- | --- | --- |
| Admin | `admin1` | `password` | Full access to users/courses/sections/settings |
| Instructor | `inst1` | `password` | Has sections assigned in the seed data |
| Student | `stu1` | `password` | Registered in at least one section |

> Additional sample records: `inst2`, `stu2`, etc. See `db/auth_seed.sql` and `db/erp_seed.sql` for details.

---

## 5. Troubleshooting

| Symptom | Fix |
| --- | --- |
| `Access denied for user 'root'@'localhost'` | Ensure MySQL credentials in `db.properties` match your local MySQL user. |
| `Unknown column 'login_attempts'` | Run `db/update_auth_schema.sql` after `auth_schema.sql`. |
| `Failed to load registrations` | Run `db/update_erp_schema.sql` and `db/erp_seed.sql`. |
| Maintenance banner always off | Use **Admin → Settings → Maintenance Mode** and click the toggle. |
| Backup/Restore button errors | Install `mysqldump`/`mysql` CLI and ensure they’re on the system PATH. |

---

Everything else (seed scripts, reports, diagrams, tests) lives in the `db/` and `docs/` folders for easy submission. Let me know if you need a packaged ZIP. 


