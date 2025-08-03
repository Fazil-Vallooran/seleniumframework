# Report Portal Setup Guide - Selenium Framework

## 🎯 **Configuration Summary**
- **Project Name**: selenium_framework
- **Login Username**: ophi
- **Login Password**: ophi@123
- **Report Portal URL**: http://localhost:8080
- **Report Portal UI**: http://localhost:8081

## 🚀 **Quick Start Steps**

### 1. Start Report Portal Services
```bash
# Start all services including Report Portal
docker-compose up -d reportportal-db reportportal reportportal-ui

# Check if services are running
docker-compose ps
```

### 2. Access Report Portal UI
1. Open your browser and go to: **http://localhost:8081**
2. Login with:
   - **Username**: `ophi`
   - **Password**: `ophi@123`

### 3. Create/Verify Project
1. After login, navigate to **Projects**
2. Look for project named **"selenium_framework"**
3. If not exists, create it with name: `selenium_framework`

### 4. Get Project UUID (Important!)
1. Go to **Project Settings** → **General**
2. Copy the **Project UUID**
3. Update the UUID in your config files:

**File: `src/main/resources/config.properties`**
```properties
rp.uuid=YOUR_ACTUAL_PROJECT_UUID_HERE
```

**File: `src/test/resources/reportportal.properties`**
```properties
rp.uuid = YOUR_ACTUAL_PROJECT_UUID_HERE
```

## 🧪 **Running Tests with Report Portal**

### Option 1: Local Execution
```bash
# Run tests with Report Portal enabled
mvn test -Drp.enable=true

# Run specific test suite
mvn test -Dtest=LoginTest -Drp.enable=true
```

### Option 2: Docker Execution
```bash
# Start all services including test execution
docker-compose up --build

# Or run specific services
docker-compose up -d reportportal-db reportportal reportportal-ui
docker-compose up test-runner
```

## 📊 **Viewing Reports**

### Access Your Reports
1. Go to **http://localhost:8081**
2. Login with `ophi` / `ophi@123`
3. Select project **"selenium_framework"**
4. View your test launches under **Launches** tab

### Report Features Available
- ✅ **Test Execution Results** - Pass/Fail status
- ✅ **Screenshots** - Automatic capture on failures
- ✅ **Logs** - Detailed test execution logs
- ✅ **Trends Analysis** - Historical test data
- ✅ **Defect Analysis** - Issue categorization

## 🔧 **Configuration Files Updated**

### config.properties
```properties
rp.endpoint=http://localhost:8080
rp.uuid=selenium-framework-uuid-12345
rp.launch=selenium-automation-tests
rp.project=selenium_framework
rp.enable=true
```

### reportportal.properties
```properties
rp.project = selenium_framework
rp.launch = selenium-automation-tests
rp.enable = true
rp.description = Selenium Framework Test Execution
```

### docker-compose.yml
```yaml
reportportal:
  environment:
    - RP_ADMIN_USER=ophi
    - RP_ADMIN_PASS=ophi@123
    - RP_PROJECT_NAME=selenium_framework
```

## 🎯 **Important Notes**

1. **First Login**: Use `ophi` / `ophi@123` credentials
2. **Project UUID**: Must be copied from Report Portal UI after project creation
3. **Project Name**: Will appear as "selenium_framework" in the UI
4. **Ports**: 
   - API: `8080`
   - UI: `8081`
5. **Database**: PostgreSQL data persisted in Docker volume

## 🚨 **Troubleshooting**

### If Report Portal doesn't show your project:
1. Check if `rp.enable=true` in your properties
2. Verify the correct UUID is set
3. Ensure the project name matches exactly: `selenium_framework`

### If tests don't appear in Report Portal:
1. Check container logs: `docker-compose logs reportportal`
2. Verify TestNG listener is configured in your tests
3. Ensure network connectivity between test runner and Report Portal

## 🎉 **You're All Set!**

Your Report Portal is now configured with:
- **Username**: ophi
- **Password**: ophi@123
- **Project**: selenium_framework

Start your services and begin testing with comprehensive reporting!