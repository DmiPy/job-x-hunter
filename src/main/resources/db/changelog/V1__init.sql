-- =========================
-- USERS
-- =========================
CREATE TABLE users(
                      user_id UUID PRIMARY KEY,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      user_role VARCHAR(255) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- USER PROFILE (1:1)
-- =========================
CREATE TABLE user_profile(
                             user_profile_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             user_id UUID UNIQUE NOT NULL,
                             firstname VARCHAR(255) NOT NULL,
                             surname VARCHAR(255) NOT NULL,
                             expected_salary FLOAT,
                             job_type VARCHAR(255),
                             FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- SKILL (справочник)
-- =========================
CREATE TABLE skill(
                      skill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      skill_name VARCHAR(255) UNIQUE NOT NULL
);

-- =========================
-- JOB VACANCY
-- =========================
CREATE TABLE job_vacancy(
                            job_vacancy_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            title VARCHAR(255),
                            company VARCHAR(255),
                            company_location VARCHAR(255),
                            description TEXT,
                            post_date DATE,
                            url VARCHAR(255)
);

-- =========================
-- RESUME
-- =========================
CREATE TABLE resume(
                       resume_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       user_id UUID,
                       url VARCHAR(255) DEFAULT '/',
                       upload_date DATE DEFAULT CURRENT_DATE,
                       description VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- USER SKILLS (M:N)
-- =========================
CREATE TABLE user_skill(
                           user_id UUID,
                           skill_id BIGINT,
                           proficiency INT DEFAULT 0,
                           PRIMARY KEY (user_id, skill_id),
                           FOREIGN KEY (user_id) REFERENCES users(user_id),
                           FOREIGN KEY (skill_id) REFERENCES skill(skill_id)
);

-- =========================
-- JOB SKILLS (M:N)
-- =========================
CREATE TABLE job_skills(
                           job_vacancy_id BIGINT,
                           skill_id BIGINT,
                           PRIMARY KEY (job_vacancy_id, skill_id),
                           FOREIGN KEY (job_vacancy_id) REFERENCES job_vacancy(job_vacancy_id),
                           FOREIGN KEY (skill_id) REFERENCES skill(skill_id)
);

-- =========================
-- RESUME ↔ JOB (M:N)
-- =========================
CREATE TABLE resume_jobs(
                            resume_id BIGINT,
                            job_vacancy_id BIGINT,
                            PRIMARY KEY (resume_id, job_vacancy_id),
                            FOREIGN KEY (resume_id) REFERENCES resume(resume_id),
                            FOREIGN KEY (job_vacancy_id) REFERENCES job_vacancy(job_vacancy_id)
);

-- =========================
-- SEARCH FILTER
-- =========================
CREATE TABLE search_filter(
                              search_filter_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              user_id UUID NOT NULL,
                              min_salary INT DEFAULT 0,
                              max_salary INT,
                              job_type VARCHAR(255),
                              isNotifyActive BOOLEAN DEFAULT false,
                              FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- FILTER → PREFERRED SKILLS (M:N)
-- =========================
CREATE TABLE search_filter_preferred_skills(
                                               search_filter_id BIGINT,
                                               skill_id BIGINT,
                                               PRIMARY KEY (search_filter_id, skill_id),
                                               FOREIGN KEY (search_filter_id) REFERENCES search_filter(search_filter_id),
                                               FOREIGN KEY (skill_id) REFERENCES skill(skill_id)
);

-- =========================
-- FILTER → PROHIBITED SKILLS (M:N)
-- =========================
CREATE TABLE search_filter_prohibited_skills(
                                                search_filter_id BIGINT,
                                                skill_id BIGINT,
                                                PRIMARY KEY (search_filter_id, skill_id),
                                                FOREIGN KEY (search_filter_id) REFERENCES search_filter(search_filter_id),
                                                FOREIGN KEY (skill_id) REFERENCES skill(skill_id)
);

-- =========================
-- JOB MATCH SCORE
-- =========================
CREATE TABLE job_match_score(
                                resume_id BIGINT,
                                job_vacancy_id BIGINT,
                                score INT CHECK(score >= 0 AND score <= 100),
                                is_seen BOOLEAN DEFAULT false,
                                PRIMARY KEY (resume_id, job_vacancy_id),
                                FOREIGN KEY (resume_id) REFERENCES resume(resume_id),
                                FOREIGN KEY (job_vacancy_id) REFERENCES job_vacancy(job_vacancy_id)
);