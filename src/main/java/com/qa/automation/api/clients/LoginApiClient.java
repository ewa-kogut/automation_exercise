package com.qa.automation.api.clients;

import static io.restassured.RestAssured.given;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class LoginApiClient {

  private static final String VERIFY_LOGIN = "/verifyLogin";

  private final String baseUrl;

  public LoginApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /** Sends POST /verifyLogin; a {@code null} argument omits that form parameter. */
  public Response verifyLogin(String email, String password) {
    RequestSpecification request = given().filter(new AllureRestAssured()).baseUri(baseUrl);
    if (email != null) {
      request.formParam("email", email);
    }
    if (password != null) {
      request.formParam("password", password);
    }
    return request.when().post(VERIFY_LOGIN);
  }

  public Response deleteVerifyLogin() {
    return given().filter(new AllureRestAssured()).baseUri(baseUrl).when().delete(VERIFY_LOGIN);
  }
}
