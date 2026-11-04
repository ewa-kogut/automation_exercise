package com.qa.automation.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

  private final By productNames = By.cssSelector("#cart_info_table .cart_description h4 a");

  private final By prices = By.cssSelector("#cart_info_table .cart_price p");

  private final By quantities = By.cssSelector("#cart_info_table .cart_quantity button");

  private final By totals = By.cssSelector("#cart_info_table .cart_total_price");

  public CartPage(WebDriver driver) {
    super(driver);
  }

  public List<String> getProductNames() {
    return getTexts(productNames);
  }

  public List<String> getPrices() {
    return getTexts(prices);
  }

  public List<String> getQuantities() {
    return getTexts(quantities);
  }

  public List<String> getTotals() {
    return getTexts(totals);
  }
}
