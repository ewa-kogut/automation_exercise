# Test Cases

Descriptions of the automated test cases. Each ID is the JUnit `@Tag` on the test, and the number
matches the scenario on the site: `TC-nn` for [UI test cases](https://automationexercise.com/test_cases),
`API-nn` for the [API list](https://automationexercise.com/api_list). Run one case with
`./mvnw -B test -Dgroups=TC-02`.

Every case says where the automated test differs from the scenario on the site, and why.

**Common preconditions**

- UI tests: a fresh browser opens https://automationexercise.com before each test. Ad networks are
  blocked (`block.ads=true`), so full-screen ads do not cover the page.
- A registered test account exists. Its email and password come from `test.user.email` and
  `test.user.password` (see *Configuration* in `README.md`).
- API tests: the API always answers HTTP `200`. The real status is the `responseCode` field in the
  body, so every API case checks the HTTP status, `responseCode` and `message`.

## Summary

| ID | Title | Layer | Automated test |
| --- | --- | --- | --- |
| [TC-02](#tc-02-login-with-correct-email-and-password) | Login with correct email and password | UI | `LoginTests#shouldLoginSuccessfully` |
| [TC-03](#tc-03-login-with-incorrect-email-and-password) | Login with incorrect email and password | UI | `LoginTests#shouldShowErrorForInvalidCredentials` |
| [TC-04](#tc-04-logout) | Logout | UI | `LoginTests#shouldLogoutSuccessfully` |
| [API-01](#api-01-get-all-products-list) | Get all products list | API | `ProductsApiTests#shouldReturnProductsSuccessfully` |
| [API-02](#api-02-post-to-all-products-list) | POST to all products list | API | `ProductsApiTests#shouldRejectPostToProductsList` |
| [API-03](#api-03-get-all-brands-list) | Get all brands list | API | `BrandsApiTests#shouldReturnBrandsSuccessfully` |
| [API-04](#api-04-put-to-all-brands-list) | PUT to all brands list | API | `BrandsApiTests#shouldRejectPutToBrandsList` |
| [API-07](#api-07-verify-login-with-valid-details) | Verify login with valid details | API | `LoginApiTests#shouldVerifyLoginWithValidCredentials` |
| [API-08](#api-08-verify-login-without-a-parameter) | Verify login without a parameter | API | `LoginApiTests#shouldReturnBadRequestWhenParameterIsMissing` |
| [API-09](#api-09-delete-to-verify-login) | DELETE to verify login | API | `LoginApiTests#shouldRejectDeleteMethod` |
| [API-10](#api-10-verify-login-with-invalid-details) | Verify login with invalid details | API | `LoginApiTests#shouldReturnUserNotFoundForInvalidCredentials` |

## UI

### TC-02 Login with correct email and password

| Step | Action | Expected result |
| --- | --- | --- |
| 1 | Open the home page. | The home page loads. |
| 2 | Click *Signup / Login*. | The login form is shown. |
| 3 | Enter the test account's email and password and click *Login*. | "Logged in as &lt;name&gt;" is visible in the header. |

**Differs from the site:** the site's steps 9–10 delete the account at the end. The test logs in
with a permanent test account, so it does not delete it. Deleting would need an account created
for each run, planned as a hybrid test (create the user via API, check in UI, delete via API).
The site's checks "home page is visible" and "Login to your account is visible" are not separate
assertions: the test fails anyway if the link or the form is missing.

### TC-03 Login with incorrect email and password

Runs three times, once for each set of data:

| Variant | Email | Password |
| --- | --- | --- |
| unknown email | random, e.g. `nonexistent-<uuid>@example.com` | the test account's password |
| wrong password | the test account's email | `wrong-password` |
| unknown email and wrong password | random | `wrong-password` |

| Step | Action | Expected result |
| --- | --- | --- |
| 1 | Open the home page and click *Signup / Login*. | The login form is shown. |
| 2 | Enter the email and password of the variant and click *Login*. | The error "Your email or password is incorrect!" is shown. "Logged in as" is not shown. |

**Differs from the site:** the site has one case with "incorrect email and password". The test
splits it into three variants, because a wrong password for an existing account is a different
path in the application than an unknown email. It also checks that the user is not logged in.

### TC-04 Logout

| Step | Action | Expected result |
| --- | --- | --- |
| 1 | Open the home page, click *Signup / Login*, log in with the test account. | "Logged in as &lt;name&gt;" is visible (precondition). |
| 2 | Click *Logout*. | The login form is shown, the URL ends with `/login`, and "Logged in as" is no longer visible. |

**Differs from the site:** the site only checks that the user is on the login page. The test also
checks the URL and that the "Logged in as" text has disappeared.

## API

### API-01 Get all products list

| Request | Expected result |
| --- | --- |
| `GET /api/productsList` | HTTP `200`, `responseCode` `200`, the `products` list is not empty. |

### API-02 POST to all products list

| Request | Expected result |
| --- | --- |
| `POST /api/productsList` without parameters | HTTP `200`, `responseCode` `405`, `message` "This request method is not supported." |

The request has no body: the API rejects the method before it reads any data, so the result does
not depend on what is sent.

### API-03 Get all brands list

| Request | Expected result |
| --- | --- |
| `GET /api/brandsList` | HTTP `200`, `responseCode` `200`, the `brands` list is not empty. |

### API-04 PUT to all brands list

| Request | Expected result |
| --- | --- |
| `PUT /api/brandsList` without parameters | HTTP `200`, `responseCode` `405`, `message` "This request method is not supported." |

### API-07 Verify login with valid details

| Request | Expected result |
| --- | --- |
| `POST /api/verifyLogin` with the test account's `email` and `password` | HTTP `200`, `responseCode` `200`, `message` "User exists!" |

### API-08 Verify login without a parameter

Runs three times, once for each missing parameter:

| Variant | Request | Expected result |
| --- | --- | --- |
| missing email | `POST /api/verifyLogin` with `password` only | HTTP `200`, `responseCode` `400`, `message` "Bad request, email or password parameter is missing in POST request." |
| missing password | `POST /api/verifyLogin` with `email` only | the same |
| missing email and password | `POST /api/verifyLogin` without parameters | the same |

**Differs from the site:** the site describes only the missing email. The test also covers a
missing password and no parameters, because the API answers all three the same way.

### API-09 DELETE to verify login

| Request | Expected result |
| --- | --- |
| `DELETE /api/verifyLogin` | HTTP `200`, `responseCode` `405`, `message` "This request method is not supported." |

### API-10 Verify login with invalid details

| Request | Expected result |
| --- | --- |
| `POST /api/verifyLogin` with a random unknown `email` and `wrong-password` | HTTP `200`, `responseCode` `404`, `message` "User not found!" |

The email is random for every run (`nonexistent-<uuid>@example.com`), so it can never belong to a
real account.
