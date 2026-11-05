package com.qa.automation.webui;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.pages.CartPage;
import com.qa.automation.pages.ProductsPage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CartTests extends BaseWebTests {

  private static final String FIRST_PRODUCT_ID = "1";
  private static final String SECOND_PRODUCT_ID = "2";

  @Test
  @Tag("TC-12")
  @DisplayName("Should show both added products in the cart with price, quantity and total")
  void shouldAddTwoProductsToCart() {
    // Arrange
    ProductsPage productsPage = new ProductsPage(driver);
    CartPage cartPage = new CartPage(driver);
    productsPage.openProductsPage();
    String firstName = productsPage.getProductName(FIRST_PRODUCT_ID);
    String firstPrice = productsPage.getProductPrice(FIRST_PRODUCT_ID);
    String secondName = productsPage.getProductName(SECOND_PRODUCT_ID);
    String secondPrice = productsPage.getProductPrice(SECOND_PRODUCT_ID);

    // Act
    productsPage.addProductToCart(FIRST_PRODUCT_ID);
    productsPage.continueShopping();
    productsPage.addProductToCart(SECOND_PRODUCT_ID);
    productsPage.viewCart();

    // Assert
    assertThat(cartPage.getProductNames()).containsExactly(firstName, secondName);
    assertThat(cartPage.getPrices()).containsExactly(firstPrice, secondPrice);
    assertThat(cartPage.getQuantities()).containsExactly("1", "1");
    assertThat(cartPage.getTotals()).containsExactly(firstPrice, secondPrice);
  }

  @Test
  @Disabled("Not automated yet: planned as the next cart scenario")
  @Tag("TC-13")
  @DisplayName("Should keep the chosen product quantity in the cart")
  void shouldKeepChosenQuantityInCart() {
    // Arrange – not automated yet

    // Act – not automated yet

    // Assert – not automated yet
  }
}
