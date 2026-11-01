package com.qa.automation.pages;

import com.qa.automation.config.Config;
import com.qa.automation.config.Timeouts;
import java.util.UUID;
import java.util.stream.Stream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

  private final By loginLink = By.cssSelector("a[href='/login']");

  private final By loginFormHeader = By.cssSelector(".login-form h2");

  private final By emailInput = By.cssSelector("input[data-qa='login-email']");

  private final By passwordInput = By.cssSelector("input[data-qa='login-password']");

  private final By submitButton = By.cssSelector("button[data-qa='login-button']");

  private final By errorMessage = By.cssSelector(".login-form form p");

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

  public static String validEmail() {
    return Config.getRequired("test.user.email");
  }

  public static String validPassword() {
    return Config.getRequired("test.user.password");
  }

  /** Rows of {scenario, email, password}; usable directly as a JUnit {@code @MethodSource}. */
  public static Stream<Object[]> invalidCredentials() {
    String unknownEmail = "nonexistent-" + UUID.randomUUID() + "@example.com";
    return Stream.of(
        new Object[] {"unknown email", unknownEmail, validPassword()},
        new Object[] {"wrong password", validEmail(), "wrong-password"},
        new Object[] {"unknown email and wrong password", unknownEmail, "wrong-password"});
  }
}
