package com.qa.automation.pages;

import com.qa.automation.config.Timeouts;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductsPage extends BasePage {

  private final By productsLink = By.cssSelector("a[href='/products']");

  private final By pageTitle = By.cssSelector(".features_items h2.title");

  private final By searchedProductsTitle =
      By.xpath("//h2[contains(@class, 'title')][normalize-space() = 'Searched Products']");

  private final By searchInput = By.id("search_product");

  private final By searchButton = By.id("submit_search");

  private final By productCard = By.cssSelector(".features_items .product-image-wrapper");

  private final By productName = By.cssSelector(".features_items .productinfo p");

  private final By firstViewProductLink =
      By.cssSelector(".features_items a[href^='/product_details/']");

  private final By cartModal = By.id("cartModal");

  private final By continueShoppingButton = By.cssSelector("#cartModal .close-modal");

  private final By viewCartLink = By.cssSelector("#cartModal a[href='/view_cart']");

  public ProductsPage(WebDriver driver) {
    super(driver);
  }

  public void openProductsPage() {
    click(productsLink);
  }

  public String getTitle() {
    return getText(pageTitle);
  }

  public boolean isProductListVisible() {
    return isVisible(productCard, Timeouts.MEDIUM);
  }

  public boolean isProductListEmpty() {
    return isNotVisible(productCard, Timeouts.MEDIUM);
  }

  public String getFirstProductName() {
    return getText(productName);
  }

  public void openFirstProduct() {
    click(firstViewProductLink);
  }

  public void searchFor(String searchTerm) {
    type(searchInput, searchTerm);
    click(searchButton);
  }

  /** Searching reloads the page, so this also confirms that the results have loaded. */
  public boolean isSearchedProductsTitleVisible() {
    return isVisible(searchedProductsTitle, Timeouts.MEDIUM);
  }

  public List<String> getProductNames() {
    return getTexts(productName);
  }

  public String getProductName(String productId) {
    return getText(By.cssSelector(productInfo(productId) + " p"));
  }

  public String getProductPrice(String productId) {
    return getText(By.cssSelector(productInfo(productId) + " h2"));
  }

  /**
   * With a mouse, hovering shows an overlay that covers the product, and its button takes the click.
   * A phone has no hover, so the overlay stays hidden and the button on the product card is used.
   */
  public void addProductToCart(String productId) {
    String addToCartButton = "a[data-product-id='" + productId + "']";
    By overlayButton = By.cssSelector(".product-overlay " + addToCartButton);

    hover(By.cssSelector(productInfo(productId)));
    if (isVisible(overlayButton, Timeouts.SHORT)) {
      clickWhenNotMoving(overlayButton);
    } else {
      click(By.cssSelector(".productinfo " + addToCartButton));
    }
  }

  public void continueShopping() {
    click(continueShoppingButton);
    isNotVisible(cartModal, Timeouts.MEDIUM);
  }

  public void viewCart() {
    click(viewCartLink);
  }

  private static String productInfo(String productId) {
    return ".productinfo:has(a[data-product-id='" + productId + "'])";
  }
}
