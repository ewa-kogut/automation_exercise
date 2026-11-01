package com.qa.automation.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.clients.ProductsApiClient;
import com.qa.automation.config.Config;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProductsApiTests {

  private final ProductsApiClient productsApiClient =
      new ProductsApiClient(Config.get("api.base.url"));

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
}
