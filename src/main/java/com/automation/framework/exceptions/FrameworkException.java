package com.automation.framework.exceptions;

/**
 * Base exception class for the automation framework.
 * Provides enhanced error context and proper exception chaining.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class FrameworkException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String context;
    private final String component;
    
    /**
     * Creates a new FrameworkException with a message.
     * 
     * @param message the error message
     */
    public FrameworkException(String message) {
        super(message);
        this.context = null;
        this.component = "Framework";
    }
    
    /**
     * Creates a new FrameworkException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
        this.context = null;
        this.component = "Framework";
    }
    
    /**
     * Creates a new FrameworkException with detailed context.
     * 
     * @param message the error message
     * @param component the framework component where error occurred
     * @param context additional context information
     */
    public FrameworkException(String message, String component, String context) {
        super(message);
        this.component = component;
        this.context = context;
    }
    
    /**
     * Creates a new FrameworkException with detailed context and cause.
     * 
     * @param message the error message
     * @param component the framework component where error occurred
     * @param context additional context information
     * @param cause the underlying cause
     */
    public FrameworkException(String message, String component, String context, Throwable cause) {
        super(message, cause);
        this.component = component;
        this.context = context;
    }
    
    /**
     * Gets the component where the error occurred.
     * 
     * @return the component name
     */
    public String getComponent() {
        return component;
    }
    
    /**
     * Gets additional context information.
     * 
     * @return the context information, or null if not provided
     */
    public String getContext() {
        return context;
    }
    
    /**
     * Returns a detailed error message including component and context.
     * 
     * @return formatted error message
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(component).append("] ");
        sb.append(super.getMessage());
        
        if (context != null && !context.trim().isEmpty()) {
            sb.append(" | Context: ").append(context);
        }
        
        return sb.toString();
    }
}