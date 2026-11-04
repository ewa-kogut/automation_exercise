package com.qa.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailsPage extends BasePage {

  private final By name = By.cssSelector(".product-information h2");

  private final By category = By.cssSelector(".product-information > p:nth-of-type(1)");

  private final By price = By.cssSelector(".product-information span span");

  private final By availability = By.cssSelector(".product-information > p:nth-of-type(2)");

  private final By condition = By.cssSelector(".product-information > p:nth-of-type(3)");

  private final By brand = By.cssSelector(".product-information > p:nth-of-type(4)");

  public ProductDetailsPage(WebDriver driver) {
    super(driver);
  }

  public String getName() {
    return getText(name);
  }

  public String getCategory() {
    return getText(category);
  }

  public String getPrice() {
    return getText(price);
  }

  public String getAvailability() {
    return getText(availability);
  }

  public String getCondition() {
    return getText(condition);
  }

  public String getBrand() {
    return getText(brand);
  }
}
