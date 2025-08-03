package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.automation.framework.base.DriverManager;
import com.automation.framework.utils.ConfigReader;
import com.automation.framework.exceptions.DriverException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validation tests for WebDriver initialization and management.
 * These tests validate the driver functionality without actual browser automation.
 */
public class DriverManagerValidationTest {
    
    private static final Logger logger = LogManager.getLogger(DriverManagerValidationTest.class);
    
    @Test(description = "Validate DriverManager initialization check")
    public void testDriverInitializationCheck() {
        logger.info("Testing driver initialization check...");
        
        // Initially no driver should be initialized
        boolean isInitialized = DriverManager.isDriverInitialized();
        Assert.assertFalse(isInitialized, "No driver should be initialized initially");
        
        logger.info("Driver initialization check validation passed");
    }
    
    @Test(description = "Validate error handling when no driver is initialized", 
          expectedExceptions = DriverException.class)
    public void testGetDriverWithoutInitialization() {
        logger.info("Testing error handling when no driver is initialized...");
        
        // Ensure no driver is initialized
        if (DriverManager.isDriverInitialized()) {
            DriverManager.quitDriver();
        }
        
        // This should throw DriverException
        DriverManager.getDriver();
    }
    
    @Test(description = "Validate error handling for invalid browser name", 
          expectedExceptions = DriverException.class)
    public void testInvalidBrowserName() {
        logger.info("Testing error handling for invalid browser name...");
        
        // This should throw DriverException
        DriverManager.setDriver("invalidbrowser");
    }
    
    @Test(description = "Validate error handling for null browser name", 
          expectedExceptions = DriverException.class)
    public void testNullBrowserName() {
        logger.info("Testing error handling for null browser name...");
        
        // This should throw DriverException
        DriverManager.setDriver(null);
    }
    
    @Test(description = "Validate error handling for empty browser name", 
          expectedExceptions = DriverException.class)
    public void testEmptyBrowserName() {
        logger.info("Testing error handling for empty browser name...");
        
        // This should throw DriverException
        DriverManager.setDriver("");
    }
    
    @Test(description = "Validate quit driver operation safety")
    public void testQuitDriverSafety() {
        logger.info("Testing quit driver operation safety...");
        
        // Should not throw exception even if no driver is initialized
        DriverManager.quitDriver();
        
        // Should still report no driver initialized
        boolean isInitialized = DriverManager.isDriverInitialized();
        Assert.assertFalse(isInitialized, "No driver should be initialized after quit");
        
        logger.info("Quit driver operation safety validation passed");
    }
}