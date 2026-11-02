ALTER TABLE users ADD COLUMN auth_issuer VARCHAR(512);
ALTER TABLE users ADD COLUMN auth_subject VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uk_users_auth_identity UNIQUE (auth_issuer, auth_subject);

ALTER TABLE users
    ADD CONSTRAINT ck_users_auth_identity_pair
    CHECK ((auth_issuer IS NULL) = (auth_subject IS NULL));
