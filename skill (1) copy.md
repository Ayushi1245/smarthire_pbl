# SmartHire --- Project Builder Skill Guide

## 1. Project purpose

Build and improve **SmartHire**, a Java-based recruitment and
job-matching web application for an MCA Project-Based Learning (PBL)
project. Students can maintain their profiles and skills, browse jobs,
apply, and track applications. Recruiters can manage job postings and
review applicants.

The application should be explainable in a college viva. Prefer
reliable, demonstrable functionality over unnecessary complexity.

## 2. Fixed technology stack

Use the existing stack unless the project owner explicitly approves a
change:

-   Java 17
-   Maven
-   Java Servlets and JSP
-   Jakarta Servlet API compatible with Tomcat 10.1
-   JDBC
-   MySQL
-   Apache Tomcat 10.1
-   HTML, CSS, and JavaScript for the interface
-   JUnit 5 for unit tests where practical
-   jBCrypt for password hashing, if already configured

**Do not introduce** Spring Boot, Spring MVC, Hibernate/JPA, Jetty,
Node.js, a separate frontend framework, or an AI/ML service unless
explicitly requested. The app is deployed to Tomcat; package it as a WAR
using Maven.

## 3. Core rules for working on the project

1.  **Inspect before editing.** Read `pom.xml`, the source tree,
    configuration, JSPs, SQL schema, and tests before proposing or
    changing code.
2.  **Do not assume a feature exists.** Verify the actual source files
    and behavior.
3.  **Preserve working code.** Make the smallest safe changes needed. Do
    not rewrite the entire project to add one feature.
4.  **Avoid duplicate implementations.** Search for existing servlets,
    DAOs, services, filters, models, and database helpers before
    creating new ones.
5.  **Use the actual schema.** Verify table and column names, primary
    keys, foreign keys, and data types before writing SQL or migrations.
6.  **Keep secrets out of source control.** Read database credentials
    from environment variables or existing local configuration. Never
    print, commit, or request the user's password. Do not reset the
    database without explicit permission.
7.  **Match Tomcat 10.1 APIs.** Use `jakarta.servlet.*`, not
    `javax.servlet.*`, for application servlet imports.
8.  **Use JDBC safely.** Prefer try-with-resources and
    `PreparedStatement`. Never concatenate user input into SQL.
9.  **Keep responsibilities separated.** Servlets handle HTTP/request
    flow; services hold business logic; DAOs handle persistence; models
    represent data; JSPs render views.
10. **Explain changes clearly.** Before editing, state the files to
    change and why. After editing, list what changed and provide exact
    build/test commands.
11. **Do not claim tests passed unless they were run and passed.**
    Distinguish code review from verified runtime behavior.
12. **Work in small steps.** Implement one logical feature, test it, and
    then continue.

## 4. Recommended application structure

Follow the project's existing structure where possible. A typical
structure is:

``` text
src/
  main/
    java/com/smarthire/
      filter/       # authentication and authorization filters
      servlet/      # HTTP request handlers
      service/      # matching and application business rules
      dao/          # JDBC database access
      model/        # User, StudentProfile, Job, Skill, Application, Resume
      util/         # database and validation helpers
    resources/
    webapp/
      WEB-INF/
        web.xml     # if used by the project
        views/      # JSP views protected from direct URL access
      css/
      js/
      images/
      index.jsp     # only if compatible with the existing routing
  test/
    java/com/smarthire/
```

Do not move files solely to match this example if the existing project
is already organized differently.

## 5. Core functionality --- required before the main demonstration

### 5.1 Authentication and access control

-   Student and recruiter registration, with role validation.
-   Login with password verification against stored password hashes.
-   Store only necessary user identity and role information in the
    session.
-   Protect student and recruiter routes with a filter or the project's
    existing authorization mechanism.
-   Prevent students from accessing recruiter-only routes and vice
    versa.
-   Provide logout that invalidates the session.
-   Configure a reasonable session timeout.
-   Handle duplicate email addresses consistently, preferably
    case-insensitively.
-   Show safe, understandable errors without exposing SQL, stack traces,
    or secrets.
-   Allow public CSS, JavaScript, images, and login/registration pages
    without authentication.
-   Validate all role and ownership checks on the server, not only in
    the UI.

### 5.2 Student workflow

