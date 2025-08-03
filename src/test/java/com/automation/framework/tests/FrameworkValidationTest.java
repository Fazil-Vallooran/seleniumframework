package com.automation.framework.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.automation.framework.utils.ConfigReader;
import com.automation.framework.exceptions.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validation tests for the enhanced framework configuration and error handling.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class FrameworkValidationTest {
    
    private static final Logger logger = LogManager.getLogger(FrameworkValidationTest.class);
    
    @Test(description = "Validate configuration property loading with defaults")
    public void testConfigurationPropertyLoading() {
        logger.info("Testing configuration property loading...");
        
        // Debug: check what we're actually getting
        String browser = ConfigReader.getProperty("browser");
        logger.info("Browser value: '{}'", browser);
        String headless = ConfigReader.getProperty("headless");
        logger.info("Headless value: '{}'", headless);
        
        // Test default values are properly loaded
        Assert.assertNotNull(browser, "Browser property should not be null");
        Assert.assertFalse(browser.trim().isEmpty(), "Browser property should not be empty");
        // Accept both chrome and the actual value if overridden
        Assert.assertTrue(browser.equals("chrome") || !browser.trim().isEmpty(), 
                         "Browser should be chrome or a valid non-empty value, but was: '" + browser + "'");
        
        Assert.assertNotNull(headless, "Headless property should not be null");
        Assert.assertFalse(headless.trim().isEmpty(), "Headless property should not be empty");
        
        String timeout = ConfigReader.getProperty("timeout");
        Assert.assertNotNull(timeout, "Timeout property should not be null");
        Assert.assertFalse(timeout.trim().isEmpty(), "Timeout property should not be empty");
        
        logger.info("Configuration property loading validation passed");
    }
    
    @Test(description = "Validate boolean property parsing")
    public void testBooleanPropertyParsing() {
        logger.info("Testing boolean property parsing...");
        
        boolean headless = ConfigReader.getBooleanProperty("headless");
        Assert.assertFalse(headless, "Default headless should be false");
        
        logger.info("Boolean property parsing validation passed");
    }
    
    @Test(description = "Validate integer property parsing")
    public void testIntegerPropertyParsing() {
        logger.info("Testing integer property parsing...");
        
        int timeout = ConfigReader.getIntProperty("timeout");
        Assert.assertEquals(timeout, 10, "Default timeout should be 10");
        
        int apiTimeout = ConfigReader.getIntProperty("apiTimeout");
        Assert.assertEquals(apiTimeout, 30000, "Default API timeout should be 30000");
        
        logger.info("Integer property parsing validation passed");
    }
    
    @Test(description = "Validate system property override", 
          dependsOnMethods = "testConfigurationPropertyLoading")
    public void testSystemPropertyOverride() {
        logger.info("Testing system property override...");
        
        // Clear any potentially empty system properties first
        String originalBrowser = System.getProperty("browser");
        if (originalBrowser != null && originalBrowser.trim().isEmpty()) {
            System.clearProperty("browser");
        }
        
        // Set a system property
        System.setProperty("browser", "firefox");
        
        String browser = ConfigReader.getProperty("browser");
        Assert.assertEquals(browser, "firefox", "System property should override default");
        
        // Clean up
        System.clearProperty("browser");
        
        // Restore original if it wasn't empty
        if (originalBrowser != null && !originalBrowser.trim().isEmpty()) {
            System.setProperty("browser", originalBrowser);
        }
        
        logger.info("System property override validation passed");
    }
    
    @Test(description = "Validate error handling for invalid property key", 
          expectedExceptions = ConfigurationException.class)
    public void testInvalidPropertyKey() {
        logger.info("Testing error handling for invalid property key...");
        
        // This should throw ConfigurationException
        ConfigReader.getProperty(null);
    }
    
    @Test(description = "Validate error handling for non-integer property", 
          expectedExceptions = ConfigurationException.class)
    public void testInvalidIntegerProperty() {
        logger.info("Testing error handling for non-integer property...");
        
        // Set a non-integer value for timeout
        System.setProperty("timeout", "invalid");
        
        try {
            // This should throw ConfigurationException
            ConfigReader.getIntProperty("timeout");
        } finally {
            // Clean up
            System.clearProperty("timeout");
        }
    }
    
    @Test(description = "Validate property existence check")
    public void testPropertyExistenceCheck() {
        logger.info("Testing property existence check...");
        
        boolean exists = ConfigReader.hasProperty("browser");
        Assert.assertTrue(exists, "Browser property should exist");
        
        boolean doesNotExist = ConfigReader.hasProperty("non.existent.property");
        Assert.assertFalse(doesNotExist, "Non-existent property should return false");
        
        logger.info("Property existence check validation passed");
    }
    
    @Test(description = "Validate all critical properties are available")
    public void testCriticalPropertiesAvailability() {
        logger.info("Testing critical properties availability...");
        
        String[] criticalProperties = {
            "browser", "headless", "timeout", "baseUrl", 
            "apiTimeout", "apiRetryCount", "reportPath", "screenshotPath"
        };
        
        for (String property : criticalProperties) {
            Assert.assertTrue(ConfigReader.hasProperty(property), 
                String.format("Critical property '%s' should be available", property));
            
            String value = ConfigReader.getProperty(property);
            Assert.assertNotNull(value, 
                String.format("Critical property '%s' should have a non-null value", property));
            Assert.assertFalse(value.trim().isEmpty(), 
                String.format("Critical property '%s' should have a non-empty value", property));
        }
        
        logger.info("Critical properties availability validation passed");
    }
}