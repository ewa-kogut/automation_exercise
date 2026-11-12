# AGENTS.md

Instructions for AI coding agents working in this repository. For human-facing documentation, see `README.md`.

## Project

UI (Selenium) and API (REST Assured) test automation for https://automationexercise.com, a demo e-commerce site.
- Source of scenarios: [UI test cases](https://automationexercise.com/test_cases), [API list](https://automationexercise.com/api_list).
- Stack: Java 21, Maven (via wrapper), JUnit 5, AssertJ, Awaitility, Selenium 4, REST Assured 5.

## Commands

Use the Maven Wrapper. Maven is not installed globally.

| Task | Command (Git Bash) |
| --- | --- |
| Compile everything | `./mvnw -B test-compile` |
| Whole suite | `./mvnw -B test` |
| One class or method | `./mvnw -B test -Dtest=LoginTests` / `-Dtest=LoginTests#shouldLogoutSuccessfully` |
| API tests only | `./mvnw -B test -Dtest="com.qa.automation.api.**"` |
| Tests by ID | `./mvnw -B test -Dgroups=TC-02,API-07` |

- Results are in `target/surefire-reports/`.
- When a UI test fails, a screenshot and the page HTML are saved to `target/screenshots/`.

## Layout

- `src/main/java/com/qa/automation/`
  - `pages/` – page objects
  - `components/` – reusable page parts
  - `api/clients/` – REST Assured clients
  - `config/` – `Config`, `Timeouts`
  - `driver/` – `DriverFactory`, `DriverManager`
  - `extensions/` – JUnit extensions: `ScreenshotOnFailureExtension`, `ConsoleProgressExtension`
- `src/test/java/com/qa/automation/`
  - `webui/` – UI tests and their base class, `WebTestBase`
  - `api/` – API tests, `ApiAssertions`, `TestAccountCleanup`

## Conventions

### Code style
- Google Java Style: 2-space indent, +4 for continuation lines, 100-character lines.
- Static imports first, then all other imports in one ASCII-sorted block.
- Empty constructors are written as `{}`.
- Files use LF line endings and end with a newline.

### Tests
- Every test has `// Arrange`, `// Act` and `// Assert` sections. If a section has nothing to do, keep the header with a short note, e.g. `// Arrange – no request data needed`.
- Every test has a `@DisplayName`.
- Every test has a unique ID as a JUnit `@Tag`, matching the scenario number on the site: `@Tag("TC-05")` for UI test case 5, `@Tag("API-11")` for API 11. A case that is not on the site gets the next free number after the site's last scenario (TC-26), e.g. `TC-27`, so the site's numbers stay free for their own scenarios. Add the ID to the *Current Test Coverage* table in `README.md` and describe the case in `docs/test-cases.md`. IDs are used by `-Dgroups=...` and the Jenkins `TEST_IDS` parameter.
- Only classes that contain tests end with `Tests`. Base or helper classes must not use that suffix.
- `@MethodSource` uses the short method name, never `"pkg.Class#method"`. If the data lives in another class, add a one-line static method in the test class that delegates to it.
- Use AssertJ (`assertThat`) with `.as(...)` descriptions for boolean checks.
- Prefer `@ParameterizedTest` for variations of the same scenario, e.g. negative cases.
- Tests must be independent. Each UI test gets a fresh browser from `WebTestBase`.

### UI (page objects)
- Page objects and components extend `BasePage` and use its wait helpers, e.g. `click`, `type`, `selectByText`, `getText`, `isVisible`, `isNotVisible`.
- Never use `Thread.sleep` or raw `driver.findElement` in page objects.
- Locators are `private final By` fields. Prefer, in this order: `data-qa` attributes, `id`, short CSS selectors. Use XPath only when matching on text is unavoidable.
- Before writing or changing a locator, confirm it against the live HTML (`curl -s https://automationexercise.com/<path>`).
- Page objects contain no assertions. They return values or booleans, and the test asserts.
- Login test data (`validEmail`, `validPassword`, `InvalidCredentials`) lives in `LoginPage`.

### API
- One client class per resource in `api/clients/`. Tests call client methods only.
- The API answers HTTP 200 even for errors, and the real status is in the body as `responseCode`. Parse the body with `JsonPath.from(response.asString())`, because the content type is `text/html`. Assert the HTTP status, `responseCode` and `message`.
- Before writing assertions, check the real response with `curl`.

### Configuration and secrets
- Read settings with `Config.get` or `Config.getRequired`. Never hard-code URLs or credentials.
- Credentials (`test.user.email`, `test.user.password`) come from the git-ignored `src/main/resources/config-local.properties`, from environment variables, or from `-D` options. Never read, print or commit their values.
- Keep real credentials out of reports: never pass them as test parameters (Allure and ReportPortal save parameter values), and send them with `BaseApiClient.unloggedRequest()` (Allure saves the form data of logged requests).

## Documentation

When tests are added or changed, keep `README.md` in sync:
- the *Project Structure* tree (new classes, with a short comment);
- the *Current Test Coverage* table;
- `docs/test-cases.md`: steps, expected results and every difference from the scenario on the site, with the reason;
- the *Roadmap*: move finished items to **Done**.

## Git

- `develop` is the default branch and `main` is the stable branch. Both are protected: changes reach
  them only through pull requests (feature branch → `develop` → `main`), which run the tests and
  Claude's review.
- Do not commit, push or switch branches unless the user asks.
