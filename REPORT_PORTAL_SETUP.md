# Report Portal Docker Setup Guide

## Quick Setup with Docker
```bash
# Download Report Portal
curl -LO https://raw.githubusercontent.com/reportportal/reportportal/master/docker-compose.yml

# Start Report Portal
docker-compose -p reportportal up -d --force-recreate

# Access at: http://localhost:8080
# Default credentials: superadmin / erebus
```

## Configuration Steps:
1. Create a new project (e.g., "selenium-automation")
2. Get your API token from Profile Settings
3. Update reportportal.properties with:
   - rp.uuid = your-api-token
   - rp.project = your-project-name
   - rp.endpoint = http://localhost:8080

## Verify Integration:
After setup, your tests will automatically report to Report Portal with:
- Real-time execution status
- Step-by-step logs
- Screenshot attachments
- Execution analytics and trends