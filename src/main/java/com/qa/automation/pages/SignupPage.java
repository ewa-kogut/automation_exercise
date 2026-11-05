package com.qa.automation.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The pages after the signup form: account details, "Account Created!", "Account Deleted!". */
public class SignupPage extends BasePage {

  /** How the checkout page shows the details from {@link #fillAddressInformation()}. */
  public static final List<String> ADDRESS_DETAILS =
      List.of(
          "Mrs. Test User",
          "None",
          "1 Test Street",
          "Flat 1",
          "Toronto Ontario",
          "12345",
          "Canada",
          "123456789");

  private final By accountInformationHeader = By.cssSelector(".login-form h2 b");

  private final By titleMrs = By.cssSelector("label[for='id_gender2']");

  private final By passwordInput = By.cssSelector("input[data-qa='password']");

  private final By birthDay = By.cssSelector("select[data-qa='days']");

  private final By birthMonth = By.cssSelector("select[data-qa='months']");

  private final By birthYear = By.cssSelector("select[data-qa='years']");

  private final By newsletterCheckbox = By.cssSelector("label[for='newsletter']");

  private final By partnerOffersCheckbox = By.cssSelector("label[for='optin']");

  private final By firstNameInput = By.cssSelector("input[data-qa='first_name']");

  private final By lastNameInput = By.cssSelector("input[data-qa='last_name']");

  private final By companyInput = By.cssSelector("input[data-qa='company']");

  private final By addressInput = By.cssSelector("input[data-qa='address']");

  private final By address2Input = By.cssSelector("input[data-qa='address2']");

  private final By countrySelect = By.cssSelector("select[data-qa='country']");

  private final By stateInput = By.cssSelector("input[data-qa='state']");

  private final By cityInput = By.cssSelector("input[data-qa='city']");

  private final By zipcodeInput = By.cssSelector("input[data-qa='zipcode']");

  private final By mobileNumberInput = By.cssSelector("input[data-qa='mobile_number']");

  private final By createAccountButton = By.cssSelector("button[data-qa='create-account']");

  private final By accountCreatedHeader = By.cssSelector("h2[data-qa='account-created']");

  private final By accountDeletedHeader = By.cssSelector("h2[data-qa='account-deleted']");

  private final By continueButton = By.cssSelector("a[data-qa='continue-button']");

  public SignupPage(WebDriver driver) {
    super(driver);
  }

  public String getAccountInformationHeader() {
    return getText(accountInformationHeader);
  }

  /** The signup form has already filled in the name and email. */
  public void fillAccountInformation(String password) {
    click(titleMrs);
    type(passwordInput, password);
    selectByText(birthDay, "1");
    selectByText(birthMonth, "January");
    selectByText(birthYear, "1990");
    click(newsletterCheckbox);
    click(partnerOffersCheckbox);
  }

  public void fillAddressInformation() {
    type(firstNameInput, "Test");
    type(lastNameInput, "User");
    type(companyInput, "None");
    type(addressInput, "1 Test Street");
    type(address2Input, "Flat 1");
    selectByText(countrySelect, "Canada");
    type(stateInput, "Ontario");
    type(cityInput, "Toronto");
    type(zipcodeInput, "12345");
    type(mobileNumberInput, "123456789");
  }

  public void clickCreateAccount() {
    click(createAccountButton);
  }

  public String getAccountCreatedHeader() {
    return getText(accountCreatedHeader);
  }

  public String getAccountDeletedHeader() {
    return getText(accountDeletedHeader);
  }

  public void clickContinue() {
    click(continueButton);
  }
}
