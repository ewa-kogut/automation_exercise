---
name: new-api-test
description: Implement one API scenario as a REST Assured test, either from https://automationexercise.com/api_list or an extra case the user describes, with a client method, cleanup, documentation and repeated runs. Use when the user asks to add, write or automate an API test, e.g. "add API-15", "test createAccount with an existing email" or "/new-api-test 15".
argument-hint: "<API number from the site, or a short description of an extra case>"
allowed-tools: Bash(./mvnw:*), Bash(curl -s https\://automationexercise.com:*), Read, Grep, Glob, Edit, Write
---

# New API test

Automate the API scenario `$ARGUMENTS`. Follow every rule in `AGENTS.md`. This skill adds the steps
that are easy to skip.

## 1. Choose the ID

- The test's tag is `API-<number>`, with two digits: `API-07`, `API-15`.
- If the argument is a number, search `src/test/java` for `@Tag("API-<number>")`. If the ID already
  exists, stop and tell the user. An ID is never used twice.
- If the argument describes a case that is not on the site, e.g. a negative case, it gets the next
  free number after the site's last API. Find the highest `API-` number in use and add one.
- If the argument is empty or unclear, list the site's APIs that have no test yet and suggest a few
  extra cases (other HTTP methods, missing or invalid parameters), then ask the user which one.

## 2. Read the scenario and the real response

1. Fetch the list: `curl -s https://automationexercise.com/api_list`, and find `API <number>:` for
   the URL, method, parameters and expected response code and message.
2. **Call the endpoint with `curl` before writing any assertion**, e.g.
   `curl -s -X POST https://automationexercise.com/api/searchProduct -d "search_product=top"`.
   The site's description and the real answer sometimes differ. The real answer wins, and the
   difference is documented.
3. Never call an endpoint with the real test account (`test.user.*`) through `curl`, and never print
   its values. Use a generated account for anything that needs a user.
4. Never call a changing endpoint (`createAccount`, `updateAccount`, `deleteAccount`) with `curl`
   for an account you did not create in the same check. Delete what you create.

## 3. Write the client method

- One client per resource in `src/main/java/com/qa/automation/api/clients/`, extending
  `BaseApiClient`. Add a method to an existing client before you create a new class.
- Paths are `private static final String` constants. Form data goes in `formParam`.
- Use `request()`, which logs to Allure. Use `unloggedRequest()` for any request with real
  credentials, because Allure saves the form data.
- Clients contain no assertions. They return the `Response`.

## 4. Write the test

- Put it in the matching `*ApiTests` class in `src/test/java/com/qa/automation/api/`, or create a
  new one. Tests call client methods only, never `given()` directly.
- Every test has `@Test` (or `@ParameterizedTest`), `@Tag("API-<number>")`, a `@DisplayName` in the
  form `"METHOD /path - what is checked"`, and `// Arrange`, `// Act` and `// Assert` sections.
- **Assertions:** the API answers HTTP 200 even for errors. Check the HTTP status, `responseCode` and
  `message`. For responses with a message, use `ApiAssertions.assertApiResponse(response, code,
  message)`. For responses with data, parse with `JsonPath.from(response.asString())` (the content
  type is `text/html`) and check the fields that matter, e.g. not empty, no `null` ids, no duplicates.
- Boolean checks get an `.as(...)` description.
- Variations of one scenario (missing parameters, invalid values) are one `@ParameterizedTest`. Its
  `@MethodSource` uses the short method name.
- **Test data:** generate a new email for each run (`"test-" + UUID.randomUUID() + "@example.com"`)
  and use a fixed fake password. Never pass real credentials as test parameters.
- **Cleanup:** if the test creates an account, add an `@AfterEach` that calls
  `TestAccountCleanup.deleteAccount(email, PASSWORD)`, so a failed run leaves nothing behind.

## 5. Run it

1. `./mvnw -B test -Dgroups=API-<number> -Dreruns=0`, three times.
2. If a run fails, read the response in `target/surefire-reports/` before changing anything. HTTP 503
   or "under heavy load" is the site being overloaded, not a bug in the test: run again.
3. A failure that comes back is a real problem. Compare with `curl` again, fix it, then run three
   times again.
4. Finish with all API tests: `./mvnw -B test -Dtest="com.qa.automation.api.**" -Dreruns=1`.

## 6. Document

- `README.md`: the *Current Test Coverage* table (in ID order), the *Project Structure* tree for new
  classes, and the *Roadmap*.
- `docs/test-cases.md`: a row in *Summary*, in ID order, and a section with a table of request and
  expected result, cleanup, and every difference from the site's description with its reason.

## 7. Report

Tell the user:
- the scenario, the request and the test method;
- the real response compared with the site's description;
- the result of each run, including site outages;
- the changed files.

Never commit, push or switch branches. The user does that.
