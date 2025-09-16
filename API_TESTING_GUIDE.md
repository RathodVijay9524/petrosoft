# Petrosoft API Testing Guide

## Overview
This guide provides comprehensive testing strategies and procedures for the Petrosoft API endpoints. It covers unit testing, integration testing, performance testing, and security testing.

---

## 🧪 Testing Strategy

### Testing Pyramid
```
        /\
       /  \
      / E2E \     <- End-to-End Tests (10%)
     /______\
    /        \
   /Integration\  <- Integration Tests (20%)
  /____________\
 /              \
/   Unit Tests   \  <- Unit Tests (70%)
/________________\
```

### Test Categories
1. **Unit Tests** - Individual components and methods
2. **Integration Tests** - API endpoint testing
3. **Performance Tests** - Load and stress testing
4. **Security Tests** - Authentication and authorization
5. **End-to-End Tests** - Complete user workflows

---

## 🔧 Unit Testing

### Sales Service Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class SalesServiceTest {
    
    @Mock
    private SaleTransactionRepository saleRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private SalesServiceImpl salesService;
    
    @Test
    void testCreateSalesTransaction_Success() {
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
            
        Customer customer = Customer.builder()
            .id(1L)
            .name("Test Customer")
            .isActive(true)
            .build();
            
        SaleTransaction savedTransaction = SaleTransaction.builder()
            .id(1L)
            .transactionNumber("TXN-001")
            .customerId(1L)
            .pumpId(1L)
            .fuelType("PETROL")
            .quantity(50.0)
            .pricePerLiter(95.50)
            .totalAmount(4775.00)
            .paymentMethod("CASH")
            .status(SaleTransaction.Status.COMPLETED)
            .build();
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(saleRepository.save(any(SaleTransaction.class))).thenReturn(savedTransaction);
        
        // When
        SalesTransactionDTO result = salesService.createSalesTransaction(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTransactionNumber()).isEqualTo("TXN-001");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        
        verify(customerRepository).findById(1L);
        verify(saleRepository).save(any(SaleTransaction.class));
    }
    
    @Test
    void testCreateSalesTransaction_CustomerNotFound() {
        // Given
        SalesTransactionDTO request = SalesTransactionDTO.builder()
            .customerId(999L)
            .pumpId(1L)
            .fuelType("PETROL")
            .quantity(50.0)
            .pricePerLiter(95.50)
            .totalAmount(4775.00)
            .paymentMethod("CASH")
            .build();
            
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> salesService.createSalesTransaction(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Customer not found with id: 999");
            
        verify(customerRepository).findById(999L);
        verify(saleRepository, never()).save(any());
    }
    
    @Test
    void testCreateSalesTransaction_InvalidQuantity() {
        // Given
        SalesTransactionDTO request = SalesTransactionDTO.builder()
            .customerId(1L)
            .pumpId(1L)
            .fuelType("PETROL")
            .quantity(-10.0) // Invalid quantity
            .pricePerLiter(95.50)
            .totalAmount(-955.00)
            .paymentMethod("CASH")
            .build();
        
        // When & Then
        assertThatThrownBy(() -> salesService.createSalesTransaction(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Quantity must be positive");
    }
}
```

### Dashboard Service Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    
    @Mock
    private PumpRepository pumpRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private SaleTransactionRepository saleRepository;
    
    @InjectMocks
    private DashboardServiceImpl dashboardService;
    
    @Test
    void testGetDashboardOverview() {
        // Given
        when(pumpRepository.count()).thenReturn(5L);
        when(customerRepository.count()).thenReturn(150L);
        when(saleRepository.count()).thenReturn(1250L);
        when(subscriptionRepository.countActiveSubscriptions()).thenReturn(3L);
        when(paymentRepository.count()).thenReturn(850L);
        
        // When
        Map<String, Object> overview = dashboardService.getOverview();
        
        // Then
        assertThat(overview).isNotNull();
        assertThat(overview.get("totalPumps")).isEqualTo(5L);
        assertThat(overview.get("totalCustomers")).isEqualTo(150L);
        assertThat(overview.get("totalSales")).isEqualTo(1250L);
        assertThat(overview.get("activeSubscriptions")).isEqualTo(3L);
        assertThat(overview.get("totalPayments")).isEqualTo(850L);
        assertThat(overview.get("lastUpdated")).isNotNull();
    }
}
```

---

## 🔗 Integration Testing

### Sales Controller Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.profiles.active=test")
class SalesControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private SaleTransactionRepository saleRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @LocalServerPort
    private int port;
    
    private String baseUrl;
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        saleRepository.deleteAll();
        customerRepository.deleteAll();
    }
    
    @Test
    void testCreateSalesTransaction_Integration() {
        // Given
        Customer customer = Customer.builder()
            .name("Test Customer")
            .email("test@example.com")
            .phone("1234567890")
            .isActive(true)
            .build();
        customerRepository.save(customer);
        
        SalesTransactionDTO request = SalesTransactionDTO.builder()
            .customerId(customer.getId())
            .pumpId(1L)
            .fuelType("PETROL")
            .quantity(50.0)
            .pricePerLiter(95.50)
            .totalAmount(4775.00)
            .paymentMethod("CASH")
            .build();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAuthToken());
        
        HttpEntity<SalesTransactionDTO> entity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<SalesTransactionDTO> response = restTemplate.postForEntity(
            baseUrl + "/sales", entity, SalesTransactionDTO.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTransactionNumber()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("COMPLETED");
        
        // Verify database
        List<SaleTransaction> transactions = saleRepository.findAll();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getCustomerId()).isEqualTo(customer.getId());
    }
    
    @Test
    void testGetSalesByDateRange_Integration() {
        // Given
        createTestSalesTransactions();
        
        String url = baseUrl + "/sales?startDate=2024-09-01&endDate=2024-09-17";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAuthToken());
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
    
    private String getAuthToken() {
        // Implementation to get JWT token
        return "test-jwt-token";
    }
    
    private void createTestSalesTransactions() {
        // Create test data
    }
}
```

### Analytics Controller Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnalyticsControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private SaleTransactionRepository saleRepository;
    
    @Test
    void testSalesRevenueTrends() {
        // Given
        createTestSalesData();
        
        String url = baseUrl + "/analytics/sales/revenue-trends?startDate=2024-09-01&endDate=2024-09-17";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAuthToken());
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.has("totalRevenue")).isTrue();
        assertThat(jsonResponse.has("averageDailyRevenue")).isTrue();
        assertThat(jsonResponse.has("dailySales")).isTrue();
    }
}
```

