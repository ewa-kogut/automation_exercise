# Automation Exercise – Test Automation Framework

UI and API test automation framework for [Automation Exercise](https://automationexercise.com), a demo e-commerce application built for practicing test automation. The site exposes both a web UI ([26 test scenarios](https://automationexercise.com/test_cases)) and a public REST API ([14 endpoints](https://automationexercise.com/api_list)), so the project covers both layers.

## Tech Stack

| Area | Tool | Version |
| --- | --- | --- |
| Language | Java | 21 |
| Build | Maven (via Maven Wrapper) | 3.9.9 |
| Test execution | Maven Surefire | 3.5.2 |
| UI automation | Selenium WebDriver | 4.52.0 |
| API automation | REST Assured | 5.5.6 |
| Test runner | JUnit 5 (Jupiter) | 5.12.2 |
| Assertions | AssertJ | 3.26.3 |
| Reporting | Allure Report (allure-jupiter, allure-rest-assured, allure-maven) | 2.35.5 / plugin 3.1.0 |
| Design pattern | Page Object Model | – |
| Code style | google-java-format | – |

## Project Structure

```
automation-exercise-tests
├── .claude/skills/run-and-heal
│   └── SKILL.md                          # Claude Code skill: run tests, fix failures, re-run
├── .mvn/wrapper
│   └── maven-wrapper.properties          # pins the Maven version used by mvnw
├── mvnw / mvnw.cmd                       # Maven Wrapper scripts (Unix / Windows)
├── Jenkinsfile                           # Jenkins pipeline: compile, API and UI stages, JUnit + Allure reports
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/qa/automation
    │   │   ├── api/clients
    │   │   │   ├── LoginApiClient.java       # REST Assured client for /verifyLogin
    │   │   │   └── ProductsApiClient.java    # REST Assured client for /productsList
    │   │   ├── components
    │   │   │   └── CookieBanner.java         # accepts the consent banner if shown
    │   │   ├── config
    │   │   │   ├── Config.java               # layered configuration loader
    │   │   │   └── Timeouts.java             # SHORT / MEDIUM / LONG wait durations
    │   │   ├── driver
    │   │   │   ├── DriverFactory.java        # creates the WebDriver instance
    │   │   │   └── DriverManager.java        # holds the current test's WebDriver
    │   │   ├── extensions
    │   │   │   └── ScreenshotOnFailureExtension.java # on UI failure: screenshot + HTML to target/screenshots and Allure
    │   │   └── pages
    │   │       ├── BasePage.java             # explicit-wait helpers: click, type, getText, isVisible, isNotVisible
    │   │       ├── HomePage.java
    │   │       ├── LoginPage.java
    │   │       ├── ProductsPage.java         # placeholder
    │   │       ├── ProductDetailsPage.java   # placeholder
    │   │       ├── CartPage.java             # placeholder
    │   │       └── CheckoutPage.java         # placeholder
    │   └── resources
    │       ├── config.properties             # shared, committed settings
    │       └── config-local.properties       # local credentials – git-ignored, not committed
    └── test
        ├── java/com/qa/automation
        │   ├── api
        │   │   ├── ProductsApiTests.java
        │   │   ├── BrandsApiTests.java       # placeholder
        │   │   └── LoginApiTests.java        # verifyLogin: valid, invalid, missing params, DELETE
        │   └── webui
        │       ├── BaseWebTests.java         # driver setup/teardown, opens app, accepts cookies
        │       └── LoginTests.java
        └── resources
            └── allure.properties             # writes Allure results to target/allure-results
```

## Current Test Coverage

| Layer | Test class | Scenario | Status |
| --- | --- | --- | --- |
| UI | `LoginTests` | Login with valid credentials shows "Logged in as" | Implemented |
| UI | `LoginTests` | Invalid credentials show "Your email or password is incorrect!" – unknown email, wrong password, both (parameterised) | Implemented |
| UI | `LoginTests` | Logout returns to the login page and hides "Logged in as" | Implemented |
| API | `ProductsApiTests` | `GET /api/productsList` returns HTTP 200, `responseCode` 200 and a non-empty product list | Implemented |
| API | `BrandsApiTests` | Brands list | Planned |
| API | `LoginApiTests` | `POST /api/verifyLogin` – valid credentials (`200`), unknown user (`404`), missing email / password / both (`400`, parameterised) | Implemented |
| API | `LoginApiTests` | `DELETE /api/verifyLogin` – method not supported (`405`) | Implemented |

## Framework Design

**Page Object Model.** Page classes hold locators and page actions, so tests read as business scenarios. Pages extend `BasePage`, which wraps every interaction in an explicit wait (`WebDriverWait` + `ExpectedConditions`); there are no `Thread.sleep` calls.

**Base test.** UI tests extend `BaseWebTests`, which before each test creates a fresh WebDriver, opens `web.base.url` and accepts the cookie banner, and after each test quits the driver. Every test gets its own browser session, so tests stay independent.

**Failure artifacts.** `BaseWebTests` registers `ScreenshotOnFailureExtension`. When a UI test fails, the extension saves a screenshot (`.png`) and the page HTML (`.html`) to `target/screenshots/` while the browser is still open. It also prints the current URL and both file paths to the console, and attaches the screenshot and HTML to the Allure report. Screenshots can show the test account's email address, so treat them as sensitive when sharing build artifacts.

**API clients.** HTTP calls live in client classes (`api/clients`), and tests only call client methods and assert on the result. Every request goes through the `AllureRestAssured` filter, so the Allure report shows each request and response.

**Note on the API under test.** Automation Exercise returns HTTP `200` even for error cases and puts the real status in the `responseCode` field of the body. The response is also served as `text/html`, so tests parse the body explicitly with `JsonPath.from(response.asString())` and assert on both the HTTP status and `responseCode`.

## Configuration

`Config` resolves each key in this order (first match wins):

1. JVM system property – `-Dweb.base.url=...`
2. Environment variable – key upper-cased with dots replaced by underscores, e.g. `TEST_USER_EMAIL`
3. `config-local.properties` – local, git-ignored file
4. `config.properties` – shared defaults committed to the repo

`config.properties`:

```properties
web.base.url=https://automationexercise.com
api.base.url=https://automationexercise.com/api
browser=chrome
# false: visible Chrome window (local runs); true: headless Chrome (Jenkins / CI)
remote=false
```

`remote` controls how Chrome runs. With `remote=false` (the default) the browser window opens, so you can watch every step. With `remote=true` Chrome runs headless at 1920×1080, with no window, as needed on Jenkins. Override it for one run with `-Dremote=true`, or with the `REMOTE` environment variable.

Optional timeout overrides (seconds): `timeout.short` (default 5), `timeout.medium` (10), `timeout.long` (30).

### Test credentials

The login test needs an existing Automation Exercise account. Credentials are never committed. Provide them in one of these ways.

Create `src/main/resources/config-local.properties`:

```properties
test.user.email=your-email@example.com
test.user.password=your-password
```

Or set environment variables (useful in CI):

```bash
export TEST_USER_EMAIL=your-email@example.com
export TEST_USER_PASSWORD=your-password
```

If a required value is missing, the test fails with a message naming the key and the ways to set it.

## Prerequisites

- Java 21 (JDK) with `JAVA_HOME` set or `java` on `PATH`
- No Maven installation needed: the Maven Wrapper downloads the pinned Maven version on first run
- Google Chrome (Selenium Manager downloads the matching ChromeDriver automatically)
- An Automation Exercise account for UI login tests

## Running Tests

All commands use the Maven Wrapper. The examples use `./mvnw` (Linux, macOS, Git Bash). On Windows PowerShell or cmd use `.\mvnw.cmd` instead.

Run the whole suite:

```bash
./mvnw clean test
```

Compile only, without running tests:

```bash
./mvnw test-compile
```

Run a single test class or method:

```bash
./mvnw test -Dtest=LoginTests
./mvnw test -Dtest=ProductsApiTests#shouldReturnProductsSuccessfully
```

Run only API tests (no browser needed):

```bash
./mvnw test -Dtest="com.qa.automation.api.**"
```

Pass credentials on the command line instead of a local file:

```bash
./mvnw test -Dtest=LoginTests -Dtest.user.email=you@example.com -Dtest.user.password=secret
```

In PowerShell, wrap each `-D` argument in quotes, otherwise the dots in the property names break the arguments:

```powershell
.\mvnw.cmd test "-Dtest=LoginTests" "-Dtest.user.email=you@example.com" "-Dtest.user.password=secret"
```

Tests can also be run from IntelliJ IDEA with the JUnit runner.

## Allure Report

Every test run writes [Allure](https://allurereport.org) results to `target/allure-results`. Allure Report is free and open source. Results include API requests and responses, plus the screenshot and page HTML of failed UI tests.

Build the HTML report after a test run:

```bash
./mvnw clean test
./mvnw allure:serve     # builds the report in a temp folder and opens it in the browser
./mvnw allure:report    # writes the report to target/site/allure-maven-plugin/index.html
```

On the first run, `allure-maven` downloads a private Node.js runtime and Allure 3 into `.allure/` (git-ignored). No global Node.js or Allure installation is needed. Use `allure:serve` to view the report locally: opening `index.html` straight from disk may show an empty page in some browsers.

## Jenkins

The `Jenkinsfile` in the repository root runs the suite with `REMOTE=true` (headless Chrome) in three stages: **Compile**, **API tests** and **UI tests**. Each test stage publishes its own JUnit results, and a failed test marks that stage and the build **UNSTABLE** without stopping the pipeline. At the end the pipeline publishes one Allure report for all tests and archives the failure screenshots. Install the **Pipeline Graph View** plugin to watch the stages live (*Pipeline Overview* on the build page). The file works on Windows (`mvnw.cmd`) and Linux (`./mvnw`) agents.

One-time Jenkins setup:

1. Install Jenkins (Windows installer from [jenkins.io/download](https://www.jenkins.io/download/), LTS) and open `http://localhost:8080`. Choose *Install suggested plugins*.
2. *Manage Jenkins → Plugins*: install **Allure Jenkins Plugin**.
3. *Manage Jenkins → Tools → Allure Commandline*: add an installation with *Install automatically*.
4. *Manage Jenkins → Credentials*: add two *Secret text* credentials with IDs `test-user-email` and `test-user-password`.
5. *New Item → Pipeline*. In *Pipeline*, choose *Pipeline script from SCM → Git*, set the repository URL, branch `*/develop` and script path `Jenkinsfile`.
6. *Build Now*. Each build then shows **Allure Report** and **Test Result** links.

Chrome and JDK 21 must be installed on the Jenkins machine. The Jenkins Windows service has no desktop, which is why the pipeline runs Chrome headless.

## Agent-Assisted Run and Heal

The repository includes a [Claude Code](https://claude.com/claude-code) skill in `.claude/skills/run-and-heal/`. It runs the tests, investigates every failure against the live application, fixes broken test code, and re-runs to confirm the fix:

```
/run-and-heal                  # whole suite
/run-and-heal LoginTests       # one class (any surefire -Dtest filter)
```

The agent fixes framework problems: changed locators, missing waits, test bugs and compile errors. It does not weaken assertions, disable tests or touch credentials. When the application itself behaves differently, or the cause is test data or the environment, it reports the problem with evidence instead of changing the test. It never commits.

## Code Style

Java sources follow [Google Java Style](https://google.github.io/styleguide/javaguide.html): 2-space indentation, a 100-character line limit, and static imports before regular imports. Formatting is done with [google-java-format](https://github.com/google/google-java-format). In IntelliJ IDEA, install the google-java-format plugin, enable it in *Settings → google-java-format Settings*, and run *Code → Reformat Code* before committing.

## Roadmap

**Done**

- [x] Maven project with Java 21, Selenium, JUnit 5, REST Assured, AssertJ
- [x] Maven Wrapper, so no local Maven installation is needed
- [x] Consistent code style (google-java-format)
- [x] Layered configuration with local, git-ignored secrets
- [x] Configurable timeouts
- [x] `BasePage` with explicit-wait helpers
- [x] `BaseWebTests` with per-test driver lifecycle
- [x] Cookie banner handling
- [x] UI: successful login
- [x] UI: login with invalid credentials (parameterised) and logout
- [x] Screenshot and page HTML on UI test failure
- [x] Headless Chrome switch (`remote`) for Jenkins / CI
- [x] Jenkins pipeline (`Jenkinsfile`) with JUnit and Allure reports
- [x] API: products list
- [x] API: `verifyLogin` positive and negative cases
- [x] Allure reporting with API request/response logs and failure screenshots

**Next**

- [ ] UI: registration and registration with an existing email (generated test data)
- [ ] UI: products, search, categories and brands
- [ ] UI: cart and end-to-end checkout
- [ ] API: brands, product search
- [ ] API: full account lifecycle – create, get, update, delete
- [ ] Hybrid tests: create the user via API, verify in UI, clean up via API
- [ ] Browser selection from the `browser` setting in `DriverFactory`
- [ ] JUnit tags (`smoke`, `regression`) and parallel execution
- [ ] GitHub Actions pipeline

## Author

Ewa Kogut

Part of a test automation portfolio demonstrating UI and API automation with Java.
