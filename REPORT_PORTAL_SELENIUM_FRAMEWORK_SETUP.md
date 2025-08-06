# 🎯 ReportPortal Integration Guide

**Complete setup guide for integrating Java/TestNG tests with ReportPortal analytics platform**

---

## 📋 Quick Setup Overview

This guide demonstrates how to integrate your Java/TestNG automation framework with ReportPortal for comprehensive test analytics and reporting.

### **🏗️ Current Architecture**

```yaml
📊 ReportPortal Platform
├── 🌐 Core Services
│   ├── Gateway (Traefik) - Port 8080/8081
│   ├── API Service - Core business logic
│   ├── UI Service - Web interface
│   ├── UAT Service - Authentication
│   ├── Jobs Service - Background processing
│   └── Index Service - Data indexing
├── 📈 Analytics Stack
│   ├── Auto Analyzer - ML-powered analysis
│   ├── Analyzer Training - Model training
│   └── OpenSearch - Search engine
└── 🗄️ Data Layer
    ├── PostgreSQL - Test metadata
    ├── RabbitMQ - Message queue
    └── File Storage - Screenshots/logs
```

## 🚀 Platform Startup

### **Start ReportPortal Services**
```bash
# Start all ReportPortal services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

### **Access Points**
- **ReportPortal UI**: http://localhost:8080
- **Traefik Dashboard**: http://localhost:8081
- **Default Login**: 
  - Username: `superadmin`
  - Password: `erebus`

## 🔧 Java Integration Setup

### **1. Maven Dependencies**
```xml
<!-- ReportPortal TestNG Integration -->
<dependency>
    <groupId>com.epam.reportportal</groupId>
    <artifactId>agent-java-testng</artifactId>
    <version>5.1.4</version>
</dependency>

<!-- ReportPortal Logger -->
<dependency>
    <groupId>com.epam.reportportal</groupId>
    <artifactId>logger-java-logback</artifactId>
    <version>5.1.5</version>
</dependency>
```

### **2. Configuration Files**

**reportportal.properties**
```properties
# ReportPortal server configuration
rp.endpoint=http://localhost:8080
rp.api.key=your-api-key-here
rp.launch=Selenium Framework Tests
rp.project=selenium_framework

# Test execution settings
rp.reporting.async=true
rp.reporting.callback=true
rp.enable=true

# Test attributes and descriptions
rp.attributes=selenium;regression;smoke
rp.description=Automated UI tests using Selenium WebDriver

# Logging configuration
rp.log.level=INFO
rp.reporting.timeout=60
```

**logback.xml** (for ReportPortal logging)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- ReportPortal appender -->
    <appender name="ReportPortalAppender" class="com.epam.reportportal.logback.appender.ReportPortalAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%t] %-5level - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Console appender -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="ReportPortalAppender"/>
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### **3. TestNG Configuration**

**testng.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="ReportPortal Integration Suite" parallel="methods" thread-count="3">
    <listeners>
        <!-- ReportPortal TestNG listener -->
        <listener class-name="com.epam.reportportal.testng.ReportPortalTestNGListener"/>
    </listeners>
    
    <test name="Selenium Framework Tests">
        <classes>
            <class name="com.framework.tests.LoginTests"/>
            <class name="com.framework.tests.DashboardTests"/>
            <class name="com.framework.tests.UserManagementTests"/>
        </classes>
    </test>
</suite>
```

## 📊 Enhanced Reporting Integration

### **1. Custom Test Reporting**
```java
import com.epam.reportportal.service.ReportPortal;
import com.epam.reportportal.utils.reflect.Accessible;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;

public class ReportPortalLogger {
    
    private static final ReportPortal reportPortal = ReportPortal.builder().build();
    
    public static void logInfo(String message) {
        reportPortal.getStepReporter().sendLog(message, "INFO", new Date());
    }
    
    public static void logError(String message, Throwable throwable) {
        reportPortal.getStepReporter().sendLog(message, "ERROR", new Date(), throwable);
    }
    
    public static void attachScreenshot(String screenshotPath) {
        try {
            byte[] screenshot = Files.readAllBytes(Paths.get(screenshotPath));
            ReportPortal.emitLog("Screenshot attached", "INFO", new Date(), 
                ReportPortal.createFileAttachment("image/png", screenshot));
        } catch (IOException e) {
            logError("Failed to attach screenshot", e);
        }
    }
}
```

### **2. Test Class Integration**
```java
@Listeners({ReportPortalTestNGListener.class})
public class BaseTest {
    
    protected WebDriver driver;
    
    @BeforeMethod
    public void setUp() {
        ReportPortalLogger.logInfo("Test setup started");
        
        // Initialize WebDriver
        driver = DriverManager.getDriver();
        ReportPortalLogger.logInfo("WebDriver initialized: " + driver.getClass().getSimpleName());
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Capture screenshot on failure
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getMethod().getMethodName());
            ReportPortalLogger.attachScreenshot(screenshotPath);
            ReportPortalLogger.logError("Test failed: " + result.getThrowable().getMessage(), result.getThrowable());
        }
        
        DriverManager.quitDriver();
        ReportPortalLogger.logInfo("Test cleanup completed");
    }
}
```

