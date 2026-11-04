package com.qa.automation.api.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ProductsApiClient extends BaseApiClient {

  private static final String PRODUCTS_LIST = "/productsList";
  private static final String SEARCH_PRODUCT = "/searchProduct";

  public Response getProducts() {
    return request().when().get(PRODUCTS_LIST);
  }

  public Response postProductsList() {
    return request().when().post(PRODUCTS_LIST);
  }

  /** Sends POST /searchProduct; a {@code null} argument omits the search_product parameter. */
  public Response searchProduct(String searchTerm) {
    RequestSpecification request = request();
    if (searchTerm != null) {
      request.formParam("search_product", searchTerm);
    }
    return request.when().post(SEARCH_PRODUCT);
  }
}
