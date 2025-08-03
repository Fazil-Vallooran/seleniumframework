package com.automation.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

public class TheInternetElementsPage extends BasePage {
    
    // Dropdown elements
    @FindBy(id = "dropdown")
    private WebElement dropdown;
    
    // Checkbox elements
    @FindBy(css = "input[type='checkbox']")
    private List<WebElement> checkboxes;
    
    // Dynamic content elements
    @FindBy(css = ".large-10.columns")
    private List<WebElement> dynamicContentElements;
    
    @FindBy(linkText = "click here")
    private WebElement refreshLink;
    
    // File upload elements
    @FindBy(id = "file-upload")
    private WebElement fileUploadInput;
    
    @FindBy(id = "file-submit")
    private WebElement uploadButton;
    
    @FindBy(id = "uploaded-files")
    private WebElement uploadedFiles;
    
    // JavaScript alerts elements
    @FindBy(css = "button[onclick='jsAlert()']")
    private WebElement jsAlertButton;
    
    @FindBy(css = "button[onclick='jsConfirm()']")
    private WebElement jsConfirmButton;
    
    @FindBy(css = "button[onclick='jsPrompt()']")
    private WebElement jsPromptButton;
    
    @FindBy(id = "result")
    private WebElement alertResult;
    
    public TheInternetElementsPage() {
        super();
        PageFactory.initElements(getDriver(), this);
    }
    
    // Dropdown methods
    public void selectDropdownOption(String value) {
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }
    
    public void selectDropdownByText(String text) {
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }
    
    public String getSelectedDropdownValue() {
        Select select = new Select(dropdown);
        return select.getFirstSelectedOption().getAttribute("value");
    }
    
    public String getSelectedDropdownText() {
        Select select = new Select(dropdown);
        return select.getFirstSelectedOption().getText();
    }
    
    // Checkbox methods
    public void checkCheckbox(int index) {
        if (index < checkboxes.size() && !checkboxes.get(index).isSelected()) {
            checkboxes.get(index).click();
        }
    }
    
    public void uncheckCheckbox(int index) {
        if (index < checkboxes.size() && checkboxes.get(index).isSelected()) {
            checkboxes.get(index).click();
        }
    }
    
    public boolean isCheckboxSelected(int index) {
        return index < checkboxes.size() && checkboxes.get(index).isSelected();
    }
    
    public int getCheckboxCount() {
        return checkboxes.size();
    }
    
    // Dynamic content methods
    public void refreshDynamicContent() {
        clickElement(refreshLink);
    }
    
    public int getDynamicContentCount() {
        return dynamicContentElements.size();
    }
    
    public String getDynamicContentText(int index) {
        if (index < dynamicContentElements.size()) {
            return getText(dynamicContentElements.get(index));
        }
        return "";
    }
    
    // File upload methods
    public void uploadFile(String filePath) {
        fileUploadInput.sendKeys(filePath);
        clickElement(uploadButton);
    }
    
    public String getUploadedFileName() {
        return getText(uploadedFiles);
    }
    
    public boolean isFileUploaded() {
        return isElementDisplayed(uploadedFiles) && !getText(uploadedFiles).isEmpty();
    }
    
    // JavaScript alerts methods
    public void clickJSAlert() {
        clickElement(jsAlertButton);
    }
    
    public void clickJSConfirm() {
        clickElement(jsConfirmButton);
    }
    
    public void clickJSPrompt() {
        clickElement(jsPromptButton);
    }
    
    public String getAlertResult() {
        return getText(alertResult);
    }
    
    public boolean isAlertResultDisplayed() {
        return isElementDisplayed(alertResult);
    }
}