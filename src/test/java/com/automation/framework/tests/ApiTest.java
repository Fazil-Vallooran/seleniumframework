package com.automation.framework.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import io.restassured.response.Response;
import com.automation.framework.api.ApiClient;
import com.automation.framework.api.ApiValidator;
import com.automation.framework.data.JsonDataProvider;
import com.automation.framework.utils.ReportProvider;
import com.automation.framework.utils.ReportProvider.StepStatus;
import com.automation.framework.utils.ReportProvider.StepType;
import com.epam.reportportal.annotations.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ApiTest {
    
    private static final Logger logger = LogManager.getLogger(ApiTest.class);
    private ApiClient apiClient;
    
    @BeforeClass
    public void setUp() {
        apiClient = new ApiClient();
        ReportProvider.info("API Test Suite initialized");
    }
    
    // Data Provider for API tests from JSON
    @DataProvider(name = "apiTestData")
    public Object[][] getApiTestData() {
        JsonDataProvider jsonProvider = new JsonDataProvider();
        return jsonProvider.getTestDataFromJson("src/test/resources/testdata/apiTestData.json");
    }
    
    // Data Provider for specific test types
    @DataProvider(name = "smokeTestData")
    public Object[][] getSmokeTestData() {
        JsonDataProvider jsonProvider = new JsonDataProvider();
        List<Map<String, Object>> smokeTests = jsonProvider.getTestCasesByCondition(
            "src/test/resources/testdata/apiTestData.json", "testType", "smoke"
        );
        
        Object[][] testData = new Object[smokeTests.size()][];
        for (int i = 0; i < smokeTests.size(); i++) {
            testData[i] = new Object[]{smokeTests.get(i)};
        }
        return testData;
    }
    
    @Test(dataProvider = "apiTestData", description = "Data-driven API testing")
    public void testApiEndpoints(Map<String, Object> testData) {
        String testCaseId = (String) testData.get("testCaseId");
        String description = (String) testData.get("description");
        
        ReportProvider.startStep("API Test: " + testCaseId, description, StepType.SCENARIO);
        ReportProvider.logTestData("API Test Data", testData);
        
        try {
            // Extract test data
            String method = (String) testData.get("method");
            String endpoint = (String) testData.get("endpoint");
            int expectedStatusCode = ((Number) testData.get("expectedStatusCode")).intValue();
            int expectedResponseTime = ((Number) testData.get("expectedResponseTime")).intValue();
            
            // Execute API request with enhanced reporting
            Response response = executeApiRequestWithReporting(method, endpoint, testData);
            
            // Perform validations with detailed reporting
            performApiValidationsWithReporting(response, testData, expectedStatusCode, expectedResponseTime);
            
            ReportProvider.info("✅ API test completed successfully: " + testCaseId);
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("API test failed: " + testCaseId, e);
            ReportProvider.finishStep(StepStatus.FAILED, "Test execution failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(dataProvider = "smokeTestData", description = "API smoke tests")
    public void testApiSmoke(Map<String, Object> testData) {
        String testCaseId = (String) testData.get("testCaseId");
        logger.info("Starting API smoke test: " + testCaseId);
        
        String method = (String) testData.get("method");
        String endpoint = (String) testData.get("endpoint");
        
        Response response = executeApiRequest(method, endpoint, testData);
        
        // Basic smoke test validations
        new ApiValidator(response)
            .validateStatusCodeRange(200, 299)
            .validateResponseTime(5000)
            .validateResponseNotEmpty();
        
        logger.info("API smoke test passed: " + testCaseId);
    }
    
    @Test(description = "API performance test - check response times")
    public void testApiPerformance() {
        logger.info("Starting API performance test");
        
        // Test multiple endpoints for performance
        String[] endpoints = {"/posts", "/posts/1", "/users", "/users/1"};
        
        for (String endpoint : endpoints) {
            Response response = apiClient.get(endpoint);
            
            new ApiValidator(response)
                .validateStatusCode(200)
                .validateResponseTime(2000); // Max 2 seconds
            
            logger.info("Performance test passed for endpoint: " + endpoint + 
                       " - Response time: " + response.getTime() + "ms");
        }
    }
    
    @Test(description = "API chaining test - create, read, update, delete")
    public void testApiChaining() {
        ReportProvider.startStep("API CRUD Operations Chain", "Complete lifecycle testing of API operations", StepType.SCENARIO);
        
        try {
            // Step 1: Create a new post
            String newPostId = createNewPostWithReporting();
            
            // Step 2: Read the created post
            readPostWithReporting(newPostId);
            
            // Step 3: Update the post
            updatePostWithReporting(newPostId);
            
            // Step 4: Delete the post
            deletePostWithReporting(newPostId);
            
            ReportProvider.info("✅ API chaining test completed successfully");
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("API chaining test failed", e);
            ReportProvider.finishStep(StepStatus.FAILED, "CRUD operation chain failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "API error handling test")
    public void testApiErrorHandling() {
        logger.info("Starting API error handling test");
        
        // Test various error scenarios
        testNotFoundError();
        testInvalidEndpoint();
        
        logger.info("API error handling test completed");
    }
    
    @Step("Execute API request with enhanced reporting")
    private Response executeApiRequestWithReporting(String method, String endpoint, Map<String, Object> testData) {
        ReportProvider.startStep("Execute " + method + " Request", "Endpoint: " + endpoint);
        
        try {
            // Reset client to ensure clean state
            apiClient.reset();
            
            // Add request body if provided
            if (testData.containsKey("requestBody")) {
                Object requestBody = testData.get("requestBody");
                ReportProvider.debug("Setting request body: " + requestBody.toString());
                apiClient.setBody(requestBody);
            }
            
            // Log the API request details
            ReportProvider.logApiRequest(method, endpoint, 
                testData.containsKey("requestBody") ? testData.get("requestBody").toString() : null);
            
            // Execute request based on method
            Response response;
            switch (method.toUpperCase()) {
                case "GET":
                    response = apiClient.get(endpoint);
                    break;
                case "POST":
                    response = apiClient.post(endpoint);
                    break;
                case "PUT":
                    response = apiClient.put(endpoint);
                    break;
                case "DELETE":
                    response = apiClient.delete(endpoint);
                    break;
                case "PATCH":
                    response = apiClient.patch(endpoint);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }
            
            // Log API response details
            ReportProvider.logApiResponse(response.getStatusCode(), response.getTime(), 
                response.getBody().asString());
            
            ReportProvider.finishStep(StepStatus.PASSED);
            return response;
            
        } catch (Exception e) {
            ReportProvider.error("API request execution failed", e);
            ReportProvider.finishStep(StepStatus.FAILED, "Request failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Step("Perform API validations with reporting")
    private void performApiValidationsWithReporting(Response response, Map<String, Object> testData, 
                                                   int expectedStatusCode, int expectedResponseTime) {
        
        ReportProvider.startStep("API Response Validation", "Validating response against expected criteria");
        
        try {
            ApiValidator validator = new ApiValidator(response);
            
            // Basic validations with reporting
            ReportProvider.startStep("Basic Response Validation");
            
            ReportProvider.info("Validating status code: " + expectedStatusCode);
            validator.validateStatusCode(expectedStatusCode);
            ReportProvider.info("✅ Status code validation passed");
            
            ReportProvider.info("Validating response time: ≤ " + expectedResponseTime + "ms");
            validator.validateResponseTime(expectedResponseTime);
            ReportProvider.info("✅ Response time validation passed: " + response.getTime() + "ms");
            
            ReportProvider.info("Validating content type: application/json");
            validator.validateContentType("application/json");
            ReportProvider.info("✅ Content type validation passed");
            
            ReportProvider.finishStep(StepStatus.PASSED);
            
            // Custom validations from test data with reporting
            if (testData.containsKey("validations")) {
                ReportProvider.startStep("Custom Validations");
                
                List<Map<String, Object>> validations = (List<Map<String, Object>>) testData.get("validations");
                
                for (Map<String, Object> validation : validations) {
                    performSpecificValidationWithReporting(validator, validation);
                }
                
                ReportProvider.finishStep(StepStatus.PASSED);
            }
            
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("API validation failed", e);
            ReportProvider.finishStep(StepStatus.FAILED, "Validation failed: " + e.getMessage());
            throw e;
        }
    }
    
    private void performSpecificValidationWithReporting(ApiValidator validator, Map<String, Object> validation) {
        String validationType = (String) validation.get("type");
        ReportProvider.info("Performing validation: " + validationType);
        
        try {
            switch (validationType) {
                case "jsonPathExists":
                    String jsonPath = (String) validation.get("jsonPath");
                    ReportProvider.debug("Checking JSON path exists: " + jsonPath);
                    validator.validateJsonPathExists(jsonPath);
                    ReportProvider.info("✅ JSON path validation passed: " + jsonPath);
                    break;
                    
                case "jsonPathValue":
                    String pathForValue = (String) validation.get("jsonPath");
                    Object expectedValue = validation.get("expectedValue");
                    ReportProvider.debug("Checking JSON path value: " + pathForValue + " = " + expectedValue);
                    validator.validateJsonPathValue(pathForValue, expectedValue);
                    ReportProvider.info("✅ JSON path value validation passed");
                    break;
                    
                case "jsonArraySize":
                    String arrayPath = (String) validation.get("jsonPath");
                    int expectedSize = ((Number) validation.get("expectedValue")).intValue();
                    ReportProvider.debug("Checking JSON array size: " + arrayPath + " = " + expectedSize);
                    validator.validateJsonArraySize(arrayPath, expectedSize);
                    ReportProvider.info("✅ JSON array size validation passed");
                    break;
                    
                case "bodyContains":
                    String expectedText = (String) validation.get("expectedValue");
                    ReportProvider.debug("Checking response body contains: " + expectedText);
                    validator.validateBodyContains(expectedText);
                    ReportProvider.info("✅ Body content validation passed");
                    break;
                    
                default:
                    ReportProvider.warn("Unknown validation type: " + validationType);
            }
        } catch (Exception e) {
            ReportProvider.error("Specific validation failed: " + validationType, e);
            throw e;
        }
    }
    
    @Step("Create new post with reporting")
    private String createNewPostWithReporting() {
        ReportProvider.startStep("Create New Post", "POST /posts");
        
        try {
            Map<String, Object> postData = Map.of(
                "title", "Test Post from Automation",
                "body", "This post was created by the automation framework",
                "userId", 1
            );
            
            ReportProvider.logApiRequest("POST", "/posts", postData.toString());
            Response response = apiClient.setBody(postData).post("/posts");
            ReportProvider.logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            
            new ApiValidator(response)
                .validateStatusCode(201)
                .validateJsonPathExists("$.id")
                .validateJsonPathValue("$.title", "Test Post from Automation");
            
            String postId = response.jsonPath().getString("id");
            ReportProvider.info("✅ Created new post with ID: " + postId);
            ReportProvider.finishStep(StepStatus.PASSED);
            return postId;
            
        } catch (Exception e) {
            ReportProvider.error("Failed to create new post", e);
            ReportProvider.finishStep(StepStatus.FAILED, "Post creation failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Step("Read post with reporting")
    private void readPostWithReporting(String postId) {
        ReportProvider.startStep("Read Post", "GET /posts/" + postId);
        
        try {
            String endpoint = "/posts/" + postId;
            ReportProvider.logApiRequest("GET", endpoint, null);
            
            Response response = apiClient.get(endpoint);
            ReportProvider.logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            
            new ApiValidator(response)
                .validateStatusCode(200)
                .validateJsonPathExists("$.id")
                .validateJsonPathExists("$.title")
                .validateJsonPathExists("$.body");
            
            ReportProvider.info("✅ Successfully read post: " + postId);
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("Failed to read post: " + postId, e);
            ReportProvider.finishStep(StepStatus.FAILED, "Post read failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Step("Update post with reporting")
    private void updatePostWithReporting(String postId) {
        ReportProvider.startStep("Update Post", "PUT /posts/" + postId);
        
        try {
            Map<String, Object> updateData = Map.of(
                "id", Integer.parseInt(postId),
                "title", "Updated Test Post",
                "body", "This post was updated by automation",
                "userId", 1
            );
            
            String endpoint = "/posts/" + postId;
            ReportProvider.logApiRequest("PUT", endpoint, updateData.toString());
            
            Response response = apiClient.setBody(updateData).put(endpoint);
            ReportProvider.logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            
            new ApiValidator(response)
                .validateStatusCode(200)
                .validateJsonPathValue("$.title", "Updated Test Post");
            
            ReportProvider.info("✅ Successfully updated post: " + postId);
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("Failed to update post: " + postId, e);
            ReportProvider.finishStep(StepStatus.FAILED, "Post update failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Step("Delete post with reporting")
    private void deletePostWithReporting(String postId) {
        ReportProvider.startStep("Delete Post", "DELETE /posts/" + postId);
        
        try {
            String endpoint = "/posts/" + postId;
            ReportProvider.logApiRequest("DELETE", endpoint, null);
            
            Response response = apiClient.delete(endpoint);
            ReportProvider.logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            
            new ApiValidator(response)
                .validateStatusCode(200);
            
            ReportProvider.info("✅ Successfully deleted post: " + postId);
            ReportProvider.finishStep(StepStatus.PASSED);
            
        } catch (Exception e) {
            ReportProvider.error("Failed to delete post: " + postId, e);
            ReportProvider.finishStep(StepStatus.FAILED, "Post deletion failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Step("Test 404 error handling")
    private void testNotFoundError() {
        Response response = apiClient.get("/posts/99999");
        
        new ApiValidator(response)
            .validateStatusCode(404);
        
        logger.info("404 error handling test passed");
    }
    
    @Step("Test invalid endpoint")
    private void testInvalidEndpoint() {
        Response response = apiClient.get("/invalid-endpoint");
        
        new ApiValidator(response)
            .validateStatusCode(404);
        
        logger.info("Invalid endpoint test passed");
    }
    
    @Step("Execute API request")
    private Response executeApiRequest(String method, String endpoint, Map<String, Object> testData) {
        // Reset client to ensure clean state
        apiClient.reset();
        
        // Add request body if provided
        if (testData.containsKey("requestBody")) {
            apiClient.setBody(testData.get("requestBody"));
        }
        
        // Execute request based on method
        Response response;
        switch (method.toUpperCase()) {
            case "GET":
                response = apiClient.get(endpoint);
                break;
            case "POST":
                response = apiClient.post(endpoint);
                break;
            case "PUT":
                response = apiClient.put(endpoint);
                break;
            case "DELETE":
                response = apiClient.delete(endpoint);
                break;
            case "PATCH":
                response = apiClient.patch(endpoint);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        
        return response;
    }
}
