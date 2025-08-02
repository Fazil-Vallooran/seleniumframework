package com.automation.framework.utils;

import com.epam.reportportal.service.ReportPortal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Stack;

public class ReportProvider {
    
    private static final Logger logger = LogManager.getLogger(ReportProvider.class);
    
    // Stack to manage nested steps
    private static final ThreadLocal<Stack<String>> stepStack = ThreadLocal.withInitial(Stack::new);
    
    // Message levels enum
    public enum LogLevel {
        TRACE("TRACE"),
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARN("WARN"),
        ERROR("ERROR"),
        FATAL("FATAL");
        
        private final String level;
        
        LogLevel(String level) {
            this.level = level;
        }
        
        public String getLevel() {
            return level;
        }
    }
    
    // Step types enum
    public enum StepType {
        SUITE("SUITE"),
        STORY("STORY"),
        TEST("TEST"),
        SCENARIO("SCENARIO"),
        STEP("STEP"),
        BEFORE_CLASS("BEFORE_CLASS"),
        BEFORE_METHOD("BEFORE_METHOD"),
        AFTER_METHOD("AFTER_METHOD"),
        AFTER_CLASS("AFTER_CLASS");
        
        private final String type;
        
        StepType(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }
    
    // Step status enum
    public enum StepStatus {
        PASSED("PASSED"),
        FAILED("FAILED"),
        SKIPPED("SKIPPED"),
        INTERRUPTED("INTERRUPTED"),
        CANCELLED("CANCELLED");
        
        private final String status;
        
        StepStatus(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
    }
    
    /**
     * Start a new step in Report Portal
     */
    public static String startStep(String stepName, String description, StepType stepType) {
        try {
            String stepId = "step_" + System.currentTimeMillis();
            stepStack.get().push(stepId);
            
            // Log step start to Report Portal using the correct API
            ReportPortal.emitLog("🚀 Started Step: " + stepName, LogLevel.INFO.getLevel(), Calendar.getInstance().getTime());
            
            if (description != null && !description.isEmpty()) {
                ReportPortal.emitLog("📝 Description: " + description, LogLevel.INFO.getLevel(), Calendar.getInstance().getTime());
            }
            
            logger.info("Report Portal step started: " + stepName);
            return stepId;
        } catch (Exception e) {
            logger.error("Failed to start Report Portal step: " + stepName, e);
            return null;
        }
    }
    
    /**
     * Start a step with just name and type
     */
    public static String startStep(String stepName, StepType stepType) {
        return startStep(stepName, null, stepType);
    }
    
    /**
     * Start a collapsible step (default STEP type)
     */
    public static String startStep(String stepName) {
        return startStep(stepName, null, StepType.STEP);
    }
    
    /**
     * Start a collapsible step with description
     */
    public static String startStep(String stepName, String description) {
        return startStep(stepName, description, StepType.STEP);
    }
    
    /**
     * Finish the current step
     */
    public static void finishStep(StepStatus status) {
        finishStep(status, null);
    }
    
    /**
     * Finish the current step with additional message
     */
    public static void finishStep(StepStatus status, String message) {
        try {
            if (!stepStack.get().isEmpty()) {
                String currentStepId = stepStack.get().pop();
                
                String statusIcon = getStatusIcon(status);
                ReportPortal.emitLog(statusIcon + " Step finished with status: " + status.getStatus(), 
                    LogLevel.INFO.getLevel(), Calendar.getInstance().getTime());
                
                if (message != null && !message.isEmpty()) {
                    ReportPortal.emitLog("📄 Final Message: " + message, 
                        LogLevel.INFO.getLevel(), Calendar.getInstance().getTime());
                }
                
                logger.info("Report Portal step finished with status: " + status.getStatus());
            } else {
                logger.warn("No active step to finish");
            }
        } catch (Exception e) {
            logger.error("Failed to finish Report Portal step", e);
        }
    }
    
