package com.qa.automation.webui;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.automation.api.TestAccountCleanup;
import com.qa.automation.api.TestAccountData;
import com.qa.automation.api.clients.AccountApiClient;
import com.qa.automation.pages.CartPage;
import com.qa.automation.pages.CheckoutPage;
import com.qa.automation.pages.HomePage;
import com.qa.automation.pages.LoginPage;
import com.qa.automation.pages.PaymentPage;
import com.qa.automation.pages.ProductsPage;
import com.qa.automation.pages.SignupPage;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CheckoutTests extends WebTestBase {

  private static final String FIRST_PRODUCT_ID = "1";
  private static final String SECOND_PRODUCT_ID = "2";
  private static final String NAME = "Test User";
  private static final String PASSWORD = "Test-password-1";

  private final String email = "test-" + UUID.randomUUID() + "@example.com";

  /** The test deletes the account itself, so this only matters when it fails before that step. */
  @AfterEach
  void deleteTestAccount() {
    TestAccountCleanup.deleteAccount(email, PASSWORD);
  }

  @Test
  @Tag("TC-14")
  @DisplayName("Should place an order for a user who registers during checkout")
  void shouldPlaceOrderWhenRegisteringDuringCheckout() {
    // Arrange – a guest fills the cart, starts checkout and registers
    ProductsPage productsPage = new ProductsPage(driver);
    CartPage cartPage = new CartPage(driver);
    LoginPage loginPage = new LoginPage(driver);
    SignupPage signupPage = new SignupPage(driver);
    HomePage homePage = new HomePage(driver);
    CheckoutPage checkoutPage = new CheckoutPage(driver);
    PaymentPage paymentPage = new PaymentPage(driver);
    productsPage.openProductsPage();
    List<String> names =
        List.of(
            productsPage.getProductName(FIRST_PRODUCT_ID),
            productsPage.getProductName(SECOND_PRODUCT_ID));
    int expectedTotal =
        rupees(productsPage.getProductPrice(FIRST_PRODUCT_ID))
            + rupees(productsPage.getProductPrice(SECOND_PRODUCT_ID));
    productsPage.addProductToCart(FIRST_PRODUCT_ID);
    productsPage.continueShopping();
    productsPage.addProductToCart(SECOND_PRODUCT_ID);
    productsPage.viewCart();
    cartPage.proceedToCheckout();
    cartPage.registerFromCheckoutDialog();
    loginPage.signup(NAME, email);
    signupPage.fillAccountInformation(PASSWORD);
    signupPage.fillAddressInformation();
    signupPage.clickCreateAccount();
    assertThat(signupPage.getAccountCreatedHeader()).isEqualTo("ACCOUNT CREATED!");
    signupPage.clickContinue();
    assertThat(homePage.getLoggedInText()).isEqualTo("Logged in as " + NAME);

    // Act – the user returns to the cart, reviews the order and pays
    cartPage.openCartPage();
    cartPage.proceedToCheckout();
    String deliveryAddress = checkoutPage.getDeliveryAddress();
    String invoiceAddress = checkoutPage.getInvoiceAddress();
    List<String> orderedNames = checkoutPage.getProductNames();
    String totalAmount = checkoutPage.getTotalAmount();
    checkoutPage.placeOrder("Please leave the parcel at the door.");
    paymentPage.payWithTestCard(NAME);

    // Assert
    assertThat(deliveryAddress).contains(SignupPage.ADDRESS_DETAILS);
    assertThat(invoiceAddress).contains(SignupPage.ADDRESS_DETAILS);
    assertThat(orderedNames).as("The cart should survive registering").isEqualTo(names);
    assertThat(rupees(totalAmount)).isEqualTo(expectedTotal);
    assertThat(paymentPage.getOrderPlacedHeader()).isEqualTo("ORDER PLACED!");
    assertThat(paymentPage.getOrderConfirmation())
        .isEqualTo("Congratulations! Your order has been confirmed!");
    homePage.deleteAccount();
    assertThat(signupPage.getAccountDeletedHeader()).isEqualTo("ACCOUNT DELETED!");
  }

  @Test
  @Tag("TC-16")
  @DisplayName("Should place an order for an API-created user who logs in before checkout")
  void shouldPlaceOrderWhenLoggedInBeforeCheckout() {
    // Arrange – the account is created through the API, then the user logs in and fills the cart
    AccountApiClient accountApiClient = new AccountApiClient();
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    ProductsPage productsPage = new ProductsPage(driver);
    CartPage cartPage = new CartPage(driver);
    CheckoutPage checkoutPage = new CheckoutPage(driver);
    PaymentPage paymentPage = new PaymentPage(driver);
    SignupPage signupPage = new SignupPage(driver);
    Response created =
        accountApiClient.createAccount(TestAccountData.accountData(NAME, email, PASSWORD));
    assertThat(JsonPath.from(created.asString()).getInt("responseCode"))
        .as("Precondition: the API creates the account")
        .isEqualTo(201);
    loginPage.openLoginPage();
    loginPage.login(email, PASSWORD);
    assertThat(homePage.isLoggedIn()).as("Precondition: the API account can log in").isTrue();
    String loggedInText = homePage.getLoggedInText();
    productsPage.openProductsPage();
    List<String> names =
        List.of(
            productsPage.getProductName(FIRST_PRODUCT_ID),
            productsPage.getProductName(SECOND_PRODUCT_ID));
    int expectedTotal =
        rupees(productsPage.getProductPrice(FIRST_PRODUCT_ID))
            + rupees(productsPage.getProductPrice(SECOND_PRODUCT_ID));
    productsPage.addProductToCart(FIRST_PRODUCT_ID);
    productsPage.continueShopping();
    productsPage.addProductToCart(SECOND_PRODUCT_ID);
    productsPage.viewCart();

    // Act – the user checks out, pays and deletes the account in the UI
    cartPage.proceedToCheckout();
    String deliveryAddress = checkoutPage.getDeliveryAddress();
    String invoiceAddress = checkoutPage.getInvoiceAddress();
    List<String> orderedNames = checkoutPage.getProductNames();
    String totalAmount = checkoutPage.getTotalAmount();
    checkoutPage.placeOrder("Please leave the parcel at the door.");
    paymentPage.payWithTestCard(NAME);
    String orderPlacedHeader = paymentPage.getOrderPlacedHeader();
    String orderConfirmation = paymentPage.getOrderConfirmation();
    homePage.deleteAccount();
    String accountDeletedHeader = signupPage.getAccountDeletedHeader();

    // Assert – the UI shows the API data and the order, and the API no longer finds the account
    assertThat(loggedInText).isEqualTo("Logged in as " + NAME);
    assertThat(deliveryAddress).contains(SignupPage.ADDRESS_DETAILS);
    assertThat(invoiceAddress).contains(SignupPage.ADDRESS_DETAILS);
    assertThat(orderedNames).isEqualTo(names);
    assertThat(rupees(totalAmount)).isEqualTo(expectedTotal);
    assertThat(orderPlacedHeader).isEqualTo("ORDER PLACED!");
    assertThat(orderConfirmation).isEqualTo("Congratulations! Your order has been confirmed!");
    assertThat(accountDeletedHeader).isEqualTo("ACCOUNT DELETED!");
    JsonPath user = JsonPath.from(accountApiClient.getUserDetailByEmail(email).asString());
    assertThat(user.getInt("responseCode"))
        .as("The API no longer finds the account")
        .isEqualTo(404);
  }

  /** Reads a price such as "Rs. 500". */
  private static int rupees(String price) {
    return Integer.parseInt(price.replaceAll("\\D", ""));
  }
}
