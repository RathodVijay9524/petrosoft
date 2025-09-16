# Petrosoft Technical API Reference

## Table of Contents
1. [Authentication & Authorization](#authentication--authorization)
2. [Request/Response Formats](#requestresponse-formats)
3. [Error Handling](#error-handling)
4. [Rate Limiting](#rate-limiting)
5. [Endpoint Details](#endpoint-details)
6. [Data Models](#data-models)
7. [Testing Guidelines](#testing-guidelines)

---

## Authentication & Authorization

### JWT Token Structure
```json
{
  "sub": "user@example.com",
  "iat": 1694902346,
  "exp": 1694905946,
  "roles": ["ADMIN", "MANAGER"],
  "pumpId": 1,
  "userId": 123
}
```

### Authentication Flow
1. **Login**: `POST /auth/login`
2. **Receive JWT**: Token expires in 1 hour
3. **Include in Requests**: `Authorization: Bearer <token>`
4. **Refresh**: `POST /auth/refresh` (before expiry)

### Role Permissions Matrix

| Role | Dashboard | Analytics | Export | Admin | CRUD Operations |
|------|-----------|-----------|--------|-------|-----------------|
| ADMIN | ✅ Full | ✅ Full | ✅ Full | ✅ Full | ✅ Full |
| MANAGER | ✅ Full | ✅ Full | ✅ Limited | ❌ None | ✅ Limited |
| OPERATOR | ✅ Basic | ✅ Basic | ❌ None | ❌ None | ✅ Own Data |
| CASHIER | ✅ Sales Only | ❌ None | ❌ None | ❌ None | ✅ Sales Only |

---

## Request/Response Formats

### Standard Request Headers
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
Accept: application/json
X-Request-ID: <unique_request_id>
```

### Standard Response Headers
```http
Content-Type: application/json
X-Response-Time: 25ms
X-Request-ID: <unique_request_id>
Cache-Control: no-cache
```

### Pagination Format
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

---

## Error Handling

### Error Response Structure
```json
{
  "timestamp": "2024-09-17T00:52:26.081Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'quantity'",
  "path": "/api/sales",
  "details": {
    "field": "quantity",
    "rejectedValue": -10,
    "message": "Quantity must be positive"
  },
  "traceId": "abc123def456"
}
```

### Common Error Codes

| Status | Error Type | Description |
|--------|------------|-------------|
| 400 | ValidationError | Request validation failed |
| 401 | Unauthorized | Invalid or missing token |
| 403 | Forbidden | Insufficient permissions |
| 404 | NotFound | Resource not found |
| 409 | Conflict | Resource already exists |
| 422 | BusinessRuleViolation | Business logic violation |
| 500 | InternalError | Server error |

---

## Rate Limiting

### Rate Limit Headers
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1694905946
X-RateLimit-Retry-After: 60
```

### Rate Limits by Endpoint Category

| Category | Limit | Window |
|----------|-------|--------|
| Authentication | 10 req/min | Per IP |
| Dashboard | 100 req/min | Per User |
| Analytics | 50 req/min | Per User |
| Export | 20 req/min | Per User |
| CRUD Operations | 200 req/min | Per User |
| Admin Operations | 500 req/min | Per User |

---

## Endpoint Details

### Sales Management

#### Create Sales Transaction
```http
POST /api/sales
Content-Type: application/json

{
  "customerId": 1,
  "pumpId": 1,
  "operatorId": 2,
  "fuelType": "PETROL",
  "quantity": 50.0,
  "pricePerLiter": 95.50,
  "totalAmount": 4775.00,
  "paymentMethod": "CASH",
  "vehicleNumber": "KA01AB1234",
  "notes": "Regular customer"
}
```

**Response (201 Created):**
```json
{
  "id": 123,
  "transactionNumber": "TXN-20240917-001",
  "customerId": 1,
  "pumpId": 1,
  "operatorId": 2,
  "fuelType": "PETROL",
  "quantity": 50.0,
  "pricePerLiter": 95.50,
  "totalAmount": 4775.00,
  "paymentMethod": "CASH",
  "vehicleNumber": "KA01AB1234",
  "notes": "Regular customer",
  "transactedAt": "2024-09-17T10:30:00Z",
  "status": "COMPLETED",
  "createdAt": "2024-09-17T10:30:00Z",
  "updatedAt": "2024-09-17T10:30:00Z"
}
```

#### Get Sales with Filters
```http
GET /api/sales?startDate=2024-09-01&endDate=2024-09-17&pumpId=1&page=0&size=20
```

### Analytics Endpoints

#### Sales Revenue Trends
```http
GET /api/analytics/sales/revenue-trends?startDate=2024-09-01&endDate=2024-09-17
```

**Response:**
```json
{
  "totalRevenue": 125000.00,
  "averageDailyRevenue": 8500.00,
  "monthlyGrowthRate": "5.25%",
  "dailySales": [
    {
      "date": "2024-09-17",
      "revenue": 8500.00,
      "transactions": 45,
      "averageTransactionValue": 188.89
    },
    {
      "date": "2024-09-16",
      "revenue": 8200.00,
      "transactions": 42,
      "averageTransactionValue": 195.24
    }
  ],
  "fuelTypeBreakdown": {
    "PETROL": 75000.00,
    "DIESEL": 40000.00,
    "CNG": 10000.00
  },
  "paymentMethodBreakdown": {
    "CASH": 80000.00,
    "CARD": 35000.00,
    "UPI": 10000.00
  }
}
```

### Dashboard Endpoints

#### Dashboard Overview
```http
GET /api/dashboard/overview
```

**Response:**
```json
{
  "totalPumps": 5,
  "activePumps": 4,
  "totalCustomers": 150,
  "activeCustomers": 145,
  "totalSales": 1250,
  "todaySales": 45,
  "totalRevenue": 125000.00,
  "todayRevenue": 8500.00,
  "activeSubscriptions": 3,
  "totalPayments": 850,
  "pendingPayments": 5,
  "lastUpdated": "2024-09-17T00:52:26.081Z",
  "systemStatus": "HEALTHY"
}
```

### Export Endpoints

#### Export Sales Data
```http
GET /api/export/sales?startDate=2024-09-01&endDate=2024-09-17&format=json
```

**Response:**
```json
{
  "dataType": "Sales",
  "startDate": "2024-09-01",
  "endDate": "2024-09-17",
  "totalSalesRecords": 1250,
  "totalSalesAmount": 125000.00,
  "exportDate": "2024-09-17T10:30:00Z",
  "status": "success",
  "data": [
    {
      "id": 1,
      "transactionNumber": "TXN-20240917-001",
      "customerName": "John Doe",
      "pumpName": "Pump 1",
      "fuelType": "PETROL",
      "quantity": 50.0,
      "totalAmount": 4775.00,
      "transactedAt": "2024-09-17T10:30:00Z"
    }
  ]
}
```

---

## Data Models

### Sales Transaction
```typescript
interface SalesTransaction {
  id: number;
  transactionNumber: string;
  customerId: number;
  pumpId: number;
  operatorId: number;
  fuelType: 'PETROL' | 'DIESEL' | 'CNG';
  quantity: number;
  pricePerLiter: number;
  totalAmount: number;
  paymentMethod: 'CASH' | 'CARD' | 'UPI';
  vehicleNumber?: string;
  notes?: string;
  transactedAt: string;
  status: 'PENDING' | 'COMPLETED' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}
```

### Customer
```typescript
interface Customer {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  vehicleNumber?: string;
  customerType: 'REGULAR' | 'PREMIUM' | 'CORPORATE';
  isActive: boolean;
  totalPurchases: number;
  lastPurchaseDate?: string;
  createdAt: string;
  updatedAt: string;
}
```

### Dashboard Metrics
```typescript
interface DashboardMetrics {
  totalPumps: number;
  activePumps: number;
  totalCustomers: number;
  activeCustomers: number;
  totalSales: number;
  todaySales: number;
  totalRevenue: number;
  todayRevenue: number;
  activeSubscriptions: number;
  totalPayments: number;
  pendingPayments: number;
  lastUpdated: string;
  systemStatus: 'HEALTHY' | 'WARNING' | 'ERROR';
}
```

---

## Testing Guidelines

### Unit Testing
```java
@Test
public void testCreateSalesTransaction() {
    // Given
    SalesTransactionDTO request = SalesTransactionDTO.builder()
        .customerId(1L)
        .pumpId(1L)
        .fuelType("PETROL")
        .quantity(50.0)
        .pricePerLiter(95.50)
        .totalAmount(4775.00)
        .paymentMethod("CASH")
        .build();
    
    // When
    ResponseEntity<SalesTransactionDTO> response = 
        salesController.createSalesTransaction(request);
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().getTransactionNumber()).isNotNull();
}
```

### Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.profiles.active=test")
class SalesControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testSalesTransactionFlow() {
        // Test complete sales transaction flow
    }
}
```

### API Testing with Postman

#### Environment Variables
```
base_url: http://localhost:8081/api
auth_token: {{jwt_token}}
```

#### Test Collection Structure
```
Petrosoft API Tests/
├── Authentication/
│   ├── Login
│   ├── Register
│   └── Refresh Token
├── Sales/
│   ├── Create Sales Transaction
│   ├── Get Sales by Date Range
│   └── Update Sales Transaction
├── Dashboard/
│   ├── Get Dashboard Overview
│   ├── Get Dashboard Metrics
│   └── Get Dashboard KPIs
├── Analytics/
│   ├── Sales Revenue Trends
│   ├── Customer Analytics
│   └── Financial Analytics
└── Export/
    ├── Export Sales Data
    ├── Export Customer Data
    └── Export Financial Data
```

### Performance Testing

#### Load Testing with JMeter
```xml
<!-- JMeter Test Plan -->
<TestPlan>
    <ThreadGroup>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController">
            <intProp name="LoopController.loops">100</intProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">10</stringProp>
        <stringProp name="ThreadGroup.ramp_time">60</stringProp>
    </ThreadGroup>
</TestPlan>
```

#### Stress Testing Scenarios
1. **High Volume Sales**: 1000 concurrent sales transactions
2. **Dashboard Load**: 500 concurrent dashboard requests
3. **Analytics Queries**: 100 concurrent analytics requests
4. **Export Operations**: 50 concurrent export requests

### Security Testing

#### OWASP Top 10 Testing
1. **SQL Injection**: Test all input parameters
2. **Cross-Site Scripting (XSS)**: Test text inputs
3. **Authentication Bypass**: Test JWT token validation
4. **Authorization Issues**: Test role-based access
5. **Data Exposure**: Test sensitive data in responses

#### Security Test Cases
```java
@Test
public void testSQLInjectionPrevention() {
    String maliciousInput = "'; DROP TABLE users; --";
    ResponseEntity<String> response = 
        restTemplate.postForEntity("/api/sales", maliciousInput, String.class);
    
    assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
}
```

---

## Monitoring & Observability

### Application Metrics
- **Response Time**: Track endpoint performance
- **Error Rate**: Monitor 4xx/5xx responses
- **Throughput**: Requests per second
- **Active Users**: Concurrent user sessions

### Business Metrics
- **Sales Volume**: Daily/monthly sales
- **Revenue**: Total and per-pump revenue
- **Customer Activity**: New vs returning customers
- **System Utilization**: Pump and system usage

### Logging Standards
```java
@Slf4j
@RestController
public class SalesController {
    
    @PostMapping("/sales")
    public ResponseEntity<SalesTransactionDTO> createSales(
            @RequestBody SalesTransactionDTO request) {
        
        log.info("Creating sales transaction for customer: {}, pump: {}", 
                request.getCustomerId(), request.getPumpId());
        
        try {
            SalesTransactionDTO result = salesService.createSales(request);
            log.info("Sales transaction created successfully: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to create sales transaction", e);
            throw e;
        }
    }
}
```

---

## Deployment & Configuration

### Environment Configuration
```yaml
# application-prod.yml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://prod-db:3306/petrosoft
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
server:
  port: 8080
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}

logging:
  level:
    com.vijay.petrosoft: INFO
    org.springframework.security: WARN
  file:
    name: /var/log/petrosoft/application.log
```

### Health Checks
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "SELECT 1"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760
      }
    }
  }
}
```

---

## Troubleshooting Guide

### Common Issues

#### 1. Authentication Errors
**Problem**: 401 Unauthorized
**Solution**: 
- Check JWT token validity
- Verify token format
- Check token expiration

#### 2. Performance Issues
**Problem**: Slow response times
**Solution**:
- Check database connection pool
- Monitor memory usage
- Review query performance

#### 3. Data Consistency Issues
**Problem**: Inconsistent data across endpoints
**Solution**:
- Check transaction boundaries
- Verify database constraints
- Review caching strategy

### Debug Endpoints
```http
GET /actuator/env          # Environment variables
GET /actuator/configprops  # Configuration properties
GET /actuator/beans        # Spring beans
GET /actuator/mappings     # Request mappings
```

---

*This technical reference is maintained by the development team and updated with each release.*
