package com.qa.automation.pages;

import com.qa.automation.config.Timeouts;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

  private final By cartLink = By.cssSelector(".shop-menu a[href='/view_cart']");

  private final By productNames = By.cssSelector("#cart_info_table .cart_description h4 a");

  private final By prices = By.cssSelector("#cart_info_table .cart_price p");

  private final By quantities = By.cssSelector("#cart_info_table .cart_quantity button");

  private final By totals = By.cssSelector("#cart_info_table .cart_total_price");

  private final By proceedToCheckoutButton = By.cssSelector("a.check_out");

  private final By registerLoginLink = By.cssSelector("#checkoutModal a[href='/login']");

  private final By emptyCartMessage = By.id("empty_cart");

  public CartPage(WebDriver driver) {
    super(driver);
  }

  public void openCartPage() {
    click(cartLink);
  }

  /**
   * Waits for the product table or the "Cart is empty!" message, then counts the products. Returns
   * 0 for an empty cart and for a page that is not the cart at all, e.g. "queue full".
   */
  public int getProductCount() {
    isEitherVisible(productNames, emptyCartMessage, Timeouts.MEDIUM);
    return countVisible(productNames);
  }

  public void proceedToCheckout() {
    click(proceedToCheckoutButton);
  }

  /** The dialog that a guest sees after clicking Proceed To Checkout. */
  public void registerFromCheckoutDialog() {
    click(registerLoginLink);
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
