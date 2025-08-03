package com.automation.framework.exceptions;

/**
 * Exception thrown when configuration-related errors occur.
 * This includes missing configuration files, invalid property values,
 * and environment setup issues.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class ConfigurationException extends FrameworkException {
    
    private static final long serialVersionUID = 1L;
    
    private final String propertyKey;
    private final String configurationSource;
    
    /**
     * Creates a new ConfigurationException with a message.
     * 
     * @param message the error message
     */
    public ConfigurationException(String message) {
        super(message, "ConfigurationManager", null);
        this.propertyKey = null;
        this.configurationSource = null;
    }
    
    /**
     * Creates a new ConfigurationException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, "ConfigurationManager", null, cause);
        this.propertyKey = null;
        this.configurationSource = null;
    }
    
    /**
     * Creates a new ConfigurationException for a specific property.
     * 
     * @param message the error message
     * @param propertyKey the configuration property that caused the error
     * @param configurationSource the source of the configuration (file, environment, etc.)
     */
    public ConfigurationException(String message, String propertyKey, String configurationSource) {
        super(message, "ConfigurationManager", 
              String.format("Property: %s, Source: %s", propertyKey, configurationSource));
        this.propertyKey = propertyKey;
        this.configurationSource = configurationSource;
    }
    
    /**
     * Creates a new ConfigurationException for a specific property with cause.
     * 
     * @param message the error message
     * @param propertyKey the configuration property that caused the error
     * @param configurationSource the source of the configuration
     * @param cause the underlying cause
     */
    public ConfigurationException(String message, String propertyKey, String configurationSource, Throwable cause) {
        super(message, "ConfigurationManager", 
              String.format("Property: %s, Source: %s", propertyKey, configurationSource), cause);
        this.propertyKey = propertyKey;
        this.configurationSource = configurationSource;
    }
    
    /**
     * Gets the property key that caused the configuration error.
     * 
     * @return the property key, or null if not applicable
     */
    public String getPropertyKey() {
        return propertyKey;
    }
    
    /**
     * Gets the configuration source where the error occurred.
     * 
     * @return the configuration source, or null if not applicable
     */
    public String getConfigurationSource() {
        return configurationSource;
    }
}