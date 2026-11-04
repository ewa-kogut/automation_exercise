package com.qa.automation.webui;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.pages.ProductDetailsPage;
import com.qa.automation.pages.ProductsPage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ProductsTests extends BaseWebTests {

  @Test
  @Tag("TC-08")
  @DisplayName("Should show the product list and the details of the first product")
  void shouldShowProductListAndProductDetails() {
    // Arrange
    ProductsPage productsPage = new ProductsPage(driver);
    ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
    productsPage.openProductsPage();
    assertThat(productsPage.getTitle()).isEqualToIgnoringCase("All Products");
    assertThat(driver.getCurrentUrl()).endsWith("/products");
    assertThat(productsPage.isProductListVisible())
        .as("The product list should be visible")
        .isTrue();
    String expectedName = productsPage.getFirstProductName();

    // Act
    productsPage.openFirstProduct();

    // Assert
    assertThat(driver.getCurrentUrl()).contains("/product_details/");
    assertThat(detailsPage.getName()).isEqualTo(expectedName);
    assertThat(detailsPage.getCategory()).matches("Category: .+");
    assertThat(detailsPage.getPrice()).matches("Rs\\. \\d+");
    assertThat(detailsPage.getAvailability()).matches("Availability: .+");
    assertThat(detailsPage.getCondition()).matches("Condition: .+");
    assertThat(detailsPage.getBrand()).matches("Brand: .+");
  }

  @Test
  @Tag("TC-09")
  @DisplayName("Should show only matching products after a search")
  void shouldShowMatchingProductsAfterSearch() {
    // Arrange
    ProductsPage productsPage = new ProductsPage(driver);
    String searchTerm = "jean";
    productsPage.openProductsPage();
    assertThat(productsPage.getTitle()).isEqualToIgnoringCase("All Products");

    // Act
    productsPage.searchFor(searchTerm);

    // Assert
    assertThat(productsPage.isSearchedProductsTitleVisible())
        .as("'Searched Products' should be visible after searching")
        .isTrue();
    assertThat(productsPage.getProductNames())
        .isNotEmpty()
        .allSatisfy(name -> assertThat(name).containsIgnoringCase(searchTerm));
  }

  @Test
  @Tag("TC-27")
  @DisplayName("Should show no products when the search term matches nothing")
  void shouldShowNoProductsForUnknownSearchTerm() {
    // Arrange
    ProductsPage productsPage = new ProductsPage(driver);
    String searchTerm = "nonexistent-" + UUID.randomUUID();
    productsPage.openProductsPage();
    assertThat(productsPage.isProductListVisible())
        .as("Precondition: products are shown before searching")
        .isTrue();

    // Act
    productsPage.searchFor(searchTerm);

    // Assert
    assertThat(productsPage.isSearchedProductsTitleVisible())
        .as("'Searched Products' should be visible after searching")
        .isTrue();
    assertThat(productsPage.isProductListEmpty()).as("No products should be shown").isTrue();
  }
}
