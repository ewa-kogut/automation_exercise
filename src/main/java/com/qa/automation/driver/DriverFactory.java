package com.qa.automation.driver;

import com.qa.automation.config.Config;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates the browser chosen by the {@code browser} setting (chrome, edge or firefox).
 *
 * <p>With {@code grid.url} set, the browser starts on Selenium Grid; otherwise it starts on this
 * machine. {@code remote=true} makes a local browser headless. A Grid node has its own screen,
 * which you can watch with noVNC, so Grid browsers are never headless.
 */
public final class DriverFactory {

  /** Window size for headless and Grid runs; smaller windows break the site's layout. */
  private static final int WIDTH = 1920;

  private static final int HEIGHT = 1080;

  private DriverFactory() {}

  public static WebDriver createDriver() {
    String browser = browser();
    String gridUrl = Config.get("grid.url");
    boolean grid = gridUrl != null && !gridUrl.isBlank();
    boolean headless = !grid && Boolean.parseBoolean(Config.get("remote"));
    boolean fixedSize = grid || headless;

    AbstractDriverOptions<?> options =
        switch (browser) {
          case "chrome" -> chromeOptions(headless, fixedSize);
          case "edge" -> edgeOptions(headless, fixedSize);
          case "firefox" -> firefoxOptions(headless, fixedSize);
          default ->
              throw new IllegalStateException(
                  "Unknown browser: " + browser + " (use chrome, edge or firefox)");
        };

    if (grid) {
      return new RemoteWebDriver(toUrl(gridUrl.trim()), options);
    }
    return switch (options) {
      case ChromeOptions chrome -> new ChromeDriver(chrome);
      case EdgeOptions edge -> new EdgeDriver(edge);
      case FirefoxOptions firefox -> new FirefoxDriver(firefox);
      default -> throw new IllegalStateException("No local driver for " + browser);
    };
  }

  private static String browser() {
    String value = Config.get("browser");
    return value == null || value.isBlank() ? "chrome" : value.trim().toLowerCase(Locale.ROOT);
  }

  private static ChromeOptions chromeOptions(boolean headless, boolean fixedSize) {
    ChromeOptions options = new ChromeOptions();
    if (headless) {
      options.addArguments("--headless=new");
    }
    if (fixedSize) {
      options.addArguments("--window-size=" + WIDTH + "," + HEIGHT);
    }
    return options;
  }

  private static EdgeOptions edgeOptions(boolean headless, boolean fixedSize) {
    EdgeOptions options = new EdgeOptions();
    if (headless) {
      options.addArguments("--headless=new");
    }
    if (fixedSize) {
      options.addArguments("--window-size=" + WIDTH + "," + HEIGHT);
    }
    return options;
  }

  private static FirefoxOptions firefoxOptions(boolean headless, boolean fixedSize) {
    FirefoxOptions options = new FirefoxOptions();
    if (headless) {
      options.addArguments("-headless");
    }
    if (fixedSize) {
      options.addArguments("--width=" + WIDTH, "--height=" + HEIGHT);
    }
    return options;
  }

  private static URL toUrl(String gridUrl) {
    try {
      return URI.create(gridUrl).toURL();
    } catch (IllegalArgumentException | MalformedURLException e) {
      throw new IllegalStateException("grid.url is not a valid URL: " + gridUrl, e);
    }
  }
}
