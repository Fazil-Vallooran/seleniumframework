package com.automation.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class TheInternetHomePage extends BasePage {
    
    @FindBy(css = "h1")
    private WebElement pageTitle;
    
    @FindBy(linkText = "Form Authentication")
    private WebElement formAuthenticationLink;
    
    @FindBy(linkText = "Basic Auth")
    private WebElement basicAuthLink;
    
    @FindBy(linkText = "Checkboxes")
    private WebElement checkboxesLink;
    
    @FindBy(linkText = "Dropdown")
    private WebElement dropdownLink;
    
    @FindBy(linkText = "Dynamic Content")
    private WebElement dynamicContentLink;
    
    @FindBy(linkText = "Dynamic Controls")
    private WebElement dynamicControlsLink;
    
    @FindBy(linkText = "File Upload")
    private WebElement fileUploadLink;
    
    @FindBy(linkText = "JavaScript Alerts")
    private WebElement javascriptAlertsLink;
    
    @FindBy(css = "ul li a")
    private WebElement allLinks;
    
    public TheInternetHomePage() {
        super();
        PageFactory.initElements(getDriver(), this);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    public boolean isPageTitleDisplayed() {
        return isElementDisplayed(pageTitle);
    }
    
    public void clickFormAuthentication() {
        clickElement(formAuthenticationLink);
    }
    
    public void clickBasicAuth() {
        clickElement(basicAuthLink);
    }
    
    public void clickCheckboxes() {
        clickElement(checkboxesLink);
    }
    
    public void clickDropdown() {
        clickElement(dropdownLink);
    }
    
    public void clickDynamicContent() {
        clickElement(dynamicContentLink);
    }
    
    public void clickDynamicControls() {
        clickElement(dynamicControlsLink);
    }
    
    public void clickFileUpload() {
        clickElement(fileUploadLink);
    }
    
    public void clickJavaScriptAlerts() {
        clickElement(javascriptAlertsLink);
    }
    
    public void navigateToPage(String pageName) {
        switch (pageName.toLowerCase()) {
            case "form authentication":
                clickFormAuthentication();
                break;
            case "checkboxes":
                clickCheckboxes();
                break;
            case "dropdown":
                clickDropdown();
                break;
            case "dynamic content":
                clickDynamicContent();
                break;
            default:
                throw new IllegalArgumentException("Page not supported: " + pageName);
        }
    }
}