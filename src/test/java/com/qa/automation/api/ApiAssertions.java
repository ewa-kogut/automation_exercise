package com.qa.automation.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/** Assertions shared by the API tests. */
final class ApiAssertions {

  private ApiAssertions() {}

  /** The API always returns HTTP 200 and carries the real status in the body. */
  static void assertApiResponse(
      Response response, int expectedResponseCode, String expectedMessage) {
    JsonPath json = JsonPath.from(response.asString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(json.getInt("responseCode")).isEqualTo(expectedResponseCode);
    assertThat(json.getString("message")).isEqualTo(expectedMessage);
  }
}
