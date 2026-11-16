# JobXHunter

AI-powered platform for automated job hunting.

## Requirements

To run the application, you need:

- Git
- Docker Desktop

Java, Maven and PostgreSQL do not need to be installed locally.
They are provided through Docker containers.

## Features
- User management
- Resume upload & parsing
- AI vacancy matching
- Automated job notifications
- Job filtering

## Tech Stack
- Java 21
- Spring Boot
- PostgreSQL
- Liquibase
- Maven
- Quartz
- Spring AI
- Angular (planned)

## Running the application

Clone the repository:

    git clone https://github.com/DmiPy/job-x-hunter.git

Navigate to the project:

    cd job-x-hunter


Create the environment file (Windows):

    copy .env.example .env

Create the environment file (Lunux/MacOs):

    cp .env.example .env

Configure the required environment variables in `.env`.

Build and start the application:

    docker compose up --build

The application will be available at:

    http://localhost:8080

To stop the application:

    docker compose down

## Environment variables

The application uses environment variables for database configuration.

Create a `.env` file based on `.env.example`:

    copy .env.example .env

Do not commit `.env` to Git because it may contain sensitive credentials.

## Environment profiles and database safety

Liquibase is used for schema migrations. The original changesets, paths, IDs,
and checksums are preserved. Reference skills are always migrated. V3 test
users, resumes and vacancies run only through the local master changelog.

- `local` is the Docker Compose default and includes local seed data.
- `test` is reserved for Testcontainers and uses a production-safe changelog.
- `prod` uses that production-safe changelog and excludes local seed data.

The production-safe changelog assigns the original logical file path to its
shared changesets. Therefore a database that already executed V3 can switch to
the production profile without reapplying V1–V3; it simply retains its history.
Production deployments must set `SPRING_PROFILES_ACTIVE=prod`.

## Tests

Integration tests use PostgreSQL through Testcontainers. Docker Desktop must
be running before executing:

    ./mvnw test

The tests dynamically provide their datasource and never use the local or
production database configuration.

## Search filters (Phase 3)

Authenticated endpoints: `GET`, `POST`, `PATCH`, and `DELETE /api/search-filters/me`.
The server resolves the profile from the JWT; request bodies must not supply a
user or profile ID. A profile has at most one filter. Create returns 201,
duplicate create 409, and a missing profile or filter 404.

`minimumSalary` is the minimum desired **annual gross salary in EUR**. It is
only a user preference; this phase does not compare or convert vacancy salaries.
`employmentTypes` and `workplaceTypes` accept arrays of existing enum names;
`preferredSkillIds` and `excludedSkillIds` reference existing catalog skills.
A skill in both lists, an unknown/duplicate skill ID, or a negative salary or
radius returns 400. `preferredLocation`, `radiusKm`, `minimumSalary`, `keywords`,
and all four collections are optional. On PATCH, omitted fields remain unchanged;
explicit `null` clears an optional field or collection, and `[]` clears a
collection.

V6 reuses the V1 skill join tables and preserves existing rows. It halts instead
of guessing if an existing profile has multiple filters, old nonzero/min-max
salary data of unknown currency/period, or conflicting preferred/excluded skills.
Resolve such data deliberately before migrating that database.

## Keycloak local authentication

Copy `.env.example` to `.env`, replace the local placeholder passwords, then
start the stack with `docker compose up --build`. Keycloak imports the
development-only `jobxhunter` realm at `http://localhost:8180`; the admin
console is at `/admin/`. Create a user in the realm, verify its email, and
assign the `USER` realm role. The Angular client `jobxhunter-angular` is public
and requires Authorization Code flow with PKCE method `S256`.

For an API token, use a local OIDC client such as Postman configured for
Authorization Code + PKCE, issuer `http://localhost:8180/realms/jobxhunter`,
client ID `jobxhunter-angular`, and callback
`https://oauth.pstmn.io/v1/callback`, or
`https://oauth.pstmn.io/v1/browser-callback` when using Postman's browser flow.
Request `openid profile email` scopes and send the access token as `Authorization: Bearer …`.
In Postman select OAuth 2.0, Authorization Code (With PKCE), challenge method
S256, authorization URL `http://localhost:8180/realms/jobxhunter/protocol/openid-connect/auth`,
token URL `http://localhost:8180/realms/jobxhunter/protocol/openid-connect/token`,
and no client secret. To switch users, add `prompt=login` as an authorization
request parameter before requesting a new token. Assign the `ADMIN` realm role
for the manual ingestion endpoint; use a separate USER-only account to test 403.
Importing a realm does not overwrite an existing realm: add the browser callback
to the existing client's valid redirect URIs manually if necessary. Do not
recreate a development Keycloak container merely to update its import file:
its embedded development database and users may be lost.
Example: `curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/profiles/me`.
The access token must contain verified `email`, `sub`, the `USER` or `ADMIN`
realm role, and the `jobxhunter-api` audience. The development client includes
Keycloak's `basic` scope for `sub` and an API audience mapper.
Production must set its own `KEYCLOAK_ISSUER_URI`, `KEYCLOAK_JWK_SET_URI`,
`KEYCLOAK_AUDIENCE`, and allowed CORS origins;
the development realm and its redirect URIs are for local use only.

