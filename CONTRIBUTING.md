# Contributing to OpenLegislation

Thank you for your interest in contributing to OpenLegislation! This project is developed in-house by the New York State Senate and we welcome contributions from the community.

## Getting Started

### Prerequisites

- **Java 21** (JDK 21 — the `pom.xml` targets `<release>21</release>`)
- **Maven** (3.8+)
- **PostgreSQL** (15+)
- **Elasticsearch** 8.x
- **Node.js** (for frontend asset builds — runs automatically during `mvn compile`)
- **Tomcat 11** (for deployment; not required for building or testing)

### Setup

Follow the backend setup guide in [`docs/backend/index.md`](docs/backend/index.md) to configure your local environment. Key steps:

1. Clone the repository
2. Copy `src/main/resources/app.properties.example` to `app.properties` and configure database credentials
3. Copy `src/main/resources/flyway.conf.example` to `flyway.conf` and configure the database URL
4. Run `mvn compile flyway:migrate` to build and apply database migrations

### Verify Your Build

```bash
# Compile Java + frontend assets
mvn compile

# Run unit tests (Surefire, ~356 tests)
mvn test

# Run integration tests (Failsafe — requires PostgreSQL + Elasticsearch)
mvn verify
```

## How to Contribute

### Reporting Issues

- Search existing [issues](https://github.com/nysenate/OpenLegislation/issues) before opening a new one
- Include the Java version, OS, and steps to reproduce
- For API-related issues, include the endpoint URL and response

### Pull Requests

1. **Fork** the repository and create a branch from `dev`
2. **Keep PRs focused** — one concern per PR. Mixed changes are harder to review and slower to merge
3. **Write tests** for any new functionality or bug fixes
4. **Run tests locally** before pushing:
   ```bash
   mvn test        # unit tests
   mvn verify      # unit + integration tests (requires DB + ES)
   ```
5. **Follow existing code style** — the project includes IntelliJ code style settings in `.idea/codeStyles/`
6. **Reference issues** in your PR description (e.g., `Fixes #123`)

### Branch Naming

Use descriptive branch names with a prefix:
- `fix/` — bug fixes (e.g., `fix/stale-java-version-in-docs`)
- `feat/` — new features (e.g., `feat/rate-limiting`)
- `docs/` — documentation only (e.g., `docs/setup-guide-update`)
- `deps/` — dependency upgrades (e.g., `deps/upgrade-shiro`)

### Commit Messages

Use conventional commit prefixes:
- `feat:` — new feature
- `fix:` — bug fix
- `docs:` — documentation only
- `deps:` — dependency updates
- `refactor:` — code refactoring (no behavior change)
- `test:` — test additions or fixes

Example: `fix: sanitize user input before passing to Elasticsearch QueryStringQuery`

## Code Conventions

### Java

- **Java 21** target
- **Spring Framework 6** (not Spring Boot) — the app is a traditional WAR deployed on Tomcat 11
- **Spring JDBC** with `NamedParameterJdbcTemplate` for data access (no JPA/Hibernate)
- **Apache Shiro** for authentication/authorization (not Spring Security)
- **Elasticsearch 8** via the `elasticsearch-java` typed client
- Package root: `gov.nysenate.openleg`
- Controllers extend `BaseCtrl` and use `@ExceptionHandler` for centralized error handling
- SQL queries are defined in enum classes implementing `BasicSqlQuery` with `${schema}` substitution
- No hardcoded secrets — all credentials go in gitignored `.properties` files

### Testing

The project uses a custom annotation-based test categorization:

| Annotation | Runner | Purpose |
|---|---|---|
| `@UnitTest` | Surefire (`mvn test`) | Pure unit tests, fast, no external dependencies |
| `@IntegrationTest` | Failsafe (`mvn verify`) | Integration tests, may require DB/ES |

**How to write a unit test:**

```java
package gov.nysenate.openleg.myfeature;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class MyFeatureTest {

    @Test
    public void testSomething() {
        assertEquals(42, MyFeature.compute());
    }
}
```

- Use **JUnit 4** (`org.junit.Test`, `org.junit.Assert.*`)
- Use **Mockito 4** for mocking (`org.mockito.Mockito.*`)
- Unit test files end with `Test.java`; integration tests end with `IT.java`
- Place tests in `src/test/java/` mirroring the main source package structure

### Frontend

- **React 17** with **Webpack 5** and **Tailwind CSS**
- Frontend source lives in `src/main/webapp/`
- `npm ci` runs automatically during Maven's `generate-sources` phase

### Database Migrations

- Uses **Flyway** with date-stamped migration files in `src/main/resources/sql/migrations/`
- Migration naming: `V{YYYYMMDD}.{HHMM}__{description}.sql`
- Never modify existing migration files — always add new ones

## License

OpenLegislation is dual-licensed under BSD and GPL. See the [NYSenate licensing page](http://www.nysenate.gov/Open-Source-Software-Licenses) for details. By contributing, you agree that your contributions will be licensed under the same terms.

## Questions?

- Open an [issue](https://github.com/nysenate/OpenLegislation/issues) for bugs or feature requests
- Contact the current Senate developers (listed in the [README](README.md)) for other questions