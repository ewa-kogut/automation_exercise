package com.qa.automation.driver;

import com.qa.automation.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

  /** Window size for headless runs; the headless default is too small for the site's layout. */
  private static final String HEADLESS_WINDOW_SIZE = "--window-size=1920,1080";

  public static WebDriver createDriver() {
    ChromeOptions options = new ChromeOptions();
    // remote=true: headless Chrome for Jenkins / CI; remote=false: visible window for local runs
    if (Boolean.parseBoolean(Config.get("remote"))) {
      options.addArguments("--headless=new", HEADLESS_WINDOW_SIZE);
    }
    return new ChromeDriver(options);
  }
}
