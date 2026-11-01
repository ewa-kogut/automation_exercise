package com.qa.automation.pages;

import com.qa.automation.config.Timeouts;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

  protected final WebDriver driver;

  protected BasePage(WebDriver driver) {
    this.driver = driver;
  }

  protected WebElement waitForVisible(By locator) {
    return waitForVisible(locator, Timeouts.MEDIUM);
  }

  protected WebElement waitForVisible(By locator, Duration timeout) {
    return new WebDriverWait(driver, timeout)
        .until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  protected WebElement waitForClickable(By locator) {
    return new WebDriverWait(driver, Timeouts.MEDIUM)
        .until(ExpectedConditions.elementToBeClickable(locator));
  }

  protected boolean isVisible(By locator, Duration timeout) {
    try {
      waitForVisible(locator, timeout);
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  /** Returns as soon as the element is absent or hidden, without waiting for the full timeout. */
  protected boolean isNotVisible(By locator, Duration timeout) {
    try {
      return new WebDriverWait(driver, timeout)
          .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    } catch (TimeoutException e) {
      return false;
    }
  }

  protected void click(By locator) {
    waitForClickable(locator).click();
  }

  protected void type(By locator, String text) {
    WebElement element = waitForVisible(locator);
    element.clear();
    element.sendKeys(text);
  }

  protected String getText(By locator) {
    return waitForVisible(locator).getText();
  }
}
