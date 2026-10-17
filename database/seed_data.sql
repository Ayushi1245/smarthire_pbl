-- Seed data for SmartHire
-- Password for all sample users is: password123
-- Hash: $2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC

USE smarthire;

-- Insert additional skills if not already existing
INSERT IGNORE INTO skills (name) VALUES 
('Spring Boot'),
('Docker'),
('Linux'),
('React'),
('Node.js'),
('Python'),
('Machine Learning'),
('REST APIs'),
('Git');

-- 1. RECRUITERS & COMPANIES
-- Company 1: Google India
INSERT INTO users (full_name, email, password_hash, role) 
VALUES ('Ananya Sengupta (Google)', 'recruiter.google@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'RECRUITER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

SET @recruiter_google_id = (SELECT id FROM users WHERE email = 'recruiter.google@example.com');

INSERT INTO recruiter_profiles (user_id, company_name, company_description)
VALUES (@recruiter_google_id, 'Google India', 'Google’s mission is to organize the world’s information and make it universally accessible and useful.')
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name), company_description=VALUES(company_description);

-- Company 2: Microsoft India
INSERT INTO users (full_name, email, password_hash, role) 
VALUES ('Vikram Malhotra (Microsoft)', 'recruiter.msft@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'RECRUITER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

SET @recruiter_msft_id = (SELECT id FROM users WHERE email = 'recruiter.msft@example.com');

INSERT INTO recruiter_profiles (user_id, company_name, company_description)
VALUES (@recruiter_msft_id, 'Microsoft India', 'Empowering every person and every organization on the planet to achieve more through cloud and AI innovations.')
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name), company_description=VALUES(company_description);

-- Company 3: Amazon India
INSERT INTO users (full_name, email, password_hash, role) 
VALUES ('Kavita Krishnan (Amazon)', 'recruiter.amazon@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'RECRUITER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

SET @recruiter_amazon_id = (SELECT id FROM users WHERE email = 'recruiter.amazon@example.com');

INSERT INTO recruiter_profiles (user_id, company_name, company_description)
VALUES (@recruiter_amazon_id, 'Amazon AWS', 'Earth’s most customer-centric company, building high-scale distributed cloud infrastructure.')
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name), company_description=VALUES(company_description);


-- 2. JOBS POSTED BY COMPANIES
-- Job 1: Google - Software Development Engineer (SDE-1)
INSERT INTO jobs (recruiter_id, title, description, location, employment_type, min_cgpa, is_active)
VALUES (
    @recruiter_google_id,
    'Software Development Engineer (SDE-1)',
    'We are looking for enthusiastic Software Engineers to join our Core Services team. You will design, develop, test, and deploy resilient, large-scale distributed systems.',
    'Bengaluru, Karnataka (Hybrid)',
    'Full-time',
    7.50,
    1
);
SET @job1_id = LAST_INSERT_ID();

-- Skills for Job 1: Java (mandatory), SQL (mandatory), MySQL, Spring Boot
INSERT IGNORE INTO job_skills (job_id, skill_id, is_mandatory, weight)
SELECT @job1_id, id, 1, 3 FROM skills WHERE name IN ('Java', 'SQL')
UNION
SELECT @job1_id, id, 0, 2 FROM skills WHERE name IN ('MySQL', 'Spring Boot');

-- Job 2: Google - Cloud Systems Engineer
INSERT INTO jobs (recruiter_id, title, description, location, employment_type, min_cgpa, is_active)
VALUES (
    @recruiter_google_id,
    'Cloud Systems Associate',
    'Join Google Cloud to help scale backend infrastructure, streamline CI/CD pipelines, and maintain high-reliability cloud operations.',
    'Hyderabad, Telangana',
    'Full-time',
    7.00,
    1
);
SET @job2_id = LAST_INSERT_ID();

INSERT IGNORE INTO job_skills (job_id, skill_id, is_mandatory, weight)
SELECT @job2_id, id, 1, 3 FROM skills WHERE name IN ('Linux', 'Docker')
UNION
SELECT @job2_id, id, 0, 2 FROM skills WHERE name IN ('Java', 'SQL');

-- Job 3: Microsoft - Full Stack Web Engineer
INSERT INTO jobs (recruiter_id, title, description, location, employment_type, min_cgpa, is_active)
VALUES (
    @recruiter_msft_id,
    'Full Stack Web Developer',
    'Build intuitive, high-performance web applications using modern front-end tech and robust Java/Node backend microservices.',
    'Hyderabad, Telangana (Hybrid)',
    'Full-time',
    7.50,
    1
);
SET @job3_id = LAST_INSERT_ID();

INSERT IGNORE INTO job_skills (job_id, skill_id, is_mandatory, weight)
SELECT @job3_id, id, 1, 3 FROM skills WHERE name IN ('JavaScript', 'HTML', 'CSS')
UNION
SELECT @job3_id, id, 0, 2 FROM skills WHERE name IN ('React', 'Java', 'SQL');

-- Job 4: Amazon - Backend Trainee Engineer
INSERT INTO jobs (recruiter_id, title, description, location, employment_type, min_cgpa, is_active)
VALUES (
    @recruiter_amazon_id,
    'Backend Trainee Engineer',
    'Great entry-level opportunity for aspiring developers. Work with senior engineers on real-time transaction processing, API development, and data modeling.',
    'Pune, Maharashtra',
    'Internship',
    6.50,
    1
);
SET @job4_id = LAST_INSERT_ID();

INSERT IGNORE INTO job_skills (job_id, skill_id, is_mandatory, weight)
SELECT @job4_id, id, 1, 3 FROM skills WHERE name IN ('Java', 'SQL')
UNION
SELECT @job4_id, id, 0, 2 FROM skills WHERE name IN ('MySQL', 'JDBC');