-   View and update the student's profile.
-   Add and remove skills without creating duplicates.
-   Browse active jobs.
-   View job details and required skills.
-   Apply to a job.
-   Prevent duplicate applications by the same student for the same job.
-   View submitted applications and their current statuses.

### 5.3 Recruiter workflow

-   View and update recruiter/company profile, if supported by the
    existing schema.
-   Create, view, edit, and delete or deactivate job postings.
-   Set job details and required skills.
-   View applicants for jobs owned by that recruiter.
-   View relevant applicant profile and resume information when
    available.
-   Update application status using a controlled set of values, such as
    `APPLIED`, `SHORTLISTED`, `REJECTED`, and `HIRED`.
-   Enforce recruiter ownership on the server for every job and
    applicant operation.

### 5.4 Database integration

-   Use the existing `smarthire` schema and verify it before changing
    it.
-   Keep SQL consistent with the actual schema.
-   Enforce appropriate primary keys, foreign keys, uniqueness, and
    not-null constraints.
-   Use transactions for multi-step operations that must succeed or fail
    together, such as saving a job and its required skills.
-   Handle foreign-key errors and failed transactions gracefully.
-   Keep database access in DAO/helper classes rather than JSPs.
-   Do not drop or recreate tables to solve an application bug.
-   If a schema change is needed, create a documented, non-destructive
    migration or update script and explain how to back up data first.

## 6. Matching feature

Implement a transparent, rule-based skill-matching service. **Do not
describe it as machine learning or AI.**

-   Compare the student's skills with the job's required skills using
    stable skill IDs where possible.

-   Calculate the percentage as:

    `matched required skills / total required skills × 100`

-   Return the matched skills, missing skills, and percentage.

-   Handle empty or missing required skills explicitly. Prefer a message
    such as "No required skills specified" rather than claiming a
    genuine 100% match.

-   Avoid division by zero.

-   Make the calculation independent of servlet and JSP code so it can
    be unit-tested.

-   Explain the algorithm accurately in the report and viva.

-   Use consistent rounding and show the same result in job cards and
    job details.

## 7. Features to reserve for the final viva

Keep the core application functional first. After it is stable, select a
small number of enhancements that can be completed, tested, and
demonstrated live.

Recommended candidates:

1.  **Enhanced skill matching:** percentage, matched skills, missing
    skills, and sorting by match score.
2.  **Application status workflow:** recruiter updates status and the
    student's dashboard reflects it.
3.  **Resume management:** upload, list, view, and delete a resume with
    file validation and access control.
4.  **Job search and filters:** keyword, location, job type, and
    sorting, based only on fields the schema supports.
5.  **Security hardening:** stronger server-side validation,
    authorization edge-case tests, safe error pages, and session
    handling.

Do not leave every student module incomplete. Finish a minimal working
version of each core workflow, then reserve improvements rather than
entire essential features. Only present an enhancement as implemented
after it actually works.

### Resume upload requirements, if implemented

-   Use Servlet multipart support (`@MultipartConfig` or the project's
    equivalent), not Spring's `MultipartFile`.
-   Set file-size limits.
-   Allow only explicitly supported file types, such as PDF, and
    validate both the extension and content as far as practical.
-   Generate server-side file names; do not trust client-provided paths.
-   Prevent path traversal and unauthorized downloads.
-   Do not store uploaded files in a publicly executable directory.
-   Give clear errors for invalid or oversized files.
-   Follow the project's current storage approach and explain its
    trade-offs.

## 8. UI and usability

-   Keep a consistent navigation bar and styling.
-   Provide role-specific dashboards.
-   Include loading, empty, success, and error states where appropriate.
-   Validate forms on the server even when client-side validation
    exists.
-   Keep JSPs focused on presentation; avoid SQL and complex business
    logic in JSP files.
-   Use clear labels and accessible form controls.
-   Make sure navigation links map to real routes and that protected
    JSPs cannot be opened directly if they are intended to be private.
-   Do not add decorative features that do not work.

## 9. Four-person work allocation

Assign ownership by business module, not simply "frontend versus
backend." Confirm the actual file list before assigning exact files.

### Student 1 --- Authentication and security

Own: - Registration and login flow - Session handling and logout -
Authentication/authorization filter - Role and access-control tests

Coordinate changes to shared routing/configuration with the integrator.

### Student 2 --- Student module

Own: - Student profile - Skill management - Job browsing and job
details - Job application and application history - Resume management if
selected

