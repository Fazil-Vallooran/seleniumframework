package com.automation.framework.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterSuite;
import org.openqa.selenium.WebDriver;
import com.automation.framework.utils.ConfigReader;
import com.automation.framework.utils.ScreenshotUtils;
import com.automation.framework.utils.ReportProvider;

public class BaseTest {
    
    // Thread-safe WebDriver instance
    protected WebDriver driver;
    
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(String browser) {
        String browserName = (browser != null) ? browser : ConfigReader.getProperty("browser");
        DriverManager.setDriver(browserName);
        this.driver = DriverManager.getDriver(); // Get thread-local driver
        driver.get(ConfigReader.getProperty("baseUrl"));
        
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