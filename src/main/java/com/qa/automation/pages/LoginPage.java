package com.qa.automation.pages;

import com.qa.automation.config.Config;
import com.qa.automation.config.Timeouts;
import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

  private final By loginLink = By.cssSelector("a[href='/login']");

  private final By loginFormHeader = By.cssSelector(".login-form h2");

  private final By emailInput = By.cssSelector("input[data-qa='login-email']");

  private final By passwordInput = By.cssSelector("input[data-qa='login-password']");

  private final By submitButton = By.cssSelector("button[data-qa='login-button']");

  private final By errorMessage = By.cssSelector(".login-form form p");

  private final By signupFormHeader = By.cssSelector(".signup-form h2");

  private final By signupNameInput = By.cssSelector("input[data-qa='signup-name']");

  private final By signupEmailInput = By.cssSelector("input[data-qa='signup-email']");

  private final By signupButton = By.cssSelector("button[data-qa='signup-button']");

  private final By signupErrorMessage = By.cssSelector(".signup-form form p");

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void openLoginPage() {
    click(loginLink);
  }

  public void enterEmail(String email) {
    type(emailInput, email);
  }

  public void enterPassword(String password) {
    type(passwordInput, password);
  }

  public void clickLogin() {
    click(submitButton);
  }

  public void login(String email, String password) {
    enterEmail(email);
    enterPassword(password);
    clickLogin();
  }

  public boolean isLoginFormVisible() {
    return isVisible(loginFormHeader, Timeouts.MEDIUM);
  }

  public String getErrorMessage() {
    return getText(errorMessage);
  }

  public boolean isSignupFormVisible() {
    return isVisible(signupFormHeader, Timeouts.MEDIUM);
  }

  public void signup(String name, String email) {
    type(signupNameInput, name);
    type(signupEmailInput, email);
    click(signupButton);
  }

  public String getSignupErrorMessage() {
    return getText(signupErrorMessage);
  }

  public static String validEmail() {
    return Config.getRequired("test.user.email");
  }

  public static String validPassword() {
    return Config.getRequired("test.user.password");
  }

  private static String unknownEmail() {
    return "nonexistent-" + UUID.randomUUID() + "@example.com";
  }

  /**
   * Invalid login cases. A test parameter is saved in reports, so the test receives only this
   * constant and asks it for the email and password, which keeps real credentials out of reports.
   */
  public enum InvalidCredentials {
    UNKNOWN_EMAIL("unknown email"),
    WRONG_PASSWORD("wrong password"),
    UNKNOWN_EMAIL_AND_WRONG_PASSWORD("unknown email and wrong password");

    private static final String WRONG_PASSWORD_VALUE = "wrong-password";

    private final String scenario;

    InvalidCredentials(String scenario) {
      this.scenario = scenario;
    }

    public String email() {
      return this == WRONG_PASSWORD ? validEmail() : unknownEmail();
    }

    public String password() {
      return this == UNKNOWN_EMAIL ? validPassword() : WRONG_PASSWORD_VALUE;
    }

    @Override
    public String toString() {
      return scenario;
    }
  }
}
