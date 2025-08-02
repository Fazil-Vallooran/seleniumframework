#!/bin/bash

# Selenium Framework - Local Test Execution Script
# Usage: ./run-tests.sh [test-suite] [browser] [environment]

set -e

# Configuration
DEFAULT_SUITE="smoke"
DEFAULT_BROWSER="chrome"
DEFAULT_ENVIRONMENT="staging"

# Get parameters
TEST_SUITE=${1:-$DEFAULT_SUITE}
BROWSER=${2:-$DEFAULT_BROWSER}
ENVIRONMENT=${3:-$DEFAULT_ENVIRONMENT}

echo "🚀 Starting Selenium Framework Test Execution"
echo "================================================"
echo "Test Suite: $TEST_SUITE"
echo "Browser: $BROWSER"
echo "Environment: $ENVIRONMENT"
echo "================================================"

# Validate test suite
case $TEST_SUITE in
    smoke|regression|api|all)
        echo "✅ Valid test suite: $TEST_SUITE"
        ;;
    *)
        echo "❌ Invalid test suite: $TEST_SUITE"
        echo "Valid options: smoke, regression, api, all"
        exit 1
        ;;
esac

# Create directories
mkdir -p target/screenshots
mkdir -p target/videos
mkdir -p target/test-results

# Function to run tests locally
run_local_tests() {
    echo "🏃 Running tests locally..."
    
    mvn clean test -B \
        -Dtest.suite=$TEST_SUITE \
        -Dbrowser=$BROWSER \
        -Denvironment=$ENVIRONMENT \
        -Dheadless=false \
        -Dparallel.tests=true \
        -Dthread.count=3
}

# Function to run tests in Docker
run_docker_tests() {
    echo "🐳 Running tests in Docker containers..."
    
    # Start Selenium Grid
    docker-compose up -d selenium-hub chrome-node firefox-node
    
    # Wait for grid to be ready
    echo "⏳ Waiting for Selenium Grid to be ready..."
    sleep 30
    
    # Check grid status
    curl -sSL http://localhost:4444/status | jq '.value.ready' || {
        echo "❌ Selenium Grid is not ready"
        docker-compose logs selenium-hub
        exit 1
    }
    
    echo "✅ Selenium Grid is ready"
    
    # Run tests
    docker run --rm \
        --network selenium-network \
        -v $(pwd)/target:/app/test-results \
        -e SELENIUM_HUB_URL=http://selenium-hub:4444/wd/hub \
        -e BROWSER=$BROWSER \
        -e TEST_SUITE=$TEST_SUITE \
        -e ENVIRONMENT=$ENVIRONMENT \
        -e PARALLEL_TESTS=true \
        selenium-framework:latest
    
    # Stop containers
    docker-compose down
}

# Function to generate reports
generate_reports() {
    echo "📊 Generating test reports..."
    
    # Generate Allure report
    mvn allure:report -B
    
    echo "✅ Reports generated successfully!"
    echo "📁 Allure Report: target/site/allure-maven-plugin/index.html"
    echo "📁 TestNG Report: target/surefire-reports/index.html"
    echo "📸 Screenshots: target/screenshots/"
}

# Main execution
echo "🔧 Choose execution mode:"
echo "1) Local execution (default)"
echo "2) Docker execution"
read -p "Enter your choice (1-2): " choice

case $choice in
    2)
        # Build Docker image first
        echo "🏗️ Building Docker image..."
        docker build -t selenium-framework:latest .
        
        run_docker_tests
        ;;
    *)
        run_local_tests
        ;;
esac

# Generate reports
generate_reports

echo "🎉 Test execution completed!"
echo "⏰ Execution time: $SECONDS seconds"