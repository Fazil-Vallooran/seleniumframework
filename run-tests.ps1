# Selenium Framework - Windows Test Execution Script
# Usage: .\run-tests.ps1 [TestSuite] [Browser] [Environment]

param(
    [Parameter(Position=0)]
    [ValidateSet("smoke", "regression", "api", "all")]
    [string]$TestSuite = "smoke",
    
    [Parameter(Position=1)]
    [ValidateSet("chrome", "firefox", "edge")]
    [string]$Browser = "chrome",
    
    [Parameter(Position=2)]
    [ValidateSet("dev", "staging", "prod")]
    [string]$Environment = "staging",
    
    [Parameter()]
    [switch]$Docker,
    
    [Parameter()]
    [switch]$Parallel = $true
)

Write-Host "🚀 Starting Selenium Framework Test Execution" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Yellow
Write-Host "Test Suite: $TestSuite" -ForegroundColor Cyan
Write-Host "Browser: $Browser" -ForegroundColor Cyan
Write-Host "Environment: $Environment" -ForegroundColor Cyan
Write-Host "Parallel Execution: $Parallel" -ForegroundColor Cyan
Write-Host "Docker Mode: $Docker" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Yellow

# Create required directories
$directories = @("target\screenshots", "target\videos", "target\test-results")
foreach ($dir in $directories) {
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "✅ Created directory: $dir" -ForegroundColor Green
    }
}

# Function to run tests locally
function Run-LocalTests {
    Write-Host "🏃 Running tests locally..." -ForegroundColor Green
    
    $mavenArgs = @(
        "clean", "test", "-B",
        "-Dtest.suite=$TestSuite",
        "-Dbrowser=$Browser",
        "-Denvironment=$Environment",
        "-Dheadless=false",
        "-Dparallel.tests=$($Parallel.ToString().ToLower())",
        "-Dthread.count=3"
    )
    
    & mvn $mavenArgs
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Test execution failed!" -ForegroundColor Red
        exit $LASTEXITCODE
    }
}

# Function to run tests in Docker
function Run-DockerTests {
    Write-Host "🐳 Running tests in Docker containers..." -ForegroundColor Green
    
    # Check if Docker is running
    try {
        docker version | Out-Null
    }
    catch {
        Write-Host "❌ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
        exit 1
    }
    
    # Build Docker image
    Write-Host "🏗️ Building Docker image..." -ForegroundColor Yellow
    docker build -t selenium-framework:latest .
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Docker image build failed!" -ForegroundColor Red
        exit $LASTEXITCODE
    }
    
    # Start Selenium Grid
    Write-Host "🌐 Starting Selenium Grid..." -ForegroundColor Yellow
    docker-compose up -d selenium-hub chrome-node firefox-node
    
    # Wait for grid to be ready
    Write-Host "⏳ Waiting for Selenium Grid to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    # Check grid status
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:4444/status"
        if ($response.value.ready -eq $true) {
            Write-Host "✅ Selenium Grid is ready" -ForegroundColor Green
        } else {
            throw "Grid not ready"
        }
    }
    catch {
        Write-Host "❌ Selenium Grid is not ready" -ForegroundColor Red
        docker-compose logs selenium-hub
        docker-compose down
        exit 1
    }
    
    # Run tests in container
    $dockerArgs = @(
        "run", "--rm",
        "--network", "selenium-network",
        "-v", "$(Get-Location)\target:/app/test-results",
        "-e", "SELENIUM_HUB_URL=http://selenium-hub:4444/wd/hub",
        "-e", "BROWSER=$Browser",
        "-e", "TEST_SUITE=$TestSuite",
        "-e", "ENVIRONMENT=$Environment",
        "-e", "PARALLEL_TESTS=$($Parallel.ToString().ToLower())",
        "selenium-framework:latest"
    )
    
    & docker $dockerArgs
    
    # Stop containers
    Write-Host "🛑 Stopping Docker containers..." -ForegroundColor Yellow
    docker-compose down
}

# Function to generate reports
function Generate-Reports {
    Write-Host "📊 Generating test reports..." -ForegroundColor Green
    
    # Generate Allure report
    mvn allure:report -B
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Reports generated successfully!" -ForegroundColor Green
        Write-Host "📁 Allure Report: target\site\allure-maven-plugin\index.html" -ForegroundColor Cyan
        Write-Host "📁 TestNG Report: target\surefire-reports\index.html" -ForegroundColor Cyan
        Write-Host "📸 Screenshots: target\screenshots\" -ForegroundColor Cyan
        
        # Open report in browser
        $reportPath = "target\site\allure-maven-plugin\index.html"
        if (Test-Path $reportPath) {
            Write-Host "🌐 Opening report in browser..." -ForegroundColor Green
            Start-Process $reportPath
        }
    } else {
        Write-Host "⚠️ Report generation failed, but continuing..." -ForegroundColor Yellow
    }
}

# Main execution
$startTime = Get-Date

try {
    if ($Docker) {
        Run-DockerTests
    } else {
        Run-LocalTests
    }
    
    Generate-Reports
    
    $endTime = Get-Date
    $duration = $endTime - $startTime
    
    Write-Host "🎉 Test execution completed successfully!" -ForegroundColor Green
    Write-Host "⏰ Total execution time: $($duration.TotalMinutes.ToString('F2')) minutes" -ForegroundColor Cyan
}
catch {
    Write-Host "❌ Test execution failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}