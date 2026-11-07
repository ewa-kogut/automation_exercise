package com.qa.automation.webui;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.components.SubscriptionFooter;
import com.qa.automation.pages.CartPage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SubscriptionTests extends WebTestBase {

  private static final String SUCCESS_MESSAGE = "You have been successfully subscribed!";

  private final String email = "test-" + UUID.randomUUID() + "@example.com";

  @Test
  @Tag("TC-10")
  @DisplayName("Should subscribe from the home page footer")
  void shouldSubscribeFromHomePage() {
    // Arrange – WebTestBase has opened the home page
    SubscriptionFooter footer = new SubscriptionFooter(driver);
    assertThat(footer.getTitle()).isEqualToIgnoringCase("Subscription");

    // Act
    footer.subscribe(email);

    // Assert
    assertThat(footer.getSuccessMessage()).isEqualTo(SUCCESS_MESSAGE);
  }

  @Test
  @Tag("TC-11")
  @DisplayName("Should subscribe from the cart page footer")
  void shouldSubscribeFromCartPage() {
    // Arrange
    SubscriptionFooter footer = new SubscriptionFooter(driver);
    new CartPage(driver).openCartPage();
    assertThat(driver.getCurrentUrl()).endsWith("/view_cart");
    assertThat(footer.getTitle()).isEqualToIgnoringCase("Subscription");

    // Act
    footer.subscribe(email);

    // Assert
    assertThat(footer.getSuccessMessage()).isEqualTo(SUCCESS_MESSAGE);
  }
}
