# Data-Driven Testing Guide: Excel, JSON, and XML

## 🎯 Overview
Your framework now supports comprehensive data-driven testing with three formats:
- **Excel** (.xlsx/.xls) - Great for large datasets, easy for non-technical users
- **JSON** - Lightweight, perfect for API testing and modern applications
- **XML** - Structured data with attributes, excellent for complex test scenarios

## 📊 Excel Data Provider Usage

### 1. Basic Excel Operations
```java
// Initialize Excel provider
ExcelDataProvider excelProvider = new ExcelDataProvider("path/to/file.xlsx");

// Select sheet and get data
excelProvider.selectSheet("LoginTests");
Object[][] testData = excelProvider.getTestData();

// Get specific cell data
String username = excelProvider.getCellData(1, 0); // Row 1, Column 0
String password = excelProvider.getCellData(1, "password"); // Row 1, Column name

// Get row as Map for easy access
Map<String, String> rowData = excelProvider.getRowDataAsMap(1);
String email = rowData.get("email");
```

### 2. Excel File Structure Example
```
| username          | password    | expectedResult | description        |
|-------------------|-------------|----------------|--------------------|
| admin@test.com    | admin123    | success        | Valid admin login  |
| user@test.com     | user123     | success        | Valid user login   |
| invalid@test.com  | wrong       | failure        | Invalid password   |
```

## 🔄 JSON Data Provider Usage

### 1. Basic JSON Operations
```java
JsonDataProvider jsonProvider = new JsonDataProvider();

// Read entire JSON file
JSONObject jsonData = jsonProvider.readJsonFile("testdata.json");

// Get test data for TestNG DataProvider
Object[][] testData = jsonProvider.getTestDataFromJson("loginTests.json");

// Get filtered test cases
List<Map<String, Object>> positiveTests = jsonProvider.getTestCasesByCondition(
    "testdata.json", "testType", "positive"
);

// Get specific value using dot notation
String username = jsonProvider.getJsonValue("config.json", "user.credentials.username");
```

### 2. JSON Structure Examples
```json
[
  {
    "testCaseId": "TC001",
    "testType": "positive",
    "username": "admin@example.com",
    "password": "password123",
    "expectedResult": "success"
  }
]
```

## 📝 XML Data Provider Usage

### 1. Basic XML Operations
```java
XmlDataProvider xmlProvider = new XmlDataProvider();

// Get single value using XPath
String username = xmlProvider.getXmlValue("testdata.xml", "//testCase[@id='TC001']/username");

// Get multiple values
List<String> allUsernames = xmlProvider.getXmlValues("testdata.xml", "//username");

// Get test data for TestNG
Object[][] testData = xmlProvider.getTestDataFromXml("testdata.xml", "//testCase");

// Get filtered test cases by attribute
List<Map<String, String>> highPriorityTests = xmlProvider.getTestCasesByAttribute(
    "testdata.xml", "//testCase", "priority", "high"
);
```

### 2. XML Structure Example
```xml
<testSuite name="LoginTests">
    <testCase id="TC001" priority="high" type="positive">
        <username>admin@example.com</username>
        <password>password123</password>
        <expectedResult>success</expectedResult>
    </testCase>
</testSuite>
```

## 🧪 TestNG DataProvider Integration

### 1. Multiple Data Sources in One Test Class
```java
@DataProvider(name = "jsonData")
public Object[][] getJsonData() {
    JsonDataProvider provider = new JsonDataProvider();
    return provider.getTestDataFromJson("src/test/resources/testdata/login.json");
}

@DataProvider(name = "xmlData")
public Object[][] getXmlData() {
    XmlDataProvider provider = new XmlDataProvider();
    return provider.getTestDataFromXml("src/test/resources/testdata/login.xml", "//testCase");
}

@DataProvider(name = "excelData")
public Object[][] getExcelData() {
    ExcelDataProvider provider = new ExcelDataProvider("src/test/resources/testdata/login.xlsx");
    Object[][] data = provider.getTestData("Sheet1");
    provider.closeWorkbook();
    return data;
}
```

### 2. Using Data in Tests
```java
@Test(dataProvider = "jsonData")
public void testWithJsonData(Map<String, Object> testData) {
    String username = (String) testData.get("username");
    String password = (String) testData.get("password");
    // Perform your test...
}

@Test(dataProvider = "xmlData")
public void testWithXmlData(Map<String, String> testData) {
    String username = testData.get("username");
    String priority = testData.get("@priority"); // XML attributes have @ prefix
    // Perform your test...
}
```

## 🎛️ Advanced Features

### 1. Conditional Test Execution
```java
// Run only positive test cases from JSON
JsonDataProvider jsonProvider = new JsonDataProvider();
List<Map<String, Object>> positiveTests = jsonProvider.getTestCasesByCondition(
    "testdata.json", "testType", "positive"
);

// Run only high priority tests from XML
XmlDataProvider xmlProvider = new XmlDataProvider();
List<Map<String, String>> highPriorityTests = xmlProvider.getTestCasesByAttribute(
    "testdata.xml", "//testCase", "priority", "high"
);
```

### 2. Dynamic Data Updates
```java
// Update Excel cell
ExcelDataProvider excelProvider = new ExcelDataProvider("results.xlsx");
excelProvider.setCellData(1, 3, "PASSED"); // Update test result

// Update XML value
XmlDataProvider xmlProvider = new XmlDataProvider();
xmlProvider.updateXmlValue("config.xml", "//environment", "production");

// Update JSON (simple values)
JsonDataProvider jsonProvider = new JsonDataProvider();
jsonProvider.updateJsonValue("config.json", "timeout", "30");
```

## 🏗️ Best Practices

### 1. Data Organization
- **Excel**: Use for large datasets, multiple test suites per file
- **JSON**: Use for API tests, configuration data, modern applications
- **XML**: Use for complex hierarchical data, when attributes are needed

### 2. File Structure
```
src/test/resources/testdata/
├── excel/
│   ├── loginTests.xlsx
│   └── checkoutTests.xlsx
├── json/
│   ├── apiTests.json
│   └── configData.json
└── xml/
    ├── complexScenarios.xml
    └── testSuiteConfig.xml
```

### 3. Error Handling
All data providers include comprehensive error handling and logging:
- File not found scenarios
- Invalid data format handling
- Graceful fallbacks for missing data
- Detailed logging for debugging

## 🚀 Next Steps
1. Create your Excel/JSON/XML test data files
2. Use appropriate DataProvider for your test scenarios
3. Leverage filtering capabilities for selective test execution
4. Implement data updates for result tracking
5. Combine with Report Portal for comprehensive reporting