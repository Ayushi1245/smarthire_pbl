---
name: smarthire
description: Context, architecture, and coding conventions for SmartHire, a Java 17 / Jakarta Servlet + JSP / MySQL campus recruitment web app (students, recruiters, skill-matching engine). Use this skill whenever the user mentions SmartHire, their PBL project, the skill-matching or MatchService logic, resume upload or resume skill extraction, recruiter or student dashboards, or asks to add, fix, debug, test, or explain any feature in this codebase, even if they don't name the project explicitly.
---

# SmartHire

SmartHire is a university campus recruitment web app. **Students** browse jobs and apply; **recruiters** post jobs and review applicants. Its signature feature is a skill-matching engine that scores a student's skills against a job's required skills.

## Tech stack

- Java 17, Jakarta Servlet 6.0 + JSP, Maven (WAR), Tomcat 10.1+
- MySQL 8.4 with raw JDBC (`PreparedStatement` only, no ORM)
- BCrypt (jBCrypt 0.4) for passwords
- JUnit Jupiter 5.10 for tests
- Plain CSS design system in `css/style.css` (CSS variables)

## Project layout

```
src/main/java/com/smarthire/
  model/     POJOs: User, StudentProfile, RecruiterProfile, Job, Application, Skill, Resume
  dao/       One DAO per entity (JDBC, PreparedStatement)
  service/   MatchService (skill matching engine)
  servlet/   HTTP controllers, routed with @WebServlet annotations
  filter/    AuthFilter (@WebFilter("/*"), auth + role checks)
  util/      DBConnection, DatabaseUtil
src/main/webapp/WEB-INF/jsp/   login, register, common/, student/, recruiter/
database/    schema.sql (8 tables), seed_data.sql
```

Tables: `users`, `student_profiles`, `recruiter_profiles`, `skills`, `student_skills`, `jobs`, `job_skills` (has `is_mandatory`, `weight`), `applications` (has `match_score`, status), `resumes`.

## Matching engine (`MatchService`)

```
score = (matched_skills / total_job_skills) x 100
```

- Lowercase and trim all skills, de-duplicate both sets.
- Job with no required skills gives 100; student with no skills gives 0.
- Returns a `MatchResult`: score (0-100, 2 decimals), matched set, missing set.
- Known gap: `job_skills.weight` and `is_mandatory` exist in the schema but are not used in the formula yet.

## Conventions to follow when changing code

1. **Routing**: use `@WebServlet` annotations, not `web.xml` routes.
2. **Database**: every query uses `PreparedStatement` with parameters. Never concatenate user input into SQL. Use explicit transactions with rollback when several tables change together (see `SkillDAO.updateStudentSkills`).
3. **Layering**: servlets handle HTTP and call DAOs or services. Put business logic in `service/`, SQL in `dao/`, markup in JSP.
4. **JSPs** live under `WEB-INF/jsp/` and are reached only through servlets. Reuse `common/header.jsp` and `common/footer.jsp`. `job_form.jsp` is shared by create and edit.
5. **Auth**: `/student/*` requires role `STUDENT`, `/recruiter/*` requires `RECRUITER`. New protected routes must fit these prefixes so `AuthFilter` covers them. Check ownership (for example `recruiter_id` matches the session user) on edit and review actions.
6. **Skills**: compare case-insensitively (`LOWER()`), and reuse `insertIfNotExists()` for custom skills.
7. **Resumes**: only PDF/.doc/.docx, max 5 MB, UUID stored names. Students access only their own; recruiters can download any applicant's.
8. **Configuration**: DB and upload settings come from environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `SMARTHIRE_UPLOAD_DIR`). Never hardcode credentials in source or in this file.
9. **Tests**: add JUnit tests next to existing ones (`MatchServiceTest`, `DBTest`, `SecurityTest`) for any new logic.

## Planned feature: resume storage and skill extraction

The user wants to store resumes in the database and extract skills from them for matching. Preferred approach:

1. Store file bytes in a `LONGBLOB` column (stream with `setBinaryStream` / `getBinaryStream`; check MySQL `max_allowed_packet`), plus the extracted text.
2. Extract text with Apache PDFBox (PDF) and Apache POI (.docx), or Apache Tika.
3. Match the text against the `skills` table using word-boundary matching so "Java" does not match "JavaScript". Handle short names (C, R) carefully.
4. Support aliases (js, springboot, postgres, ML) via a `skill_aliases` table.
5. Show detected skills to the student as suggestions and save only after confirmation.

## Working style for this project

- Explain design choices briefly; this is a student group project and the code should stay readable and explainable in a review.
- Prefer small, testable changes that fit the existing structure over introducing frameworks.
- Flag security issues (CSRF, file validation, credentials) when they come up.