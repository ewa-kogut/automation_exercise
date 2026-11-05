package com.qa.automation.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

  private final By deliveryAddress = By.id("address_delivery");

  private final By invoiceAddress = By.id("address_invoice");

  private final By productNames = By.cssSelector("#cart_info .cart_description h4 a");

  private final By totalAmount = By.cssSelector("#cart_info tr:last-child .cart_total_price");

  private final By commentInput = By.cssSelector("textarea[name='message']");

  private final By placeOrderLink = By.cssSelector("a[href='/payment']");

  public CheckoutPage(WebDriver driver) {
    super(driver);
  }

  public String getDeliveryAddress() {
    return getText(deliveryAddress);
  }

  public String getInvoiceAddress() {
    return getText(invoiceAddress);
  }

  public List<String> getProductNames() {
    return getTexts(productNames);
  }

  public String getTotalAmount() {
    return getText(totalAmount);
  }

  public void placeOrder(String comment) {
    type(commentInput, comment);
    click(placeOrderLink);
  }
}