    /**
     * Log a message with specified level
     */
    public static void log(LogLevel level, String message) {
        try {
            ReportPortal.emitLog(message, level.getLevel(), Calendar.getInstance().getTime());
            
            // Also log to console with appropriate level
            switch (level) {
                case TRACE:
                    logger.trace(message);
                    break;
                case DEBUG:
                    logger.debug(message);
                    break;
                case INFO:
                    logger.info(message);
                    break;
                case WARN:
                    logger.warn(message);
                    break;
                case ERROR:
                    logger.error(message);
                    break;
                case FATAL:
                    logger.fatal(message);
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to send log to Report Portal: " + message, e);
        }
    }
    
    /**
     * Log INFO level message
     */
    public static void info(String message) {
        log(LogLevel.INFO, "ℹ️ " + message);
    }
    
    /**
     * Log TRACE level message
     */
    public static void trace(String message) {
        log(LogLevel.TRACE, "🔍 " + message);
    }
    
    /**
     * Log DEBUG level message
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, "🐛 " + message);
    }
    
    /**
     * Log WARN level message
     */
    public static void warn(String message) {
        log(LogLevel.WARN, "⚠️ " + message);
    }
    
    /**
     * Log ERROR level message
     */
    public static void error(String message) {
        log(LogLevel.ERROR, "❌ " + message);
    }
    
    /**
     * Log FATAL level message
     */
    public static void fatal(String message) {
        log(LogLevel.FATAL, "💀 " + message);
    }
    
    /**
     * Log an exception with ERROR level
     */
    public static void error(String message, Throwable throwable) {
        String fullMessage = "❌ " + message + "\n🔥 Exception: " + throwable.getMessage();
        log(LogLevel.ERROR, fullMessage);
        logger.error(message, throwable);
    }
    
    /**
     * Log test step with automatic step management
     */
    public static void testStep(String stepName, Runnable stepAction) {
        String stepId = startStep(stepName, StepType.STEP);
        try {
            stepAction.run();
            finishStep(StepStatus.PASSED);
        } catch (Exception e) {
            error("Step failed: " + stepName, e);
            finishStep(StepStatus.FAILED, "Step execution failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Log test scenario with automatic management
     */
    public static void testScenario(String scenarioName, String description, Runnable scenarioAction) {
        String stepId = startStep(scenarioName, description, StepType.SCENARIO);
        try {
            info("🎬 Starting scenario: " + scenarioName);
            scenarioAction.run();
            info("✅ Scenario completed successfully");
            finishStep(StepStatus.PASSED);
        } catch (Exception e) {
            error("Scenario failed: " + scenarioName, e);
            finishStep(StepStatus.FAILED, "Scenario execution failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Attach screenshot to current step
     */
    public static void attachScreenshot(String screenshotPath, String description) {
        try {
            java.io.File screenshot = new java.io.File(screenshotPath);
            if (screenshot.exists()) {
                ReportPortal.emitLog("📸 " + description, LogLevel.INFO.getLevel(), 
                    Calendar.getInstance().getTime(), screenshot);
                info("Screenshot attached: " + description);
            } else {
                warn("Screenshot file not found: " + screenshotPath);
            }
        } catch (Exception e) {
            error("Failed to attach screenshot", e);
        }
    }
    
    /**
     * Attach file to current step
     */
    public static void attachFile(String filePath, String description) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                ReportPortal.emitLog("📎 " + description, LogLevel.INFO.getLevel(), 
                    Calendar.getInstance().getTime(), file);
                info("File attached: " + description);
            } else {
                warn("File not found: " + filePath);
            }
        } catch (Exception e) {
            error("Failed to attach file", e);
        }
    }
    
    /**
     * Log API request details
     */
    public static void logApiRequest(String method, String endpoint, String requestBody) {
        info("🌐 API Request: " + method + " " + endpoint);
        if (requestBody != null && !requestBody.isEmpty()) {
            debug("📤 Request Body: " + requestBody);
        }
    }
    
    /**
     * Log API response details
     */
    public static void logApiResponse(int statusCode, long responseTime, String responseBody) {
        String statusIcon = statusCode >= 200 && statusCode < 300 ? "✅" : "❌";
        info(statusIcon + " API Response: Status " + statusCode + " | Time: " + responseTime + "ms");
        
        if (responseBody != null && !responseBody.isEmpty()) {
            debug("📥 Response Body: " + responseBody);
        }
    }
    
    /**
     * Log UI action
     */
    public static void logUiAction(String action, String element) {
        info("🖱️ UI Action: " + action + " on '" + element + "'");
    }
    
    /**
     * Log UI verification
     */
    public static void logUiVerification(String verification, boolean result) {
        String icon = result ? "✅" : "❌";
        info(icon + " UI Verification: " + verification + " - " + (result ? "PASSED" : "FAILED"));
    }
    
    /**
     * Log test data being used
     */
    public static void logTestData(String dataDescription, Object data) {
        info("📊 Test Data: " + dataDescription);
        debug("📋 Data Details: " + data.toString());
    }
    
    /**
     * Get status icon for different step statuses
     */
    private static String getStatusIcon(StepStatus status) {
        switch (status) {
            case PASSED:
                return "✅";
            case FAILED:
                return "❌";
            case SKIPPED:
                return "⏭️";
            case INTERRUPTED:
                return "⚠️";
            case CANCELLED:
                return "🚫";
            default:
                return "❓";
        }
    }
    
    /**
     * Clean up thread-local step stack
     */
    public static void cleanup() {
        try {
            // Finish any remaining steps
            while (!stepStack.get().isEmpty()) {
                finishStep(StepStatus.INTERRUPTED, "Step cleanup - test ended unexpectedly");
            }
            stepStack.remove();
        } catch (Exception e) {
            logger.error("Error during ReportProvider cleanup", e);
        }
    }
}