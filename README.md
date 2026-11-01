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
