package com.qa.automation.extensions;

import com.epam.reportportal.junit5.ItemType;
import com.epam.reportportal.junit5.ReportPortalExtension;
import io.reactivex.Maybe;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * ReportPortal agent that leaves passed {@code @BeforeEach} and {@code @AfterEach} methods
 * ({@code setUp()}, {@code tearDown()}) out of the launch, so the test list shows only tests.
 *
 * <p>A failed fixture is still reported as its own item (the client attaches the error). When
 * {@code @BeforeEach} fails, the test is also shown as skipped, as in the standard agent.
 *
 * <p>Registered instead of {@link ReportPortalExtension} in
 * {@code src/test/resources/META-INF/services}.
 */
public class QuietFixturesReportPortalExtension extends ReportPortalExtension {

  @Override
  public void interceptBeforeEachMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext context)
      throws Throwable {
    Instant startTime = Instant.now().truncatedTo(ChronoUnit.MICROS);
    try {
      invocation.proceed();
    } catch (Throwable cause) {
      reportFailedFixture(invocationContext, context, ItemType.BEFORE_METHOD, cause);
      reportSkippedStep(invocationContext, context, cause, startTime);
      throw cause;
    }
  }

  @Override
  public void interceptAfterEachMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext context)
      throws Throwable {
    try {
      invocation.proceed();
    } catch (Throwable cause) {
      reportFailedFixture(invocationContext, context, ItemType.AFTER_METHOD, cause);
      throw cause;
    }
  }

  private void reportFailedFixture(
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext context,
      ItemType itemType,
      Throwable cause) {
    ExtensionContext parentContext = context.getParent().orElse(context.getRoot());
    Maybe<String> id =
        startBeforeAfter(invocationContext.getExecutable(), parentContext, context, itemType);
    getLaunch(context)
        .finishTestItem(id, buildFinishTestItemRq(context, getExecutionStatus(context, cause)));
  }
}
