package com.qa.automation.api;

import static java.util.Map.entry;

import java.util.Map;

/**
 * Form data for {@code POST /createAccount} and {@code PUT /updateAccount}. The address matches
 * {@code SignupPage.ADDRESS_DETAILS}, so UI tests can check it on the checkout page.
 */
public final class TestAccountData {

  private TestAccountData() {}

  public static Map<String, String> accountData(String name, String email, String password) {
    return Map.ofEntries(
        entry("name", name),
        entry("email", email),
        entry("password", password),
        entry("title", "Mrs"),
        entry("birth_date", "1"),
        entry("birth_month", "1"),
        entry("birth_year", "1990"),
        entry("firstname", "Test"),
        entry("lastname", "User"),
        entry("company", "None"),
        entry("address1", "1 Test Street"),
        entry("address2", "Flat 1"),
        entry("country", "Canada"),
        entry("zipcode", "12345"),
        entry("state", "Ontario"),
        entry("city", "Toronto"),
        entry("mobile_number", "123456789"));
  }
}
