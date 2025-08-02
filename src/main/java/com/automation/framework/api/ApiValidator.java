package com.automation.framework.api;

import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import com.jayway.jsonpath.JsonPath;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.hamcrest.Matchers.*;

public class ApiValidator {
    
    private static final Logger logger = LogManager.getLogger(ApiValidator.class);
    private Response response;
    
    public ApiValidator(Response response) {
        this.response = response;
    }
    
    /**
     * Validate status code
     */
    public ApiValidator validateStatusCode(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
            "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
        logger.info("Status code validation passed: " + expectedStatusCode);
        return this;
    }
    
    /**
     * Validate status code is in range
     */
    public ApiValidator validateStatusCodeRange(int minCode, int maxCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertTrue(actualStatusCode >= minCode && actualStatusCode <= maxCode,
            "Status code not in expected range [" + minCode + "-" + maxCode + "]. Actual: " + actualStatusCode);
        logger.info("Status code range validation passed: " + actualStatusCode);
        return this;
    }
    
    /**
     * Validate response time is within limit
     */
    public ApiValidator validateResponseTime(long maxTimeInMs) {
        long actualTime = response.getTime();
        Assert.assertTrue(actualTime <= maxTimeInMs,
            "Response time exceeded limit. Expected: <=" + maxTimeInMs + "ms, Actual: " + actualTime + "ms");
        logger.info("Response time validation passed: " + actualTime + "ms");
        return this;
    }
    
    /**
     * Validate response contains specific header
     */
    public ApiValidator validateHeader(String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        Assert.assertNotNull(actualValue, "Header '" + headerName + "' not found in response");
        Assert.assertEquals(actualValue, expectedValue,
            "Header value mismatch for '" + headerName + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
        logger.info("Header validation passed: " + headerName + " = " + expectedValue);
        return this;
    }
    
    /**
     * Validate response header exists
     */
    public ApiValidator validateHeaderExists(String headerName) {
        String headerValue = response.getHeader(headerName);
        Assert.assertNotNull(headerValue, "Header '" + headerName + "' not found in response");
        logger.info("Header existence validation passed: " + headerName);
        return this;
    }
    
    /**
     * Validate content type
     */
    public ApiValidator validateContentType(String expectedContentType) {
        String actualContentType = response.getContentType();
        Assert.assertTrue(actualContentType.contains(expectedContentType),
            "Content type mismatch. Expected to contain: " + expectedContentType + ", Actual: " + actualContentType);
        logger.info("Content type validation passed: " + expectedContentType);
        return this;
    }
    
    /**
     * Validate JSON path exists
     */
    public ApiValidator validateJsonPathExists(String jsonPath) {
        try {
            Object value = JsonPath.read(response.getBody().asString(), jsonPath);
            Assert.assertNotNull(value, "JSON path '" + jsonPath + "' returned null");
            logger.info("JSON path existence validation passed: " + jsonPath);
        } catch (Exception e) {
            Assert.fail("JSON path '" + jsonPath + "' not found in response. Error: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Validate JSON path value
     */
    public ApiValidator validateJsonPathValue(String jsonPath, Object expectedValue) {
        try {
            Object actualValue = JsonPath.read(response.getBody().asString(), jsonPath);
            Assert.assertEquals(actualValue, expectedValue,
                "JSON path value mismatch for '" + jsonPath + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
            logger.info("JSON path value validation passed: " + jsonPath + " = " + expectedValue);
        } catch (Exception e) {
            Assert.fail("Failed to validate JSON path '" + jsonPath + "'. Error: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Validate JSON array size
     */
    public ApiValidator validateJsonArraySize(String jsonPath, int expectedSize) {
        try {
            List<Object> array = JsonPath.read(response.getBody().asString(), jsonPath);
            Assert.assertEquals(array.size(), expectedSize,
                "JSON array size mismatch for '" + jsonPath + "'. Expected: " + expectedSize + ", Actual: " + array.size());
            logger.info("JSON array size validation passed: " + jsonPath + " size = " + expectedSize);
        } catch (Exception e) {
            Assert.fail("Failed to validate JSON array size for '" + jsonPath + "'. Error: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Validate JSON array is not empty
     */
    public ApiValidator validateJsonArrayNotEmpty(String jsonPath) {
        try {
            List<Object> array = JsonPath.read(response.getBody().asString(), jsonPath);
            Assert.assertTrue(array.size() > 0, "JSON array '" + jsonPath + "' is empty");
            logger.info("JSON array non-empty validation passed: " + jsonPath + " size = " + array.size());
        } catch (Exception e) {
            Assert.fail("Failed to validate JSON array '" + jsonPath + "'. Error: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Validate response body contains text
     */
    public ApiValidator validateBodyContains(String expectedText) {
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains(expectedText),
            "Response body does not contain expected text: " + expectedText);
        logger.info("Body content validation passed: contains '" + expectedText + "'");
        return this;
    }
    
    /**
     * Validate response body does not contain text
     */
    public ApiValidator validateBodyDoesNotContain(String unexpectedText) {
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.contains(unexpectedText),
            "Response body contains unexpected text: " + unexpectedText);
        logger.info("Body content validation passed: does not contain '" + unexpectedText + "'");
        return this;
    }
    
    /**
     * Validate response is not empty
     */
    public ApiValidator validateResponseNotEmpty() {
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.isEmpty(), "Response body is empty");
        logger.info("Response body non-empty validation passed");
        return this;
    }
    
    /**
     * Validate JSON schema (requires schema file)
     */
    public ApiValidator validateJsonSchema(String schemaPath) {
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
            logger.info("JSON schema validation passed: " + schemaPath);
        } catch (Exception e) {
            Assert.fail("JSON schema validation failed for '" + schemaPath + "'. Error: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Custom validation with lambda expression
     */
    public ApiValidator validateCustom(String description, ValidationFunction validationFunction) {
        try {
            boolean isValid = validationFunction.validate(response);
            Assert.assertTrue(isValid, "Custom validation failed: " + description);
            logger.info("Custom validation passed: " + description);
        } catch (Exception e) {
            Assert.fail("Custom validation error for '" + description + "'. Error: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Extract value from JSON path for further processing
     */
    public <T> T extractJsonPathValue(String jsonPath, Class<T> type) {
        try {
            Object value = JsonPath.read(response.getBody().asString(), jsonPath);
            logger.info("Extracted value from JSON path '" + jsonPath + "': " + value);
            return type.cast(value);
        } catch (Exception e) {
            logger.error("Failed to extract value from JSON path '" + jsonPath + "'. Error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get response body as string for additional processing
     */
    public String getResponseBody() {
        return response.getBody().asString();
    }
    
    /**
     * Get response headers as map
     */
    public Map<String, String> getResponseHeaders() {
        Map<String, String> headersMap = new HashMap<>();
        response.getHeaders().forEach(header -> 
            headersMap.put(header.getName(), header.getValue())
        );
        return headersMap;
    }
    
    /**
     * Functional interface for custom validations
     */
    @FunctionalInterface
    public interface ValidationFunction {
        boolean validate(Response response) throws Exception;
    }
}