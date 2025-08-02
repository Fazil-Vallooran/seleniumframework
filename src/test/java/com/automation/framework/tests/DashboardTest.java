package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.HomePage;
import com.automation.framework.pages.LoginPage;
import com.automation.framework.pages.DashboardPage;
import com.automation.framework.utils.ConfigReader;
import com.epam.reportportal.annotations.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DashboardTest extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(DashboardTest.class);
    
    @Test(priority = 1, description = "Verify dashboard navigation elements")
    public void testDashboardNavigation() {
        logger.info("Starting dashboard navigation test");
        
        // Login first
        performLogin();
        
        DashboardPage dashboardPage = new DashboardPage();
        
        // Verify dashboard elements
        verifyDashboardElements(dashboardPage);
        
        // Test navigation links
        testNavigationLinks(dashboardPage);
        
        logger.info("Dashboard navigation test completed successfully");
    }
    
    @Test(priority = 2, description = "Verify user can logout from dashboard")
    public void testLogout() {
        logger.info("Starting logout test");
        
        // Login first
        performLogin();
        
        DashboardPage dashboardPage = new DashboardPage();
        
        // Perform logout
        performLogout(dashboardPage);
        
        // Verify logout successful
        verifyLogoutSuccessful();
        
        logger.info("Logout test completed successfully");
    }
    
    @Step("Perform login to access dashboard")
    private void performLogin() {
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        
        String email = ConfigReader.getProperty("testEmail");
        String password = ConfigReader.getProperty("testPassword");
        
        loginPage.login(email, password);
        logger.info("Login performed for dashboard access");
    }
    
    @Step("Verify dashboard elements are displayed")
    private void verifyDashboardElements(DashboardPage dashboardPage) {
        Assert.assertTrue(dashboardPage.isAccountNavigationDisplayed(), 
            "Account navigation should be displayed");
        
        String pageTitle = dashboardPage.getPageTitle();
        Assert.assertNotNull(pageTitle, "Page title should not be null");
        
        logger.info("Dashboard elements verified successfully");
    }
    
    @Step("Test dashboard navigation links")
    private void testNavigationLinks(DashboardPage dashboardPage) {
        // Test Customer Info link
        dashboardPage.clickCustomerInfo();
        logger.info("Customer Info link clicked");
        
        // Test Orders link
        dashboardPage.clickOrders();
        logger.info("Orders link clicked");
        
        // Test Addresses link
        dashboardPage.clickAddresses();
        logger.info("Addresses link clicked");
    }
    
    @Step("Perform logout")
    private void performLogout(DashboardPage dashboardPage) {
        dashboardPage.logout();
        logger.info("Logout performed");
    }
    
    @Step("Verify logout was successful")
    private void verifyLogoutSuccessful() {
        HomePage homePage = new HomePage();
        Assert.assertTrue(homePage.getPageTitle().contains("nopCommerce"), 
            "Should be redirected to home page after logout");
        logger.info("Logout verification successful");
    }
}