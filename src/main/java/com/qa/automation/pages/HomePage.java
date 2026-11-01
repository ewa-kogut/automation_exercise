package com.qa.automation.pages;

import com.qa.automation.config.Timeouts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

  private final By loggedInUser = By.xpath("//a[contains(text(), 'Logged in as')]");

  private final By logoutLink = By.cssSelector("a[href='/logout']");

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public boolean isLoggedIn() {
    return isVisible(loggedInUser, Timeouts.MEDIUM);
  }

  public boolean isLoggedOut() {
    return isNotVisible(loggedInUser, Timeouts.MEDIUM);
  }

  public void logout() {
    click(logoutLink);
  }
}