---

## ⚡ Performance Testing

### JMeter Test Plan
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan testname="Petrosoft Performance Tests">
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.tearDown_on_shutdown">true</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup testname="Sales Transaction Load Test">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">100</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">50</stringProp>
        <stringProp name="ThreadGroup.ramp_time">60</stringProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
        <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy testname="Create Sales Transaction">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments"/>
          </elementProp>
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8081</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.contentEncoding"></stringProp>
          <stringProp name="HTTPSampler.path">/api/sales</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
          <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
          <stringProp name="HTTPSampler.connect_timeout"></stringProp>
          <stringProp name="HTTPSampler.response_timeout"></stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <HeaderManager testname="HTTP Header Manager">
            <collectionProp name="HeaderManager.headers">
              <elementProp name="" elementType="Header">
                <stringProp name="Header.name">Content-Type</stringProp>
                <stringProp name="Header.value">application/json</stringProp>
              </elementProp>
              <elementProp name="" elementType="Header">
                <stringProp name="Header.name">Authorization</stringProp>
                <stringProp name="Header.value">Bearer ${__P(auth_token)}</stringProp>
              </elementProp>
            </collectionProp>
          </HeaderManager>
          <hashTree/>
          <HTTPSamplerArguments testname="HTTP Request Arguments">
            <collectionProp name="Arguments.arguments">
              <elementProp name="" elementType="HTTPArgument">
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
                <stringProp name="Argument.value">{
  "customerId": 1,
  "pumpId": 1,
  "fuelType": "PETROL",
  "quantity": 50.0,
  "pricePerLiter": 95.50,
  "totalAmount": 4775.00,
  "paymentMethod": "CASH"
}</stringProp>
                <stringProp name="Argument.metadata">=</stringProp>
              </elementProp>
            </collectionProp>
          </HTTPSamplerArguments>
          <hashTree/>
        </hashTree>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

### Performance Test Scenarios

#### 1. Load Testing - Sales Transactions
```bash
# Run JMeter test
jmeter -n -t Petrosoft_Performance_Tests.jmx -l results.jtl -e -o report/

# Expected Results:
# - Response time: < 200ms (95th percentile)
# - Throughput: > 100 requests/second
# - Error rate: < 1%
```

