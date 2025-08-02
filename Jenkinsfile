pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.4'
        jdk 'JDK-11'
    }
    
    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['smoke', 'regression', 'api', 'all'],
            description: 'Select test suite to run'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Select browser for testing'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Select environment to test'
        )
        booleanParam(
            name: 'PARALLEL_EXECUTION',
            defaultValue: true,
            description: 'Run tests in parallel'
        )
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
        DOCKER_BUILDKIT = '1'
        BROWSER = "${params.BROWSER}"
        TEST_SUITE = "${params.TEST_SUITE}"
        ENVIRONMENT = "${params.ENVIRONMENT}"
        PARALLEL_TESTS = "${params.PARALLEL_EXECUTION}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                }
            }
        }
        
        stage('Code Quality Analysis') {
            parallel {
                stage('Compile & Unit Tests') {
                    steps {
                        echo 'Compiling code and running unit tests...'
                        sh 'mvn clean compile test-compile -B'
                        sh 'mvn test -B -Dtest=!**/*IntegrationTest'
                    }
                    post {
                        always {
                            publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                        }
                    }
                }
                
                stage('Static Code Analysis') {
                    steps {
                        echo 'Running static code analysis...'
                        script {
                            if (env.SONAR_TOKEN) {
                                sh '''
                                    mvn sonar:sonar \
                                        -Dsonar.projectKey=selenium-framework \
                                        -Dsonar.host.url=${SONAR_HOST_URL} \
                                        -Dsonar.login=${SONAR_TOKEN}
                                '''
                            } else {
                                echo 'SonarQube analysis skipped - SONAR_TOKEN not configured'
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Framework') {
            steps {
                echo 'Building Selenium framework JAR...'
                sh 'mvn clean package -DskipTests -B'
                sh 'mvn source:jar javadoc:jar -B'
                
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Building Docker image for test execution...'
                script {
                    def dockerImage = docker.build("selenium-framework:${env.BUILD_NUMBER}")
                    dockerImage.tag("selenium-framework:latest")
                }
            }
        }
        
        stage('Integration Tests') {
            parallel {
                stage('Chrome Tests') {
                    when {
                        anyOf {
                            environment name: 'BROWSER', value: 'chrome'
                            environment name: 'BROWSER', value: 'all'
                        }
                    }
                    steps {
                        script {
                            runTestsInDocker('chrome')
                        }
                    }
                }
                
                stage('Firefox Tests') {
                    when {
                        anyOf {
                            environment name: 'BROWSER', value: 'firefox'
                            environment name: 'BROWSER', value: 'all'
                        }
                    }
                    steps {
                        script {
                            runTestsInDocker('firefox')
                        }
                    }
                }
                
                stage('Edge Tests') {
                    when {
                        anyOf {
                            environment name: 'BROWSER', value: 'edge'
                            environment name: 'BROWSER', value: 'all'
                        }
                    }
                    steps {
                        script {
                            runTestsInDocker('edge')
                        }
                    }
                }
            }
        }
        
        stage('Performance Tests') {
            when {
                anyOf {
                    environment name: 'TEST_SUITE', value: 'regression'
                    environment name: 'TEST_SUITE', value: 'all'
                }
            }
            steps {
                echo 'Running performance tests...'
                sh '''
                    mvn test -B \
                        -Dtest=**/*PerformanceTest \
                        -Dbrowser=${BROWSER} \
                        -Denvironment=${ENVIRONMENT} \
                        -Dheadless=true
                '''
            }
        }
        
        stage('Security Tests') {
            when {
                environment name: 'TEST_SUITE', value: 'all'
            }
            steps {
                echo 'Running security tests...'
                script {
                    try {
                        sh '''
                            docker run --rm \
                                -v $(pwd):/zap/wrk/:rw \
                                -t owasp/zap2docker-stable zap-baseline.py \
                                -t https://${ENVIRONMENT}.yourapp.com \
                                -J zap-report.json || true
                        '''
                    } catch (Exception e) {
                        echo "Security scan completed with warnings: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                echo 'Generating test reports...'
                
                // Generate Allure Report
                sh 'mvn allure:report -B'
                
                // Publish reports
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/site/allure-maven-plugin',
                    reportFiles: 'index.html',
                    reportName: 'Allure Test Report',
                    reportTitles: ''
                ])
                
                // Archive test artifacts
                archiveArtifacts artifacts: 'target/screenshots/**, target/videos/**, target/surefire-reports/**', allowEmptyArchive: true
            }
        }
        
        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'main'
                }
            }
            steps {
                echo 'Deploying application to staging environment...'
                script {
                    // Add your deployment logic here
                    sh '''
                        echo "Deploying to ${ENVIRONMENT} environment..."
                        # kubectl apply -f k8s/staging/
                        # or docker-compose -f docker-compose.staging.yml up -d
                    '''
                }
                
                // Run smoke tests on deployed environment
                sh '''
                    mvn test -B \
                        -Dtest.suite=smoke \
                        -Dbrowser=chrome \
                        -Denvironment=staging \
                        -Dheadless=true
                '''
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up...'
            
            // Stop and remove Docker containers
            sh 'docker-compose down --remove-orphans || true'
            sh 'docker system prune -f || true'
            
            // Archive test results
            publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo 'Pipeline executed successfully! 🎉'
            
            // Send success notifications
            script {
                sendNotifications('SUCCESS')
            }
        }
        
        failure {
            echo 'Pipeline failed! ❌'
            
            // Send failure notifications
            script {
                sendNotifications('FAILURE')
            }
        }
        
        unstable {
            echo 'Pipeline completed with test failures! ⚠️'
            
            script {
                sendNotifications('UNSTABLE')
            }
        }
    }
}

// Helper function to run tests in Docker
def runTestsInDocker(browser) {
    echo "Running ${env.TEST_SUITE} tests with ${browser} browser..."
    
    sh """
        docker-compose up -d selenium-hub ${browser}-node
        sleep 30
        
        docker run --rm \
            --network selenium-network \
            -v \$(pwd)/target:/app/test-results \
            -e SELENIUM_HUB_URL=http://selenium-hub:4444/wd/hub \
            -e BROWSER=${browser} \
            -e TEST_SUITE=${env.TEST_SUITE} \
            -e ENVIRONMENT=${env.ENVIRONMENT} \
            -e PARALLEL_TESTS=${env.PARALLEL_TESTS} \
            selenium-framework:${env.BUILD_NUMBER} \
            /app/run-tests.sh -Dsuite=${env.TEST_SUITE}
    """
}

// Helper function to send notifications
def sendNotifications(status) {
    def color = status == 'SUCCESS' ? 'good' : (status == 'FAILURE' ? 'danger' : 'warning')
    def emoji = status == 'SUCCESS' ? '✅' : (status == 'FAILURE' ? '❌' : '⚠️')
    
    // Slack notification
    if (env.SLACK_WEBHOOK_URL) {
        slackSend(
            channel: '#automation-tests',
            color: color,
            message: """
                ${emoji} *Selenium Framework Pipeline ${status}*
                
                *Repository:* ${env.JOB_NAME}
                *Branch:* ${env.BRANCH_NAME}
                *Build:* ${env.BUILD_NUMBER}
                *Commit:* ${env.GIT_COMMIT_SHORT}
                *Test Suite:* ${env.TEST_SUITE}
                *Browser:* ${env.BROWSER}
                *Environment:* ${env.ENVIRONMENT}
                
                <${env.BUILD_URL}|View Build Details>
                <${env.BUILD_URL}allure|View Test Report>
            """.stripIndent()
        )
    }
    
    // Email notification
    emailext(
        subject: "${emoji} Selenium Framework - ${status} - Build #${env.BUILD_NUMBER}",
        body: """
            <h2>Selenium Framework Test Results</h2>
            <p><strong>Status:</strong> ${status}</p>
            <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
            <p><strong>Test Suite:</strong> ${env.TEST_SUITE}</p>
            <p><strong>Browser:</strong> ${env.BROWSER}</p>
            <p><strong>Environment:</strong> ${env.ENVIRONMENT}</p>
            
            <p><a href="${env.BUILD_URL}">View Build Details</a></p>
            <p><a href="${env.BUILD_URL}allure">View Test Report</a></p>
        """,
        to: '${DEFAULT_RECIPIENTS}',
        mimeType: 'text/html'
    )
}