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