package com.qa.automation.webui;

import static com.qa.automation.pages.LoginPage.validEmail;
import static com.qa.automation.pages.LoginPage.validPassword;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.pages.HomePage;
import com.qa.automation.pages.LoginPage;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class LoginTests extends BaseWebTests {

  private static final String INVALID_CREDENTIALS_MESSAGE = "Your email or password is incorrect!";

  static Stream<Object[]> invalidCredentials() {
    return LoginPage.invalidCredentials();
  }

  @Test
  @DisplayName("Should login successfully with valid credentials")
  void shouldLoginSuccessfully() {
    // Arrange
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    loginPage.openLoginPage();

    // Act
    loginPage.login(validEmail(), validPassword());

    // Assert
    assertThat(homePage.isLoggedIn())
        .as("User should be logged in after submitting valid credentials")
        .isTrue();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("invalidCredentials")
  @DisplayName("Should show an error for invalid credentials")
  void shouldShowErrorForInvalidCredentials(String scenario, String email, String password) {
    // Arrange
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    loginPage.openLoginPage();

    // Act
    loginPage.login(email, password);

    // Assert
    assertThat(loginPage.getErrorMessage()).isEqualTo(INVALID_CREDENTIALS_MESSAGE);
    assertThat(homePage.isLoggedOut())
        .as("User should not be logged in after submitting invalid credentials")
        .isTrue();
  }

  @Test
  @DisplayName("Should logout and return to the login page")
  void shouldLogoutSuccessfully() {
    // Arrange
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    loginPage.openLoginPage();
    loginPage.login(validEmail(), validPassword());
    assertThat(homePage.isLoggedIn()).as("Precondition: user is logged in").isTrue();

    // Act
    homePage.logout();

    // Assert
    assertThat(loginPage.isLoginFormVisible())
        .as("Login form should be shown after logout (current URL: %s)", driver.getCurrentUrl())
        .isTrue();
    assertThat(driver.getCurrentUrl()).endsWith("/login");
    assertThat(homePage.isLoggedOut())
        .as("'Logged in as' should disappear after logout")
        .isTrue();
  }
}
