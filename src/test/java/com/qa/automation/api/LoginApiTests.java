package com.qa.automation.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.clients.LoginApiClient;
import com.qa.automation.config.Config;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LoginApiTests {

  private static final String MISSING_PARAMETER_MESSAGE =
      "Bad request, email or password parameter is missing in POST request.";

  private final LoginApiClient loginApiClient = new LoginApiClient(Config.get("api.base.url"));

  @Test
  @DisplayName("POST /verifyLogin - valid credentials return 'User exists!'")
  void shouldVerifyLoginWithValidCredentials() {
    // Arrange
    String email = Config.getRequired("test.user.email");
    String password = Config.getRequired("test.user.password");

    // Act
    Response response = loginApiClient.verifyLogin(email, password);

    // Assert
    assertApiResponse(response, 200, "User exists!");
  }

  @Test
  @DisplayName("POST /verifyLogin - unknown user returns 'User not found!'")
  void shouldReturnUserNotFoundForInvalidCredentials() {
    // Arrange
    String unknownEmail = "nonexistent-" + UUID.randomUUID() + "@example.com";

    // Act
    Response response = loginApiClient.verifyLogin(unknownEmail, "wrong-password");

    // Assert
    assertApiResponse(response, 404, "User not found!");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("missingParameters")
  @DisplayName("POST /verifyLogin - missing parameter returns bad request")
  void shouldReturnBadRequestWhenParameterIsMissing(
      String scenario, String email, String password) {
    // Arrange – email and password come from missingParameters()

    // Act
    Response response = loginApiClient.verifyLogin(email, password);

    // Assert
    assertApiResponse(response, 400, MISSING_PARAMETER_MESSAGE);
  }

  @Test
  @DisplayName("DELETE /verifyLogin - method is not supported")
  void shouldRejectDeleteMethod() {
    // Arrange – no request data needed

    // Act
    Response response = loginApiClient.deleteVerifyLogin();

    // Assert
    assertApiResponse(response, 405, "This request method is not supported.");
  }

  private static Stream<Arguments> missingParameters() {
    return Stream.of(
        Arguments.of("missing email", null, "any-password"),
        Arguments.of("missing password", "user@example.com", null),
        Arguments.of("missing email and password", null, null));
  }

  /** The API always returns HTTP 200 and carries the real status in the body. */
  private static void assertApiResponse(
      Response response, int expectedResponseCode, String expectedMessage) {
    JsonPath json = JsonPath.from(response.asString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(json.getInt("responseCode")).isEqualTo(expectedResponseCode);
    assertThat(json.getString("message")).isEqualTo(expectedMessage);
  }
}
