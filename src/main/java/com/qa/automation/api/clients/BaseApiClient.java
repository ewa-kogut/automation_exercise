package com.qa.automation.api.clients;

import static io.restassured.RestAssured.given;

import com.qa.automation.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.specification.RequestSpecification;

/** Common setup for API clients: the base URL and Allure request logging. */
public abstract class BaseApiClient {

  private final String baseUrl;

  /** Reads {@code api.base.url}; override it for another environment with -Dapi.base.url=... */
  protected BaseApiClient() {
    this.baseUrl = Config.getRequired("api.base.url");
  }

  /** Starts a request with the base URL and the Allure filter already set. */
  protected RequestSpecification request() {
    return given().filter(new AllureRestAssured()).baseUri(baseUrl);
  }
}
