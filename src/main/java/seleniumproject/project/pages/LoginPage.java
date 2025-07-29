package seleniumproject.project.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {
    
    @FindBy(linkText = "Log in")
    private WebElement loginLink;
    
    @FindBy(id = "Email")
    private WebElement emailField;
    
    @FindBy(id = "Password")
    private WebElement passwordField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    @FindBy(css = ".validation-summary-errors")
    private WebElement errorMessage;
    
    @FindBy(linkText = "Log out")
    private WebElement logoutLink;
    
    public LoginPage() {
        super();
        PageFactory.initElements(driver, this);
    }
    
    public void clickLoginLink() {
        clickElement(loginLink);
    }
    
    public void enterEmail(String email) {
        sendKeys(emailField, email); 
    }
    
    public void enterPassword(String password) {
        sendKeys(passwordField, password);
    }
    
    public void clickLoginButton() {
        clickElement(loginButton);
    }
    
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    public boolean isLogoutLinkDisplayed() {
        return isElementDisplayed(logoutLink);
    }
    
    public void login(String email, String password) {
        clickLoginLink();
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }
}