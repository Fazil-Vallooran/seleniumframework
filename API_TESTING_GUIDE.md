# API Testing Framework Guide

## 🚀 Overview
Your framework now includes comprehensive API testing capabilities using REST Assured, seamlessly integrated with your existing Selenium tests and Report Portal reporting.

## 🏗️ Framework Architecture

### Core API Components
```
com.automation.framework.api/
├── ApiClient.java      # Main API client with fluent interface
└── ApiValidator.java   # Comprehensive response validation utilities
```

### Key Features Added
✅ **REST Assured Integration** - Industry-standard API testing library  
✅ **Fluent API Interface** - Clean, readable test code  
✅ **Comprehensive Validations** - Status codes, response times, JSON paths, schemas  
✅ **Data-Driven Testing** - JSON-based test data with multiple scenarios  
✅ **Report Portal Integration** - API test results alongside UI tests  
✅ **Performance Testing** - Built-in response time validation  
✅ **Error Handling** - Robust error scenarios and negative testing  

## 🔧 API Client Usage

### Basic API Operations
```java
// Initialize API client
ApiClient apiClient = new ApiClient();

// Simple GET request
Response response = apiClient.get("/posts");

// POST request with JSON body
Map<String, Object> requestBody = Map.of(
    "title", "Test Post",
    "body", "Test content",
    "userId", 1
);
Response response = apiClient.setBody(requestBody).post("/posts");

// GET with query parameters
Response response = apiClient
    .addQueryParam("userId", "1")
    .addQueryParam("_limit", "10")
    .get("/posts");

// Request with authentication
Response response = apiClient
    .setAuthToken("your-jwt-token")
    .get("/protected-endpoint");
```

### Advanced API Operations
```java
// Multiple headers
Map<String, String> headers = Map.of(
    "Accept", "application/json",
    "X-Custom-Header", "custom-value"
);
Response response = apiClient
    .addHeaders(headers)
    .setBasicAuth("username", "password")
    .get("/api/data");

// Chain multiple operations
String postId = apiClient
    .setBody(newPostData)
    .post("/posts")
    .jsonPath().getString("id");

// Use the ID in next request
Response updatedPost = apiClient
    .setBody(updateData)
    .put("/posts/" + postId);
```

## ✅ API Validation Capabilities

### Response Validations
```java
ApiValidator validator = new ApiValidator(response);

// Basic validations
validator.validateStatusCode(200)
         .validateResponseTime(2000)
         .validateContentType("application/json");

// JSON path validations
validator.validateJsonPathExists("$.data.id")
         .validateJsonPathValue("$.data.name", "Expected Name")
         .validateJsonArraySize("$.data.items", 5);

// Content validations
validator.validateBodyContains("success")
         .validateBodyDoesNotContain("error")
         .validateResponseNotEmpty();

// Header validations
validator.validateHeader("Content-Type", "application/json")
         .validateHeaderExists("X-Rate-Limit");
```

### Advanced Validations
```java
// Custom validation with lambda
validator.validateCustom("User ID should be positive", (response) -> {
    int userId = response.jsonPath().getInt("userId");
    return userId > 0;
});

// JSON Schema validation
validator.validateJsonSchema("schemas/user-schema.json");

// Performance validation
validator.validateResponseTime(1500); // Max 1.5 seconds
```

## 📊 Data-Driven API Testing

### JSON Test Data Structure
```json
[
  {
    "testCaseId": "API_TC001",
    "testType": "smoke",
    "method": "GET",
    "endpoint": "/posts",
    "expectedStatusCode": 200,
    "expectedResponseTime": 2000,
    "validations": [
      {
        "type": "jsonArraySize",
        "jsonPath": "$",
        "expectedValue": 100
      }
    ]
  }
]
```

### Using Test Data in Tests
```java
@DataProvider(name = "apiTestData")
public Object[][] getApiTestData() {
    JsonDataProvider jsonProvider = new JsonDataProvider();
    return jsonProvider.getTestDataFromJson("src/test/resources/testdata/apiTestData.json");
}

@Test(dataProvider = "apiTestData")
public void testApiEndpoints(Map<String, Object> testData) {
    String method = (String) testData.get("method");
    String endpoint = (String) testData.get("endpoint");
    
    Response response = executeApiRequest(method, endpoint, testData);
    performApiValidations(response, testData);
}
```

## 🧪 Test Examples Included

### 1. Data-Driven API Tests
- **6+ test scenarios** covering GET, POST, PUT, DELETE
- **Positive, negative, and edge cases**
- **Automatic validation** based on test data

