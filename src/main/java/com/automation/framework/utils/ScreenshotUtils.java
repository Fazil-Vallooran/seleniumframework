package com.automation.framework.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.automation.framework.base.DriverManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    
    public static String captureScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = System.getProperty("user.dir") + "/screenshots/" + fileName;
        
        try {
            // Check if WebDriver is available (for UI tests)
            if (DriverManager.getDriver() == null) {
                System.out.println("No WebDriver available - skipping screenshot for API test: " + testName);
                return null;
            }
            
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(System.getProperty("user.dir") + "/screenshots/");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) DriverManager.getDriver();
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);
            
            System.out.println("Screenshot captured: " + filePath);
            return filePath;
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}