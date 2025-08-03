package com.automation.framework.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.automation.framework.utils.ConfigReader;
import com.automation.framework.exceptions.DriverException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Thread-safe WebDriver manager with enhanced error handling and null safety.
 * 
 * @author Automation Framework
 * @version 2.0
 */
public class DriverManager {
    
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    /**
     * Initialize and set the WebDriver for the current thread.
     * 
     * @param browserName the browser to initialize (chrome, firefox, edge)
     * @throws DriverException if driver initialization fails
     */
    public static void setDriver(String browserName) {
        if (browserName == null || browserName.trim().isEmpty()) {
            throw new DriverException("Browser name cannot be null or empty", null, "initialization");
        }
        
        String browser = browserName.toLowerCase().trim();
        logger.info("Initializing WebDriver for browser: {}", browser);
        
        try {
            WebDriver webDriver = createDriver(browser);
            driver.set(webDriver);
            
            configureDriver(webDriver);
            logger.info("WebDriver initialized successfully for browser: {}", browser);
            
        } catch (Exception e) {
            throw new DriverException(
                String.format("Failed to initialize WebDriver for browser: %s", browser),
                browser,
                "initialization",
                e
            );
        }
    }
    
    /**
     * Create a WebDriver instance for the specified browser.
     * 
     * @param browser the browser name
     * @return the configured WebDriver instance
     * @throws DriverException if browser is not supported or driver creation fails
     */
    private static WebDriver createDriver(String browser) throws DriverException {
        boolean isHeadless = ConfigReader.getBooleanProperty("headless");
        logger.debug("Creating driver for browser: {}, headless: {}", browser, isHeadless);
        
        switch (browser) {
            case "chrome":
                return createChromeDriver(isHeadless);
                
            case "firefox":
                return createFirefoxDriver(isHeadless);
                
            case "edge":
                return createEdgeDriver(isHeadless);
                
            default:
                throw new DriverException(
                    String.format("Unsupported browser: %s. Supported browsers: chrome, firefox, edge", browser),
                    browser,
                    "browser validation"
                );
        }
    }
    
    /**
     * Create and configure Chrome WebDriver.
     * 
     * @param isHeadless whether to run in headless mode
     * @return configured ChromeDriver instance
     */
    private static WebDriver createChromeDriver(boolean isHeadless) {
        try {
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--disable-notifications");
            chromeOptions.addArguments("--disable-popup-blocking");
            chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            
            if (isHeadless) {
                chromeOptions.addArguments("--headless");
                chromeOptions.addArguments("--disable-gpu");
                logger.debug("Chrome configured for headless mode");
            }
            
            return new ChromeDriver(chromeOptions);
            
        } catch (Exception e) {
            throw new DriverException("Failed to create Chrome driver", "chrome", "driver creation", e);
        }
    }
    
    /**
     * Create and configure Firefox WebDriver.
     * 
     * @param isHeadless whether to run in headless mode
     * @return configured FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(boolean isHeadless) {
        try {
            WebDriverManager.firefoxdriver().setup();
            
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            
            if (isHeadless) {
                firefoxOptions.addArguments("--headless");
                logger.debug("Firefox configured for headless mode");
            }
            
            return new FirefoxDriver(firefoxOptions);
            
        } catch (Exception e) {
            throw new DriverException("Failed to create Firefox driver", "firefox", "driver creation", e);
        }
    }
    
    /**
     * Create and configure Edge WebDriver.
     * 
     * @param isHeadless whether to run in headless mode
     * @return configured EdgeDriver instance
     */
    private static WebDriver createEdgeDriver(boolean isHeadless) {
        try {
            WebDriverManager.edgedriver().setup();
            
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.addArguments("--disable-notifications");
            edgeOptions.addArguments("--disable-popup-blocking");
            
            if (isHeadless) {
                edgeOptions.addArguments("--headless");
                edgeOptions.addArguments("--disable-gpu");
                logger.debug("Edge configured for headless mode");
            }
            
            return new EdgeDriver(edgeOptions);
            
        } catch (Exception e) {
            throw new DriverException("Failed to create Edge driver", "edge", "driver creation", e);
        }
    }
    
    /**
     * Configure the WebDriver with common settings.
     * 
     * @param webDriver the WebDriver instance to configure
     */
    private static void configureDriver(WebDriver webDriver) {
        try {
            int timeout = ConfigReader.getIntProperty("timeout");
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
            webDriver.manage().window().maximize();
            
            logger.debug("Driver configured with timeout: {} seconds", timeout);
            
        } catch (Exception e) {
            logger.warn("Failed to configure driver settings: {}", e.getMessage());
            // Continue with default settings
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            webDriver.manage().window().maximize();
        }
    }
    
    /**
     * Get the WebDriver instance for the current thread.
     * 
     * @return the WebDriver instance
     * @throws DriverException if no driver is initialized for the current thread
     */
    public static WebDriver getDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver == null) {
            throw new DriverException(
                "No WebDriver initialized for current thread. Call setDriver() first.",
                null,
                "driver retrieval"
            );
        }
        return webDriver;
    }
    
    /**
     * Quit the WebDriver and clean up resources for the current thread.
     */
    public static void quitDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
                logger.debug("WebDriver quit successfully");
            } catch (Exception e) {
                logger.warn("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                driver.remove();
                logger.debug("WebDriver removed from ThreadLocal");
            }
        }
    }
    
    /**
     * Check if a WebDriver is initialized for the current thread.
     * 
     * @return true if driver is initialized, false otherwise
     */
    public static boolean isDriverInitialized() {
        return driver.get() != null;
    }
}