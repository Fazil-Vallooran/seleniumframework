package com.automation.framework.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.automation.framework.base.DriverManager;
import com.automation.framework.utils.ConfigReader;
import com.automation.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Base page class providing common page functionality with enhanced error handling.
 * Uses thread-safe WebDriver access for parallel execution support.
 * 
 * @author Automation Framework
 * @version 2.0
 */
public class BasePage {
    
    private static final Logger logger = LogManager.getLogger(BasePage.class);
    protected final WebDriverWait wait;
    
    public BasePage() {
        int timeout = ConfigReader.getIntProperty("timeout");
        this.wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        logger.debug("BasePage initialized with timeout: {} seconds", timeout);
    }
    
    /**
     * Get the thread-local WebDriver instance.
     * 
     * @return the WebDriver instance for the current thread
     * @throws FrameworkException if no driver is available
     */
    protected WebDriver getDriver() {
        try {
            return DriverManager.getDriver();
        } catch (Exception e) {
            throw new FrameworkException(
                "Failed to get WebDriver instance for page operation",
                "BasePage",
                "WebDriver access",
                e
            );
        }
    }
    
    /**
     * Wait for an element to be visible on the page.
     * 
     * @param element the element to wait for
     * @throws FrameworkException if the element is not visible within timeout
     */
    protected void waitForElementToBeVisible(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            logger.debug("Element became visible: {}", element);
        } catch (Exception e) {
            throw new FrameworkException(
                "Element did not become visible within timeout",
                "BasePage",
                "Element visibility wait",
                e
            );
        }
    }
    
    /**
     * Wait for an element to be clickable.
     * 
     * @param element the element to wait for
     * @throws FrameworkException if the element is not clickable within timeout
     */
    protected void waitForElementToBeClickable(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            logger.debug("Element became clickable: {}", element);
        } catch (Exception e) {
            throw new FrameworkException(
                "Element did not become clickable within timeout",
                "BasePage",
                "Element clickability wait",
                e
            );
        }
    }
    
    /**
     * Click on an element after waiting for it to be clickable.
     * 
     * @param element the element to click
     * @throws FrameworkException if the click operation fails
     */
    protected void clickElement(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            logger.debug("Successfully clicked element: {}", element);
        } catch (Exception e) {
            throw new FrameworkException(
                "Failed to click element",
                "BasePage",
                "Element click operation",
                e
            );
        }
    }
    
    /**
     * Send text to an element after waiting for it to be visible.
     * 
     * @param element the element to send text to
     * @param text the text to send
     * @throws FrameworkException if the operation fails
     */
    protected void sendKeys(WebElement element, String text) {
        if (text == null) {
            throw new FrameworkException(
                "Text to send cannot be null",
                "BasePage",
                "Input validation"
            );
        }
        
        try {
            waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            logger.debug("Successfully sent text '{}' to element: {}", text, element);
        } catch (Exception e) {
            throw new FrameworkException(
                String.format("Failed to send text '%s' to element", text),
                "BasePage",
                "Text input operation",
                e
            );
        }
    }
    
    /**
     * Get text from an element after waiting for it to be visible.
     * 
     * @param element the element to get text from
     * @return the element's text
     * @throws FrameworkException if the operation fails
     */
    protected String getText(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            String text = element.getText();
            logger.debug("Retrieved text '{}' from element: {}", text, element);
            return text;
        } catch (Exception e) {
            throw new FrameworkException(
                "Failed to get text from element",
                "BasePage",
                "Text retrieval operation",
                e
            );
        }
    }
    
    /**
     * Check if an element is displayed on the page.
     * 
     * @param element the element to check
     * @return true if the element is displayed, false otherwise
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            boolean isDisplayed = element.isDisplayed();
            logger.debug("Element display status: {} for element: {}", isDisplayed, element);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Element is not displayed (exception caught): {}", element);
            return false;
        }
    }
    
    /**
     * Get the current page title.
     * 
     * @return the page title
     * @throws FrameworkException if unable to get the title
     */
    protected String getPageTitle() {
        try {
            String title = getDriver().getTitle();
            logger.debug("Current page title: {}", title);
            return title;
        } catch (Exception e) {
            throw new FrameworkException(
                "Failed to get page title",
                "BasePage",
                "Page title retrieval",
                e
            );
        }
    }
    
    /**
     * Get the current page URL.
     * 
     * @return the current URL
     * @throws FrameworkException if unable to get the URL
     */
    protected String getCurrentUrl() {
        try {
            String url = getDriver().getCurrentUrl();
            logger.debug("Current page URL: {}", url);
            return url;
        } catch (Exception e) {
            throw new FrameworkException(
                "Failed to get current URL",
                "BasePage",
                "URL retrieval",
                e
            );
        }
    }
}