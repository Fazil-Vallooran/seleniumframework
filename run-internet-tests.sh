#!/bin/bash

# The Internet Test Execution Script
# This script runs the updated Selenium framework tests against The Internet site

echo "=================================================="
echo "🚀 Starting The Internet Test Execution"
echo "=================================================="

# Set default values
BROWSER=${1:-chrome}
TEST_SUITE=${2:-"The Internet Tests"}
HEADLESS=${3:-false}

echo "📋 Test Configuration:"
echo "   Browser: $BROWSER"
echo "   Test Suite: $TEST_SUITE"
echo "   Headless Mode: $HEADLESS"
echo ""

# Function to run specific test suite
run_test_suite() {
    local suite_name="$1"
    echo "🧪 Running $suite_name..."
    
    case "$suite_name" in
        "smoke")
            mvn test -Dgroups=smoke -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        "regression")
            mvn test -Dgroups=regression -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        "api")
            mvn test -Dgroups=api
            ;;
        "login")
            mvn test -Dtest=LoginTest -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        "dashboard")
            mvn test -Dtest=DashboardTest -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        "internet")
            mvn test -Dtest=TheInternetTests -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        "simple")
            mvn test -Dtest=SimpleWebDriverTest -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        "all")
            mvn test -Dbrowser=$BROWSER -Dheadless=$HEADLESS
            ;;
        *)
            echo "❌ Unknown test suite: $suite_name"
            echo "Available options: smoke, regression, api, login, dashboard, internet, simple, all"
            exit 1
            ;;
    esac
}

# Clean previous results
echo "🧹 Cleaning previous test results..."
mvn clean > /dev/null 2>&1

# Run the specified test suite
run_test_suite "$TEST_SUITE"

# Check test results
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Tests completed successfully!"
    echo "📊 Check the following for results:"
    echo "   - Console output above"
    echo "   - target/surefire-reports/ for detailed reports"
    echo "   - screenshots/ for captured screenshots"
else
    echo ""
    echo "❌ Tests failed! Check the output above for details."
    exit 1
fi

echo ""
echo "=================================================="
echo "🏁 Test execution completed"
echo "=================================================="