package com.qa.automation.components;

import com.qa.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The subscription form in the footer, which is the same on every page. */
public class SubscriptionFooter extends BasePage {

  private final By title = By.cssSelector(".single-widget h2");

  private final By emailInput = By.id("susbscribe_email");

  private final By subscribeButton = By.id("subscribe");

  private final By successMessage = By.cssSelector("#success-subscribe .alert-success");

  public SubscriptionFooter(WebDriver driver) {
    super(driver);
  }

  public String getTitle() {
    return getText(title);
  }

  public void subscribe(String email) {
    type(emailInput, email);
    click(subscribeButton);
  }

  /** The site hides the message after 1.5 seconds, so read it right after subscribing. */
  public String getSuccessMessage() {
    return getText(successMessage);
  }
}
