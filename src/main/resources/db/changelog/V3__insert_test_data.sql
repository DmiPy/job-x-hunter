-- =========================================================
-- TEST USERS
-- =========================================================

INSERT INTO users (
    user_id,
    email,
    user_role
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'dmitriy@example.com',
        'USER'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'anna@example.com',
        'USER'
    );


-- =========================================================
-- TEST USER PROFILES
-- =========================================================

INSERT INTO user_profile (
    user_id,
    firstname,
    surname,
    expected_salary,
    employment_type,
    workplace_type
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'Dmitriy',
        'Test',
        60000.00,
        'FULL_TIME',
        'HYBRID'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'Anna',
        'Test',
        50000.00,
        'FULL_TIME',
        'REMOTE'
    );


-- =========================================================
-- TEST USER PROFILE SKILLS
-- =========================================================
-- Skills already exist from V2:
-- Java
-- Spring Boot
-- PostgreSQL
-- Docker
-- Angular

-- Dmitriy:
-- Java           90
-- Spring Boot    90
-- PostgreSQL     80
-- Docker         70
-- Angular        60

INSERT INTO user_profile_skill (
    user_profile_id,
    skill_id,
    proficiency
)
SELECT
    up.user_profile_id,
    s.skill_id,
    skills.proficiency
FROM user_profile up
         JOIN (
    VALUES
        ('Java', 90),
        ('Spring Boot', 90),
        ('PostgreSQL', 80),
        ('Docker', 70),
        ('Angular', 60)
) AS skills(skill_name, proficiency)
              ON TRUE
         JOIN skill s
              ON s.skill_name = skills.skill_name
WHERE up.user_id = '11111111-1111-1111-1111-111111111111';


-- Anna:
-- Java           70
-- Angular        90
-- PostgreSQL     60

INSERT INTO user_profile_skill (
    user_profile_id,
    skill_id,
    proficiency
)
SELECT
    up.user_profile_id,
    s.skill_id,
    skills.proficiency
FROM user_profile up
         JOIN (
    VALUES
        ('Java', 70),
        ('Angular', 90),
        ('PostgreSQL', 60)
) AS skills(skill_name, proficiency)
              ON TRUE
         JOIN skill s
              ON s.skill_name = skills.skill_name
WHERE up.user_id = '22222222-2222-2222-2222-222222222222';


-- =========================================================
-- TEST RESUMES
-- =========================================================

INSERT INTO resume (
    title,
    user_profile_id,
    url,
    upload_date,
    description
)
SELECT
    'Dmitriy Java Developer CV',
    up.user_profile_id,
    '/test-resumes/dmitriy-java-developer.pdf',
    CURRENT_DATE,
    'Java developer with experience in Spring Boot, PostgreSQL, Docker and Angular.'
FROM user_profile up
WHERE up.user_id = '11111111-1111-1111-1111-111111111111';


INSERT INTO resume (
    title,
    user_profile_id,
    url,
    upload_date,
    description
)
SELECT
    'Anna Frontend Developer CV',
    up.user_profile_id,
    '/test-resumes/anna-frontend-developer.pdf',
    CURRENT_DATE,
    'Frontend developer with experience in Angular, Java and PostgreSQL.'
FROM user_profile up
WHERE up.user_id = '22222222-2222-2222-2222-222222222222';


-- =========================================================
-- TEST JOB VACANCIES
-- =========================================================

INSERT INTO job_vacancy (
    title,
    company,
    company_location,
    description,
    post_date,
    url,
    source,
    source_job_id
)
VALUES
    (
        'Java Backend Developer',
        'TechCorp',
        'Berlin, Germany',
        'We are looking for a Java Backend Developer with experience in Spring Boot, PostgreSQL and Docker.',
        CURRENT_DATE,
        'https://example.com/jobs/techcorp-java-backend',
        'LINKEDIN',
        'linkedin-100001'
    ),
    (
        'Full Stack Developer',
        'WebSolutions',
        'Hamburg, Germany',
        'Full Stack Developer position requiring Java, Spring Boot, PostgreSQL, Docker and Angular.',
        CURRENT_DATE,
        'https://example.com/jobs/websolutions-fullstack',
        'INDEED',
        'indeed-200001'
    ),
    (
        'Angular Frontend Developer',
        'FrontendLabs',
        'Remote, Germany',
        'Angular developer position. Experience with Angular, PostgreSQL and modern web development is required.',
        CURRENT_DATE,
        'https://example.com/jobs/frontendlabs-angular',
        'LINKEDIN',
        'linkedin-100002'
    );