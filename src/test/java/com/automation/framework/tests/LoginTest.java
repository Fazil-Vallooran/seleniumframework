package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.TheInternetHomePage;
import com.automation.framework.pages.TheInternetLoginPage;
import com.automation.framework.data.JsonDataProvider;
import com.automation.framework.data.XmlDataProvider;
import com.automation.framework.data.ExcelDataProvider;
import com.automation.framework.utils.ReportProvider;
import com.automation.framework.utils.ReportProvider.LogLevel;
import com.automation.framework.utils.ReportProvider.StepStatus;
import com.automation.framework.utils.ReportProvider.StepType;
import com.epam.reportportal.annotations.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LoginTest extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(LoginTest.class);
    
    // JSON Data Provider
    @DataProvider(name = "jsonLoginData")
    public Object[][] getJsonLoginData() {
        JsonDataProvider jsonProvider = new JsonDataProvider();
        return jsonProvider.getTestDataFromJson("src/test/resources/testdata/loginTestData.json");
    }
    
    // XML Data Provider
    @DataProvider(name = "xmlLoginData")
    public Object[][] getXmlLoginData() {
        XmlDataProvider xmlProvider = new XmlDataProvider();
        return xmlProvider.getTestDataFromXml("src/test/resources/testdata/loginTestData.xml", "//testCase");
    }
    
    // Excel Data Provider (when Excel file is available)
    @DataProvider(name = "excelLoginData")
    public Object[][] getExcelLoginData() {
        try {
            ExcelDataProvider excelProvider = new ExcelDataProvider("src/test/resources/testdata/loginTestData.xlsx");
            Object[][] data = excelProvider.getTestData("LoginTests");
            excelProvider.closeWorkbook();
            return data;
        } catch (Exception e) {
            logger.warn("Excel file not found, using fallback data");
            // Return fallback data if Excel file doesn't exist
            return new Object[][]{
                {"admin@example.com", "password123", "success"},
                {"invalid@example.com", "wrongpassword", "failure"}
            };
        }
    }
    
    @Test(dataProvider = "jsonLoginData", description = "Data-driven login test using JSON data")
    public void testLoginWithJsonData(Map<String, Object> testData) {
        ReportProvider.startStep("JSON Data-Driven Login Test", "Testing login functionality with JSON test data", StepType.SCENARIO);
        
        String testCaseId = (String) testData.get("testCaseId");
        ReportProvider.info("Starting test case: " + testCaseId);
        ReportProvider.logTestData("Login Test Data", testData);
        
        // Navigate to the login page directly
        driver.get("https://the-internet.herokuapp.com/login");
        
        TheInternetLoginPage loginPage = new TheInternetLoginPage();
        
        // Extract test data
        String username = (String) testData.get("username");
        String password = (String) testData.get("password");
        String expectedResult = (String) testData.get("expectedResult");
        String description = (String) testData.get("description");
        
        ReportProvider.info("Test Description: " + description);
        
        // Perform test steps with detailed reporting
        performLoginTestWithReporting(loginPage, username, password, expectedResult, description);
        
        ReportProvider.info("JSON data-driven test completed successfully: " + testCaseId);
        ReportProvider.finishStep(StepStatus.PASSED);
    }
    
    @Test(dataProvider = "xmlLoginData", description = "Data-driven login test using XML data")
    public void testLoginWithXmlData(Map<String, String> testData) {
        ReportProvider.startStep("XML Data-Driven Login Test", "Testing login functionality with XML test data", StepType.SCENARIO);
        
        String testCaseId = testData.get("@id");
        String priority = testData.get("@priority");
        String environment = testData.get("environment");
        
        ReportProvider.info("Starting XML test case: " + testCaseId);
        ReportProvider.info("Test Priority: " + priority + " | Environment: " + environment);
        ReportProvider.logTestData("XML Test Data", testData);
        
        // Navigate to the login page directly
        driver.get("https://the-internet.herokuapp.com/login");
        
        TheInternetLoginPage loginPage = new TheInternetLoginPage();
        
        // Extract test data
        String username = testData.get("username");
        String password = testData.get("password");
        String expectedResult = testData.get("expectedResult");
        String description = testData.get("description");
        
        // Perform test steps
        performLoginTestWithReporting(loginPage, username, password, expectedResult, description);
        
        ReportProvider.info("XML data-driven test completed: " + testCaseId);
        ReportProvider.finishStep(StepStatus.PASSED);
    }
    
    @Test(description = "Filtered test cases - only positive tests from JSON")
    public void testPositiveLoginScenariosOnly() {
        logger.info("Running filtered positive login scenarios");
        
        JsonDataProvider jsonProvider = new JsonDataProvider();
        var positiveTests = jsonProvider.getTestCasesByCondition(
            "src/test/resources/testdata/loginTestData.json", 
            "testType", 
            "positive"
        );
        
        for (Map<String, Object> testData : positiveTests) {
            String username = (String) testData.get("username");
            String password = (String) testData.get("password");
            String expectedResult = (String) testData.get("expectedResult");
            String description = (String) testData.get("description");
            
            logger.info("Executing positive test: " + testData.get("testCaseId"));
            
            // Navigate to login page for each test case
            driver.get("https://the-internet.herokuapp.com/login");
            TheInternetLoginPage loginPage = new TheInternetLoginPage();
            
            performLoginTest(loginPage, username, password, expectedResult, description);
        }
        
        logger.info("All positive login scenarios completed");
    }
    
    @Test(description = "XML filtered test - high priority tests only")
    public void testHighPriorityLoginScenarios() {
        logger.info("Running high priority login scenarios from XML");
        
        XmlDataProvider xmlProvider = new XmlDataProvider();
        var highPriorityTests = xmlProvider.getTestCasesByAttribute(
            "src/test/resources/testdata/loginTestData.xml",
            "//testCase",
            "priority",
            "high"
        );
        
        for (Map<String, String> testData : highPriorityTests) {
            String username = testData.get("username");
            String password = testData.get("password");
            String expectedResult = testData.get("expectedResult");
            String description = testData.get("description");
            
            logger.info("Executing high priority test: " + testData.get("@id"));
            
            // Navigate to login page for each test case
            driver.get("https://the-internet.herokuapp.com/login");
            TheInternetLoginPage loginPage = new TheInternetLoginPage();
            
            performLoginTest(loginPage, username, password, expectedResult, description);
        }
        
        logger.info("All high priority scenarios completed");
    }
    
    @Test(groups = {"smoke", "regression"}, priority = 1)
    public void testValidLogin() {
        ReportProvider.info("Starting valid login test with The Internet");
        
        // Navigate to form authentication page
        driver.get("https://the-internet.herokuapp.com/login");
        
        TheInternetLoginPage loginPage = new TheInternetLoginPage();
        
        // Verify login form is displayed
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "Login form should be displayed");
        
        // Perform valid login
        loginPage.performValidLogin();
        
        // Verify successful login
        Assert.assertTrue(loginPage.isUserLoggedIn(), "User should be logged in successfully");
        Assert.assertTrue(loginPage.getSuccessMessage().contains("You logged into a secure area!"), 
                         "Success message should be displayed");
        
        ReportProvider.info("Valid login test completed successfully");
    }
    
    @Test(groups = {"regression"}, priority = 2)
    public void testInvalidLogin() {
        ReportProvider.info("Starting invalid login test");
        
        driver.get("https://the-internet.herokuapp.com/login");
        
        TheInternetLoginPage loginPage = new TheInternetLoginPage();
        
        // Test with invalid credentials
        loginPage.performLogin("invaliduser", "wrongpassword");
        
        // Verify error message
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Your username is invalid!"), 
                         "Error message should indicate invalid username");
        
        ReportProvider.info("Invalid login test completed successfully");
    }
    
    @Test(groups = {"smoke"}, priority = 3)
    public void testLoginPageElements() {
        ReportProvider.info("Starting login page elements test");
        
        driver.get("https://the-internet.herokuapp.com/login");
        
        TheInternetLoginPage loginPage = new TheInternetLoginPage();
        
        // Verify all login form elements are present
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "Login form should be displayed");
        Assert.assertEquals(loginPage.getPageTitle(), "Login Page", "Page title should be 'Login Page'");
        
        ReportProvider.info("Login page elements test completed successfully");
    }
    
    @Step("Perform login test with enhanced reporting")
    private void performLoginTestWithReporting(TheInternetLoginPage loginPage, 
                                             String username, String password, 
                                             String expectedResult, String description) {
        
        ReportProvider.startStep("Login Test Execution", description);
        
        try {
            // Step 1: Verify login page is loaded
            ReportProvider.startStep("Login Page Verification");
            ReportProvider.logUiAction("Verify", "Login form elements");
            boolean formDisplayed = loginPage.isLoginFormDisplayed();
            ReportProvider.logUiVerification("Login form is displayed", formDisplayed);
            Assert.assertTrue(formDisplayed, "Login form should be displayed");
            ReportProvider.finishStep(StepStatus.PASSED);
            
            // Step 2: Enter credentials
            ReportProvider.startStep("Enter Login Credentials");
            ReportProvider.info("Entering username: " + username);
            ReportProvider.debug("Password length: " + password.length() + " characters");
            
            loginPage.enterUsername(username);
            ReportProvider.logUiAction("Enter text", "Username field");
            
            loginPage.enterPassword(password);
            ReportProvider.logUiAction("Enter text", "Password field");
            
            loginPage.clickLoginButton();
            ReportProvider.logUiAction("Click", "Login button");
            ReportProvider.finishStep(StepStatus.PASSED);
            
            // Step 3: Verify result
            ReportProvider.startStep("Verify Login Result", "Expected: " + expectedResult);
            
            if ("success".equals(expectedResult)) {
                verifySuccessfulLoginWithReporting(loginPage);
            } else if ("failure".equals(expectedResult)) {
                verifyLoginFailureWithReporting(loginPage);
            }
            
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("Login test failed", e);
            ReportProvider.finishStep(StepStatus.FAILED, "Test execution failed: " + e.getMessage());
            throw e;
        } finally {
            ReportProvider.finishStep(StepStatus.PASSED);
        }
    }
    
    private void verifySuccessfulLoginWithReporting(TheInternetLoginPage loginPage) {
        ReportProvider.info("Verifying successful login indicators");
        
        boolean logoutButtonDisplayed = loginPage.isLogoutButtonDisplayed();
        ReportProvider.logUiVerification("Logout button is displayed", logoutButtonDisplayed);
        
        if (logoutButtonDisplayed) {
            ReportProvider.info("✅ Login successful - user authenticated");
        } else {
            ReportProvider.error("❌ Login failed - logout button not found");
        }
        
        Assert.assertTrue(logoutButtonDisplayed, "Logout button should be displayed after successful login");
    }
    
    private void verifyLoginFailureWithReporting(TheInternetLoginPage loginPage) {
        ReportProvider.info("Verifying login failure indicators");
        
        String errorMessage = loginPage.getErrorMessage();
        ReportProvider.debug("Error message received: " + errorMessage);
        
        boolean hasErrorMessage = errorMessage.contains("unsuccessful") || errorMessage.contains("invalid");
        ReportProvider.logUiVerification("Error message displayed", hasErrorMessage);
        
        if (hasErrorMessage) {
            ReportProvider.info("✅ Login failure correctly detected");
        } else {
            ReportProvider.warn("⚠️ Expected error message not found");
        }
        
        Assert.assertTrue(hasErrorMessage, "Error message should indicate login failure");
    }
    
    @Step("Perform login test with data: {username}, expected: {expectedResult}")
    private void performLoginTest(TheInternetLoginPage loginPage, 
                                String username, String password, 
                                String expectedResult, String description) {
        
        logger.info("Test Description: " + description);
        
        // Verify login form is displayed
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "Login form should be displayed");
        
        // Perform login
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
        
        // Verify result based on expected outcome
        if ("success".equals(expectedResult)) {
            verifySuccessfulLogin(loginPage);
        } else if ("failure".equals(expectedResult)) {
            verifyLoginFailure(loginPage);
        }
    }
    
    @Step("Verify successful login")
    private void verifySuccessfulLogin(TheInternetLoginPage loginPage) {
        Assert.assertTrue(loginPage.isLogoutButtonDisplayed(), 
            "Logout button should be displayed after successful login");
        Assert.assertTrue(loginPage.isSuccessMessageDisplayed(),
            "Success message should be displayed after successful login");
        logger.info("Login success verified");
    }
    
    @Step("Verify login failure")
    private void verifyLoginFailure(TheInternetLoginPage loginPage) {
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("username is invalid") || errorMessage.contains("password is invalid"), 
            "Error message should indicate login failure");
        logger.info("Login failure verified: " + errorMessage);
    }
}