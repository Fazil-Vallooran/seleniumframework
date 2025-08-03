package com.automation.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class TheInternetLoginPage extends BasePage {
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    @FindBy(css = ".flash.success")
    private WebElement successMessage;
    
    @FindBy(css = ".flash.error")
    private WebElement errorMessage;
    
    @FindBy(linkText = "Logout")
    private WebElement logoutButton;
    
    @FindBy(css = "h2")
    private WebElement pageTitle;
    
    @FindBy(css = ".flash")
    private WebElement flashMessage;
    
    // Valid credentials for The Internet site
    public static final String VALID_USERNAME = "tomsmith";
    public static final String VALID_PASSWORD = "SuperSecretPassword!";
    
    public TheInternetLoginPage() {
        super();
        PageFactory.initElements(getDriver(), this);
    }
    
    public void enterUsername(String username) {
        sendKeys(usernameField, username);
    }
    
    public void enterPassword(String password) {
        sendKeys(passwordField, password);
    }
    
    public void clickLoginButton() {
        clickElement(loginButton);
    }
    
    public void clickLogoutButton() {
        clickElement(logoutButton);
    }
    
    public void performLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
    
    public void performValidLogin() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
    }
    
    public boolean isSuccessMessageDisplayed() {
        return isElementDisplayed(successMessage);
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public boolean isLogoutButtonDisplayed() {
        return isElementDisplayed(logoutButton);
    }
    
    public String getSuccessMessage() {
        return getText(successMessage);
    }
    
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    public String getFlashMessage() {
        return getText(flashMessage);
    }
    
    public boolean isLoginFormDisplayed() {
        return isElementDisplayed(usernameField) && 
               isElementDisplayed(passwordField) && 
               isElementDisplayed(loginButton);
    }
    
    public boolean isUserLoggedIn() {
        return isLogoutButtonDisplayed() && isSuccessMessageDisplayed();
    }
}