### 2. Smoke Tests
```java
@Test(dataProvider = "smokeTestData")
public void testApiSmoke(Map<String, Object> testData) {
    // Quick health checks for critical endpoints
    Response response = executeApiRequest(method, endpoint, testData);
    
    new ApiValidator(response)
        .validateStatusCodeRange(200, 299)
        .validateResponseTime(5000)
        .validateResponseNotEmpty();
}
```

### 3. Performance Tests
```java
@Test
public void testApiPerformance() {
    String[] endpoints = {"/posts", "/posts/1", "/users"};
    
    for (String endpoint : endpoints) {
        Response response = apiClient.get(endpoint);
        new ApiValidator(response)
            .validateStatusCode(200)
            .validateResponseTime(2000); // Max 2 seconds
    }
}
```

### 4. API Chaining (CRUD Operations)
```java
@Test
public void testApiChaining() {
    // Create → Read → Update → Delete
    String postId = createNewPost();
    readPost(postId);
    updatePost(postId);
    deletePost(postId);
}
```

### 5. Error Handling Tests
```java
@Test
public void testApiErrorHandling() {
    // Test 404 scenarios
    Response response = apiClient.get("/posts/99999");
    new ApiValidator(response).validateStatusCode(404);
    
    // Test invalid endpoints
    Response invalidResponse = apiClient.get("/invalid-endpoint");
    new ApiValidator(invalidResponse).validateStatusCode(404);
}
```

## 🔄 Integration with Existing Framework

### Combined UI + API Testing
```java
public class IntegratedTest extends BaseTest {
    
    @Test
    public void testEndToEndFlow() {
        // Step 1: API - Create user data
        ApiClient apiClient = new ApiClient();
        Response createUser = apiClient.setBody(userData).post("/users");
        String userId = createUser.jsonPath().getString("id");
        
        // Step 2: UI - Login with created user
        HomePage homePage = new HomePage();
        LoginPage loginPage = new LoginPage();
        loginPage.login(userEmail, userPassword);
        
        // Step 3: API - Verify user session
        Response sessionCheck = apiClient
            .setAuthToken(extractedToken)
            .get("/users/" + userId + "/session");
        
        new ApiValidator(sessionCheck)
            .validateStatusCode(200)
            .validateJsonPathValue("$.status", "active");
    }
}
```

### Report Portal Integration
All API tests automatically report to Report Portal with:
- **Request/Response details**
- **Step-by-step execution**
- **Performance metrics**
- **Validation results**
- **Error screenshots when combined with UI tests**

## 🚀 Running API Tests

### Command Line Execution
```bash
# Run all tests (UI + API)
mvn clean test

# Run only API tests
mvn clean test -Dtest=ApiTest

# Run specific API test method
mvn clean test -Dtest=ApiTest#testApiChaining

# Run with different environment
mvn clean test -DapiBaseUrl=https://staging-api.example.com
```

### Test Categories
- **Smoke Tests**: Quick health checks
- **Functional Tests**: Core business logic validation
- **Performance Tests**: Response time validation
- **Negative Tests**: Error handling scenarios
- **Integration Tests**: Combined UI + API workflows

## 📋 Configuration

### API Settings in config.properties
```properties
# API Configuration
apiBaseUrl=https://jsonplaceholder.typicode.com
apiTimeout=30000
apiRetryCount=3

# Authentication
authToken=your-jwt-token-here
basicAuthUsername=api-user
basicAuthPassword=api-password
```

### Environment-Specific Testing
```bash
# Development environment
mvn test -DapiBaseUrl=https://dev-api.company.com

# Staging environment  
mvn test -DapiBaseUrl=https://staging-api.company.com

# Production smoke tests
mvn test -Dtest=ApiTest#testApiSmoke -DapiBaseUrl=https://api.company.com
```

## 🎯 Best Practices Implemented

### 1. **Separation of Concerns**
- API client handles HTTP operations
- Validator handles all assertions
- Test data managed externally

### 2. **Comprehensive Error Handling**
- Automatic retry mechanisms
- Detailed error logging
- Graceful failure handling

### 3. **Performance Monitoring**
- Built-in response time tracking
- Performance thresholds
- Report Portal integration

### 4. **Security Testing**
- Authentication scenarios
- Authorization validation
- Error message security

## 🔮 Next Steps

1. **Install Maven** (if not done yet)
2. **Run your first API test**: `mvn test -Dtest=ApiTest#testApiSmoke`
3. **Customize test data** in `apiTestData.json`
4. **Add your API endpoints** to configuration
5. **Combine UI + API** for end-to-end testing

Your framework now supports **complete test automation** covering both UI and API testing with professional-grade reporting and data management!