---
name: recruitment-platform
description: Guidance for designing and building a rule-based (non-AI) recruitment platform in Java and MySQL, where students manage profiles and apply for jobs and recruiters post vacancies, review applicants and shortlist candidates. Use this skill whenever the user mentions the recruitment platform, job portal, placement portal, student/recruiter modules, job applications, applicant shortlisting, skill-match percentage, or the related database tables (users, student_profiles, skills, jobs, job_skills, applications, resumes), even if they do not say "recruitment platform" explicitly.
---

# Recruitment Platform (Rule-Based, No AI)

## Objective

A web platform where **students** manage profiles and apply for jobs, and **recruiters** post vacancies, review applications, and shortlist eligible candidates. Matching is purely rule-based: no machine learning, LLMs, embeddings, or external AI APIs.

## Modules

### Student
- Register, log in
- Create and update profile (education, skills, experience)
- Upload and download resume (PDF)
- Search and filter job listings
- Apply for jobs and track application status

### Recruiter
- Register, log in
- Create, edit, delete job postings
- Set required skills and eligibility criteria (min CGPA, education, experience)
- View applicants and their resumes
- Shortlist or reject candidates

### Admin (optional)
- Manage users and recruiters
- Monitor job postings
- Remove inappropriate listings

## Matching Logic

```
Match Score = (Matched Required Skills / Total Required Skills) x 100
```

Example: job requires Java, SQL, OOP, DBMS.

| Candidate | Matching skills | Score |
|-----------|-----------------|-------|
| A | Java, SQL, OOP, DBMS | 100% |
| B | Java, SQL | 50% |
| C | Java, OOP, DBMS | 75% |

Rules to follow:
1. **Normalize skills** (trim, lowercase) before comparing. Prefer picking from the `skills` master table over free text.
2. **Hard filters vs. soft score.** Min CGPA, education, and experience are pass/fail eligibility filters. Skills produce the score. An applicant who fails a hard filter is flagged as ineligible, never ranked as a top match.
3. **Sort applicants** by score descending, with ties broken by CGPA.
4. **Optional weighting:** add `is_mandatory` or `weight` to `job_skills`; score = matched weight / total weight.
5. Guard against a job with zero required skills (avoid divide-by-zero).
6. Keep the logic in one pure class, `MatchService.calculate(job, student)`, with no database or web dependencies, so it is easy to unit test.

## Technology Stack

- **Backend:** Java
- **Frontend:** HTML, CSS, JavaScript (with form validation)
- **Database:** MySQL
- **Web layer, choose one and confirm with the user before generating code:**
  - Java Servlets + JSP + JDBC: traditional, teaches fundamentals, more boilerplate.
  - Spring Boot + Spring Security + JPA: less boilerplate, industry standard, steeper learning curve.

## Database Tables

| Table | Purpose |
|-------|---------|
| `users` | Login credentials (hashed password) and role (STUDENT, RECRUITER, ADMIN) |
| `student_profiles` | Student details, CGPA, summary |
| `education` | Multiple education entries per student |
| `experience` | Multiple experience entries per student |
| `companies` | Recruiter/company details |
| `skills` | Master list of skills |
| `student_skills` | Student-to-skill mapping |
| `jobs` | Job details and eligibility (min CGPA, education, experience) |
| `job_skills` | Required skills per job (optional weight/mandatory flag) |
| `applications` | Student applications and status |
| `resumes` | Resume file references (path, original name, upload time) |

Constraints to include:
- Unique `(student_id, job_id)` on `applications` to block duplicate applications.
- Unique `(student_id, skill_id)` and `(job_id, skill_id)` on the mapping tables.
- Foreign keys with sensible `ON DELETE` behavior (deleting a job should remove its `job_skills`).

## Application Status Lifecycle

`APPLIED -> UNDER_REVIEW -> SHORTLISTED` or `REJECTED`

Only recruiters who own the job may change the status. Students can view but not edit it.

## Security and Quality Checklist

- Hash passwords with BCrypt; never store plain text.
- Use `PreparedStatement` (or JPA parameters) for every query to prevent SQL injection.
- Enforce role checks on every protected URL (a student must not reach recruiter pages, and a recruiter must not edit another recruiter's job).
- Resume upload: accept PDF only, limit file size, rename files to unique names, store on disk and keep only the path in the database.
- Validate input on both client (JavaScript) and server.
- Use sessions or tokens correctly; invalidate on logout.

## Suggested Work Split (4 people)

1. Auth, roles, and admin module
2. Student module (profile, education, skills, resume upload)
3. Recruiter module (job CRUD, applicant review screen)
4. Job search/filter, application flow, and the matching engine

## Workflow When Using This Skill

1. Confirm the web-layer choice (Servlets/JSP or Spring Boot) if not already decided.
2. Start with the schema, then the `MatchService` with unit tests, then modules in the order: auth, job CRUD, profile, apply, applicant review.
3. Seed the database with sample students, jobs, and skills so matching can be demoed immediately.
4. When generating code, keep the matching logic free of web and database dependencies.
