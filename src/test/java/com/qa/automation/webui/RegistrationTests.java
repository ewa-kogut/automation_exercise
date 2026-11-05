package com.qa.automation.webui;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.TestAccountCleanup;
import com.qa.automation.pages.HomePage;
import com.qa.automation.pages.LoginPage;
import com.qa.automation.pages.SignupPage;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class RegistrationTests extends BaseWebTests {

  private static final String NAME = "Test User";
  private static final String PASSWORD = "Test-password-1";

  private final String email = "test-" + UUID.randomUUID() + "@example.com";

  /** The test deletes the account itself, so this only matters when it fails before that step. */
  @AfterEach
  void deleteTestAccount() {
    TestAccountCleanup.deleteAccount(email, PASSWORD);
  }

  @Test
  @Tag("TC-01")
  @DisplayName("Should register a new user, then delete the account")
  void shouldRegisterAndDeleteUser() {
    // Arrange
    LoginPage loginPage = new LoginPage(driver);
    SignupPage signupPage = new SignupPage(driver);
    HomePage homePage = new HomePage(driver);
    loginPage.openLoginPage();
    assertThat(loginPage.isSignupFormVisible())
        .as("Precondition: the signup form is visible")
        .isTrue();

    // Act
    loginPage.signup(NAME, email);
    assertThat(signupPage.getAccountInformationHeader()).isEqualTo("ENTER ACCOUNT INFORMATION");
    signupPage.fillAccountInformation(PASSWORD);
    signupPage.fillAddressInformation();
    signupPage.clickCreateAccount();

    // Assert
    assertThat(signupPage.getAccountCreatedHeader()).isEqualTo("ACCOUNT CREATED!");
    signupPage.clickContinue();
    assertThat(homePage.getLoggedInText()).isEqualTo("Logged in as " + NAME);
    homePage.deleteAccount();
    assertThat(signupPage.getAccountDeletedHeader()).isEqualTo("ACCOUNT DELETED!");
    signupPage.clickContinue();
    assertThat(homePage.isLoggedOut())
        .as("'Logged in as' should disappear after the account is deleted")
        .isTrue();
  }
}
