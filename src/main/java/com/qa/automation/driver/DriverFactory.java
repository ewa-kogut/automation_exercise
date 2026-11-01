package com.qa.automation.driver;

import com.qa.automation.config.Config;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates the browser chosen by the {@code browser} setting: chrome, edge, firefox or
 * chrome-mobile (Chrome emulating a phone screen).
 *
 * <p>With {@code grid.url} set, the browser starts on Selenium Grid; otherwise it starts on this
 * machine. {@code remote=true} makes a local browser headless. A Grid node has its own screen,
 * which you can watch with noVNC, so Grid browsers are never headless.
 *
 * <p>With {@code block.ads=true} (the default) the browser cannot reach the site's ad networks.
 * Their full-screen ads ({@code #google_vignette}) cover the page and swallow clicks.
 */
public final class DriverFactory {

  /** Window size for headless and Grid runs; smaller windows break the site's layout. */
  private static final int WIDTH = 1920;

  private static final int HEIGHT = 1080;

  /**
   * Pixel 7 screen and a Chrome-on-Android user agent, so the site serves its mobile layout. The
   * page still runs in desktop Chrome: this checks the layout, not a real phone browser.
   */
  private static final Map<String, Object> PHONE =
      Map.of(
          "deviceMetrics",
          Map.of("width", 412, "height", 915, "pixelRatio", 2.625, "touch", true),
          "userAgent",
          "Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) "
              + "Chrome/140.0.0.0 Mobile Safari/537.36");

  /** Ad networks loaded by the site; subdomains are blocked too. */
  private static final List<String> AD_DOMAINS =
      List.of(
          "googlesyndication.com",
          "doubleclick.net",
          "googleadservices.com",
          "adtrafficquality.google");

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
          case "chrome-mobile" -> chromeMobileOptions(headless, fixedSize);
          default ->
              throw new IllegalStateException(
                  "Unknown browser: " + browser + " (use chrome, edge, firefox or chrome-mobile)");
        };

    if (Boolean.parseBoolean(Config.get("block.ads"))) {
      blockAds(options);
    }

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

  private static ChromeOptions chromeMobileOptions(boolean headless, boolean fixedSize) {
    ChromeOptions options = chromeOptions(headless, fixedSize);
    options.setExperimentalOption("mobileEmulation", PHONE);
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

  /**
   * Chrome and Edge resolve ad domains to "not found". Firefox has no such switch, so a proxy
   * auto-config (PAC) script sends ad requests to a closed local port, where they fail at once.
   */
  private static void blockAds(AbstractDriverOptions<?> options) {
    if (options instanceof ChromiumOptions<?> chromium) {
      String rules =
          AD_DOMAINS.stream()
              .map(domain -> "MAP " + domain + " ~NOTFOUND, MAP *." + domain + " ~NOTFOUND")
              .collect(Collectors.joining(", "));
      chromium.addArguments("--host-resolver-rules=" + rules);
    } else if (options instanceof FirefoxOptions firefox) {
      String conditions =
          AD_DOMAINS.stream()
              .map(domain -> "dnsDomainIs(host, '" + domain + "')")
              .collect(Collectors.joining(" || "));
      String pac =
          "function FindProxyForURL(url, host) { return ("
              + conditions
              + ") ? 'PROXY 127.0.0.1:9' : 'DIRECT'; }";
      String pacUrl =
          "data:application/x-ns-proxy-autoconfig;base64,"
              + Base64.getEncoder().encodeToString(pac.getBytes(StandardCharsets.UTF_8));
      firefox.addPreference("network.proxy.type", 2);
      firefox.addPreference("network.proxy.autoconfig_url", pacUrl);
    }
  }

  private static URL toUrl(String gridUrl) {
    try {
      return URI.create(gridUrl).toURL();
    } catch (IllegalArgumentException | MalformedURLException e) {
      throw new IllegalStateException("grid.url is not a valid URL: " + gridUrl, e);
    }
  }
}
