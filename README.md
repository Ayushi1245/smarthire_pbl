# SmartHire

A beginner-friendly Java web application for a recruitment and job portal built with:

- Java 17+
- Maven
- Servlet + JSP
- MySQL + JDBC
- Apache Tomcat 10.1+

## Project Status

This is the initial project scaffold and database schema for the SmartHire platform.

## Environment notes

This workspace has Maven available, and the database schema is prepared for MySQL.
Before deploying the app, confirm Java and Tomcat are properly installed and available on your machine.

## Database setup

1. Start MySQL.
2. Create the database and tables:

```sql
mysql -u root -p < database/schema.sql
```

3. If needed, provide runtime environment variables for the app:

```bash
export SMARTHIRE_DB_URL=jdbc:mysql://localhost:3306/smarthire
export SMARTHIRE_DB_USER=root
export SMARTHIRE_DB_PASSWORD=your_password
```

## Build and test

```bash
export JAVA_HOME=/usr/local/Cellar/openjdk/25.0.1/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
mvn package
```

Then deploy the generated `target/SmartHire.war` to Tomcat.

## Next steps

- Add registration and login.
- Add student and recruiter profiles.
- Add jobs and applications.
- Add the rule-based match engine.
- Add resume upload and authorization.
# smarthire_pbl
