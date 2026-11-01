---
name: run-and-heal
description: Run the test suite (or a subset), investigate every failure, fix broken test code (locators, waits, outdated assertions, compile errors), re-run to confirm, and report. Use when the user asks to run tests and fix failures, "run and heal", or "check why tests fail".
argument-hint: "[surefire -Dtest filter, e.g. LoginTests or com.qa.automation.api.**]"
allowed-tools: Bash(./mvnw:*), Bash(curl -s https\://automationexercise.com:*), Read, Grep, Glob, Edit
---

# Run and heal

Run the tests, find out why each failure happens, fix what is broken **in the test framework**, and prove the fix by re-running. A real defect in the application under test is reported, never hidden.

## 1. Run

- Filter from the arguments: `$ARGUMENTS`. If empty, run the whole suite.
- Command (Git Bash): `./mvnw -B test` or `./mvnw -B test -Dtest="<filter>"`.
  Add `-Dsurefire.failIfNoSpecifiedTests=false` when the filter is a package pattern.
- Read the results from `target/surefire-reports/`:
  - `*.txt` – summary per class (`Tests run / Failures / Errors`).
  - `TEST-*.xml` – the failure message and stack trace for each test case.
- All green → report the counts and stop.

## 2. Investigate each failure

For every failed test, before touching any code:

1. Read the assertion message and stack trace, then the test and the page object or API client it uses.
   For a UI failure, open the artifacts in `target/screenshots/` (the console output lists their paths):
   - the `.png` screenshot, viewed with Read, shows what the browser displayed, including ads or overlays;
   - the `.html` page source shows the exact DOM at the moment of failure, so you can grep it for the locator.
2. Re-run that single test once (`-Dtest=Class#method`). If it passes now, mark it **flaky** and still look for the cause (usually a missing wait).
3. Check the live application. Do not guess:
   - UI: fetch the page HTML with `curl -s https://automationexercise.com/<path>` and confirm whether the locator still matches.
   - API: call the endpoint with `curl -s -X <METHOD> -d "<form params>" https://automationexercise.com/api/<endpoint>` and compare with the expected `responseCode` and `message`.
4. Put the failure into one category:

| Category | Typical signal | Action |
| --- | --- | --- |
| Compile error | build fails before tests run | Fix the code |
| Locator changed | `NoSuchElementException` / `TimeoutException` waiting for an element; the live HTML has a different attribute | Update the locator in the page object |
| Timing / flaky | passes on re-run; element exists but was not ready | Use or add a `BasePage` wait helper |
| Test bug | wrong expected value in the test, wrong order of steps, a typo | Fix the test |
| Application behaviour changed | live response or page really differs from the expectation | **Do not edit the assertion.** Report it as a possible app bug, with evidence |
| Test data / config | missing `test.user.*`, the test account no longer exists, wrong password | **Do not fix.** Tell the user what to set |
| Environment | Chrome or driver errors, network errors, site down, HTTP 5xx | **Do not fix.** Report it and suggest re-running later |

## 3. Heal

Allowed:
- Change locators, page object methods, API clients, waits, and test code.
- Fix compile errors.
- Update an expected value **only** when the live app and the [API docs](https://automationexercise.com/api_list) or [test cases](https://automationexercise.com/test_cases) agree that the new behaviour is intended.

Never:
- Delete or weaken an assertion so that it passes, or add `@Disabled` / `assumeTrue`.
- Catch exceptions just to hide a failure.
- Add `Thread.sleep`, or raise timeouts without evidence that the page is simply slower.
- Edit `config-local.properties` or put credentials in code.
- Commit, push, or change git branches.

Every change follows the project conventions in `AGENTS.md`: code style, AAA sections, naming, page objects and API tests.

## 4. Verify

- Re-run the healed tests. Then re-run the whole originally requested scope, to check that nothing else broke.
- At most **3 heal iterations**. If a test still fails, stop and report what you found and what you tried.

## 5. Report

Finish with a short table:

| Test | Category | Root cause (evidence) | Change made | Result after re-run |
| --- | --- | --- | --- | --- |

Then list the changed files, and separately anything the user has to act on: app bugs, data or config, environment.
