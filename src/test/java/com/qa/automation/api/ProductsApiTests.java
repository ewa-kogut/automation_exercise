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
    assertThat(json.getList("products.id")).doesNotContainNull().doesNotHaveDuplicates();
    assertThat(json.getList("products.name", String.class))
        .allSatisfy(name -> assertThat(name).isNotBlank());
    assertThat(json.getList("products.price", String.class))
        .allSatisfy(price -> assertThat(price).matches("Rs\\. \\d+"));
    assertThat(json.getList("products.brand", String.class))
        .allSatisfy(brand -> assertThat(brand).isNotBlank());
    assertThat(json.getList("products.category.category", String.class))
        .allSatisfy(category -> assertThat(category).isNotBlank());
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

  @Test
  @Tag("API-05")
  @DisplayName("POST /searchProduct - returns products matching the search term")
  void shouldReturnProductsMatchingSearchTerm() {
    // Arrange
    String searchTerm = "jean";

    // Act
    Response response = productsApiClient.searchProduct(searchTerm);

    // Assert
    JsonPath json = JsonPath.from(response.asString());
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(json.getInt("responseCode")).isEqualTo(200);
    assertThat(json.getList("products.name", String.class))
        .isNotEmpty()
        .allSatisfy(name -> assertThat(name).containsIgnoringCase(searchTerm));
  }

  @Test
  @Tag("API-06")
  @DisplayName("POST /searchProduct - missing search_product returns bad request")
  void shouldReturnBadRequestWhenSearchTermIsMissing() {
    // Arrange – the request deliberately has no search term

    // Act
    Response response = productsApiClient.searchProduct(null);

    // Assert
    assertApiResponse(
        response, 400, "Bad request, search_product parameter is missing in POST request.");
  }
}
