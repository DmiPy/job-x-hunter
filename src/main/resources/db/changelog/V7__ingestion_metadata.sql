ALTER TABLE job_vacancy ADD COLUMN source_published_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE job_vacancy ADD COLUMN first_seen_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE job_vacancy ADD COLUMN last_seen_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE job_vacancy ADD COLUMN salary_min NUMERIC(19, 2);
ALTER TABLE job_vacancy ADD COLUMN salary_max NUMERIC(19, 2);
ALTER TABLE job_vacancy ADD COLUMN salary_currency VARCHAR(3);
ALTER TABLE job_vacancy ADD COLUMN salary_period VARCHAR(32);
ALTER TABLE job_vacancy ADD COLUMN salary_is_predicted BOOLEAN;
CREATE INDEX ix_job_vacancy_last_seen ON job_vacancy (last_seen_at);
