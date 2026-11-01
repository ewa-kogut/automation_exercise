package com.qa.automation.webui;

import com.qa.automation.components.CookieBanner;
import com.qa.automation.config.Config;
import com.qa.automation.driver.DriverManager;
import com.qa.automation.extensions.ScreenshotOnFailureExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseWebTests {

  protected WebDriver driver;

  @BeforeEach
  protected void setUp() {
    driver = DriverManager.startDriver();
    driver.get(Config.get("web.base.url"));
    new CookieBanner(driver).acceptCookiesIfPresent();
  }

  @AfterEach
  protected void tearDown() {
    try {
      DriverManager.quitDriver();
    } finally {
      driver = null;
    }
  }
}
