-- External sources may omit company, location and description, but every
-- persisted vacancy must have a title, source identity and URL.
ALTER TABLE job_vacancy
    ALTER COLUMN title SET NOT NULL;
