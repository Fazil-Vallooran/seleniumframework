# Report Provider Usage Guide

## 🎯 Overview
The `ReportProvider` class gives you complete control over Report Portal logging with proper message categorization, collapsible step management, and enhanced visual reporting.

## 🏗️ Key Features

### **Message Level Classification**
- **TRACE** 🔍 - Detailed debugging information
- **DEBUG** 🐛 - Development debugging messages  
- **INFO** ℹ️ - General informational messages
- **WARN** ⚠️ - Warning messages for potential issues
- **ERROR** ❌ - Error messages for failures
- **FATAL** 💀 - Critical system failures

### **Collapsible Step Management**
Each step appears as a collapsible heading in Report Portal with:
- Automatic step tracking using ThreadLocal stack
- Nested step support
- Status management (PASSED, FAILED, SKIPPED, etc.)
- Visual icons for different message types

## 🚀 Basic Usage Examples

### **Simple Message Logging**
```java
// Different message levels with visual icons
ReportProvider.info("Test started successfully");
ReportProvider.debug("Variable value: " + someValue);
ReportProvider.warn("Response time is higher than expected");
ReportProvider.error("Element not found on page");
ReportProvider.fatal("Critical system failure");
```

### **Step Management**
```java
// Start a collapsible step
ReportProvider.startStep("User Login Process");

// Add messages within the step
ReportProvider.info("Navigating to login page");
ReportProvider.debug("Entering credentials");
ReportProvider.warn("Captcha required");

// Finish the step with status
ReportProvider.finishStep(StepStatus.PASSED);
```

### **Advanced Step Types**
```java
// Start different types of steps
ReportProvider.startStep("Login Test", "Verify user authentication", StepType.SCENARIO);
ReportProvider.startStep("API Validation", StepType.STEP);
ReportProvider.startStep("Database Setup", StepType.BEFORE_METHOD);
```

## 🧪 Test Integration Examples

### **UI Test with Detailed Reporting**
```java
@Test
public void testUserLogin() {
    ReportProvider.startStep("User Login Test", "Complete login flow validation", StepType.SCENARIO);
    
    try {
        // Step 1: Home page verification
        ReportProvider.startStep("Home Page Verification");
        ReportProvider.logUiAction("Verify", "Home page logo");
        boolean logoDisplayed = homePage.isLogoDisplayed();
        ReportProvider.logUiVerification("Logo is displayed", logoDisplayed);
        ReportProvider.finishStep(StepStatus.PASSED);
        
        // Step 2: Login process
        ReportProvider.startStep("Login Process");
        ReportProvider.logUiAction("Click", "Login button");
        ReportProvider.info("Entering credentials");
        loginPage.login(username, password);
        ReportProvider.finishStep(StepStatus.PASSED);
        
        ReportProvider.finishStep(StepStatus.PASSED);
        
    } catch (Exception e) {
        ReportProvider.error("Login test failed", e);
        ReportProvider.finishStep(StepStatus.FAILED);
        throw e;
    }
}
```

### **API Test with Enhanced Reporting**
```java
@Test
public void testApiEndpoint() {
    ReportProvider.startStep("API Test: Create User", "POST /users endpoint validation", StepType.SCENARIO);
    
    try {
        // Log API request
        ReportProvider.logApiRequest("POST", "/users", requestBody);
        Response response = apiClient.post("/users");
        
        // Log API response
        ReportProvider.logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
        
        // Validate response
        ReportProvider.startStep("Response Validation");
        ReportProvider.info("Validating status code: 201");
        new ApiValidator(response).validateStatusCode(201);
        ReportProvider.info("✅ Status code validation passed");
        ReportProvider.finishStep(StepStatus.PASSED);
        
        ReportProvider.finishStep(StepStatus.PASSED);
        
    } catch (Exception e) {
        ReportProvider.error("API test failed", e);
        ReportProvider.finishStep(StepStatus.FAILED);
        throw e;
    }
}
```

## 🎨 Visual Report Portal Output

When you run your tests, Report Portal will display:

