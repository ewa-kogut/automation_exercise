package com.qa.automation.api;

import static com.qa.automation.api.ApiAssertions.assertApiResponse;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qa.automation.api.clients.AccountApiClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class AccountApiTests {

  private static final String ACCOUNT_NOT_FOUND_MESSAGE =
      "Account not found with this email, try another email!";
  private static final Duration CLEANUP_RETRY_INTERVAL = Duration.ofSeconds(2);
  private static final Duration CLEANUP_TIMEOUT = Duration.ofSeconds(10);
  private static final String NAME = "Test User";
  private static final String PASSWORD = "Test-password-1";

  private final AccountApiClient accountApiClient = new AccountApiClient();
  private final String email = "test-" + UUID.randomUUID() + "@example.com";

  /**
   * Removes the test's account, repeating the request while the site is overloaded (HTTP 503). A 404
   * is fine: API-12 has already deleted the account, or the test failed before creating it.
   */
  @AfterEach
  void deleteTestAccount() {
    Response response =
        await("deleting " + email)
            .pollInSameThread() // keeps the request in this test's Allure and ReportPortal logs
            .pollDelay(Duration.ZERO)
            .pollInterval(CLEANUP_RETRY_INTERVAL)
            .atMost(CLEANUP_TIMEOUT)
            .until(
                () -> accountApiClient.deleteAccount(email, PASSWORD),
                deleteResponse -> deleteResponse.statusCode() != 503);

    assertThat(response.statusCode()).as("HTTP status of deleting %s", email).isEqualTo(200);
    assertThat(JsonPath.from(response.asString()).getInt("responseCode"))
        .as("responseCode of deleting %s", email)
        .isIn(200, 404);
  }

  @Test
  @Tag("API-11")
  @DisplayName("POST /createAccount - creates a new user")
  void shouldCreateAccount() {
    // Arrange
    Map<String, String> account = accountData();

    // Act
    Response response = accountApiClient.createAccount(account);

    // Assert
    assertApiResponse(response, 201, "User created!");
  }

  @Test
  @Tag("API-12")
  @DisplayName("DELETE /deleteAccount - deletes an existing user")
  void shouldDeleteAccount() {
    // Arrange
    createTestAccount();

    // Act
    Response response = accountApiClient.deleteAccount(email, PASSWORD);

    // Assert
    assertApiResponse(response, 200, "Account deleted!");
    assertApiResponse(accountApiClient.getUserDetailByEmail(email), 404, ACCOUNT_NOT_FOUND_MESSAGE);
  }

  @Test
  @Tag("API-13")
  @DisplayName("PUT /updateAccount - updates an existing user")
  void shouldUpdateAccount() {
    // Arrange
    createTestAccount();
    Map<String, String> updatedAccount = new HashMap<>(accountData());
    updatedAccount.put("firstname", "Changed");

    // Act
    Response response = accountApiClient.updateAccount(updatedAccount);

    // Assert
    assertApiResponse(response, 200, "User updated!");
    JsonPath user = JsonPath.from(accountApiClient.getUserDetailByEmail(email).asString());
    assertThat(user.getString("user.first_name")).isEqualTo("Changed");
  }

  @Test
  @Tag("API-14")
  @DisplayName("GET /getUserDetailByEmail - returns the user's details")
  void shouldReturnUserDetailByEmail() {
    // Arrange
    createTestAccount();

    // Act
    Response response = accountApiClient.getUserDetailByEmail(email);

    // Assert
    JsonPath json = JsonPath.from(response.asString());
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(json.getInt("responseCode")).isEqualTo(200);
    assertThat(json.getString("user.email")).isEqualTo(email);
    assertThat(json.getString("user.name")).isEqualTo(NAME);
  }

  /** Fails with a clear message when the precondition itself does not work. */
  private void createTestAccount() {
    assertApiResponse(accountApiClient.createAccount(accountData()), 201, "User created!");
  }

  private Map<String, String> accountData() {
    return Map.ofEntries(
        entry("name", NAME),
        entry("email", email),
        entry("password", PASSWORD),
        entry("title", "Mrs"),
        entry("birth_date", "1"),
        entry("birth_month", "1"),
        entry("birth_year", "1990"),
        entry("firstname", "Test"),
        entry("lastname", "User"),
        entry("company", "None"),
        entry("address1", "1 Test Street"),
        entry("address2", "Flat 1"),
        entry("country", "Canada"),
        entry("zipcode", "12345"),
        entry("state", "Ontario"),
        entry("city", "Toronto"),
        entry("mobile_number", "123456789"));
  }
}
