package com.qa.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The payment form and the "Order Placed!" page after it. The site takes no real payment. */
public class PaymentPage extends BasePage {

  /** A standard test card number, not a real card. */
  private static final String TEST_CARD_NUMBER = "4111111111111111";

  private final By nameOnCardInput = By.cssSelector("input[data-qa='name-on-card']");

  private final By cardNumberInput = By.cssSelector("input[data-qa='card-number']");

  private final By cvcInput = By.cssSelector("input[data-qa='cvc']");

  private final By expiryMonthInput = By.cssSelector("input[data-qa='expiry-month']");

  private final By expiryYearInput = By.cssSelector("input[data-qa='expiry-year']");

  private final By payButton = By.cssSelector("button[data-qa='pay-button']");

  private final By orderPlacedHeader = By.cssSelector("h2[data-qa='order-placed']");

  private final By orderConfirmation = By.cssSelector("h2[data-qa='order-placed'] + p");

  public PaymentPage(WebDriver driver) {
    super(driver);
  }

  public void payWithTestCard(String nameOnCard) {
    type(nameOnCardInput, nameOnCard);
    type(cardNumberInput, TEST_CARD_NUMBER);
    type(cvcInput, "123");
    type(expiryMonthInput, "12");
    type(expiryYearInput, "2030");
    click(payButton);
  }

  public String getOrderPlacedHeader() {
    return getText(orderPlacedHeader);
  }

  public String getOrderConfirmation() {
    return getText(orderConfirmation);
  }
}