### Student 3 --- Recruiter module

Own: - Recruiter profile/dashboard - Job CRUD and required skills -
Applicant listing - Application status updates - Ownership checks for
recruiter-owned resources

### Student 4 --- Matching, integration, and quality

Own: - Matching service and unit tests - Shared navigation/integration
coordination - Database/schema consistency review - Cross-module test
checklist - Demo data and technical documentation

The fourth student should not become the only person responsible for
integrating everyone else's untested code. Each module owner must test
their own changes and provide a short handover.

### Team coordination rules

-   Agree on model fields, DAO method signatures, route names, and
    status values before parallel work.
-   Avoid having multiple people edit the same file simultaneously.
-   Use Git branches or clearly separated commits where available.
-   Integrate and build regularly rather than waiting until the final
    day.
-   Never commit credentials or real student data.
-   Each member should be able to explain their own module and its
    database interactions in the viva.

## 10. Quality and testing checklist

At minimum, verify:

-   Maven build completes successfully.
-   WAR deploys to Tomcat 10.1.
-   A student can register, log in, browse jobs, apply, and view
    application status.
-   A recruiter can log in, create a job, view applicants, and update
    status.
-   Duplicate email and duplicate application attempts are handled.
-   Incorrect passwords are rejected.
-   Logged-out users cannot access protected routes.
-   Students cannot access recruiter-only functions.
-   Recruiters cannot modify another recruiter's jobs or applicants.
-   Matching calculations work for full, partial, zero, and
    empty-required-skill cases.
-   Invalid form inputs produce useful errors.
-   Database failures do not leave partial records in multi-step
    operations.
-   Uploaded files, if supported, are validated and access-controlled.
-   Unit tests cover matching and other testable business rules.

Use commands appropriate to the actual project directory and POM, for
example:

``` bash
mvn test
mvn clean package
```

Do not delete `target`, database tables, or user data unless necessary
and explicitly explained.

## 11. Final viva preparation

Prepare these deliverables:

-   Problem statement and objectives
-   Functional and non-functional requirements
-   Use-case diagram
-   ER diagram matching the actual database
-   Architecture diagram (Servlet/JSP → service → DAO → MySQL)
-   Database schema/table explanations
-   Screenshots of the main workflows
-   Test cases with expected and actual results
-   Setup instructions in `README.md`
-   Known limitations and future enhancements
-   A short live demonstration using safe sample data

### Suggested 8--10 minute demonstration

1.  Explain the problem and the two roles.
2.  Register or log in as a recruiter and create a job with required
    skills.
3.  Log in as a student, show the profile and skills, and browse jobs.
4.  Demonstrate the skill-match score and missing skills, if
    implemented.
5.  Apply to the job and show the application on the recruiter
    dashboard.
6.  Update the application status and show the student's updated status.
7.  Demonstrate one security boundary, such as blocking access to a
    recruiter route from a student account.
8.  Briefly explain the database schema, architecture, and tests.

Use prepared sample accounts and data, but do not fake a successful
workflow. Keep a backup demo plan in case of network or database issues.

## 12. Viva concepts every member should understand

-   Why Java Servlets and JSP are used
-   What Maven and `pom.xml` do
-   What Tomcat does and why the WAR is deployed there
-   MVC and separation of concerns
-   JDBC, DAOs, `PreparedStatement`, and try-with-resources
-   Primary keys, foreign keys, joins, and transactions
-   Password hashing versus encryption
-   Authentication versus authorization
-   Session management and role-based access
-   The matching formula and its limitations
-   Input validation and error handling
-   Unit testing and test-case design
-   Limitations of the current implementation and realistic future work

## 13. How the coding assistant should respond

For each requested change:

1.  Inspect the relevant source and database schema first.
2.  Summarize the current implementation and the gap.
3.  Identify the exact files that need changes.
4.  Implement the smallest complete solution.
5.  Explain any database migration before asking the user to run it.
6.  Run or provide the most relevant tests and build commands.
7.  Report what is verified, what is not verified, and any manual test
    needed.
8.  Give concise, step-by-step run instructions suitable for macOS.

When asked what remains to be implemented, produce a table with: -
Feature - Current state (verified / missing / needs inspection) -
Required change - Files involved - Priority - Whether it is core or a
reserved viva enhancement

**Important:** This guide is a specification, not evidence that any
feature already exists. Inspect the actual repository before marking
work complete.
