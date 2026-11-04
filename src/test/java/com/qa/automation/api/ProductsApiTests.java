package com.qa.automation.api;

import static com.qa.automation.api.ApiAssertions.assertApiResponse;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.clients.ProductsApiClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProductsApiTests {

  private final ProductsApiClient productsApiClient = new ProductsApiClient();

  @Test
  @Tag("API-01")
  @DisplayName("GET /productsList - returns non-empty list")
  void shouldReturnProductsSuccessfully() {
    // Arrange – the client is created as a field, no request data needed

    // Act
    Response response = productsApiClient.getProducts();

    // Assert
    JsonPath json = JsonPath.from(response.asString());
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(json.getInt("responseCode")).isEqualTo(200);
    assertThat(json.getList("products")).isNotEmpty();
  }

  @Test
  @Tag("API-02")
  @DisplayName("POST /productsList - method is not supported")
  void shouldRejectPostToProductsList() {
    // Arrange – no request data needed

    // Act
    Response response = productsApiClient.postProductsList();

    // Assert
    assertApiResponse(response, 405, "This request method is not supported.");
  }
}
