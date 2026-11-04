package com.qa.automation.api.clients;

import io.restassured.response.Response;

public class BrandsApiClient extends BaseApiClient {

  private static final String BRANDS_LIST = "/brandsList";

  public Response getBrands() {
    return request().when().get(BRANDS_LIST);
  }

  public Response putBrandsList() {
    return request().when().put(BRANDS_LIST);
  }
}
