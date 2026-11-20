# AGENTS.md

This file provides guidance to coding agents when working with code in this repository.

## Project Overview

OpenLegislation is the NY Senate's legislative data platform. It continuously ingests raw legislative data as LBDC publishes it (mostly SOBI/XML), processes it into structured domain models (bills, laws, agendas, calendars, committees, transcripts), stores it in PostgreSQL, indexes it in Elasticsearch, and serves it via a REST API and React frontend.

## Build & Run Commands

```bash
# Compile and migrate the main database (flyway.conf)
mvn compile flyway:migrate

# Build WAR for deployment (target/legislation##<version>.war): frontend, unit and
# integration tests, and migrations (needs PostgreSQL + Elasticsearch)
mvn clean verify

# Build the same WAR running only the unit tests (no database needed)
mvn clean package

# Run unit tests only (*Test classes)
mvn test

# Run a single unit test class
mvn test -Dtest=LawTitleParserTest

# Run one integration test without npm, the unit tests, the WAR or Flyway (PostgreSQL
# and Elasticsearch must be running and the test database migrated)
mvn test-compile failsafe:integration-test failsafe:verify -Dit.test=LawGetCtrlIT

# Migrate only the test database
mvn compile flyway:migrate@flyway-migrate-test

# Frontend (from src/main/webapp/)
npm ci          # install deps
npm run build   # production webpack build; Maven runs it only when building the WAR
npm start       # dev server on :3000 (proxies API/static to Tomcat on :8080; set OL_IP to use another address)

# Regenerate the public API docs (needs Sphinx; see docs/backend/index.md)
make -C docs/api html    # writes src/main/webapp/static/docs/html
make -C docs/api clean   # deletes only the Sphinx cache in docs/api/_build
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

- **Java 21**, **Spring 7.0** (not Spring Boot), **Tomcat 11**
- **PostgreSQL** with C3P0 connection pooling, **Flyway 12** migrations
- **Elasticsearch 8.19** for search (`elasticsearch-java` client)
- **Apache Shiro 3** for auth (not Spring Security)
- **JUnit 4**; the filename decides whether a test is a unit or an integration test
- **React 17** + Webpack 5 + TailwindCSS 3 frontend in `src/main/webapp/`

## Testing Conventions

- The filename alone decides how a test runs: `*Test.java` are unit tests, run by `mvn test` (surefire); `*IT.java` are integration tests, run by `mvn verify` (failsafe), and need PostgreSQL and Elasticsearch. A class named any other way never runs
- Integration tests usually extend `BaseTests`, which provides the Spring context, `@Transactional` rollback and the `test` profile. A class that loads the Spring test context, as every `BaseTests` subclass does, must be named `*IT`
- `TestClassNamesTest` fails, naming the class, when a test class breaks either rule. Abstract base classes and `SillyTest` classes are exempt
- `@Category(SillyTest.class)` (from `gov.nysenate.openleg.config.annotation`) excludes a class from both surefire and failsafe, regardless of its filename
- Test output (logging, stdout, stderr) goes to `<package>.<Class>-output.txt` in `target/surefire-reports/` or `target/failsafe-reports/`, not the console. Add `-Dmaven.test.redirectTestOutputToFile=false` to see it on the console
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
- `docs/api/conventions.rst` - the rules every public endpoint follows (half-open date ranges, paging, the error envelope). Check any endpoint change against it; an endpoint that disagrees with it is a bug to raise, not behavior to preserve
- `src/main/webapp/static/docs/html/` - the generated HTML, tracked in git and served at `/static/docs/html/`. Regenerate it with `make -C docs/api html` after changing `docs/api/`, and commit the source and the regenerated HTML separately. A rebuild of unchanged source changes nothing, so a diff outside the pages you edited usually means a different Sphinx or theme version
- `docs/backend/index.md` - local development environment setup, including the docs build; not published

## Naming Conventions

- REST controllers: `*Ctrl.java`
- API response DTOs: `*View.java`
- Data access: `Sql*Dao.java` (concrete), `*Dao.java` (interface)
- Processors: `Xml*Processor.java` for XML data handlers
- Search: `Elastic*SearchService.java`
- Unit tests: `*Test.java`; integration tests: `*IT.java`

## Git Commit Messages

- Subject: imperative mood ("Add", "Fix"), at most 50 characters, capitalized, no trailing period
- Blank line, then a body wrapped at 72 characters
- The body says why: the problem, the motivation, the user-visible effect. Write only what a reader can't get from the subject and the diff
- Leave out how: don't narrate code, steps or testing, and don't describe how the implementation behaves (safeguards, edge cases it handles, how it can be run)
- Leave out the body when the subject says it all
