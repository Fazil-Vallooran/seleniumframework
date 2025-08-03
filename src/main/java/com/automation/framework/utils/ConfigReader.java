package com.automation.framework.utils;

import com.automation.framework.exceptions.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Enhanced configuration reader with support for multiple configuration sources
 * and proper error handling.
 * 
 * Priority order:
 * 1. System properties (highest)
 * 2. Environment variables
 * 3. Properties file (lowest)
 * 
 * @author Automation Framework
 * @version 2.0
 */
public class ConfigReader {
    
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    
    // Default values for critical properties
    private static final Properties DEFAULT_PROPERTIES = new Properties();
    
    static {
        initializeDefaultProperties();
        loadConfiguration();
    }
    
    /**
     * Initialize default property values to ensure framework stability.
     */
    private static void initializeDefaultProperties() {
        DEFAULT_PROPERTIES.setProperty("browser", "chrome");
        DEFAULT_PROPERTIES.setProperty("headless", "false");
        DEFAULT_PROPERTIES.setProperty("timeout", "10");
        DEFAULT_PROPERTIES.setProperty("baseUrl", "https://example.com");
        DEFAULT_PROPERTIES.setProperty("apiTimeout", "30000");
        DEFAULT_PROPERTIES.setProperty("apiRetryCount", "3");
        DEFAULT_PROPERTIES.setProperty("reportPath", "target/reports/");
        DEFAULT_PROPERTIES.setProperty("screenshotPath", "screenshots/");
        
        logger.info("Default configuration properties initialized");
    }
    
    /**
     * Load configuration from various sources with proper error handling.
     */
    private static void loadConfiguration() {
        try {
            loadPropertiesFromClasspath();
            logger.info("Configuration loaded successfully from classpath");
        } catch (ConfigurationException e) {
            logger.warn("Failed to load configuration from classpath: {}", e.getMessage());
            logger.info("Using default configuration values");
        }
    }
    
    /**
     * Load properties from classpath resources.
     * 
     * @throws ConfigurationException if configuration cannot be loaded
     */
    private static void loadPropertiesFromClasspath() throws ConfigurationException {
        try (InputStream inputStream = ConfigReader.class.getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            
            if (inputStream == null) {
                throw new ConfigurationException(
                    "Configuration file not found in classpath: " + DEFAULT_CONFIG_FILE,
                    null,
                    "classpath"
                );
            }
            
            properties.load(inputStream);
            logger.debug("Loaded {} properties from {}", properties.size(), DEFAULT_CONFIG_FILE);
            
        } catch (IOException e) {
            throw new ConfigurationException(
                "Failed to load configuration file: " + DEFAULT_CONFIG_FILE,
                null,
                "classpath",
                e
            );
        }
    }
    
    /**
     * Get a configuration property with multi-source lookup and validation.
     * 
     * Priority order:
     * 1. System properties
     * 2. Environment variables  
     * 3. Properties file
     * 4. Default values
     * 
     * @param key the property key
     * @return the property value, never null
     * @throws ConfigurationException if the key is null or empty
     */
    public static String getProperty(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new ConfigurationException("Property key cannot be null or empty", key, "input validation");
        }
        
        String value = null;
        String source = null;
        
        // 1. Check system properties first (highest priority)
        value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            source = "system properties";
            logger.debug("Property '{}' found in system properties: {}", key, value);
        } else {
            value = null; // Treat empty strings as null
        }
        
        // 2. Check environment variables
        if (value == null) {
            value = System.getenv(key);
            if (value != null && !value.trim().isEmpty()) {
                source = "environment variables";
                logger.debug("Property '{}' found in environment variables: {}", key, value);
            } else {
                value = null; // Treat empty strings as null
            }
        }
        
        // 3. Check properties file
        if (value == null) {
            value = properties.getProperty(key);
            if (value != null && !value.trim().isEmpty()) {
                source = "properties file";
                logger.debug("Property '{}' found in properties file: {}", key, value);
            } else {
                value = null; // Treat empty strings as null
            }
        }
        
        // 4. Use default value as fallback
        if (value == null) {
            value = DEFAULT_PROPERTIES.getProperty(key);
            if (value != null) {
                source = "default values";
                logger.debug("Property '{}' using default value: {}", key, value);
            }
        }
        
        // If still null, throw exception with helpful context
        if (value == null) {
            throw new ConfigurationException(
                String.format("Property '%s' not found in any configuration source", key),
                key,
                "all sources"
            );
        }
        
        return value.trim();
    }
    
    /**
     * Get a property as a boolean value.
     * 
     * @param key the property key
     * @return true if the property value is "true" (case-insensitive), false otherwise
     */
    public static boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get a property as an integer value.
     * 
     * @param key the property key
     * @return the integer value
     * @throws ConfigurationException if the value cannot be parsed as an integer
     */
    public static int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                String.format("Property '%s' value '%s' is not a valid integer", key, value),
                key,
                "value parsing",
                e
            );
        }
    }
    
    /**
     * Check if a property exists in any configuration source.
     * 
     * @param key the property key
     * @return true if the property exists, false otherwise
     */
    public static boolean hasProperty(String key) {
        try {
            getProperty(key);
            return true;
        } catch (ConfigurationException e) {
            return false;
        }
    }
    
    /**
     * Get all loaded properties for debugging purposes.
     * 
     * @return a copy of the properties
     */
    public static Properties getAllProperties() {
        Properties copy = new Properties();
        copy.putAll(DEFAULT_PROPERTIES);
        copy.putAll(properties);
        return copy;
    }
}