## Centralized Adzuna ingestion

Ingestion is independent of user SearchFilters. Set `ADZUNA_ENABLED=true`,
`ADZUNA_APP_ID`, and `ADZUNA_APP_KEY` in your local `.env`; credentials must
never be committed. Search uses Germany (`de`). The semicolon-separated
`ADZUNA_KEYWORDS`, `ADZUNA_LOCATIONS`, and `ADZUNA_CATEGORIES` lists form a
Cartesian product, so keep them small. `ADZUNA_PAGE_SIZE` (1–50),
`ADZUNA_MAX_PAGES`, `ADZUNA_RUN_BUDGET`, and `ADZUNA_DAILY_BUDGET` bound work.
Every HTTP attempt, including retries, counts against both budgets. The daily
budget resets at midnight UTC and is held in process memory: restarting or
running multiple instances resets/multiplies it, so configure conservatively
and monitor the Adzuna account quota independently.

`ADZUNA_RETRIES`, `ADZUNA_PACING_MS`, `ADZUNA_BACKOFF_MS`,
`ADZUNA_CONNECT_TIMEOUT_MS`, and `ADZUNA_READ_TIMEOUT_MS` control retries,
pacing, and timeouts. `INGESTION_SCHEDULE_ENABLED=false` by default;
`INGESTION_INTERVAL_MS` sets fixed delay when enabled. A manual run uses the
same overlap lock and budget. In Postman, send an ADMIN access token with
`POST http://localhost:8080/api/admin/ingestion` and no request body, or:

    curl -X POST -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/admin/ingestion

The response reports fetched, inserted, updated, invalid, and failed counts;
409 means a run is already active. Partial pages already received are saved.
Currently `failed` includes incomplete source fetches as well as source and
persistence errors: it does **not** distinguish a page-limit truncation from an
exhausted request budget or a genuine HTTP/transport/response failure. A full
page at the configured last page can therefore increment `failed` even when the
request succeeded. Budget exhaustion can occur before any HTTP request; 429,
timeouts, malformed responses, and server errors are actual source failures.
The current API does not expose a reason, so its summary alone cannot tell these
cases apart. Do not infer expiry or delete jobs from an incomplete run.
Missing source salary currency and period remain unknown and are not compared
with SearchFilter salary. Adzuna descriptions may be snippets. Existing
`post_date` falls back to ingestion date for legacy compatibility; the nullable
`source_published_at` is the actual source timestamp. Automatic vacancy deletion
is disabled pending a retention policy that accounts for user interactions.

### Isolated Windows smoke test

Never point a fresh smoke test at an existing development database without
approval: the local profile applies V1–V7, including V3 test seed data. Use a
separate PostgreSQL container and named volume, such as `jobxhunter-phase4a-db`
and `jobxhunter_phase4a_pgdata`. In an application container on the same Docker
network, set `SPRING_DATASOURCE_URL` to
`jdbc:postgresql://jobxhunter-phase4a-db:5432/jobxhunter_phase4a`, use the
isolated database credentials, and set `SPRING_PROFILES_ACTIVE=local`.
Point `KEYCLOAK_JWK_SET_URI` at the existing Keycloak container's internal URL.
Bind the app to `127.0.0.1:18080:8080`; keep
`INGESTION_SCHEDULE_ENABLED=false`. Start with `ADZUNA_ENABLED=false`, verify
startup and the isolated database's `databasechangelog` IDs 1 through 7, then
enable Adzuna only for an explicitly approved manual test. For the smallest
repeatable test use one keyword and one location, `ADZUNA_PAGE_SIZE=10`,
`ADZUNA_MAX_PAGES=1`, `ADZUNA_RUN_BUDGET=1`, `ADZUNA_DAILY_BUDGET=2`, and
`ADZUNA_RETRIES=0`. Do not delete existing volumes or restart Keycloak.

Send `POST http://localhost:18080/api/admin/ingestion` with an ADMIN bearer
token; repeat once, then verify a USER token receives 403 and no token receives
401. In the isolated database, compare `job_vacancy_id`, `first_seen_at`, and
`last_seen_at` for matching `(source, source_job_id)` values, and check for
duplicates with:

    SELECT source, source_job_id, COUNT(*) FROM job_vacancy
    GROUP BY source, source_job_id HAVING COUNT(*) > 1;
