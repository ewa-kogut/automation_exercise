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
  body, so every API case checks the HTTP status and `responseCode`, plus `message` when the response has one.

## Summary

| ID | Title | Layer | Automated test |
| --- | --- | --- | --- |
| [TC-02](#tc-02-login-with-correct-email-and-password) | Login with correct email and password | UI | `LoginTests#shouldLoginSuccessfully` |
| [TC-03](#tc-03-login-with-incorrect-email-and-password) | Login with incorrect email and password | UI | `LoginTests#shouldShowErrorForInvalidCredentials` |
| [TC-04](#tc-04-logout) | Logout | UI | `LoginTests#shouldLogoutSuccessfully` |
| [TC-08](#tc-08-all-products-and-product-details) | All products and product details | UI | `ProductsTests#shouldShowProductListAndProductDetails` |
| [TC-09](#tc-09-search-product) | Search product | UI | `ProductsTests#shouldShowMatchingProductsAfterSearch` |
| [TC-12](#tc-12-add-products-in-cart) | Add products in cart | UI | `CartTests#shouldAddTwoProductsToCart` |
| [API-01](#api-01-get-all-products-list) | Get all products list | API | `ProductsApiTests#shouldReturnProductsSuccessfully` |
| [API-02](#api-02-post-to-all-products-list) | POST to all products list | API | `ProductsApiTests#shouldRejectPostToProductsList` |
| [API-03](#api-03-get-all-brands-list) | Get all brands list | API | `BrandsApiTests#shouldReturnBrandsSuccessfully` |
| [API-04](#api-04-put-to-all-brands-list) | PUT to all brands list | API | `BrandsApiTests#shouldRejectPutToBrandsList` |
| [API-05](#api-05-post-to-search-product) | POST to search product | API | `ProductsApiTests#shouldReturnProductsMatchingSearchTerm` |
| [API-06](#api-06-post-to-search-product-without-search_product) | POST to search product without `search_product` | API | `ProductsApiTests#shouldReturnBadRequestWhenSearchTermIsMissing` |
| [API-07](#api-07-verify-login-with-valid-details) | Verify login with valid details | API | `LoginApiTests#shouldVerifyLoginWithValidCredentials` |
| [API-08](#api-08-verify-login-without-a-parameter) | Verify login without a parameter | API | `LoginApiTests#shouldReturnBadRequestWhenParameterIsMissing` |
| [API-09](#api-09-delete-to-verify-login) | DELETE to verify login | API | `LoginApiTests#shouldRejectDeleteMethod` |
| [API-10](#api-10-verify-login-with-invalid-details) | Verify login with invalid details | API | `LoginApiTests#shouldReturnUserNotFoundForInvalidCredentials` |
| [API-11](#api-11-create-user-account) | Create user account | API | `AccountApiTests#shouldCreateAccount` |
| [API-12](#api-12-delete-user-account) | Delete user account | API | `AccountApiTests#shouldDeleteAccount` |
| [API-13](#api-13-update-user-account) | Update user account | API | `AccountApiTests#shouldUpdateAccount` |
| [API-14](#api-14-get-user-detail-by-email) | Get user detail by email | API | `AccountApiTests#shouldReturnUserDetailByEmail` |

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

### TC-08 All products and product details

| Step | Action | Expected result |
| --- | --- | --- |
| 1 | Open the home page and click *Products*. | The title is "All Products" (any case), the URL ends with `/products`, and the product list is visible. |
| 2 | Click *View Product* on the first product. | The URL contains `/product_details/`, and the name matches the one on the list. |
| 3 | Read the product's details. | Category, price, availability, condition and brand all have a value. |

**Differs from the site:** the site only asks for the details to be visible. The test also checks
that the page belongs to the chosen product (the same name) and that each detail has a value. It
does not expect specific values, so it does not depend on which product comes first. The site's
check "home page is visible" is not a separate assertion: the test fails anyway if the *Products*
link is missing.

### TC-09 Search product

| Step | Action | Expected result |
| --- | --- | --- |
| 1 | Open the home page and click *Products*. | The title is "All Products" (any case). |
| 2 | Enter `jean` in the search field and click the search button. | "Searched Products" is visible. |
| 3 | Read the names of the results. | The list is not empty, and every name contains "jean" (any case). |

**Differs from the site:** the site only asks for the related products to be visible. The test also
checks that each result's name contains the search term. It uses `jean` because results for some
other terms, such as `top`, include products without that word in the name. The site's check "home
page is visible" is not a separate assertion: the test fails anyway if the *Products* link is
missing.

### TC-12 Add products in cart

| Step | Action | Expected result |
| --- | --- | --- |
| 1 | Open the home page and click *Products*. Note the name and price of products 1 and 2. | The products are shown. |
| 2 | Hover over product 1, click *Add to cart*, then *Continue Shopping*. | The dialog opens and closes. |
| 3 | Hover over product 2, click *Add to cart*, then *View Cart*. | The cart opens. |
| 4 | Read the cart. | Both products are there in the order added, with the noted names and prices, quantity `1`, and a total equal to the price. |

**Differs from the site:** the site says "first product" and "second product". The test selects them
by `data-product-id` (1 and 2, currently the first two on the list), because the page has no other
short selector for one product. It takes the expected values from the list, so a price change on
the site does not break it. The site's check "home page is visible" is not a separate assertion: the
test fails anyway if the *Products* link is missing.

**Timing:** the *Add to cart* button sits in an overlay that slides in for 500 ms when the mouse is
over the product. A click during that time can land next to the button, so the test waits until the
button stops moving (`BasePage.clickWhenNotMoving`).

**Phones:** with `browser=chrome-mobile` there is no hover, so the overlay stays hidden. After a short
wait the test clicks the *Add to cart* button on the product card instead.

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

### API-05 POST to search product

| Request | Expected result |
| --- | --- |
| `POST /api/searchProduct` with `search_product=jean` | HTTP `200`, `responseCode` `200`, the `products` list is not empty and every product name contains "jean" (any case). |

**Differs from the site:** the site only expects a list of products. The test also checks that the
names match. It searches for `jean` because the API also searches other fields: results for `top`
include products without that word in the name.

### API-06 POST to search product without search_product

| Request | Expected result |
| --- | --- |
| `POST /api/searchProduct` without parameters | HTTP `200`, `responseCode` `400`, `message` "Bad request, search_product parameter is missing in POST request." |

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

### Account tests (API-11 to API-14)

Each test uses its own account with a random email (`test-<uuid>@example.com`), so the tests do not
depend on each other or on the permanent test account. API-12, API-13 and API-14 create the account
first and check that this worked. After every test the account is deleted, whatever the result. If
the site answers HTTP `503` (overloaded), the delete is repeated every 2 seconds for up to 10 seconds.
If the account still cannot be deleted, the test fails and the message gives its email. There is no cleanup before a test:
every test has a new email, and the API has no request that lists existing accounts.

### API-11 Create user account

| Request | Expected result |
| --- | --- |
| `POST /api/createAccount` with all account fields | HTTP `200`, `responseCode` `201`, `message` "User created!" |

### API-12 Delete user account

| Request | Expected result |
| --- | --- |
| `DELETE /api/deleteAccount` with `email` and `password` | HTTP `200`, `responseCode` `200`, `message` "Account deleted!" |
| `GET /api/getUserDetailByEmail` with the same `email` | HTTP `200`, `responseCode` `404`, `message` "Account not found with this email, try another email!" |

**Differs from the site:** the site only checks the delete response. The test also checks that the
account can no longer be found.

### API-13 Update user account

| Request | Expected result |
| --- | --- |
| `PUT /api/updateAccount` with all account fields and a new `firstname` | HTTP `200`, `responseCode` `200`, `message` "User updated!" |
| `GET /api/getUserDetailByEmail` with the same `email` | `user.first_name` is the new value. |

**Differs from the site:** the site only checks the update response. The test also checks that the
change was saved.

### API-14 Get user detail by email

| Request | Expected result |
| --- | --- |
| `GET /api/getUserDetailByEmail` with `email` | HTTP `200`, `responseCode` `200`, `user.email` and `user.name` match the created account. |
