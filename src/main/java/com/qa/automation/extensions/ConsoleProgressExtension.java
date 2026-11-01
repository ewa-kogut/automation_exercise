package com.qa.automation.extensions;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * Prints each test to the console as it starts and finishes, with its ID, name, time and, on
 * failure, the error and the line in the test code. Makes the Jenkins Console Output readable.
 *
 * <p>Registered for all tests in {@code src/test/resources/META-INF/services}. Output is plain
 * ASCII, so it shows correctly in any console encoding.
 */
public class ConsoleProgressExtension implements BeforeTestExecutionCallback, TestWatcher {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(ConsoleProgressExtension.class);

  private static final String START_TIME = "startTime";

  /** Stack frames from this package point at the failing line in the test or page code. */
  private static final String PROJECT_PACKAGE = "com.qa.automation.";

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    context.getStore(NAMESPACE).put(START_TIME, System.nanoTime());
    print("START ", context, "");
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    print("PASSED", context, elapsed(context));
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    print("ABORTED", context, elapsed(context) + " - " + cause.getMessage());
  }

  @Override
  public void testDisabled(ExtensionContext context, Optional<String> reason) {
    print("SKIPPED", context, reason.map(r -> "- " + r).orElse(""));
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    StringBuilder details = new StringBuilder(elapsed(context));
    details.append(System.lineSeparator()).append("    ").append(cause.getClass().getName());
    if (cause.getMessage() != null) {
      details.append(": ").append(cause.getMessage().strip().replace("\n", "\n    "));
    }
    for (StackTraceElement frame : cause.getStackTrace()) {
      if (frame.getClassName().startsWith(PROJECT_PACKAGE)) {
        details.append(System.lineSeparator()).append("    at ").append(frame);
        break;
      }
    }
    print("FAILED", context, details.toString());
  }

  private static void print(String status, ExtensionContext context, String details) {
    String ids =
        context.getTags().stream().sorted().collect(Collectors.joining(",", "[", "] "));
    String testClass = context.getRequiredTestClass().getSimpleName();
    String name = displayName(context);
    System.out.printf("[TEST] %-7s %s%s > %s %s%n", status, ids, testClass, name, details);
    System.out.flush();
  }

  /** For a parameterized test, adds the method's display name before the invocation's name. */
  private static String displayName(ExtensionContext context) {
    String name = context.getDisplayName();
    return context
        .getParent()
        .filter(parent -> parent.getTestMethod().isPresent())
        .map(parent -> parent.getDisplayName() + " " + name)
        .orElse(name);
  }

  private static String elapsed(ExtensionContext context) {
    Long start = context.getStore(NAMESPACE).get(START_TIME, Long.class);
    if (start == null) {
      return "";
    }
    return String.format(Locale.ROOT, "(%.1f s)", (System.nanoTime() - start) / 1_000_000_000.0);
  }
}