### **3. Enhanced Test Execution**
```java
public class LoginTests extends BaseTest {
    
    @Test(description = "Verify successful login with valid credentials")
    @Attributes(attributes = {@Attribute(key = "TestType", value = "Smoke")})
    public void testSuccessfulLogin() {
        ReportPortalLogger.logInfo("Starting login test with valid credentials");
        
        try {
            // Test steps with detailed logging
            ReportPortalLogger.logInfo("Navigating to login page");
            driver.get("https://example.com/login");
            
            LoginPage loginPage = new LoginPage(driver);
            
            ReportPortalLogger.logInfo("Entering credentials");
            loginPage.enterUsername("testuser@example.com");
            loginPage.enterPassword("validPassword123");
            
            ReportPortalLogger.logInfo("Clicking login button");
            loginPage.clickLoginButton();
            
            // Verification with reporting
            DashboardPage dashboard = new DashboardPage(driver);
            Assert.assertTrue(dashboard.isDashboardDisplayed(), "Dashboard should be displayed after login");
            
            ReportPortalLogger.logInfo("Login test completed successfully");
            
        } catch (Exception e) {
            ReportPortalLogger.logError("Login test failed", e);
            throw e;
        }
    }
}
```

## 🔧 Advanced Configuration

### **1. Custom Launch Attributes**
```java
// Add custom attributes to test launches
@BeforeSuite
public void setLaunchAttributes() {
    System.setProperty("rp.attributes", 
        "env:" + ConfigReader.getEnvironment() + 
        ";browser:" + ConfigReader.getBrowser() + 
        ";build:" + System.getProperty("build.number", "local"));
}
```

### **2. Dynamic Project Configuration**
```java
public class ReportPortalConfig {
    
    public static void configureDynamicProperties() {
        // Dynamic project configuration based on environment
        String environment = System.getProperty("test.env", "dev");
        String project = "selenium_framework_" + environment;
        
        System.setProperty("rp.project", project);
        System.setProperty("rp.launch", "Automated Tests - " + environment.toUpperCase());
        
        // Add environment-specific attributes
        String attributes = "env:" + environment + ";execution:automated;framework:selenium";
        System.setProperty("rp.attributes", attributes);
    }
}
```

## 📈 Analytics and Reporting Features

### **1. Real-Time Test Monitoring**
- Live test execution tracking
- Immediate failure notifications
- Resource utilization monitoring
- Performance metrics collection

### **2. Historical Analytics**
- Test execution trends
- Failure pattern analysis
- Performance regression detection
- Quality metrics over time

### **3. ML-Powered Insights**
- Automatic failure categorization
- Flaky test identification
- Root cause analysis suggestions
- Predictive quality analytics

## 🎯 Best Practices

### **1. Logging Strategy**
```java
public class SmartLogger {
    
    public static void logTestStep(String step, Object... parameters) {
        String message = String.format(step, parameters);
        ReportPortalLogger.logInfo("STEP: " + message);
    }
    
    public static void logBusinessAction(String action) {
        ReportPortalLogger.logInfo("BUSINESS ACTION: " + action);
    }
    
    public static void logTechnicalDetail(String detail) {
        ReportPortalLogger.logInfo("TECHNICAL: " + detail);
    }
}
```

### **2. Screenshot Management**
```java
public class ScreenshotManager {
    
    public static void captureOnFailure(ITestResult result, WebDriver driver) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String testName = result.getMethod().getMethodName();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = testName + "_" + timestamp + ".png";
            
            String screenshotPath = ScreenshotUtils.captureFullPageScreenshot(driver, filename);
            ReportPortalLogger.attachScreenshot(screenshotPath);
        }
    }
}
```

## 🚀 Execution Commands

### **Run Tests with ReportPortal Integration**
```bash
# Run all tests
mvn clean test -Dsuite=testng.xml

# Run with specific environment
mvn clean test -Dsuite=testng.xml -Dtest.env=staging

# Run with custom attributes
mvn clean test -Dsuite=testng.xml -Drp.attributes="smoke;regression;sprint-23"

# Generate and publish results
mvn clean test -Dsuite=testng.xml -Drp.launch="Release Candidate Testing"
```

## 📊 Accessing Reports

1. **Open ReportPortal UI**: http://localhost:8080
2. **Login** with your credentials
3. **Navigate to your project**: selenium_framework
4. **View launches** and detailed test results
5. **Analyze trends** and failure patterns

## 🎯 Integration Benefits

### **Immediate Value**
- ✅ **Real-time test monitoring** with live execution tracking
- ✅ **Detailed failure analysis** with screenshots and logs
- ✅ **Historical trend analysis** for quality insights
- ✅ **Team collaboration** with shared reporting platform

### **Advanced Analytics**
- ✅ **ML-powered failure categorization** and root cause analysis
- ✅ **Flaky test identification** and stability tracking
- ✅ **Performance regression detection** and optimization insights
- ✅ **Executive reporting** with business-level quality metrics

---

*This ReportPortal integration transforms your test automation into a comprehensive quality analytics platform, providing the insights needed for data-driven quality decisions.*
