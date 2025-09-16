# Test script for Performance Testing and Security Audit endpoints

Write-Host "=== Petrosoft Performance Testing & Security Audit Endpoint Test ===" -ForegroundColor Green

# Test if application is running
Write-Host "`n1. Testing application health..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET
    Write-Host "✅ Application is running!" -ForegroundColor Green
    Write-Host "Health Status: $($health.status)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Application is not running on port 8081" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Performance Testing endpoints
Write-Host "`n2. Testing Performance Testing endpoints..." -ForegroundColor Yellow

# Test Performance Health (should work without authentication)
try {
    $perfHealth = Invoke-RestMethod -Uri "http://localhost:8081/api/testing/performance/health" -Method GET
    Write-Host "✅ Performance Health endpoint working!" -ForegroundColor Green
    Write-Host "Performance Health: $($perfHealth.status)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Performance Health endpoint failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Performance Summary (requires authentication)
try {
    $perfSummary = Invoke-RestMethod -Uri "http://localhost:8081/api/testing/performance/summary" -Method GET
    Write-Host "✅ Performance Summary endpoint working!" -ForegroundColor Green
    Write-Host "Performance Summary: Active Connections: $($perfSummary.activeConnections)" -ForegroundColor Cyan
} catch {
    Write-Host "⚠️ Performance Summary endpoint requires authentication (expected)" -ForegroundColor Yellow
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Test Security Audit endpoints
Write-Host "`n3. Testing Security Audit endpoints..." -ForegroundColor Yellow

# Test Security Health (should work without authentication)
try {
    $secHealth = Invoke-RestMethod -Uri "http://localhost:8081/api/security/audit/health" -Method GET
    Write-Host "✅ Security Health endpoint working!" -ForegroundColor Green
    Write-Host "Security Health: $($secHealth.status)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Security Health endpoint failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Security Recommendations (requires authentication)
try {
    $secRecs = Invoke-RestMethod -Uri "http://localhost:8081/api/security/audit/recommendations" -Method GET
    Write-Host "✅ Security Recommendations endpoint working!" -ForegroundColor Green
    Write-Host "Security Recommendations: $($secRecs.Count) recommendations found" -ForegroundColor Cyan
} catch {
    Write-Host "⚠️ Security Recommendations endpoint requires authentication (expected)" -ForegroundColor Yellow
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Test Swagger UI
Write-Host "`n4. Testing Swagger UI..." -ForegroundColor Yellow
try {
    $swagger = Invoke-RestMethod -Uri "http://localhost:8081/swagger-ui.html" -Method GET
    Write-Host "✅ Swagger UI is accessible!" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Swagger UI might require authentication or not be accessible" -ForegroundColor Yellow
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green
