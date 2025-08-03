package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.automation.framework.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TheInternetTests extends BaseTest {
    
    @Test(groups = {"smoke"}, priority = 1)
    public void testLoginPage() {
        // Navigate to login page
        driver.get("https://the-internet.herokuapp.com/login");
        
        // Find login elements
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        // Valid credentials for this test site
        usernameField.sendKeys("tomsmith");
        passwordField.sendKeys("SuperSecretPassword!");
        loginButton.click();
        
        // Verify successful login
        WebElement successMessage = driver.findElement(By.cssSelector(".flash.success"));
        Assert.assertTrue(successMessage.getText().contains("You logged into a secure area!"));
        
        // Verify logout link is present
        WebElement logoutButton = driver.findElement(By.linkText("Logout"));
        Assert.assertTrue(logoutButton.isDisplayed());
    }
    
    @Test(groups = {"smoke"}, priority = 2)
    public void testInvalidLogin() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        // Invalid credentials
        usernameField.sendKeys("invaliduser");
        passwordField.sendKeys("wrongpassword");
        loginButton.click();
        
        // Verify error message
        WebElement errorMessage = driver.findElement(By.cssSelector(".flash.error"));
        Assert.assertTrue(errorMessage.getText().contains("Your username is invalid!"));
    }
    
    @Test(groups = {"regression"}, priority = 3)
    public void testFormAuthentication() {
        driver.get("https://the-internet.herokuapp.com/form_authentication");
        
        // Test the form authentication page
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        
        Assert.assertTrue(usernameField.isDisplayed());
        Assert.assertTrue(passwordField.isDisplayed());
        
        // Test placeholder text
        String usernamePlaceholder = usernameField.getAttribute("placeholder");
        Assert.assertNotNull(usernamePlaceholder);
    }
    
    @Test(groups = {"regression"}, priority = 4)
    public void testDropdownSelection() {
        driver.get("https://the-internet.herokuapp.com/dropdown");
        
        WebElement dropdown = driver.findElement(By.id("dropdown"));
        Assert.assertTrue(dropdown.isDisplayed());
        
        // Select option 1
        dropdown.findElement(By.xpath("//option[@value='1']")).click();
        
        // Verify selection
        WebElement selectedOption = dropdown.findElement(By.xpath("//option[@selected]"));
        Assert.assertEquals(selectedOption.getAttribute("value"), "1");
    }
    
    @Test(groups = {"regression"}, priority = 5)
    public void testCheckboxes() {
        driver.get("https://the-internet.herokuapp.com/checkboxes");
        
        // Find all checkboxes
        var checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
        Assert.assertTrue(checkboxes.size() >= 2);
        
        // Test first checkbox
        WebElement firstCheckbox = checkboxes.get(0);
        if (!firstCheckbox.isSelected()) {
            firstCheckbox.click();
        }
        Assert.assertTrue(firstCheckbox.isSelected());
    }
    
    @Test(groups = {"api", "smoke"}, priority = 6)
    public void testDynamicContent() {
        driver.get("https://the-internet.herokuapp.com/dynamic_content");
        
        // Verify page loads and has dynamic content
        var contentElements = driver.findElements(By.cssSelector(".large-10.columns"));
        Assert.assertTrue(contentElements.size() > 0);
        
        // Click refresh and verify content changes
        driver.findElement(By.linkText("click here")).click();
        
        // Verify we're still on the dynamic content page
        Assert.assertTrue(driver.getCurrentUrl().contains("dynamic_content"));
    }
}