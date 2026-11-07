package com.qa.automation.api;

import static com.qa.automation.api.ApiAssertions.assertApiResponse;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.clients.AccountApiClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
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
  private static final String NAME = "Test User";
  private static final String PASSWORD = "Test-password-1";

  private final AccountApiClient accountApiClient = new AccountApiClient();
  private final String email = "test-" + UUID.randomUUID() + "@example.com";

  @AfterEach
  void deleteTestAccount() {
    TestAccountCleanup.deleteAccount(email, PASSWORD);
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
    JsonPath user = JsonPath.from(accountApiClient.getUserDetailByEmail(email).asString());
    assertThat(user.getInt("responseCode")).as("The new user can be found").isEqualTo(200);
    assertThat(user.getString("user.email")).isEqualTo(email);
    assertThat(user.getString("user.name")).isEqualTo(NAME);
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
