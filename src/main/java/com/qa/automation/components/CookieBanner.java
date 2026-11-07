package com.qa.automation.components;

import com.qa.automation.config.Timeouts;
import com.qa.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The consent banner. It is not shown to every visitor, so a missing banner is not an error. */
public class CookieBanner extends BasePage {

  private final By acceptButton = By.cssSelector("button.fc-cta-consent.fc-primary-button");

  public CookieBanner(WebDriver driver) {
    super(driver);
  }

  public void acceptCookiesIfPresent() {
    if (isVisible(acceptButton, Timeouts.SHORT)) {
      click(acceptButton);
    }
  }
}
