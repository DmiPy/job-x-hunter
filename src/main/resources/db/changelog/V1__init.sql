-- =========================
-- USERS
-- =========================
CREATE TABLE users
(
    user_id    UUID PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    user_role  VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =========================
-- USER PROFILE (1:1)
-- =========================
CREATE TABLE user_profile
(
    user_profile_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         UUID UNIQUE  NOT NULL,
    firstname       VARCHAR(255) NOT NULL,
    surname         VARCHAR(255) NOT NULL,
    expected_salary NUMERIC(10, 2),
    employment_type VARCHAR(255),
    workplace_type  VARCHAR(255),

    FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);


-- =========================
-- SKILL
-- =========================
CREATE TABLE skill
(
    skill_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    skill_name VARCHAR(255) UNIQUE NOT NULL
);


-- =========================
-- JOB VACANCY
-- =========================
CREATE TABLE job_vacancy
(
    job_vacancy_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title            VARCHAR(255),
    company          VARCHAR(255),
    company_location VARCHAR(255),
    description      TEXT,
    post_date        DATE DEFAULT CURRENT_DATE,
    url              VARCHAR(2048) NOT NULL,
    source           VARCHAR(100)  NOT NULL,
    source_job_id    VARCHAR(255)  NOT NULL,

    UNIQUE (source, source_job_id)
);


-- =========================
-- RESUME
-- =========================
CREATE TABLE resume
(
    title           VARCHAR(255) NOT NULL,
    resume_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_profile_id BIGINT       NOT NULL,
    url             VARCHAR(255) DEFAULT '/',
    upload_date     DATE         DEFAULT CURRENT_DATE,
    description     TEXT,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
        ON DELETE CASCADE
);


-- =========================
-- USER SKILLS (M:N)
-- =========================
CREATE TABLE user_profile_skill
(
    user_profile_id BIGINT,
    skill_id        BIGINT,
    proficiency     INT CHECK (proficiency BETWEEN 0 AND 100),

    PRIMARY KEY (user_profile_id, skill_id),

    FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
        ON DELETE CASCADE,

    FOREIGN KEY (skill_id)
        REFERENCES skill (skill_id)
);


-- =========================
-- JOB SKILLS (M:N)
-- =========================
CREATE TABLE job_skill
(
    job_vacancy_id BIGINT,
    skill_id       BIGINT,

    PRIMARY KEY (job_vacancy_id, skill_id),

    FOREIGN KEY (job_vacancy_id)
        REFERENCES job_vacancy (job_vacancy_id)
        ON DELETE CASCADE,

    FOREIGN KEY (skill_id)
        REFERENCES skill (skill_id)
);


-- =========================
-- RESUME ↔ JOB
-- =========================
CREATE TABLE resume_job
(
    resume_id      BIGINT,
    job_vacancy_id BIGINT,

    PRIMARY KEY (resume_id, job_vacancy_id),

    FOREIGN KEY (resume_id)
        REFERENCES resume (resume_id)
        ON DELETE CASCADE,

    FOREIGN KEY (job_vacancy_id)
        REFERENCES job_vacancy (job_vacancy_id)
        ON DELETE CASCADE
);


-- =========================
-- SEARCH FILTER
-- =========================
CREATE TABLE search_filter
(
    search_filter_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_profile_id  BIGINT NOT NULL,
    min_salary       INT     DEFAULT 0,
    max_salary       INT,
    employment_type  VARCHAR(255),
    workplace_type   VARCHAR(255),
    is_notify_active BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
        ON DELETE CASCADE
);


-- =========================
-- FILTER → PREFERRED SKILLS
-- =========================
CREATE TABLE search_filter_preferred_skill
(
    search_filter_id BIGINT,
    skill_id         BIGINT,

    PRIMARY KEY (search_filter_id, skill_id),

    FOREIGN KEY (search_filter_id)
        REFERENCES search_filter (search_filter_id)
        ON DELETE CASCADE,

    FOREIGN KEY (skill_id)
        REFERENCES skill (skill_id)
);


-- =========================
-- FILTER → PROHIBITED SKILLS
-- =========================
CREATE TABLE search_filter_prohibited_skill
(
    search_filter_id BIGINT,
    skill_id         BIGINT,

    PRIMARY KEY (search_filter_id, skill_id),

    FOREIGN KEY (search_filter_id)
        REFERENCES search_filter (search_filter_id)
        ON DELETE CASCADE,

    FOREIGN KEY (skill_id)
        REFERENCES skill (skill_id)
);


-- =========================
-- JOB MATCH SCORE
-- =========================
CREATE TABLE job_match_score
(
    resume_id      BIGINT,
    job_vacancy_id BIGINT,

    score          SMALLINT CHECK (score >= 0 AND score <= 100),

    is_seen        BOOLEAN   DEFAULT false,

    match_reason   TEXT,
    model_version  VARCHAR(100),
    calculated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (resume_id, job_vacancy_id),

    FOREIGN KEY (resume_id)
        REFERENCES resume (resume_id)
        ON DELETE CASCADE,

    FOREIGN KEY (job_vacancy_id)
        REFERENCES job_vacancy (job_vacancy_id)
        ON DELETE CASCADE
);


-- =========================
-- SAVED JOB
-- =========================
CREATE TABLE saved_job
(
    user_id        UUID,
    job_vacancy_id BIGINT,

    saved_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, job_vacancy_id),

    FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,

    FOREIGN KEY (job_vacancy_id)
        REFERENCES job_vacancy (job_vacancy_id)
        ON DELETE CASCADE
);