# Multi-stage Dockerfile for Selenium Framework
FROM maven:3.9.4-eclipse-temurin-11 AS builder

# Set working directory
WORKDIR /app

# Copy POM file first (for better caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:11-jre-jammy

# Install dependencies for Selenium
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    xvfb \
    x11vnc \
    fluxbox \
    && rm -rf /var/lib/apt/lists/*

# Install Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox \
    && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Copy test resources
COPY --from=builder /app/target/test-classes ./test-classes

# Copy Maven dependencies
COPY --from=builder /root/.m2/repository ./m2-repo

# Set environment variables
ENV DISPLAY=:99
ENV MAVEN_REPO=/app/m2-repo
ENV JAVA_OPTS="-Xmx1024m -Dfile.encoding=UTF-8"

# Create test results directory
RUN mkdir -p /app/test-results /app/screenshots /app/videos

# Create startup script
RUN echo '#!/bin/bash\n\
set -e\n\
\n\
# Start Xvfb\n\
Xvfb :99 -screen 0 1920x1080x24 &\n\
export DISPLAY=:99\n\
\n\
# Wait for Xvfb to start\n\
sleep 3\n\
\n\
# Run tests\n\
java $JAVA_OPTS -cp "app.jar:test-classes" \\\n\
  -Dmaven.repo.local=$MAVEN_REPO \\\n\
  org.testng.TestNG \\\n\
  test-classes/testng.xml \\\n\
  "$@"\n\
' > /app/run-tests.sh && chmod +x /app/run-tests.sh

# Expose port for VNC (optional - for debugging)
EXPOSE 5900

# Default command
CMD ["/app/run-tests.sh"]