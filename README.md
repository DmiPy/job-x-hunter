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
`https://oauth.pstmn.io/v1/callback` (or the Angular localhost callback).
Request `openid profile email` scopes and send the access token as `Authorization: Bearer …`.
Example: `curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/profiles/me`.
The access token must contain verified `email`, `sub`, the `USER` or `ADMIN`
realm role, and the `jobxhunter-api` audience. The development client includes
Keycloak's `basic` scope for `sub` and an API audience mapper.
Production must set its own `KEYCLOAK_ISSUER_URI`, `KEYCLOAK_JWK_SET_URI`,
`KEYCLOAK_AUDIENCE`, and allowed CORS origins;
the development realm and its redirect URIs are for local use only.
