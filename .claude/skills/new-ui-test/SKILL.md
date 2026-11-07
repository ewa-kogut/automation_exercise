---
name: new-ui-test
description: Implement one UI scenario from https://automationexercise.com/test_cases as a Selenium test, with page objects, cleanup, documentation and repeated runs. Use when the user asks to add, write or automate a UI test case, e.g. "add TC-16" or "/new-ui-test 16".
argument-hint: "<scenario number from the site, e.g. 16>"
allowed-tools: Bash(./mvnw:*), Bash(curl -s https\://automationexercise.com:*), Read, Grep, Glob, Edit, Write
---

# New UI test

Automate scenario `TC-$ARGUMENTS` from https://automationexercise.com/test_cases. Follow every rule in
`AGENTS.md`. This skill adds the steps that are easy to skip.

## 1. Check the ID

- The test's tag is `TC-<number>`, with two digits: `TC-06`, `TC-16`.
- Search `src/test/java` for `@Tag("TC-<number>")`. If the ID already exists, stop and tell the user.
  An ID is never used twice.
- A case that is not on the site gets the next free number after `TC-26`. Find the highest number in
  use and add one.

## 2. Read the scenario and the live pages

1. Fetch the scenario text: `curl -s https://automationexercise.com/test_cases`, and find
   `Test Case <number>:`.
2. Fetch the HTML of every page that the scenario visits. A page that needs a login or a filled cart
   can't be fetched with a plain `curl`, so build those locators from the pages you can fetch, and
   confirm them in step 5 by running the test.
3. Note where the scenario can't be followed exactly, e.g. a message that is shown only for a moment.
   Each such difference goes into the documentation, with the reason.

## 3. Write the page objects

- Reuse the existing page objects and components first (`pages/`, `components/`). Add a method to an
  existing page before you create a new class.
- New page objects extend `BasePage` and use its wait helpers. Locators are `private final By` fields,
  preferring `data-qa`, then `id`, then short CSS. Use XPath only to match on text.
- No assertions, no `Thread.sleep` and no `driver.findElement` in page objects.

## 4. Write the test

- Put it in the matching `*Tests` class in `webui/`, or create a new class that extends `WebTestBase`.
- Every test has `@Test`, `@Tag("TC-<number>")`, a `@DisplayName`, and `// Arrange`, `// Act` and
  `// Assert` sections.
- Keep assertions in `// Assert`. If a step's result is only visible before the next action, store it
  in a variable during `// Act` and assert it later. Checks of preconditions go in `// Arrange`, with
  `.as("Precondition: ...")`.
- Boolean checks get an `.as(...)` description.
- **Test data:** generate a new email for each run (`"test-" + UUID.randomUUID() + "@example.com"`) and
  use a fixed fake password. Never use the real test account (`test.user.*`) for a test that changes
  data, and never pass real credentials as test parameters.
- **Cleanup:** if the test creates an account, add an `@AfterEach` that calls
  `TestAccountCleanup.deleteAccount(email, PASSWORD)`, so a failed run leaves nothing behind.

## 5. Run it

1. `./mvnw -B test -Dgroups=TC-<number> -Dreruns=0`, three times.
2. If a run fails, open the screenshot and HTML in `target/screenshots/` before changing anything.
   "under heavy load (queue full)" or HTTP 503 is the site being overloaded, not a bug in the test:
   run again.
3. A failure that comes back is a real problem. Fix it, then run three times again.
4. Finish with the whole suite: `./mvnw -B test -Dreruns=1`.

## 6. Document

- `README.md`: the *Current Test Coverage* table, the *Project Structure* tree for new classes, and the
  *Roadmap*.
- `docs/test-cases.md`: a row in *Summary*, in ID order, and a section with steps, expected results,
  cleanup, and every difference from the site's scenario with its reason.

## 7. Report

Tell the user:
- the scenario and the test method;
- the result of each run, including site outages;
- every difference from the site's scenario;
- the changed files.

Never commit, push or switch branches. The user does that.
