package com.automation.framework.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.automation.framework.utils.ConfigReader;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiClient {
    
    private static final Logger logger = LogManager.getLogger(ApiClient.class);
    private RequestSpecification requestSpec;
    
    public ApiClient() {
        initializeApiClient();
    }
    
    private void initializeApiClient() {
        String baseUrl = ConfigReader.getProperty("apiBaseUrl");
        if (baseUrl != null) {
            RestAssured.baseURI = baseUrl;
            logger.info("API Base URL set to: " + baseUrl);
        }
        
        // Set default configurations
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Initialize request specification
        requestSpec = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        
        logger.info("API Client initialized successfully");
    }
    
    /**
     * Set authentication token
     */
    public ApiClient setAuthToken(String token) {
        requestSpec.header("Authorization", "Bearer " + token);
        logger.info("Authentication token set");
        return this;
    }
    
    /**
     * Set basic authentication
     */
    public ApiClient setBasicAuth(String username, String password) {
        requestSpec.auth().basic(username, password);
        logger.info("Basic authentication set for user: " + username);
        return this;
    }
    
    /**
     * Add custom header
     */
    public ApiClient addHeader(String key, String value) {
        requestSpec.header(key, value);
        logger.info("Header added: " + key + " = " + value);
        return this;
    }
    
    /**
     * Add multiple headers
     */
    public ApiClient addHeaders(Map<String, String> headers) {
        requestSpec.headers(headers);
        logger.info("Multiple headers added: " + headers.size() + " headers");
        return this;
    }
    
    /**
     * Add query parameter
     */
    public ApiClient addQueryParam(String key, String value) {
        requestSpec.queryParam(key, value);
        logger.info("Query parameter added: " + key + " = " + value);
        return this;
    }
    
    /**
     * Add multiple query parameters
     */
    public ApiClient addQueryParams(Map<String, String> params) {
        requestSpec.queryParams(params);
        logger.info("Multiple query parameters added: " + params.size() + " parameters");
        return this;
    }
    
    /**
     * Set request body as JSON string
     */
    public ApiClient setJsonBody(String jsonBody) {
        requestSpec.body(jsonBody);
        logger.info("JSON body set");
        return this;
    }
    
    /**
     * Set request body as Object (will be serialized to JSON)
     */
    public ApiClient setBody(Object body) {
        requestSpec.body(body);
        logger.info("Request body set from object: " + body.getClass().getSimpleName());
        return this;
    }
    
    /**
     * Perform GET request
     */
    public Response get(String endpoint) {
        logger.info("Executing GET request to: " + endpoint);
        Response response = requestSpec.when().get(endpoint);
        logResponse(response, "GET", endpoint);
        return response;
    }
    
    /**
     * Perform POST request
     */
    public Response post(String endpoint) {
        logger.info("Executing POST request to: " + endpoint);
        Response response = requestSpec.when().post(endpoint);
        logResponse(response, "POST", endpoint);
        return response;
    }
    
    /**
     * Perform PUT request
     */
    public Response put(String endpoint) {
        logger.info("Executing PUT request to: " + endpoint);
        Response response = requestSpec.when().put(endpoint);
        logResponse(response, "PUT", endpoint);
        return response;
    }
    
    /**
     * Perform PATCH request
     */
    public Response patch(String endpoint) {
        logger.info("Executing PATCH request to: " + endpoint);
        Response response = requestSpec.when().patch(endpoint);
        logResponse(response, "PATCH", endpoint);
        return response;
    }
    
    /**
     * Perform DELETE request
     */
    public Response delete(String endpoint) {
        logger.info("Executing DELETE request to: " + endpoint);
        Response response = requestSpec.when().delete(endpoint);
        logResponse(response, "DELETE", endpoint);
        return response;
    }
    
    /**
     * Log response details
     */
    private void logResponse(Response response, String method, String endpoint) {
        logger.info(method + " " + endpoint + " - Status: " + response.getStatusCode() + 
                   " - Response Time: " + response.getTime() + "ms");
        
        if (response.getStatusCode() >= 400) {
            logger.error("API Error Response Body: " + response.getBody().asString());
        }
    }
    
    /**
     * Reset request specification to defaults
     */
    public ApiClient reset() {
        requestSpec = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        logger.info("API Client reset to default configuration");
        return this;
    }
    
    /**
     * Get current request specification for advanced usage
     */
    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }
}