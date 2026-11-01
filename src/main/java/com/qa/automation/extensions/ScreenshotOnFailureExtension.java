package com.qa.automation.extensions;

import com.qa.automation.driver.DriverManager;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Saves a screenshot and the page HTML when a test fails, then rethrows the failure.
 *
 * <p>Runs before {@code @AfterEach}, so the browser is still open. The driver comes from {@link
 * DriverManager}. Files go to {@code target/screenshots}, and both are also attached to the Allure
 * report.
 */
public class ScreenshotOnFailureExtension implements TestExecutionExceptionHandler {

  private static final Path OUTPUT_DIR = Path.of("target", "screenshots");
  private static final DateTimeFormatter TIMESTAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    Optional<WebDriver> driver = DriverManager.currentDriver();
    if (driver.isPresent()) {
      try {
        saveArtifacts(driver.get(), fileBaseName(context));
      } catch (Exception e) {
        throwable.addSuppressed(e);
      }
    }
    throw throwable;
  }

  private static void saveArtifacts(WebDriver driver, String baseName) throws IOException {
    Files.createDirectories(OUTPUT_DIR);

    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    String html = driver.getPageSource();

    Path screenshot = OUTPUT_DIR.resolve(baseName + ".png");
    Files.write(screenshot, screenshotBytes);

    Path pageSource = OUTPUT_DIR.resolve(baseName + ".html");
    Files.writeString(pageSource, html, StandardCharsets.UTF_8);

    Allure.addAttachment(
        "Screenshot", "image/png", new ByteArrayInputStream(screenshotBytes), "png");
    Allure.addAttachment("Page source", "text/html", html, "html");

    System.out.printf(
        "Test failed at %s%n  screenshot: %s%n  page source: %s%n",
        driver.getCurrentUrl(), screenshot.toAbsolutePath(), pageSource.toAbsolutePath());
  }

  private static String fileBaseName(ExtensionContext context) {
    String testName =
        context.getRequiredTestClass().getSimpleName()
            + "_"
            + context.getRequiredTestMethod().getName()
            + "_"
            + context.getDisplayName();
    return testName.replaceAll("[^A-Za-z0-9._-]+", "_")
        + "_"
        + LocalDateTime.now().format(TIMESTAMP);
  }
}
