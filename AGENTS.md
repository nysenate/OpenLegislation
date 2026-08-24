# AGENTS.md

This file provides guidance to coding agents when working with code in this repository.

## Project Overview

OpenLegislation is the NY Senate's legislative data platform. It ingests raw legislative data (mostly SOBI/XML from LBDC), processes it into structured domain models (bills, laws, agendas, calendars, committees, transcripts), stores it in PostgreSQL, indexes it in Elasticsearch, and serves it via a REST API and React frontend.

## Build & Run Commands

```bash
# Compile and run database migrations
mvn compile flyway:migrate

# Build WAR for deployment (target/legislation##<version>.war)
mvn clean package

# Run unit tests only (uses @UnitTest category)
mvn test

# Run a single unit test class
mvn test -Dtest=LawTitleParserTest

# Run integration tests (requires running PostgreSQL + Elasticsearch)
mvn integration-test

# Run a single integration test class
mvn -Dit.test=DatabaseConfigIT integration-test

# Frontend (from src/main/webapp/)
npm ci          # install deps (also runs automatically during mvn compile)
npm run build   # production webpack build
npm start       # dev server on :3000 (proxies API/static to Tomcat on :8080)
```

The `integration-test` phase runs Flyway twice beforehand: once against the main database
(`flyway.conf`) and once against the test database (`flyway.conf` overlaid with
`test.flyway.conf`).

## Architecture

### Data Flow

```
LBDC XML Source Files -> Processors -> Domain Models -> PostgreSQL -> Elasticsearch Index -> REST API / React UI
```

### Key Layers

All Java lives under `src/main/java/gov/nysenate/openleg/`:

- **`api/`** - Spring MVC REST controllers (named `*Ctrl.java`). Response objects are `*View.java` classes.
- **`processors/`** - Ingest and parse raw legislative XML data into domain models. Sub-packages per entity type (bill, law, agenda, calendar, committee, transcripts).
- **`legislation/`** - Domain models and data access (Bill, Law, Agenda, Calendar, Committee, Member, Transcript). DAOs use Spring JdbcTemplate with PostgreSQL.
- **`search/`** - Elasticsearch indexing and query services, one sub-package per entity type.
- **`spotchecks/`** - Data QA system that compares OpenLeg data against external sources (Daybreak, Senate website scraping) to detect mismatches.
- **`notifications/`** - Email and Slack alerting with subscription management.
- **`updates/`** - Change tracking/audit log for all entities.
- **`auth/`** - Apache Shiro-based authentication and API key management.
- **`config/`** - Spring configuration classes (`WebApplicationConfig`, `DatabaseConfig`, `SecurityConfig`).
- **`common/`** - Shared DAO base classes, utilities, and standalone CLI scripts (`common/script/`, subclasses of `BaseScript`).

### Frontend

- Source: `src/main/webapp/WEB-INF/app/` (entry point `index.js`)
- Build output: `src/main/webapp/static/dist/`, content-hashed filenames
- Webpack config, Tailwind config, and PostCSS config are at `src/main/webapp/`
- Webpack resolves the alias `app` to `WEB-INF/app`, so imports look like `app/shared/Input`

### Event System

Uses Guava `AsyncEventBus` for intra-process messaging (notifications, index updates). Listeners use `@Subscribe`.

### Caching

In-memory caches use Ehcache 3, wired through `OpenLegCacheManager`. Each cache is a
subclass of `legislation/CachingService` that declares a `CacheType`. Sizes are derived
from the number of initial entries, except the `BILL` and `BILL_INFO` caches, which read
`bill.cache.size` / `bill_info.cache.size` from `app.properties` and fail at startup if
those are missing.

## Tech Stack

- **Java 21**, **Spring 6.2** (not Spring Boot), **Tomcat 11**
- **PostgreSQL** with C3P0 connection pooling, **Flyway 12** migrations
- **Elasticsearch 8.14** for search (`elasticsearch-java` client)
- **Apache Shiro 2** for auth (not Spring Security)
- **JUnit 4** with `@Category(UnitTest.class)` / `@Category(IntegrationTest.class)`
- **React 17** + Webpack 5 + TailwindCSS 3 frontend in `src/main/webapp/`

## Testing Conventions

- Tests are categorized via JUnit `@Category` annotations from `gov.nysenate.openleg.config.annotation`
- `@Category(UnitTest.class)` - runs with `mvn test` (surefire); classes are named `*Test.java`
- `@Category(IntegrationTest.class)` - runs with `mvn integration-test` (failsafe), requires DB and ES; classes must be named `*IT.java` to be picked up
- `@Category(SillyTest.class)` - matches neither plugin's `groups`, so these classes never run in a build
- Integration tests extend `BaseTests` which provides Spring context, `@Transactional` rollback, and the `test` profile
- Test config loads `test.app.properties` which overrides DB name to `openleg_test`

## Configuration

- `src/main/resources/app.properties` - main config (copy from `app.properties.example`)
- `src/main/resources/flyway.conf` - Flyway config (copy from `flyway.conf.example`)
- `src/main/resources/log4j2.xml` - logging config (copy from `log4j2.xml.example`)
- `src/main/resources/shiro.ini` - Shiro auth config
- `src/test/resources/test.app.properties` - test DB overrides
- `src/test/resources/test.flyway.conf` - test DB Flyway overrides
- DB schemas: `public` and `master`; legislative content lives in the schema named by `env.schema` (`master`)
- Migrations: `src/main/resources/sql/migrations/` (named `V<yyyyMMdd>.<HHmm>__description.sql`; Flyway runs out-of-order)

## Documentation

- `docs/api/` - Sphinx (`.rst`) source for the public JSON API docs; update it alongside changes to public API endpoints or response shapes
- `docs/backend/index.md` - local development environment setup

## Naming Conventions

- REST controllers: `*Ctrl.java`
- API response DTOs: `*View.java`
- Data access: `Sql*Dao.java` (concrete), `*Dao.java` (interface)
- Processors: `Xml*Processor.java` for XML data handlers
- Search: `Elastic*SearchService.java`
- Unit tests: `*Test.java`; integration tests: `*IT.java`
