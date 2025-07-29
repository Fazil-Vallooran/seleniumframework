package seleniumproject.project.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DashboardPage extends BasePage {
    
    @FindBy(css = ".account-navigation")
    private WebElement accountNavigation;
    
    @FindBy(linkText = "Customer info")
    private WebElement customerInfoLink;
    
    @FindBy(linkText = "Orders")
    private WebElement ordersLink;
    
    @FindBy(linkText = "Addresses")
    private WebElement addressesLink;
    
    @FindBy(linkText = "Log out")
    private WebElement logoutLink;
    
    @FindBy(css = ".page-title h1")
    private WebElement pageTitle;
    
    public DashboardPage() {
        super();
        PageFactory.initElements(driver, this);
    }
    
    public boolean isAccountNavigationDisplayed() {
        return isElementDisplayed(accountNavigation);
    }
    
    public void clickCustomerInfo() {
        clickElement(customerInfoLink);
    }
    
    public void clickOrders() {
        clickElement(ordersLink);
    }
    
    public void clickAddresses() {
        clickElement(addressesLink);
    }
    
    public void logout() {
        clickElement(logoutLink);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
}