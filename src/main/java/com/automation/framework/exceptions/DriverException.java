package com.automation.framework.exceptions;

/**
 * Exception thrown when WebDriver-related errors occur.
 * This includes driver initialization failures, browser compatibility issues,
 * and WebDriver operation failures.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class DriverException extends FrameworkException {
    
    private static final long serialVersionUID = 1L;
    
    private final String browserName;
    private final String driverOperation;
    
    /**
     * Creates a new DriverException with a message.
     * 
     * @param message the error message
     */
    public DriverException(String message) {
        super(message, "DriverManager", null);
        this.browserName = null;
        this.driverOperation = null;
    }
    
    /**
     * Creates a new DriverException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public DriverException(String message, Throwable cause) {
        super(message, "DriverManager", null, cause);
        this.browserName = null;
        this.driverOperation = null;
    }
    
    /**
     * Creates a new DriverException for a specific browser and operation.
     * 
     * @param message the error message
     * @param browserName the browser that caused the error
     * @param driverOperation the operation that failed
     */
    public DriverException(String message, String browserName, String driverOperation) {
        super(message, "DriverManager", 
              String.format("Browser: %s, Operation: %s", browserName, driverOperation));
        this.browserName = browserName;
        this.driverOperation = driverOperation;
    }
    
    /**
     * Creates a new DriverException for a specific browser and operation with cause.
     * 
     * @param message the error message
     * @param browserName the browser that caused the error
     * @param driverOperation the operation that failed
     * @param cause the underlying cause
     */
    public DriverException(String message, String browserName, String driverOperation, Throwable cause) {
        super(message, "DriverManager", 
              String.format("Browser: %s, Operation: %s", browserName, driverOperation), cause);
        this.browserName = browserName;
        this.driverOperation = driverOperation;
    }
    
    /**
     * Gets the browser name that caused the driver error.
     * 
     * @return the browser name, or null if not applicable
     */
    public String getBrowserName() {
        return browserName;
    }
    
    /**
     * Gets the driver operation that failed.
     * 
     * @return the driver operation, or null if not applicable
     */
    public String getDriverOperation() {
        return driverOperation;
    }
}