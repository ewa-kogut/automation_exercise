package com.qa.automation.api.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class LoginApiClient extends BaseApiClient {

  private static final String VERIFY_LOGIN = "/verifyLogin";

  /** Sends POST /verifyLogin; a {@code null} argument omits that form parameter. */
  public Response verifyLogin(String email, String password) {
    return sendVerifyLogin(request(), email, password);
  }

  /** Sends POST /verifyLogin with real credentials, without saving the request in reports. */
  public Response verifyLoginWithRealCredentials(String email, String password) {
    return sendVerifyLogin(unloggedRequest(), email, password);
  }

  private static Response sendVerifyLogin(
      RequestSpecification request, String email, String password) {
    if (email != null) {
      request.formParam("email", email);
    }
    if (password != null) {
      request.formParam("password", password);
    }
    return request.when().post(VERIFY_LOGIN);
  }

  public Response deleteVerifyLogin() {
    return request().when().delete(VERIFY_LOGIN);
  }
}