#### 2. Stress Testing - Dashboard
```bash
# Stress test dashboard endpoints
jmeter -n -t Dashboard_Stress_Test.jmx -l dashboard_results.jtl

# Expected Results:
# - Response time: < 500ms under load
# - No errors under normal load
# - Graceful degradation under stress
```

#### 3. Endurance Testing
```bash
# Run for 1 hour
jmeter -n -t Endurance_Test.jmx -l endurance_results.jtl -Jduration=3600

# Expected Results:
# - No memory leaks
# - Consistent performance
# - No errors over time
```

---

## 🔒 Security Testing

### Authentication Testing
```java
@Test
void testUnauthenticatedAccess() {
    // Given
    String url = baseUrl + "/api/sales";
    SalesTransactionDTO request = createValidSalesRequest();
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // No Authorization header
    
    HttpEntity<SalesTransactionDTO> entity = new HttpEntity<>(request, headers);
    
    // When
    ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
}
```

### Authorization Testing
```java
@Test
void testOperatorCannotAccessAdminEndpoints() {
    // Given
    String url = baseUrl + "/api/users"; // Admin only endpoint
    String operatorToken = getOperatorToken();
    
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(operatorToken);
    
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    // When
    ResponseEntity<String> response = restTemplate.exchange(
        url, HttpMethod.GET, entity, String.class);
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
}
```

### SQL Injection Testing
```java
@Test
void testSQLInjectionPrevention() {
    // Given
    String maliciousInput = "'; DROP TABLE sales; --";
    String url = baseUrl + "/api/sales?customerName=" + maliciousInput;
    
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getAuthToken());
    
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    // When
    ResponseEntity<String> response = restTemplate.exchange(
        url, HttpMethod.GET, entity, String.class);
    
    // Then
    assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    // Verify database is still intact
    assertThat(saleRepository.count()).isGreaterThan(0);
}
```

### XSS Testing
```java
@Test
void testXSSPrevention() {
    // Given
    SalesTransactionDTO request = SalesTransactionDTO.builder()
        .customerId(1L)
        .pumpId(1L)
        .fuelType("PETROL")
        .quantity(50.0)
        .pricePerLiter(95.50)
        .totalAmount(4775.00)
        .paymentMethod("CASH")
        .notes("<script>alert('XSS')</script>") // XSS payload
        .build();
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(getAuthToken());
    
    HttpEntity<SalesTransactionDTO> entity = new HttpEntity<>(request, headers);
    
    // When
    ResponseEntity<SalesTransactionDTO> response = restTemplate.postForEntity(
        baseUrl + "/api/sales", entity, SalesTransactionDTO.class);
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().getNotes()).doesNotContain("<script>");
}
```

---

## 📊 Test Data Management

### Test Data Factory
```java
@Component
public class TestDataFactory {
    
    public Customer createTestCustomer() {
        return Customer.builder()
            .name("Test Customer " + System.currentTimeMillis())
            .email("test" + System.currentTimeMillis() + "@example.com")
            .phone("1234567890")
            .address("Test Address")
            .isActive(true)
            .build();
    }
    
    public SalesTransactionDTO createValidSalesRequest() {
        return SalesTransactionDTO.builder()
            .customerId(1L)
            .pumpId(1L)
            .fuelType("PETROL")
            .quantity(50.0)
            .pricePerLiter(95.50)
            .totalAmount(4775.00)
            .paymentMethod("CASH")
            .build();
    }
    
    public SalesTransaction createTestSalesTransaction() {
        return SaleTransaction.builder()
            .customerId(1L)
            .pumpId(1L)
            .fuelType("PETROL")
            .quantity(50.0)
            .pricePerLiter(95.50)
            .totalAmount(4775.00)
            .paymentMethod("CASH")
            .status(SaleTransaction.Status.COMPLETED)
            .build();
    }
}
```

### Database Setup for Tests
```java
@TestConfiguration
public class TestDatabaseConfig {
    
    @Bean
    @Primary
    public DataSource testDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb");
        config.setUsername("sa");
        config.setPassword("");
        return new HikariDataSource(config);
    }
    
    @Bean
    @Primary
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.H2);
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);
        return adapter;
    }
}
```

---

## 🎯 Test Automation

