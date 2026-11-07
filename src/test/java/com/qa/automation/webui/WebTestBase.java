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
public abstract class WebTestBase {

  protected WebDriver driver;

  @BeforeEach
  protected void setUp() {
    driver = DriverManager.startDriver();
    driver.get(Config.get("web.base.url"));
    // The consent banner comes from the same Google script as the ads. With block.ads=true it
    // never appears, so waiting for it would only add 5 seconds to every test.
    if (!Boolean.parseBoolean(Config.get("block.ads"))) {
      new CookieBanner(driver).acceptCookiesIfPresent();
    }
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
