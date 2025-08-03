package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.TheInternetElementsPage;
import com.automation.framework.pages.TheInternetHomePage;
import com.automation.framework.utils.ReportProvider;
import org.openqa.selenium.Alert;

public class DashboardTest extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, priority = 1)
    public void testTheInternetHomePage() {
        ReportProvider.info("Starting The Internet home page test");
        
        // Navigate to The Internet home page
        driver.get("https://the-internet.herokuapp.com/");
        
        TheInternetHomePage homePage = new TheInternetHomePage();
        
        // Verify home page elements
        Assert.assertTrue(homePage.isPageTitleDisplayed(), "Page title should be displayed");
        Assert.assertEquals(homePage.getPageTitle(), "Welcome to the-internet", "Page title should match");
        
        ReportProvider.info("The Internet home page test completed successfully");
    }
    
    @Test(groups = {"regression"}, priority = 2)
    public void testDropdownFunctionality() {
        ReportProvider.info("Starting dropdown functionality test");
        
        // Navigate to dropdown page
        driver.get("https://the-internet.herokuapp.com/dropdown");
        
        TheInternetElementsPage elementsPage = new TheInternetElementsPage();
        
        // Test dropdown selection
        elementsPage.selectDropdownOption("1");
        Assert.assertEquals(elementsPage.getSelectedDropdownValue(), "1", "Option 1 should be selected");
        
        elementsPage.selectDropdownOption("2");
        Assert.assertEquals(elementsPage.getSelectedDropdownValue(), "2", "Option 2 should be selected");
        
        ReportProvider.info("Dropdown functionality test completed successfully");
    }
    
    @Test(groups = {"smoke"}, priority = 3)
    public void testCheckboxInteractions() {
        ReportProvider.info("Starting checkbox interactions test");
        
        // Navigate to checkboxes page
        driver.get("https://the-internet.herokuapp.com/checkboxes");
        
        TheInternetElementsPage elementsPage = new TheInternetElementsPage();
        
        // Test checkbox interactions
        int checkboxCount = elementsPage.getCheckboxCount();
        Assert.assertTrue(checkboxCount >= 2, "Should have at least 2 checkboxes");
        
        // Test first checkbox
        elementsPage.checkCheckbox(0);
        Assert.assertTrue(elementsPage.isCheckboxSelected(0), "First checkbox should be selected");
        
        // Test second checkbox
        if (checkboxCount > 1) {
            boolean wasSelected = elementsPage.isCheckboxSelected(1);
            if (wasSelected) {
                elementsPage.uncheckCheckbox(1);
                Assert.assertFalse(elementsPage.isCheckboxSelected(1), "Second checkbox should be unselected");
            } else {
                elementsPage.checkCheckbox(1);
                Assert.assertTrue(elementsPage.isCheckboxSelected(1), "Second checkbox should be selected");
            }
        }
        
        ReportProvider.info("Checkbox interactions test completed successfully");
    }
    
    @Test(groups = {"regression"}, priority = 4)
    public void testDynamicContent() {
        ReportProvider.info("Starting dynamic content test");
        
        // Navigate to dynamic content page
        driver.get("https://the-internet.herokuapp.com/dynamic_content");
        
        TheInternetElementsPage elementsPage = new TheInternetElementsPage();
        
        // Verify dynamic content exists
        int contentCount = elementsPage.getDynamicContentCount();
        Assert.assertTrue(contentCount > 0, "Should have dynamic content elements");
        
        // Refresh and verify content still exists
        elementsPage.refreshDynamicContent();
        
        // Verify we're still on the dynamic content page
        Assert.assertTrue(driver.getCurrentUrl().contains("dynamic_content"), 
                         "Should still be on dynamic content page after refresh");
        
        ReportProvider.info("Dynamic content test completed successfully");
    }
    
    @Test(groups = {"smoke"}, priority = 5)
    public void testJavaScriptAlerts() {
        ReportProvider.info("Starting JavaScript alerts test");
        
        // Navigate to JavaScript alerts page
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        TheInternetElementsPage elementsPage = new TheInternetElementsPage();
        
        // Test JS Alert
        elementsPage.clickJSAlert();
        Alert alert = driver.switchTo().alert();
        Assert.assertEquals(alert.getText(), "I am a JS Alert", "Alert text should match");
        alert.accept();
        
        // Verify result
        Assert.assertTrue(elementsPage.isAlertResultDisplayed(), "Alert result should be displayed");
        Assert.assertTrue(elementsPage.getAlertResult().contains("You successfuly clicked an alert"), 
                         "Alert result should indicate success");
        
        ReportProvider.info("JavaScript alerts test completed successfully");
    }
}