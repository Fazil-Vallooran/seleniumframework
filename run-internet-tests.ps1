# The Internet Test Execution Script for Windows PowerShell
# Usage: .\run-internet-tests.ps1 [browser] [test-suite] [headless]

param(
    [string]$Browser = "chrome",
    [string]$TestSuite = "internet",
    [string]$Headless = "false"
)

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "🚀 Starting The Internet Test Execution" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "📋 Test Configuration:" -ForegroundColor Yellow
Write-Host "   Browser: $Browser" -ForegroundColor White
Write-Host "   Test Suite: $TestSuite" -ForegroundColor White
Write-Host "   Headless Mode: $Headless" -ForegroundColor White
Write-Host ""

# Function to run specific test suite
function Run-TestSuite {
    param([string]$SuiteName)
    
    Write-Host "🧪 Running $SuiteName tests..." -ForegroundColor Blue
    
    switch ($SuiteName.ToLower()) {
        "smoke" {
            mvn test -Dgroups=smoke -Dbrowser=$Browser -Dheadless=$Headless
        }
        "regression" {
            mvn test -Dgroups=regression -Dbrowser=$Browser -Dheadless=$Headless
        }
        "api" {
            mvn test -Dgroups=api
        }
        "login" {
            mvn test -Dtest=LoginTest -Dbrowser=$Browser -Dheadless=$Headless
        }
        "dashboard" {
            mvn test -Dtest=DashboardTest -Dbrowser=$Browser -Dheadless=$Headless
        }
        "internet" {
            mvn test -Dtest=TheInternetTests -Dbrowser=$Browser -Dheadless=$Headless
        }
        "simple" {
            mvn test -Dtest=SimpleWebDriverTest -Dbrowser=$Browser -Dheadless=$Headless
        }
        "all" {
            mvn test -Dbrowser=$Browser -Dheadless=$Headless
        }
        default {
            Write-Host "❌ Unknown test suite: $SuiteName" -ForegroundColor Red
            Write-Host "Available options: smoke, regression, api, login, dashboard, internet, simple, all" -ForegroundColor Yellow
            exit 1
        }
    }
}

# Clean previous results
Write-Host "🧹 Cleaning previous test results..." -ForegroundColor Magenta
mvn clean | Out-Null

# Run the specified test suite
Run-TestSuite -SuiteName $TestSuite

# Check test results
if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Tests completed successfully!" -ForegroundColor Green
    Write-Host "📊 Check the following for results:" -ForegroundColor Yellow
    Write-Host "   - Console output above" -ForegroundColor White
    Write-Host "   - target/surefire-reports/ for detailed reports" -ForegroundColor White
    Write-Host "   - screenshots/ for captured screenshots" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "❌ Tests failed! Check the output above for details." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "🏁 Test execution completed" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Cyan