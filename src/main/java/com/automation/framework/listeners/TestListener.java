package com.automation.framework.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import com.automation.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestListener implements ITestListener {
    
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: " + result.getMethod().getMethodName());
        ScreenshotUtils.captureScreenshot(result.getMethod().getMethodName() + "_PASSED");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: " + result.getMethod().getMethodName());
        logger.error("Failure reason: " + result.getThrowable().getMessage());
        ScreenshotUtils.captureScreenshot(result.getMethod().getMethodName() + "_FAILED");
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: " + result.getMethod().getMethodName());
    }
}