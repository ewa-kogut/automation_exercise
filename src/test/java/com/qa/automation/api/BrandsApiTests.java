package com.qa.automation.api;

import static com.qa.automation.api.ApiAssertions.assertApiResponse;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.clients.BrandsApiClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class BrandsApiTests {

  private final BrandsApiClient brandsApiClient = new BrandsApiClient();

  @Test
  @Tag("API-03")
  @DisplayName("GET /brandsList - returns non-empty list")
  void shouldReturnBrandsSuccessfully() {
    // Arrange – the client is created as a field, no request data needed

    // Act
    Response response = brandsApiClient.getBrands();

    // Assert
    JsonPath json = JsonPath.from(response.asString());
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(json.getInt("responseCode")).isEqualTo(200);
    assertThat(json.getList("brands")).isNotEmpty();
  }

  @Test
  @Tag("API-04")
  @DisplayName("PUT /brandsList - method is not supported")
  void shouldRejectPutToBrandsList() {
    // Arrange – no request data needed

    // Act
    Response response = brandsApiClient.putBrandsList();

    // Assert
    assertApiResponse(response, 405, "This request method is not supported.");
  }
}