```
🎬 Scenario: User Login Test
├── 🚀 Started Step: Home Page Verification
│   ├── ℹ️ UI Action: Verify on 'Home page logo'
│   ├── ✅ UI Verification: Logo is displayed - PASSED
│   └── ✅ Step finished with status: PASSED
├── 🚀 Started Step: Login Process  
│   ├── ℹ️ UI Action: Click on 'Login button'
│   ├── ℹ️ Entering credentials
│   ├── 🐛 Password length: 8 characters
│   └── ✅ Step finished with status: PASSED
├── 📸 Screenshot attached: Test completion screenshot
└── ✅ Step finished with status: PASSED
```

## 🔧 Specialized Logging Methods

### **UI-Specific Logging**
```java
ReportProvider.logUiAction("Click", "Login button");
ReportProvider.logUiAction("Enter text", "Username field");
ReportProvider.logUiVerification("Element is visible", true);
ReportProvider.logUiVerification("Page title correct", false);
```

### **API-Specific Logging**
```java
ReportProvider.logApiRequest("GET", "/users/123", null);
ReportProvider.logApiResponse(200, 150, responseBody);
```

### **Test Data Logging**
```java
ReportProvider.logTestData("User credentials", userDataMap);
ReportProvider.logTestData("API request payload", requestObject);
```

### **File Attachments**
```java
ReportProvider.attachScreenshot("screenshots/login-error.png", "Login error screenshot");
ReportProvider.attachFile("testdata/user-data.json", "Test data file");
```

## 🔄 Automatic Features

### **Automatic Cleanup**
The `BaseTest` class now includes automatic cleanup:
- Screenshots attached to Report Portal automatically
- Step stack cleanup on test completion
- Proper error handling and logging

### **Exception Handling**
```java
ReportProvider.error("Database connection failed", exception);
// This logs both the message and exception details to Report Portal
```

### **Nested Step Management**
```java
ReportProvider.startStep("Main Test Flow");
    ReportProvider.startStep("Sub-step 1");
        ReportProvider.startStep("Detailed action");
        ReportProvider.finishStep(StepStatus.PASSED);
    ReportProvider.finishStep(StepStatus.PASSED);
ReportProvider.finishStep(StepStatus.PASSED);
```

## 🎯 Best Practices

### **1. Step Naming Convention**
- Use descriptive names: "User Login Validation" instead of "Test1"
- Include context: "API Test: Create User" for API tests
- Add descriptions for complex scenarios

### **2. Message Hierarchy**
- Use **INFO** for main test flow messages
- Use **DEBUG** for detailed technical information
- Use **WARN** for unexpected but non-failing conditions
- Use **ERROR** for failures with context

### **3. Step Structure**
```java
// Good structure
ReportProvider.startStep("Complete User Journey", "End-to-end user flow test", StepType.SCENARIO);
try {
    // Nested steps for major phases
    ReportProvider.startStep("Registration Phase");
    // ... registration logic
    ReportProvider.finishStep(StepStatus.PASSED);
    
    ReportProvider.startStep("Login Phase");  
    // ... login logic
    ReportProvider.finishStep(StepStatus.PASSED);
    
} catch (Exception e) {
    ReportProvider.error("User journey failed", e);
    ReportProvider.finishStep(StepStatus.FAILED);
}
```

## 🚀 Advanced Usage

### **Automatic Step Execution**
```java
// Execute code within a managed step
ReportProvider.testStep("Database Validation", () -> {
    // Your test logic here
    validateDatabaseState();
});

// Execute scenario with automatic management
ReportProvider.testScenario("User Registration", "Complete user registration flow", () -> {
    // Your scenario logic here
    performRegistration();
});
```

### **Custom Validation Logging**
```java
ReportProvider.startStep("Custom Business Logic Validation");
boolean isValid = validateBusinessRule();
if (isValid) {
    ReportProvider.info("✅ Business rule validation passed");
} else {
    ReportProvider.error("❌ Business rule validation failed");
}
ReportProvider.finishStep(isValid ? StepStatus.PASSED : StepStatus.FAILED);
```

Your framework now provides **enterprise-level reporting** with:
- 🎨 **Visual hierarchy** with collapsible steps
- 🔍 **Detailed message categorization** 
- 📊 **Automatic test metrics**
- 📷 **Screenshot integration**
- 🔗 **API request/response logging**
- ⚡ **Real-time step management**

This gives you **professional-grade test reporting** that stakeholders can easily understand and navigate!