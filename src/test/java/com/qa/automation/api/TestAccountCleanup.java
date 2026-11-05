package com.qa.automation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qa.automation.api.clients.AccountApiClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.time.Duration;

/** Removes accounts that tests created, so a failed test does not leave one behind. */
public final class TestAccountCleanup {

  private static final Duration RETRY_INTERVAL = Duration.ofSeconds(2);
  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  private TestAccountCleanup() {}

  /**
   * Repeats the request while the site is overloaded (HTTP 503). A 404 is fine: the test has already
   * deleted the account, or it failed before creating it.
   */
  public static void deleteAccount(String email, String password) {
    AccountApiClient accountApiClient = new AccountApiClient();
    Response response =
        await("deleting " + email)
            .pollInSameThread() // keeps the request in this test's Allure and ReportPortal logs
            .pollDelay(Duration.ZERO)
            .pollInterval(RETRY_INTERVAL)
            .atMost(TIMEOUT)
            .until(
                () -> accountApiClient.deleteAccount(email, password),
                deleteResponse -> deleteResponse.statusCode() != 503);

    assertThat(response.statusCode()).as("HTTP status of deleting %s", email).isEqualTo(200);
    assertThat(JsonPath.from(response.asString()).getInt("responseCode"))
        .as("responseCode of deleting %s", email)
        .isIn(200, 404);
  }
}
