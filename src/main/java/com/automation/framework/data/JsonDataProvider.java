package com.automation.framework.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonDataProvider {
    
    private static final Logger logger = LogManager.getLogger(JsonDataProvider.class);
    private ObjectMapper objectMapper;
    private JSONParser jsonParser;
    
    public JsonDataProvider() {
        this.objectMapper = new ObjectMapper();
        this.jsonParser = new JSONParser();
    }
    
    /**
     * Read JSON file and return as JSONObject
     */
    public JSONObject readJsonFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            logger.info("JSON file loaded successfully: " + filePath);
            return jsonObject;
        } catch (Exception e) {
            logger.error("Failed to read JSON file: " + filePath + " - " + e.getMessage());
            throw new RuntimeException("Cannot read JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read JSON array from file
     */
    public JSONArray readJsonArray(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
            logger.info("JSON array loaded successfully: " + filePath);
            return jsonArray;
        } catch (Exception e) {
            logger.error("Failed to read JSON array: " + filePath + " - " + e.getMessage());
            throw new RuntimeException("Cannot read JSON array: " + filePath, e);
        }
    }
    
    /**
     * Read JSON and convert to Map
     */
    public Map<String, Object> readJsonAsMap(String filePath) {
        try {
            JsonNode jsonNode = objectMapper.readTree(new FileReader(filePath));
            Map<String, Object> map = objectMapper.convertValue(jsonNode, Map.class);
            logger.info("JSON converted to Map successfully: " + filePath);
            return map;
        } catch (IOException e) {
            logger.error("Failed to convert JSON to Map: " + e.getMessage());
            throw new RuntimeException("Cannot convert JSON to Map: " + filePath, e);
        }
    }
    
    /**
     * Read JSON array and convert to List of Maps (useful for test data)
     */
    public List<Map<String, Object>> readJsonArrayAsMapList(String filePath) {
        try {
            JsonNode jsonNode = objectMapper.readTree(new FileReader(filePath));
            List<Map<String, Object>> mapList = new ArrayList<>();
            
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    Map<String, Object> map = objectMapper.convertValue(node, Map.class);
                    mapList.add(map);
                }
            }
            
            logger.info("JSON array converted to Map List: " + mapList.size() + " items");
            return mapList;
        } catch (IOException e) {
            logger.error("Failed to convert JSON array to Map List: " + e.getMessage());
            throw new RuntimeException("Cannot convert JSON array to Map List: " + filePath, e);
        }
    }
    
    /**
     * Get specific value from JSON using dot notation (e.g., "user.credentials.username")
     */
    public String getJsonValue(String filePath, String jsonPath) {
        try {
            JsonNode rootNode = objectMapper.readTree(new FileReader(filePath));
            String[] pathParts = jsonPath.split("\\.");
            JsonNode currentNode = rootNode;
            
            for (String part : pathParts) {
                if (currentNode.has(part)) {
                    currentNode = currentNode.get(part);
                } else {
                    logger.warn("JSON path not found: " + jsonPath);
                    return null;
                }
            }
            
            return currentNode.asText();
        } catch (IOException e) {
            logger.error("Failed to get JSON value: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get test data from JSON array for TestNG DataProvider
     */
    public Object[][] getTestDataFromJson(String filePath) {
        List<Map<String, Object>> dataList = readJsonArrayAsMapList(filePath);
        
        if (dataList.isEmpty()) {
            return new Object[0][0];
        }
        
        // Convert List of Maps to Object[][]
        Object[][] testData = new Object[dataList.size()][];
        
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> dataMap = dataList.get(i);
            testData[i] = new Object[]{dataMap};
        }
        
        logger.info("Test data prepared from JSON: " + testData.length + " test cases");
        return testData;
    }
    
    /**
     * Get specific test case data by index
     */
    public Map<String, Object> getTestCase(String filePath, int index) {
        List<Map<String, Object>> dataList = readJsonArrayAsMapList(filePath);
        
        if (index >= 0 && index < dataList.size()) {
            return dataList.get(index);
        } else {
            logger.error("Invalid test case index: " + index);
            return new HashMap<>();
        }
    }
    
    /**
     * Get test cases by specific criteria
     */
    public List<Map<String, Object>> getTestCasesByCondition(String filePath, String key, Object value) {
        List<Map<String, Object>> allData = readJsonArrayAsMapList(filePath);
        List<Map<String, Object>> filteredData = new ArrayList<>();
        
        for (Map<String, Object> dataMap : allData) {
            if (dataMap.containsKey(key) && dataMap.get(key).equals(value)) {
                filteredData.add(dataMap);
            }
        }
        
        logger.info("Filtered test cases: " + filteredData.size() + " matches for " + key + "=" + value);
        return filteredData;
    }
    
    /**
     * Write data to JSON file
     */
    public void writeJsonFile(String filePath, Object data) {
        try (FileWriter writer = new FileWriter(filePath)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, data);
            logger.info("Data written to JSON file: " + filePath);
        } catch (IOException e) {
            logger.error("Failed to write JSON file: " + e.getMessage());
            throw new RuntimeException("Cannot write to JSON file: " + filePath, e);
        }
    }
    
    /**
     * Update specific value in JSON file
     */
    public void updateJsonValue(String filePath, String jsonPath, String newValue) {
        try {
            JsonNode rootNode = objectMapper.readTree(new FileReader(filePath));
            
            // This is a simplified implementation
            // For complex JSON updates, consider using JsonPath library
            String[] pathParts = jsonPath.split("\\.");
            
            if (pathParts.length == 1) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) rootNode).put(pathParts[0], newValue);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileWriter(filePath), rootNode);
                logger.info("JSON value updated: " + jsonPath + " = " + newValue);
            } else {
                logger.warn("Complex JSON path updates not implemented in this simplified version");
            }
            
        } catch (IOException e) {
            logger.error("Failed to update JSON value: " + e.getMessage());
        }
    }
}