ALTER TABLE search_filter ADD CONSTRAINT uk_search_filter_profile UNIQUE (user_profile_id);
ALTER TABLE search_filter ADD COLUMN preferred_location VARCHAR(255);
ALTER TABLE search_filter ADD COLUMN radius_km INTEGER;
ALTER TABLE search_filter ADD COLUMN keywords VARCHAR(1000);
ALTER TABLE search_filter ALTER COLUMN min_salary DROP DEFAULT;
ALTER TABLE search_filter ADD CONSTRAINT ck_search_filter_salary CHECK (min_salary IS NULL OR min_salary >= 0);
ALTER TABLE search_filter ADD CONSTRAINT ck_search_filter_radius CHECK (radius_km IS NULL OR radius_km >= 0);

CREATE TABLE search_filter_employment_type (
    search_filter_id BIGINT NOT NULL REFERENCES search_filter(search_filter_id) ON DELETE CASCADE,
    employment_type VARCHAR(40) NOT NULL,
    PRIMARY KEY (search_filter_id, employment_type)
);
CREATE TABLE search_filter_workplace_type (
    search_filter_id BIGINT NOT NULL REFERENCES search_filter(search_filter_id) ON DELETE CASCADE,
    workplace_type VARCHAR(40) NOT NULL,
    PRIMARY KEY (search_filter_id, workplace_type)
);

-- Preserve values from the legacy single-choice columns.
INSERT INTO search_filter_employment_type (search_filter_id, employment_type)
SELECT search_filter_id, employment_type FROM search_filter WHERE employment_type IS NOT NULL;
INSERT INTO search_filter_workplace_type (search_filter_id, workplace_type)
SELECT search_filter_id, workplace_type FROM search_filter WHERE workplace_type IS NOT NULL;
