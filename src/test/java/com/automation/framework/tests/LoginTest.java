package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.HomePage;
import com.automation.framework.pages.LoginPage;
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
        
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        
        // Extract test data
        String username = (String) testData.get("username");
        String password = (String) testData.get("password");
        String expectedResult = (String) testData.get("expectedResult");
        String description = (String) testData.get("description");
        
        ReportProvider.info("Test Description: " + description);
        
        // Perform test steps with detailed reporting
        performLoginTestWithReporting(homePage, loginPage, username, password, expectedResult, description);
        
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
        
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        
        // Extract test data
        String username = testData.get("username");
        String password = testData.get("password");
        String expectedResult = testData.get("expectedResult");
        String description = testData.get("description");
        
        // Perform test steps
        performLoginTestWithReporting(homePage, loginPage, username, password, expectedResult, description);
        
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
        
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        
        for (Map<String, Object> testData : positiveTests) {
            String username = (String) testData.get("username");
            String password = (String) testData.get("password");
            String expectedResult = (String) testData.get("expectedResult");
            String description = (String) testData.get("description");
            
            logger.info("Executing positive test: " + testData.get("testCaseId"));
            performLoginTest(homePage, loginPage, username, password, expectedResult, description);
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
        
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        
        for (Map<String, String> testData : highPriorityTests) {
            String username = testData.get("username");
            String password = testData.get("password");
            String expectedResult = testData.get("expectedResult");
            String description = testData.get("description");
            
            logger.info("Executing high priority test: " + testData.get("@id"));
            performLoginTest(homePage, loginPage, username, password, expectedResult, description);
        }
        
        logger.info("All high priority scenarios completed");
    }
    
    @Step("Perform login test with enhanced reporting")
    private void performLoginTestWithReporting(HomePage homePage, LoginPage loginPage, 
                                             String username, String password, 
                                             String expectedResult, String description) {
        
        ReportProvider.startStep("Login Test Execution", description);
        
        try {
            // Step 1: Verify home page
            ReportProvider.startStep("Home Page Verification");
            ReportProvider.logUiAction("Verify", "Home page logo");
            boolean logoDisplayed = homePage.isLogoDisplayed();
            ReportProvider.logUiVerification("Logo is displayed", logoDisplayed);
            Assert.assertTrue(logoDisplayed, "Home page logo should be displayed");
            ReportProvider.finishStep(StepStatus.PASSED);
            
            // Step 2: Navigate to login page
            ReportProvider.startStep("Navigate to Login Page");
            ReportProvider.logUiAction("Click", "Login link");
            homePage.clickLoginLink();
            ReportProvider.info("Successfully navigated to login page");
            ReportProvider.finishStep(StepStatus.PASSED);
            
            // Step 3: Enter credentials
            ReportProvider.startStep("Enter Login Credentials");
            ReportProvider.info("Entering username: " + username);
            ReportProvider.debug("Password length: " + password.length() + " characters");
            
            loginPage.enterEmail(username);
            ReportProvider.logUiAction("Enter text", "Email field");
            
            loginPage.enterPassword(password);
            ReportProvider.logUiAction("Enter text", "Password field");
            
            loginPage.clickLoginButton();
            ReportProvider.logUiAction("Click", "Login button");
            ReportProvider.finishStep(StepStatus.PASSED);
            
            // Step 4: Verify result
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
    
    private void verifySuccessfulLoginWithReporting(LoginPage loginPage) {
        ReportProvider.info("Verifying successful login indicators");
        
        boolean logoutLinkDisplayed = loginPage.isLogoutLinkDisplayed();
        ReportProvider.logUiVerification("Logout link is displayed", logoutLinkDisplayed);
        
        if (logoutLinkDisplayed) {
            ReportProvider.info("✅ Login successful - user authenticated");
        } else {
            ReportProvider.error("❌ Login failed - logout link not found");
        }
        
        Assert.assertTrue(logoutLinkDisplayed, "Logout link should be displayed after successful login");
    }
    
    private void verifyLoginFailureWithReporting(LoginPage loginPage) {
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
    private void performLoginTest(HomePage homePage, LoginPage loginPage, 
                                String username, String password, 
                                String expectedResult, String description) {
        
        logger.info("Test Description: " + description);
        
        // Navigate to login page
        verifyHomePageLoaded(homePage);
        homePage.clickLoginLink();
        
        // Perform login
        loginPage.enterEmail(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
        
        // Verify result based on expected outcome
        if ("success".equals(expectedResult)) {
            verifySuccessfulLogin(loginPage);
        } else if ("failure".equals(expectedResult)) {
            verifyLoginFailure(loginPage);
        }
    }
    
    @Step("Verify home page is loaded")
    private void verifyHomePageLoaded(HomePage homePage) {
        Assert.assertTrue(homePage.isLogoDisplayed(), "Home page logo should be displayed");
        logger.info("Home page verified");
    }
    
    @Step("Verify successful login")
    private void verifySuccessfulLogin(LoginPage loginPage) {
        Assert.assertTrue(loginPage.isLogoutLinkDisplayed(), 
            "Logout link should be displayed after successful login");
        logger.info("Login success verified");
    }
    
    @Step("Verify login failure")
    private void verifyLoginFailure(LoginPage loginPage) {
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("unsuccessful") || errorMessage.contains("invalid"), 
            "Error message should indicate login failure");
        logger.info("Login failure verified: " + errorMessage);
    }
}
