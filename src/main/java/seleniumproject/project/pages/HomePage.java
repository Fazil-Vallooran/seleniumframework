package seleniumproject.project.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends BasePage {
    
    @FindBy(css = ".header-logo")
    private WebElement logo;
    
    @FindBy(linkText = "Register")
    private WebElement registerLink;
    
    @FindBy(linkText = "Log in")
    private WebElement loginLink;
    
    @FindBy(css = ".search-box-text")
    private WebElement searchBox;
    
    @FindBy(css = ".search-box-button")
    private WebElement searchButton;
    
    @FindBy(css = ".product-item")
    private WebElement firstProduct;
    
    @FindBy(css = ".cart-label")
    private WebElement shoppingCartLink;
    
    public HomePage() {
        super();
        PageFactory.initElements(driver, this);
    }
    
    public boolean isLogoDisplayed() {
        return isElementDisplayed(logo);
    }
    
    public void clickRegisterLink() {
        clickElement(registerLink);
    }
    
    public void clickLoginLink() {
        clickElement(loginLink);
    }
    
    public void searchForProduct(String productName) {
        sendKeys(searchBox, productName);
        clickElement(searchButton);
    }
    
    public void clickFirstProduct() {
        clickElement(firstProduct);
    }
    
    public void clickShoppingCart() {
        clickElement(shoppingCartLink);
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
}