package com.qa.automation.driver;

import java.util.Optional;
import org.openqa.selenium.WebDriver;

/** Holds the WebDriver of the current test thread, so framework code can reach it. */
public final class DriverManager {

  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  private DriverManager() {}

  public static WebDriver startDriver() {
    WebDriver driver = DriverFactory.createDriver();
    DRIVER.set(driver);
    return driver;
  }

  public static WebDriver getDriver() {
    return currentDriver()
        .orElseThrow(() -> new IllegalStateException("No WebDriver started for this thread"));
  }

  public static Optional<WebDriver> currentDriver() {
    return Optional.ofNullable(DRIVER.get());
  }

  public static void quitDriver() {
    WebDriver driver = DRIVER.get();
    if (driver != null) {
      try {
        driver.quit();
      } finally {
        DRIVER.remove();
      }
    }
  }
}