### CI/CD Pipeline Tests
```yaml
# .github/workflows/test.yml
name: API Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run unit tests
      run: ./mvnw test
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Unit Test Results
        path: target/surefire-reports/*.xml
        reporter: java-junit

  integration-tests:
    runs-on: ubuntu-latest
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: petrosoft_test
        options: >-
          --health-cmd="mysqladmin ping"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=3
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run integration tests
      run: ./mvnw verify -P integration-test
      env:
        SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/petrosoft_test
        SPRING_DATASOURCE_USERNAME: root
        SPRING_DATASOURCE_PASSWORD: root

  performance-tests:
    runs-on: ubuntu-latest
    needs: [unit-tests, integration-tests]
    steps:
    - uses: actions/checkout@v2
    - name: Start application
      run: |
        ./mvnw spring-boot:run &
        sleep 60
    - name: Run performance tests
      run: |
        jmeter -n -t src/test/jmeter/Petrosoft_Performance_Tests.jmx -l performance_results.jtl
    - name: Upload performance results
      uses: actions/upload-artifact@v2
      with:
        name: performance-results
        path: performance_results.jtl
```

### Test Coverage
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

---

## 📈 Test Reporting

### Test Results Dashboard
```java
@RestController
@RequestMapping("/api/test-results")
public class TestResultsController {
    
    @GetMapping("/coverage")
    public ResponseEntity<TestCoverageDTO> getTestCoverage() {
        TestCoverageDTO coverage = TestCoverageDTO.builder()
            .lineCoverage(85.5)
            .branchCoverage(78.2)
            .methodCoverage(92.1)
            .classCoverage(95.8)
            .build();
        
        return ResponseEntity.ok(coverage);
    }
    
    @GetMapping("/performance")
    public ResponseEntity<PerformanceMetricsDTO> getPerformanceMetrics() {
        PerformanceMetricsDTO metrics = PerformanceMetricsDTO.builder()
            .averageResponseTime(125.5)
            .p95ResponseTime(250.0)
            .throughput(150.0)
            .errorRate(0.5)
            .build();
        
        return ResponseEntity.ok(metrics);
    }
}
```

---

## 🔍 Debugging Tests

### Common Test Issues

#### 1. Database State Issues
```java
@Transactional
@Rollback
@Test
void testWithCleanDatabase() {
    // Test implementation
    // Database will be rolled back after test
}
```

#### 2. Async Operations
```java
@Test
void testAsyncOperation() {
    // Given
    CompletableFuture<String> future = asyncService.processAsync();
    
    // When & Then
    assertThat(future).succeedsWithin(Duration.ofSeconds(5))
        .isEqualTo("expected result");
}
```

#### 3. Time-dependent Tests
```java
@Test
void testTimeDependentLogic() {
    // Given
    LocalDateTime fixedTime = LocalDateTime.of(2024, 9, 17, 10, 0);
    try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
        mockedTime.when(LocalDateTime::now).thenReturn(fixedTime);
        
        // Test implementation
    }
}
```

---

## 📚 Best Practices

### 1. Test Naming Convention
```java
// Format: test[MethodName]_[Scenario]_[ExpectedResult]
@Test
void testCreateSalesTransaction_WithValidData_ShouldReturnCreatedTransaction() {
    // Test implementation
}

@Test
void testCreateSalesTransaction_WithInvalidQuantity_ShouldThrowException() {
    // Test implementation
}
```

### 2. Test Data Management
```java
// Use builders for test data
@Test
void testSalesTransaction() {
    SalesTransactionDTO request = SalesTransactionDTO.builder()
        .customerId(1L)
        .pumpId(1L)
        .fuelType("PETROL")
        .quantity(50.0)
        .pricePerLiter(95.50)
        .totalAmount(4775.00)
        .paymentMethod("CASH")
        .build();
    
    // Test implementation
}
```

### 3. Assertion Best Practices
```java
@Test
void testAssertions() {
    // Use descriptive assertions
    assertThat(result.getStatus()).isEqualTo("COMPLETED");
    assertThat(result.getTotalAmount()).isGreaterThan(BigDecimal.ZERO);
    assertThat(result.getTransactionNumber()).isNotNull();
    
    // Use soft assertions for multiple checks
    SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(result.getId()).isNotNull();
        softly.assertThat(result.getCustomerId()).isEqualTo(1L);
        softly.assertThat(result.getPumpId()).isEqualTo(1L);
    });
}
```

### 4. Mock Usage
```java
@Test
void testWithMocks() {
    // Given
    when(repository.findById(1L)).thenReturn(Optional.of(entity));
    when(service.process(any())).thenReturn(result);
    
    // When
    SomeResult result = serviceUnderTest.process(1L);
    
    // Then
    verify(repository).findById(1L);
    verify(service).process(any());
    verifyNoMoreInteractions(repository);
}
```

---

*This testing guide is maintained by the development team and updated with each release.*
