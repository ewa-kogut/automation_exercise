package com.qa.automation.components;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CookieBanner {

  private final WebDriver driver;
  private final WebDriverWait wait;

  private final By acceptButton = By.cssSelector("button.fc-cta-consent.fc-primary-button");

  public CookieBanner(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
  }

  public void acceptCookiesIfPresent() {
    try {
      wait.until(ExpectedConditions.elementToBeClickable(acceptButton)).click();
    } catch (Exception ignored) {
      // Cookie banner is not displayed
    }
  }
}
