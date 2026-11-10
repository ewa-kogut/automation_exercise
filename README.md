# Automation Exercise – Test Automation Framework

[![Tests](https://github.com/ewa-kogut/automation_exercise/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/ewa-kogut/automation_exercise/actions/workflows/tests.yml)

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
| Retrying API cleanup | Awaitility | 4.3.0 |
| Reporting | Allure Report (allure-jupiter, allure-rest-assured, allure-maven) | 2.35.5 / plugin 3.1.0 |
| Reporting | ReportPortal JUnit 5 agent (agent-java-junit5), optional | 5.5.8 |
| Design pattern | Page Object Model | – |
| Code style | google-java-format | – |

## Project Structure

```
automation-exercise-tests
├── .claude/skills                        # Claude Code skills
│   ├── run-and-heal/SKILL.md             # run tests, fix failures, re-run
│   ├── new-ui-test/SKILL.md              # automate a UI scenario from the site
│   ├── new-api-test/SKILL.md             # automate an API scenario
│   └── check-secrets/SKILL.md            # look for credentials before a commit
├── .github/workflows
│   └── tests.yml                         # GitHub Actions: runs all tests on every push
├── .mvn/wrapper
│   └── maven-wrapper.properties          # pins the Maven version used by mvnw
├── docker
│   ├── reportportal/docker-compose.yml   # local ReportPortal (test run reporting)
│   └── selenium-grid/docker-compose.yml  # local Selenium Grid: Chrome, Edge, Firefox + noVNC
├── docs
│   └── test-cases.md                     # test case descriptions, linked to the test IDs
├── mvnw / mvnw.cmd                       # Maven Wrapper scripts (Unix / Windows)
├── Jenkinsfile                           # Jenkins pipeline: compile, API and UI stages, JUnit + Allure reports
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/qa/automation
    │   │   ├── api/clients
    │   │   │   ├── AccountApiClient.java     # REST Assured client for account create, get, update, delete
    │   │   │   ├── BaseApiClient.java        # base URL and Allure filter for all API clients
    │   │   │   ├── BrandsApiClient.java      # REST Assured client for /brandsList
    │   │   │   ├── LoginApiClient.java       # REST Assured client for /verifyLogin
    │   │   │   └── ProductsApiClient.java    # REST Assured client for /productsList, /searchProduct
    │   │   ├── components
    │   │   │   ├── CookieBanner.java         # accepts the consent banner if shown
    │   │   │   └── SubscriptionFooter.java   # subscription form in the footer of every page
    │   │   ├── config
    │   │   │   ├── Config.java               # layered configuration loader
    │   │   │   └── Timeouts.java             # SHORT / MEDIUM / LONG wait durations
    │   │   ├── driver
    │   │   │   ├── DriverFactory.java        # creates Chrome, Edge, Firefox or mobile Chrome, locally or on Grid; blocks ads
    │   │   │   └── DriverManager.java        # holds the current test's WebDriver
    │   │   ├── extensions
    │   │   │   ├── ConsoleProgressExtension.java # prints each test's start, result, time and error to the console
    │   │   │   └── ScreenshotOnFailureExtension.java # on UI failure: screenshot + HTML to target/screenshots, Allure, ReportPortal
    │   │   └── pages
    │   │       ├── BasePage.java             # explicit-wait helpers: click, type, getText, isVisible, isNotVisible
    │   │       ├── HomePage.java
    │   │       ├── LoginPage.java
    │   │       ├── SignupPage.java           # account details form, "Account Created!", "Account Deleted!"
    │   │       ├── ProductsPage.java         # All Products page: title, list, first product, search, add to cart
    │   │       ├── ProductDetailsPage.java   # product name, category, price, availability, condition, brand
    │   │       ├── CartPage.java             # cart table: names, prices, quantities, totals
    │   │       ├── CheckoutPage.java         # addresses, order review, comment, place order
    │   │       └── PaymentPage.java          # payment form and the "Order Placed!" page
    │   └── resources
    │       ├── config.properties             # shared, committed settings
    │       └── config-local.properties       # local credentials – git-ignored, not committed
    └── test
        ├── java/com/qa/automation
        │   ├── api
        │   │   ├── AccountApiTests.java      # account lifecycle: create, delete, update, get by email
        │   │   ├── ApiAssertions.java        # shared check of HTTP status, responseCode, message
        │   │   ├── ProductsApiTests.java     # productsList: GET, POST not supported; searchProduct: match, missing param
        │   │   ├── BrandsApiTests.java       # brandsList: GET list, PUT not supported
        │   │   ├── LoginApiTests.java        # verifyLogin: valid, invalid, missing params, DELETE
        │   │   └── TestAccountCleanup.java   # deletes accounts that tests created, also used by UI tests
        │   └── webui
        │       ├── WebTestBase.java          # driver setup/teardown, opens app, accepts cookies
        │       ├── CartTests.java            # adding products to the cart
        │       ├── CheckoutTests.java        # end-to-end purchase, registering during checkout
        │       ├── LoginTests.java
        │       ├── ProductsTests.java        # product list, product details, search
        │       ├── RegistrationTests.java    # registering a new user and deleting the account
        │       └── SubscriptionTests.java    # subscribing from the home and cart pages
        └── resources
            ├── META-INF/services/...Extension # registers console progress and the ReportPortal JUnit 5 agent
            ├── allure.properties             # writes Allure results to target/allure-results
            ├── junit-platform.properties     # turns on JUnit extension auto-detection
            ├── reportportal.properties       # ReportPortal endpoint, project, launch; off by default
            └── simplelogger.properties       # SLF4J: show only warnings and errors
```

## Current Test Coverage

Each test has a unique ID, a JUnit `@Tag` that matches the scenario number on the site (extra cases that are not on the site continue after its last scenario, from `TC-27`): `TC-nn` for [UI test cases](https://automationexercise.com/test_cases), `API-nn` for the [API list](https://automationexercise.com/api_list). Use IDs to run selected tests (see [Running Tests](#running-tests) and [Jenkins](#jenkins)). Steps, expected results and differences from the site's scenarios are described in [docs/test-cases.md](docs/test-cases.md).

| ID | Layer | Test class | Scenario | Status |
| --- | --- | --- | --- | --- |
| `TC-01` | UI | `RegistrationTests` | Registers a new user with generated data, checks "Logged in as", then deletes the account | Implemented |
| `TC-02` | UI | `LoginTests` | Login with valid credentials shows "Logged in as" | Implemented |
| `TC-03` | UI | `LoginTests` | Invalid credentials show "Your email or password is incorrect!" – unknown email, wrong password, both (parameterised) | Implemented |
| `TC-04` | UI | `LoginTests` | Logout returns to the login page and hides "Logged in as" | Implemented |
| `TC-05` | UI | `LoginTests` | Signing up with an existing email shows "Email Address already exist!" | Implemented |
| `TC-08` | UI | `ProductsTests` | All Products page shows the list; the first product's page shows its name, category, price, availability, condition and brand | Implemented |
| `TC-09` | UI | `ProductsTests` | Searching shows "Searched Products", and every result's name contains the search term | Implemented |
| `TC-10` | UI | `SubscriptionTests` | Subscribing from the home page footer shows the success message | Implemented |
| `TC-11` | UI | `SubscriptionTests` | Subscribing from the cart page footer shows the success message | Implemented |
| `TC-12` | UI | `CartTests` | Two added products are in the cart with the right name, price, quantity and total | Implemented |
| `TC-13` | UI | `CartTests` | Product quantity in the cart | Planned (skipped) |
| `TC-14` | UI | `CheckoutTests` | End-to-end purchase: a guest fills the cart, registers during checkout, reviews the address and order, pays and sees the confirmation | Implemented |
| `TC-27` | UI | `ProductsTests` | Negative case, not on the site's list: searching for an unknown term shows "Searched Products" and no products | Implemented |
| `API-01` | API | `ProductsApiTests` | `GET /api/productsList` returns HTTP 200, `responseCode` 200 and a non-empty product list | Implemented |
| `API-02` | API | `ProductsApiTests` | `POST /api/productsList` – method not supported (`405`) | Implemented |
| `API-03` | API | `BrandsApiTests` | `GET /api/brandsList` returns HTTP 200, `responseCode` 200 and a non-empty brand list | Implemented |
| `API-04` | API | `BrandsApiTests` | `PUT /api/brandsList` – method not supported (`405`) | Implemented |
| `API-05` | API | `ProductsApiTests` | `POST /api/searchProduct` – every returned product name contains the search term (`200`) | Implemented |
| `API-06` | API | `ProductsApiTests` | `POST /api/searchProduct` without `search_product` (`400`) | Implemented |
| `API-07` | API | `LoginApiTests` | `POST /api/verifyLogin` – valid credentials (`200`) | Implemented |
| `API-08` | API | `LoginApiTests` | `POST /api/verifyLogin` – missing email / password / both (`400`, parameterised) | Implemented |
| `API-09` | API | `LoginApiTests` | `DELETE /api/verifyLogin` – method not supported (`405`) | Implemented |
| `API-10` | API | `LoginApiTests` | `POST /api/verifyLogin` – unknown user (`404`) | Implemented |
| `API-11` | API | `AccountApiTests` | `POST /api/createAccount` – creates a user (`201`) | Implemented |
| `API-12` | API | `AccountApiTests` | `DELETE /api/deleteAccount` – deletes the user, who can no longer be found (`200`) | Implemented |
| `API-13` | API | `AccountApiTests` | `PUT /api/updateAccount` – updates the user, the change is saved (`200`) | Implemented |
| `API-14` | API | `AccountApiTests` | `GET /api/getUserDetailByEmail` – returns the user's details (`200`) | Implemented |

## Framework Design

**Page Object Model.** Page classes hold locators and page actions, so tests read as business scenarios. Pages extend `BasePage`, which wraps every interaction in an explicit wait (`WebDriverWait` + `ExpectedConditions`); there are no `Thread.sleep` calls.

**Base test.** UI tests extend `WebTestBase`, which before each test creates a fresh WebDriver, opens `web.base.url` and accepts the cookie banner (only when ads are not blocked, see `block.ads`), and after each test quits the driver. Every test gets its own browser session, so tests stay independent.

**Failure artifacts.** `WebTestBase` registers `ScreenshotOnFailureExtension`. When a UI test fails, the extension saves a screenshot (`.png`) and the page HTML (`.html`) to `target/screenshots/` while the browser is still open. It also prints the current URL and both file paths to the console, and attaches the screenshot and HTML to the Allure report. Screenshots can show the test account's email address, so treat them as sensitive when sharing build artifacts.

**API clients.** HTTP calls live in client classes (`api/clients`), and tests only call client methods and assert on the result. Clients extend `BaseApiClient`, which reads `api.base.url` and starts every request with `request()`: the base URL plus the `AllureRestAssured` filter, so the Allure report shows each request and response. A new client only extends `BaseApiClient` and adds its endpoint methods, e.g. `request().when().get("/brandsList")`. Tests create clients with `new BrandsApiClient()`.

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
# chrome, edge, firefox or chrome-mobile (Chrome emulating a phone screen)
browser=chrome
# false: visible browser window (local runs); true: headless browser (Jenkins / CI)
remote=false
# Empty: the browser starts on this machine. Set, e.g. http://localhost:4444: on Selenium Grid.
grid.url=
# true: the browser cannot reach ad networks, whose full-screen ads cover the page and block clicks
block.ads=true
```

`browser` chooses the browser for UI tests: `chrome` (default), `edge`, `firefox` or `chrome-mobile`. Selenium Manager downloads the matching driver, and also Firefox itself if it is not installed. Example: `./mvnw -B test -Dbrowser=edge`.

`chrome-mobile` is Chrome emulating a phone: a Pixel 7 screen (412×915), touch and an Android user agent, so the site serves its mobile layout. It checks how pages look and work on a small screen, but it is still desktop Chrome, not a real phone browser. Safari (iPhone) cannot be tested here, because it runs only on macOS.

`remote` controls how a local browser runs. With `remote=false` (the default) the browser window opens, so you can watch every step. With `remote=true` the browser runs headless at 1920×1080, with no window, as needed on Jenkins. Override it for one run with `-Dremote=true`, or with the `REMOTE` environment variable.

`block.ads` (default `true`) stops the browser from reaching the site's ad networks (`googlesyndication.com`, `doubleclick.net`, `googleadservices.com`, `adtrafficquality.google`). Their full-screen ads (`#google_vignette` in the URL) cover the page and swallow clicks, which made tests fail at random, most often on Grid and in `chrome-mobile`. Chrome and Edge resolve these domains to "not found"; Firefox gets a proxy auto-config (PAC) script that sends them to a closed local port. Both work locally and on Grid. The Google consent (cookie) banner is loaded by the same script, so with blocked ads it does not appear and `WebTestBase` skips it. Use `-Dblock.ads=false` to see the site as a real visitor does.

`threads` (Maven property, default `1`) sets how many tests run at the same time, e.g. `./mvnw -B test -Dthreads=3`. JUnit runs classes and methods in parallel, and each test still gets its own browser: `DriverManager` keeps one WebDriver per thread in a `ThreadLocal`. The setting lives in the Surefire configuration in `pom.xml`, so runs from IntelliJ IDEA stay sequential. On Selenium Grid each browser node takes up to 3 tests at once.

`reruns` (Maven property, default `0`) runs a failed test again, e.g. `./mvnw -B test -Dreruns=1`. A test that passes on a rerun is reported as **flaky** (Surefire, JUnit in Jenkins and Allure all show it), so a short outage of the site does not hide a real problem. Automation Exercise is a shared demo site and sometimes answers HTTP 503 or "This website is under heavy load (queue full)"; Jenkins reruns once by default. Locally reruns are off, so a failing test fails straight away while you work on it.

`grid.url` sends UI tests to Selenium Grid (see *Local Tools (Docker)*), e.g. `./mvnw -B test -Dgrid.url=http://localhost:4444 -Dbrowser=firefox`. Grid browsers ignore `remote`: they always have a 1920×1080 screen, which you can watch live with noVNC.

Every test prints a line to the console when it starts and when it ends, with its ID, name and time. A failed test also prints the error and the line in the test code where it failed:

```
[TEST] START   [TC-02] LoginTests > Should login successfully with valid credentials
[TEST] FAILED  [TC-02] LoginTests > Should login successfully with valid credentials (12.6 s)
    org.opentest4j.AssertionFailedError: [User should be logged in after submitting valid credentials]
    at com.qa.automation.webui.LoginTests.shouldLoginSuccessfully(LoginTests.java:39)
```

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
- Google Chrome or Microsoft Edge (Selenium Manager downloads the matching driver automatically); Firefox is downloaded when needed
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

Run tests by ID (comma-separated, see [Current Test Coverage](#current-test-coverage)):

```bash
./mvnw test -Dgroups=TC-04
./mvnw test -Dgroups=TC-02,TC-04,API-07
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

## ReportPortal

Test results can also be sent to [ReportPortal](https://reportportal.io), which is free and open source. It keeps the history of launches, groups failures by cause and shows the screenshot and page HTML of failed UI tests. Start it locally first (see *Local Tools (Docker)*).

Sending is **off by default** (`rp.enable=false` in `src/test/resources/reportportal.properties`), so a run without ReportPortal works as before. To turn it on:

1. In ReportPortal (http://localhost:8090) open *Profile → API Keys* and generate a key.
2. Set two environment variables for your Windows account (*Start → "Edit environment variables for your account"*), then restart IntelliJ IDEA or the terminal:
   - `RP_ENABLE` = `true`
   - `RP_API_KEY` = the generated key
3. Run the tests as usual. Each Maven run creates one launch named `automation-exercise` in the `superadmin_personal` project.

Every setting in `reportportal.properties` can be overridden with `-D<key>` or an environment variable (`rp.project` → `RP_PROJECT`). The API key is a secret: keep it in the environment variable, never in a file in the repository. With `RP_ENABLE=true` but no key, the run stops at once with `Neither OAuth 2.0 nor API key authentication is configured`. The agent's anonymous usage statistics are turned off in `pom.xml` (`AGENT_NO_ANALYTICS`).

In Jenkins, add the key once as a *Secret text* credential with the ID `rp-api-key`, then tick **REPORT_PORTAL** in *Build with Parameters*. Each test stage creates its own launch with the attributes `build:<number>` and `stage:api` or `stage:ui`.

## Jenkins

The `Jenkinsfile` in the repository root runs the suite with `REMOTE=true` (headless browser) in three stages: **Compile**, **API tests** and **UI tests**. Each test stage publishes its own JUnit results, and a failed test marks that stage and the build **UNSTABLE** without stopping the pipeline. At the end the pipeline publishes one Allure report for all tests and archives the failure screenshots. Install the **Pipeline Graph View** plugin to watch the stages live (*Pipeline Overview* on the build page). The file works on Windows (`mvnw.cmd`) and Linux (`./mvnw`) agents.

One-time Jenkins setup:

1. Install Jenkins (Windows installer from [jenkins.io/download](https://www.jenkins.io/download/), LTS) and open `http://localhost:8080`. Choose *Install suggested plugins*.
2. *Manage Jenkins → Plugins*: install **Allure Jenkins Plugin** and **Plot**.
3. *Manage Jenkins → Tools → Allure Commandline*: add an installation with *Install automatically*.
4. *Manage Jenkins → Credentials*: add two *Secret text* credentials with IDs `test-user-email` and `test-user-password`.
5. *New Item → Pipeline*. In *Pipeline*, choose *Pipeline script from SCM → Git*, set the repository URL, branch `*/develop` and script path `Jenkinsfile`.
6. *Build with Parameters* (*Build Now* before the first build). Each build then shows **Allure Report** and **Test Result** links.

The **TEST_IDS** parameter chooses the tests. The default `ALL` (also `All` or `all`) runs every test, and an empty value does the same. To run selected tests, enter their IDs separated by commas, e.g. `TC-02, TC-04, API-07`. Case and spaces do not matter. A stage with none of the selected tests runs nothing and stays green. Jenkins reads the parameter definitions during a build, so a new parameter appears only after the first build that uses the changed `Jenkinsfile`.

Other parameters:

| Parameter | Values | What it does |
| --- | --- | --- |
| **SUITE** | `ALL`, `API`, `UI` | Which test stages run; the other stage is skipped. `TEST_IDS` still filters inside them. |
| **THREADS** | `1`–`4` | How many tests run at the same time (`-Dthreads`). |
| **RERUN_FAILED** | `1` (default), `0`, `2` | How many times a failed test runs again (`-Dreruns`); a test that passes on a rerun is reported as flaky. |
| **BROWSER** | `chrome`, `edge`, `firefox`, `chrome-mobile` | Browser for the UI tests; `chrome-mobile` emulates a phone screen. |
| **GRID** | on / off | Runs the UI tests on Selenium Grid at `http://localhost:4444` (start it first). Watch them live at http://localhost:7900 (Chrome), 7901 (Edge) or 7902 (Firefox). |
| **REPORT_PORTAL** | on / off | Also sends results to ReportPortal (see *ReportPortal*). |
| **DASHBOARD** | on / off | Also sends results to [Live Test Dashboard](#live-test-dashboard) and adds a link to the build. Starts the dashboard server if it is not running. |
| **DASHBOARD_JAR** | file path | Where the dashboard jar is on the Jenkins machine. Used only with **DASHBOARD**. |

**Charts.** After a full run (`TEST_IDS` = `ALL`) the [Plot](https://plugins.jenkins.io/plot/) plugin adds a point to three charts on the job page (*Plots* in the side menu): passed, failed and skipped **API tests**, the same for **UI tests**, and the **stage duration** in seconds. The pipeline writes the values to small CSV files in `target/plot/` (first line: series names, second line: this build's values). Runs with selected test IDs add no point, so every point counts the same tests. A stage skipped by SUITE adds no point to its series. Durations depend on THREADS, BROWSER and GRID, so compare builds with the same options. The charts show the whole history: `keepRecords: true` keeps the points of builds that Jenkins deleted (the job keeps only the last 20 builds), and there is no `numBuilds` limit. Without `keepRecords`, Plot deletes those points from its data file for good. The charts are 750×450 pixels; Plot has no setting for their size.

> **Note: changing series or colours resets the chart.** Plot has no colour setting: it colours series by their order (1st red, 2nd green, 3rd blue), which is why the CSV columns are `Failed,Passed,Skipped`. The order is fixed by the data Plot has already stored in the job folder (`csvFileName`, e.g. `plot-api-test-counts.csv`). To change the order or the series, you must also give the plot a new `csvFileName` in the `Jenkinsfile`, and the chart then starts again from an empty history. The old file stays in the job folder, unused. Change the series only when losing the chart history is acceptable.

The build history shows the chosen options next to each build number, e.g. `UI | firefox on Grid | 3x | rerun 1 | ALL`. The *Console Output* lists every test with its result, as described in *Configuration*.

JDK 21 must be installed on the Jenkins machine, and Chrome or Edge for local runs. The Jenkins Windows service has no desktop, so the pipeline runs a local browser headless; on Grid the browser has its own screen.

## GitHub Actions

`.github/workflows/tests.yml` runs all tests in headless Chrome on every push to `main` or `develop`. To start it by hand, open the **Actions** tab, choose **Tests** and click **Run workflow**. The badge at the top of this README shows the result of the latest run on `main`.

The tests that log in need the test account, so the repository has two secrets under *Settings → Secrets and variables → Actions*: `TEST_USER_EMAIL` and `TEST_USER_PASSWORD`. GitHub hides their values in the logs.

The workflow does not upload reports or screenshots, because screenshots can show the test account's email and the files of a public repository's runs can be downloaded by anyone with a GitHub account.

The tests use the live demo site, so a red badge can also mean that the site was overloaded or blocked GitHub's servers. Check the log before you treat it as a bug.

## Local Tools (Docker)

The `docker/` folder holds Docker Compose files for two free, self-hosted tools. Both are based on the official files, with host ports changed so they do not clash with Jenkins (port 8080). They need [Docker Desktop](https://www.docker.com/products/docker-desktop/) to be running.

| Tool | Purpose | Start (from the repository root) | URL |
| --- | --- | --- | --- |
| [ReportPortal](https://reportportal.io) | test run reports and failure analysis | `docker compose -f docker/reportportal/docker-compose.yml -p reportportal up -d` | http://localhost:8090 |
| [Selenium Grid](https://www.selenium.dev/documentation/grid/) | Chrome, Edge and Firefox in containers, with a live view | `docker compose -f docker/selenium-grid/docker-compose.yml -p selenium-grid up -d` | http://localhost:4444 |

- ReportPortal: the first start downloads several GB of images and takes a few minutes. Log in as `superadmin` with the initial password from the compose file and change it right away.
- Selenium Grid: each browser node runs up to 3 tests at once (`SE_NODE_MAX_SESSIONS`). To watch a test, open http://localhost:7900 (Chrome), 7901 (Edge) or 7902 (Firefox) and click *Connect*, or click the camera icon next to a session at http://localhost:4444. The ports are open only on this computer, so there is no VNC password. Safari is not available: it runs only on macOS.
- Stop with the same command, replacing `up -d` with `stop`; data is kept in Docker volumes. Passwords can be overridden in a git-ignored `.env` file next to each compose file.

## Claude Code Skills

The repository includes [Claude Code](https://claude.com/claude-code) skills in `.claude/skills/`. They write the project's rules down once, so the AI agent follows them every time instead of being reminded.

| Skill | What it does |
| --- | --- |
| `/run-and-heal [filter]` | Runs the tests, finds the cause of each failure, fixes broken test code and runs again |
| `/new-ui-test <number>` | Automates a scenario from the site: checks the live pages, writes page objects and the test with cleanup, runs it three times and updates the documentation |
| `/new-api-test <number or description>` | Automates an API from the site or an extra case: checks the real response with `curl`, writes the client method and the test with cleanup, runs it three times and updates the documentation |
| `/check-secrets [reports]` | Before a commit or push, looks for passwords, keys, tokens, real email addresses and personal paths, and reports them with masked values. It changes nothing |

None of the skills commits, pushes or switches branches.

### Run and heal

`/run-and-heal` runs the tests, investigates every failure against the live application, fixes broken test code, and re-runs to confirm the fix:

```
/run-and-heal                  # whole suite
/run-and-heal LoginTests       # one class (any surefire -Dtest filter)
```

The agent fixes framework problems: changed locators, missing waits, test bugs and compile errors. It does not weaken assertions, disable tests or touch credentials. When the application itself behaves differently, or the cause is test data or the environment, it reports the problem with evidence instead of changing the test. It never commits.

## Code Style

Java sources follow [Google Java Style](https://google.github.io/styleguide/javaguide.html): 2-space indentation, a 100-character line limit, and static imports before regular imports. Formatting is done with [google-java-format](https://github.com/google/google-java-format). In IntelliJ IDEA, install the google-java-format plugin, enable it in *Settings → google-java-format Settings*, and run *Code → Reformat Code* before committing.

## Live Test Dashboard

[Live Test Dashboard](https://github.com/ewa-kogut/live-test-dashboard) is a separate tool that shows a run live in the browser, and keeps every run with its own link. This project needs no code for it: the jar is added to the test run only when you want it.

```powershell
.\mvnw -B test "-Dmaven.test.additionalClasspath=C:\path\to\live-test-dashboard.jar"
```

In Jenkins, tick **DASHBOARD** in *Build with Parameters*, and open the **Dashboard** link in the build's description or Console Output. If nothing answers at http://localhost:8095, the build starts the server itself (`java -jar` with **DASHBOARD_JAR**), and the server keeps running after the build, so the link keeps working. Both test stages appear together as one run. If the jar is missing or the server does not start, the build runs normally without it.

The link in the description is clickable when *Manage Jenkins → Security → Markup Formatter* is set to **Safe HTML**. With **Plain text**, use the link in the Console Output.

## Roadmap

**Done**

- [x] Maven project with Java 21, Selenium, JUnit 5, REST Assured, AssertJ
- [x] Maven Wrapper, so no local Maven installation is needed
- [x] Consistent code style (google-java-format)
- [x] Layered configuration with local, git-ignored secrets
- [x] Configurable timeouts
- [x] `BasePage` with explicit-wait helpers
- [x] `WebTestBase` with per-test driver lifecycle
- [x] Cookie banner handling
- [x] UI: registration with generated test data, cleaned up through the API
- [x] UI: successful login
- [x] UI: login with invalid credentials (parameterised) and logout
- [x] UI: product list, product details and product search
- [x] UI: adding products to the cart
- [x] UI: end-to-end checkout, registering during checkout
- [x] UI: subscription from the home and cart pages
- [x] UI: negative cases – signup with an existing email, search with no results
- [x] Screenshot and page HTML on UI test failure
- [x] Headless browser switch (`remote`) for Jenkins / CI
- [x] Browser selection (`browser`: Chrome, Edge, Firefox, mobile emulation in Chrome)
- [x] Selenium Grid in Docker with a live view (noVNC), `grid.url` setting
- [x] Jenkins parameters: SUITE, BROWSER, GRID, REPORT_PORTAL
- [x] Test progress and errors in the console (Jenkins Console Output)
- [x] Parallel execution (`-Dthreads`, Jenkins THREADS parameter)
- [x] Jenkins charts with the Plot plugin: test counts and stage duration per build
- [x] Test case descriptions linked to the test IDs (`docs/test-cases.md`)
- [x] Ad blocking (`block.ads`) against full-screen ads that swallow clicks
- [x] Jenkins pipeline (`Jenkinsfile`) with JUnit and Allure reports
- [x] GitHub Actions pipeline with a status badge
- [x] API: products list (GET) and unsupported POST
- [x] API: `verifyLogin` positive and negative cases
- [x] API: brands list (GET) and unsupported PUT
- [x] API: product search
- [x] API: full account lifecycle – create, get, update, delete
- [x] Rerun of failed tests (`-Dreruns`, Jenkins RERUN_FAILED), reported as flaky
- [x] Allure reporting with API request/response logs and failure screenshots
- [x] Docker Compose file for local ReportPortal
- [x] Optional ReportPortal reporting (JUnit 5 agent) with failure screenshots

**Next**

- [ ] UI: categories and brands
- [ ] Hybrid tests: create the user via API, verify in UI, clean up via API
- [ ] JUnit tags (`smoke`, `regression`)
- [ ] ReportPortal: REST Assured request/response logs

## Author

Ewa Kogut

Part of a test automation portfolio demonstrating UI and API automation with Java.
