package com.qa.automation.api.clients;

import io.restassured.response.Response;

public class ProductsApiClient extends BaseApiClient {

  private static final String PRODUCTS_LIST = "/productsList";

  public Response getProducts() {
    return request().when().get(PRODUCTS_LIST);
  }

  public Response postProductsList() {
    return request().when().post(PRODUCTS_LIST);
  }
}
