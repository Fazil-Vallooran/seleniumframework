package seleniumproject.project.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import seleniumproject.project.utils.ConfigReader;
import seleniumproject.project.utils.ScreenshotUtils;

public class BaseTest {
    
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(String browser) {
        String browserName = (browser != null) ? browser : ConfigReader.getProperty("browser");
        DriverManager.setDriver(browserName);
        DriverManager.getDriver().get(ConfigReader.getProperty("baseUrl"));
    }
    
    @AfterMethod
    public void tearDown() {
        ScreenshotUtils.captureScreenshot("test-end");
        DriverManager.quitDriver();
    }
}