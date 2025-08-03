package com.automation.framework.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterSuite;
import com.automation.framework.utils.ConfigReader;
import com.automation.framework.utils.ScreenshotUtils;
import com.automation.framework.utils.ReportProvider;

/**
 * Base test class providing common setup and teardown functionality.
 * Thread-safe implementation using ThreadLocal WebDriver instances.
 * 
 * @author Automation Framework
 * @version 2.0
 */
public class BaseTest {
    
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        try {
            String browserName = (browser != null && !browser.trim().isEmpty()) ? browser : ConfigReader.getProperty("browser");
            
            // Fallback to chrome if still null or empty
            if (browserName == null || browserName.trim().isEmpty()) {
                browserName = "chrome";
            }
            
            ReportProvider.info("Setting up test with browser: " + browserName);
            
            DriverManager.setDriver(browserName);
            this.driver = DriverManager.getDriver();
            
            if (this.driver != null) {
                String baseUrl = ConfigReader.getProperty("baseUrl");
                if (baseUrl != null && !baseUrl.trim().isEmpty()) {
                    this.driver.get(baseUrl);
                    ReportProvider.info("Navigated to: " + baseUrl);
                }
                ReportProvider.info("Test environment initialized successfully with browser: " + browserName);
            } else {
                throw new RuntimeException("WebDriver instance is null after initialization");
            }
        } catch (Exception e) {
            ReportProvider.error("Critical error in test setup", e);
            throw new RuntimeException("Test setup failed: " + e.getMessage(), e);
        }
    public void setUp(String browser) {
        String browserName = (browser != null) ? browser : ConfigReader.getProperty("browser");
        DriverManager.setDriver(browserName);
        
        // Use thread-local driver directly - no instance variable
        DriverManager.getDriver().get(ConfigReader.getProperty("baseUrl"));
        
        ReportProvider.info("Test environment initialized with browser: " + browserName);
    public void setUp(String browser) {
        String browserName = (browser != null) ? browser : ConfigReader.getProperty("browser");
        DriverManager.setDriver(browserName);
        
        // Use thread-local driver directly - no instance variable
        DriverManager.getDriver().get(ConfigReader.getProperty("baseUrl"));
        
        ReportProvider.info("Test environment initialized with browser: " + browserName);
    public void setUp(String browser) {
        String browserName = (browser != null) ? browser : ConfigReader.getProperty("browser");
        DriverManager.setDriver(browserName);
        
        // Use thread-local driver directly - no instance variable
        DriverManager.getDriver().get(ConfigReader.getProperty("baseUrl"));
        
        ReportProvider.info("Test environment initialized with browser: " + browserName);
    }
    
    @AfterMethod
    public void tearDown() {
        try {
            String screenshotPath = ScreenshotUtils.captureScreenshot("test-end");
            if (screenshotPath != null) {
                ReportProvider.attachScreenshot(screenshotPath, "Test completion screenshot");
            }
        } catch (Exception e) {
            ReportProvider.warn("Failed to capture end-of-test screenshot: " + e.getMessage());
        } finally {
            DriverManager.quitDriver();
            ReportProvider.info("Test environment cleaned up");
        }
    }
    
    @AfterSuite
    public void suiteTearDown() {
        // Clean up any remaining Report Portal steps
        ReportProvider.cleanup();
        ReportProvider.info("Test suite execution completed");
    }
}