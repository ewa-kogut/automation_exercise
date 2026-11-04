package com.qa.automation.api.clients;

import io.restassured.response.Response;
import java.util.Map;

public class AccountApiClient extends BaseApiClient {

  private static final String CREATE_ACCOUNT = "/createAccount";
  private static final String DELETE_ACCOUNT = "/deleteAccount";
  private static final String UPDATE_ACCOUNT = "/updateAccount";
  private static final String USER_DETAIL_BY_EMAIL = "/getUserDetailByEmail";

  public Response createAccount(Map<String, String> account) {
    return request().formParams(account).when().post(CREATE_ACCOUNT);
  }

  public Response deleteAccount(String email, String password) {
    return request()
        .formParam("email", email)
        .formParam("password", password)
        .when()
        .delete(DELETE_ACCOUNT);
  }

  public Response updateAccount(Map<String, String> account) {
    return request().formParams(account).when().put(UPDATE_ACCOUNT);
  }

  public Response getUserDetailByEmail(String email) {
    return request().queryParam("email", email).when().get(USER_DETAIL_BY_EMAIL);
  }
}
