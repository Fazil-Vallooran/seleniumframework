package seleniumproject.project.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import seleniumproject.project.base.BaseTest;
import seleniumproject.project.pages.HomePage;
import seleniumproject.project.pages.LoginPage;
import seleniumproject.project.pages.DashboardPage;
import seleniumproject.project.utils.ConfigReader;
import com.epam.reportportal.annotations.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginTest extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(LoginTest.class);
    
    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        logger.info("Starting valid login test");
        
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = new DashboardPage();
        
        // Step 1: Verify home page is loaded
        verifyHomePageLoaded(homePage);
        
        // Step 2: Navigate to login page
        navigateToLoginPage(homePage, loginPage);
        
        // Step 3: Perform login
        performLogin(loginPage);
        
        // Step 4: Verify successful login
        verifySuccessfulLogin(dashboardPage);
        
        logger.info("Valid login test completed successfully");
    }
    
    @Test(priority = 2, description = "Verify login failure with invalid credentials")
    public void testInvalidLogin() {
        logger.info("Starting invalid login test");
        
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        
        // Step 1: Navigate to login page
        homePage.clickLoginLink();
        
        // Step 2: Enter invalid credentials
        enterInvalidCredentials(loginPage);
        
        // Step 3: Verify error message
        verifyLoginError(loginPage);
        
        logger.info("Invalid login test completed successfully");
    }
    
    @Step("Verify home page is loaded")
    private void verifyHomePageLoaded(HomePage homePage) {
        Assert.assertTrue(homePage.isLogoDisplayed(), "Home page logo should be displayed");
        Assert.assertTrue(homePage.getPageTitle().contains("nopCommerce"), "Page title should contain nopCommerce");
        logger.info("Home page loaded successfully");
    }
    
    @Step("Navigate to login page")
    private void navigateToLoginPage(HomePage homePage, LoginPage loginPage) {
        homePage.clickLoginLink();
        logger.info("Navigated to login page");
    }
    
    @Step("Perform login with valid credentials")
    private void performLogin(LoginPage loginPage) {
        String email = ConfigReader.getProperty("testEmail");
        String password = ConfigReader.getProperty("testPassword");
        
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
        
        logger.info("Login attempted with email: " + email);
    }
    
    @Step("Verify successful login")
    private void verifySuccessfulLogin(DashboardPage dashboardPage) {
        Assert.assertTrue(dashboardPage.isAccountNavigationDisplayed(), 
            "Account navigation should be displayed after successful login");
        logger.info("Login verification successful");
    }
    
    @Step("Enter invalid credentials")
    private void enterInvalidCredentials(LoginPage loginPage) {
        loginPage.enterEmail("invalid@email.com");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLoginButton();
        logger.info("Invalid credentials entered");
    }
    
    @Step("Verify login error message")
    private void verifyLoginError(LoginPage loginPage) {
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("unsuccessful"), 
            "Error message should indicate unsuccessful login");
        logger.info("Login error verified: " + errorMessage);
    }
}