-- 3. STUDENTS
-- Student 1: Rahul Sharma
INSERT INTO users (full_name, email, password_hash, role)
VALUES ('Rahul Sharma', 'rahul.sharma@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'STUDENT')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);
SET @student1_id = (SELECT id FROM users WHERE email = 'rahul.sharma@example.com');

INSERT INTO student_profiles (user_id, education, graduation_year, cgpa, summary, experience)
VALUES (
    @student1_id,
    'B.Tech in Computer Science and Engineering',
    2026,
    8.75,
    'Enthusiastic backend software engineer with solid foundations in Java, algorithms, and relational database systems.',
    'Interned at a fintech startup building RESTful services; built SmartHire university recruitment application.'
)
ON DUPLICATE KEY UPDATE education=VALUES(education), graduation_year=VALUES(graduation_year), cgpa=VALUES(cgpa), summary=VALUES(summary), experience=VALUES(experience);

INSERT IGNORE INTO student_skills (student_id, skill_id)
SELECT @student1_id, id FROM skills WHERE name IN ('Java', 'SQL', 'MySQL', 'JDBC', 'Spring Boot', 'Git');

-- Student 2: Priya Patel
INSERT INTO users (full_name, email, password_hash, role)
VALUES ('Priya Patel', 'priya.patel@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'STUDENT')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);
SET @student2_id = (SELECT id FROM users WHERE email = 'priya.patel@example.com');

INSERT INTO student_profiles (user_id, education, graduation_year, cgpa, summary, experience)
VALUES (
    @student2_id,
    'B.Tech in Information Technology',
    2026,
    9.10,
    'Creative full stack developer skilled in modern JavaScript, accessible web UI design, and responsive design systems.',
    'Frontend developer for university web portal; contributed to open-source UI libraries with React and CSS animations.'
)
ON DUPLICATE KEY UPDATE education=VALUES(education), graduation_year=VALUES(graduation_year), cgpa=VALUES(cgpa), summary=VALUES(summary), experience=VALUES(experience);

INSERT IGNORE INTO student_skills (student_id, skill_id)
SELECT @student2_id, id FROM skills WHERE name IN ('JavaScript', 'HTML', 'CSS', 'React', 'Java', 'SQL');

-- Student 3: Rohan Verma
INSERT INTO users (full_name, email, password_hash, role)
VALUES ('Rohan Verma', 'rohan.verma@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'STUDENT')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);
SET @student3_id = (SELECT id FROM users WHERE email = 'rohan.verma@example.com');

INSERT INTO student_profiles (user_id, education, graduation_year, cgpa, summary, experience)
VALUES (
    @student3_id,
    'MCA (Master of Computer Applications)',
    2027,
    7.90,
    'Aspiring Data & AI Engineer focusing on data pipelines, predictive modeling, and SQL database analytics.',
    'Developed a student placement predictor using Python and scikit-learn; hands-on experience with RAG and LLMs.'
)
ON DUPLICATE KEY UPDATE education=VALUES(education), graduation_year=VALUES(graduation_year), cgpa=VALUES(cgpa), summary=VALUES(summary), experience=VALUES(experience);

INSERT IGNORE INTO student_skills (student_id, skill_id)
SELECT @student3_id, id FROM skills WHERE name IN ('Python', 'SQL', 'MySQL', 'AI/Ml', 'Rag', 'langchain');

-- Student 4: Sneha Nair
INSERT INTO users (full_name, email, password_hash, role)
VALUES ('Sneha Nair', 'sneha.nair@example.com', '$2a$10$WlitSwg8En2dGmE.cFOouuKJ4Qa4GIeCoAElRIVzY4AFuNxE0PYpC', 'STUDENT')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);
SET @student4_id = (SELECT id FROM users WHERE email = 'sneha.nair@example.com');

INSERT INTO student_profiles (user_id, education, graduation_year, cgpa, summary, experience)
VALUES (
    @student4_id,
    'B.E. in Electronics & Communication',
    2026,
    8.30,
    'Disciplined engineer transitioning to software systems. Strong command over core Java OOP, SQL, and backend patterns.',
    'Led academic robotics project with embedded C and Java telemetry dashboards.'
)
ON DUPLICATE KEY UPDATE education=VALUES(education), graduation_year=VALUES(graduation_year), cgpa=VALUES(cgpa), summary=VALUES(summary), experience=VALUES(experience);

INSERT IGNORE INTO student_skills (student_id, skill_id)
SELECT @student4_id, id FROM skills WHERE name IN ('Java', 'SQL', 'MySQL', 'JDBC', 'CSS', 'Git');


-- 4. APPLICATIONS
-- Rahul applied to Google SDE-1 (high match: 92%)
INSERT IGNORE INTO applications (student_id, job_id, status, match_score)
VALUES (@student1_id, @job1_id, 'SHORTLISTED', 92.50);

-- Priya applied to Microsoft Full Stack (high match: 95%)
INSERT IGNORE INTO applications (student_id, job_id, status, match_score)
VALUES (@student2_id, @job3_id, 'UNDER_REVIEW', 95.00);

-- Sneha applied to Amazon Backend Trainee (high match: 88%)
INSERT IGNORE INTO applications (student_id, job_id, status, match_score)
VALUES (@student4_id, @job4_id, 'APPLIED', 88.00);

-- Rahul also applied to Amazon Backend Trainee
INSERT IGNORE INTO applications (student_id, job_id, status, match_score)
VALUES (@student1_id, @job4_id, 'UNDER_REVIEW', 90.00);
