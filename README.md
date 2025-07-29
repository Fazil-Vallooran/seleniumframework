# Selenium Java Framework with Page Object Model and Report Portal Integration

## Framework Overview
This is a comprehensive Selenium WebDriver framework built with Java, TestNG, and integrated with Report Portal for advanced test reporting and analytics.

## Key Features
- **Page Object Model (POM)** design pattern for maintainable test code
- **Report Portal** integration for real-time test reporting and analytics
- **WebDriver Manager** for automatic driver management
- **TestNG** for test execution and parallel testing
- **Log4j2** for comprehensive logging
- **Screenshot capture** on test failures and success
- **Configuration-driven** testing with properties files
- **Custom listeners** for enhanced reporting

## Framework Structure
```
src/
├── main/java/seleniumproject/project/
│   ├── base/
│   │   ├── BaseTest.java          # Base test class with setup/teardown
│   │   └── DriverManager.java     # WebDriver management with ThreadLocal
│   ├── pages/
│   │   ├── BasePage.java          # Base page with common web operations
│   │   ├── HomePage.java          # Home page objects and methods
│   │   ├── LoginPage.java         # Login page objects and methods
│   │   └── DashboardPage.java     # Dashboard page objects and methods
│   ├── utils/
│   │   ├── ConfigReader.java      # Configuration file reader
│   │   └── ScreenshotUtils.java   # Screenshot utility
│   └── listeners/
│       └── TestListener.java      # Custom TestNG listener
└── test/
    ├── java/seleniumproject/project/tests/
    │   ├── LoginTest.java          # Login functionality tests
    │   └── DashboardTest.java      # Dashboard functionality tests
    └── resources/
        ├── config.properties       # Test configuration
        ├── reportportal.properties # Report Portal configuration
        ├── log4j2.xml             # Logging configuration
        └── testng.xml             # TestNG suite configuration
```

## Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Report Portal server (optional - for full reporting features)

## Setup Instructions

### 1. Report Portal Setup (Optional)
If you want to use Report Portal for advanced reporting:
1. Install Report Portal using Docker: `docker-compose up -d`
2. Access Report Portal at `http://localhost:8080`
3. Create a project and get your API token
4. Update `reportportal.properties` with your server details

### 2. Configuration
Update the following files as needed:

**config.properties:**
```properties
baseUrl=https://demo.nopcommerce.com/
browser=chrome
headless=false
testEmail=your-test-email@example.com
testPassword=your-test-password
```

**reportportal.properties:**
```properties
rp.endpoint=http://localhost:8080
rp.uuid=your-api-token-here
rp.project=your-project-name
rp.launch=selenium-automation
```

## Running Tests

### Command Line Execution
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginTest

# Run with specific browser
mvn clean test -Dbrowser=firefox

# Run in headless mode
mvn clean test -Dheadless=true
```

### IDE Execution
- Right-click on `testng.xml` and select "Run"
- Run individual test classes from IDE

## Reporting
The framework provides multiple reporting options:
1. **Report Portal** - Real-time reporting with analytics (if configured)
2. **TestNG HTML Reports** - Generated in `target/surefire-reports/`
3. **Screenshots** - Captured automatically in `screenshots/` folder
4. **Console Logs** - Detailed execution logs

## Framework Features Explained

### Page Object Model
- Each page is represented by a separate class
- Web elements are defined using `@FindBy` annotations
- Page methods represent user actions on that page
- Inheritance from `BasePage` provides common functionality

### Driver Management
- Thread-safe WebDriver management using ThreadLocal
- Automatic driver setup using WebDriverManager
- Support for Chrome, Firefox, and Edge browsers
- Configurable headless execution

### Test Structure
- All test classes extend `BaseTest` for common setup/teardown
- Tests are organized by functionality (Login, Dashboard, etc.)
- Each test method focuses on a specific scenario
- Comprehensive assertions and logging

### Report Portal Integration
- Automatic test execution reporting
- Step-by-step execution tracking
- Screenshot attachments on failures
- Real-time test analytics and trends

## Best Practices Implemented
- Separation of concerns (Page Objects, Tests, Utilities)
- Configuration-driven approach
- Comprehensive error handling and logging
- Screenshot capture for debugging
- Thread-safe execution for parallel testing
- Clean and maintainable code structure

## Extending the Framework
1. **Add new pages:** Create new classes in `pages/` package extending `BasePage`
2. **Add new tests:** Create test classes in `tests/` package extending `BaseTest`
3. **Add utilities:** Add helper classes in `utils/` package
4. **Configure new environments:** Update `config.properties`

## Troubleshooting
- Ensure correct Java and Maven versions
- Verify browser drivers are accessible (handled by WebDriverManager)
- Check Report Portal server connectivity if using RP integration
- Review logs in console and Report Portal for detailed error information