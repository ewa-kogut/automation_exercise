package com.qa.automation.api.clients;

import static io.restassured.RestAssured.given;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

public class ProductsApiClient {

  private final String baseUrl;

  public ProductsApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public Response getProducts() {
    return given().filter(new AllureRestAssured()).baseUri(baseUrl).when().get("/productsList");
  }
}
