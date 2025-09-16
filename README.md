# Petrosoft - Petrol Pump Management System

A comprehensive Spring Boot application for managing petrol pump operations, built with modern Java technologies and designed to match the functionality of the reference Petrosoft India application.

## 🚀 Features Overview

### ✅ **Authentication & Security**
- **JWT-based Authentication**: Secure token-based authentication system
- **OTP Integration**: SMS OTP functionality via Twilio for two-factor authentication
- **Password Management**: Forgot password, change password, and secure password reset
- **User Management**: Complete user registration, login, and role management
- **Spring Security**: Comprehensive security configuration with endpoint protection

### ✅ **Core Business Modules**

#### **1. Master Setup System**
- **Company Configuration**: Company information, address, contact details
- **Financial Year Management**: Multiple financial year support with date ranges
- **Business Settings**: Currency, timezone, business hours configuration
- **Notification Configuration**: Email and SMS notification settings
- **System Settings**: General application configuration
- **Subscription Management**: Plan management and billing configuration

#### **2. Accounts Module**
- **Chart of Accounts**: Complete accounting structure with account groups
- **Voucher Management**: Multiple voucher types (Customer Receipt, Payment, Journal, etc.)
- **Ledger Entries**: Double-entry bookkeeping system
- **Account Groups**: Assets, Liabilities, Equity, Income, Expenses categorization
- **Transaction Tracking**: Complete audit trail for all financial transactions

#### **3. Petrol Pump Operations**
- **Sales Entry System**: Comprehensive fuel sales transaction management
- **Cash Management**: Cash flow tracking, denomination management, collection tracking
- **Shift Management**: Multi-shift operations with opening/closing cash tracking
- **Purchase Bill Entry**: Supplier management and purchase transaction handling
- **Inventory Management**: Fuel stock tracking and management

#### **4. Financial Reports**
- **Trial Balance**: Complete trial balance with debit/credit summaries
- **Profit & Loss Statement**: Comprehensive P&L with income and expense categorization
- **Balance Sheet**: Assets, liabilities, and equity reporting
- **Cash Book**: Detailed cash transaction reporting
- **Day Book**: Daily transaction summaries

#### **5. Dashboard & Analytics**
- **Real-time Metrics**: Live dashboard with key performance indicators
- **Sales Analytics**: Sales trends, payment method analysis, operator performance
- **Cash Flow Analysis**: Cash movement tracking and reconciliation
- **Inventory Reports**: Stock levels, consumption patterns, reorder alerts
- **Performance Charts**: Visual analytics with charts and graphs

### ✅ **Payment & Notification Systems**
- **Payment Gateway Integration**: Razorpay integration (conditionally enabled)
- **Email Notifications**: Spring Mail integration for automated notifications
- **SMS Notifications**: Twilio SMS service for real-time alerts
- **Notification Scheduling**: Automated notification processing with retry logic

## 🏗️ Technical Architecture

### **Backend Stack**
- **Framework**: Spring Boot 3.5.5
- **Database**: MySQL 8.0 with Hibernate ORM
- **Security**: Spring Security with JWT tokens
- **Build Tool**: Maven
- **Java Version**: Java 24
- **Lombok**: Code generation and boilerplate reduction

### **Database Design**
- **Entity Relationships**: Well-defined JPA entities with proper relationships
- **Audit Trail**: Automatic creation/update tracking on all entities
- **Data Integrity**: Foreign key constraints and validation
- **Performance**: Optimized queries with proper indexing

### **API Design**
- **RESTful APIs**: Standard REST endpoints following best practices
- **DTO Pattern**: Data Transfer Objects for clean API contracts
- **Validation**: Input validation using Jakarta Validation
- **Error Handling**: Global exception handling with proper HTTP status codes

## 📁 Project Structure

```
src/main/java/com/vijay/petrosoft/
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   └── PaymentGatewayConfig.java
├── controller/             # REST Controllers
│   ├── AuthController.java
│   ├── AccountController.java
│   ├── VoucherController.java
│   ├── LedgerController.java
│   ├── SalesController.java
│   ├── CashManagementController.java
│   ├── FinancialReportsController.java
│   └── DashboardController.java
├── domain/                 # JPA Entities
│   ├── Auditable.java      # Base entity with audit fields
│   ├── User.java
│   ├── Account.java
│   ├── Voucher.java
│   ├── VoucherEntry.java
│   ├── LedgerEntry.java
│   ├── SaleTransaction.java
│   ├── Shift.java
│   ├── CashMovement.java
│   └── [Other entities]
├── dto/                    # Data Transfer Objects
│   ├── AccountDTO.java
│   ├── VoucherDTO.java
│   ├── SaleTransactionDTO.java
│   └── [Other DTOs]
├── repository/             # JPA Repositories
│   ├── AccountRepository.java
│   ├── VoucherRepository.java
│   ├── SaleRepository.java
│   └── [Other repositories]
├── service/                # Service Interfaces
│   ├── AccountService.java
│   ├── VoucherService.java
│   ├── SalesService.java
│   └── [Other services]
├── service/impl/           # Service Implementations
│   ├── AccountServiceImpl.java
│   ├── VoucherServiceImpl.java
│   ├── SalesServiceImpl.java
│   └── [Other implementations]
├── security/               # Security components
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── exception/              # Custom exceptions
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BusinessLogicException.java
└── PetrosoftApplication.java
```

## 🚀 Getting Started

### Prerequisites
- Java 24 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd petrosoft
   ```

2. **Database Setup**
   - Create MySQL database named `petrosoftdb`
   - Update database credentials in `application.properties`

3. **Configure Application Properties**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/petrosoftdb
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JWT Configuration
   jwt.secret=your_jwt_secret_key
   jwt.expiration=86400000
   
   # Twilio SMS Configuration (Optional)
   twilio.account.sid=your_twilio_sid
   twilio.auth.token=your_twilio_token
   twilio.phone.number=your_twilio_phone
   
   # Email Configuration (Optional)
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your_email
   spring.mail.password=your_app_password
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the Application**
   - Application runs on: `http://localhost:8081`
   - API Documentation: Available through REST endpoints

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/send-otp` - Send OTP for verification
- `POST /api/auth/verify-otp` - Verify OTP
- `POST /api/auth/forgot-password` - Password reset request

### Account Management
- `GET /api/accounts` - Get all accounts
- `POST /api/accounts` - Create new account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Voucher Management
- `GET /api/vouchers` - Get all vouchers
- `POST /api/vouchers` - Create new voucher
- `PUT /api/vouchers/{id}` - Update voucher
- `POST /api/vouchers/{id}/post` - Post voucher

### Sales Management
- `GET /api/sales` - Get all sales
- `POST /api/sales` - Create new sale
- `GET /api/sales/pump/{pumpId}` - Get sales by pump
- `GET /api/sales/date-range` - Get sales by date range

### Financial Reports
- `GET /api/reports/trial-balance` - Trial Balance report
- `GET /api/reports/profit-loss` - Profit & Loss report
- `GET /api/reports/balance-sheet` - Balance Sheet report
- `GET /api/reports/cash-book` - Cash Book report

### Dashboard
- `GET /api/dashboard/metrics` - Get dashboard metrics
- `GET /api/dashboard/sales-summary` - Sales summary
- `GET /api/dashboard/cash-summary` - Cash flow summary

## 🔧 Configuration

### Environment Profiles
- **Development**: `application-dev.properties`
- **Production**: `application-prod.properties`
- **Default**: `application.properties`

### Key Configuration Options
- **Database**: MySQL connection settings
- **JWT**: Token expiration and secret configuration
- **SMS**: Twilio integration settings
- **Email**: SMTP configuration for notifications
- **Payment Gateway**: Razorpay settings (conditionally enabled)

## 🛡️ Security Features

### Authentication
- JWT token-based authentication
- OTP verification for enhanced security
- Password encryption using BCrypt
- Session management and token expiration

### Authorization
- Role-based access control
- Endpoint-level security
- Method-level security annotations
- Custom security filters

### Data Protection
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CSRF protection

## 📊 Database Schema

### Core Tables
- **users**: User authentication and profile information
- **accounts**: Chart of accounts structure
- **vouchers**: Financial transaction vouchers
- **voucher_entries**: Individual debit/credit entries
- **ledger_entries**: General ledger entries
- **sales**: Fuel sales transactions
- **shifts**: Shift management and cash tracking
- **cash_movements**: Cash flow tracking
- **notifications**: Notification queue and history

### Relationships
- Proper foreign key relationships
- Cascade operations where appropriate
- Audit trail on all entities
- Optimized indexing for performance

## 🔄 Business Logic

### Double-Entry Bookkeeping
- Every transaction affects at least two accounts
- Debit and credit entries must balance
- Automatic ledger entry generation
- Voucher posting and validation

### Cash Management
- Opening and closing cash tracking
- Denomination-wise cash counting
- Cash shortage/excess tracking
- Inter-shift cash transfers

### Sales Processing
- Real-time sales entry
- Multiple payment methods support
- Customer credit management
- Receipt generation

## 🚀 Deployment

### Production Deployment
1. **Build the application**
   ```bash
   mvn clean package
   ```

2. **Configure production database**
   - Set up MySQL production database
   - Configure connection pooling
   - Set up database backups

3. **Environment Configuration**
   - Set production environment variables
   - Configure logging levels
   - Set up monitoring and health checks

4. **Deploy to server**
   - Use Spring Boot executable JAR
   - Configure reverse proxy (Nginx/Apache)
   - Set up SSL certificates

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:24-jdk-slim
COPY target/petrosoft-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📈 Monitoring & Maintenance

### Health Checks
- Spring Boot Actuator endpoints
- Database connection monitoring
- External service health checks
- Custom health indicators

### Logging
- Structured logging with SLF4J
- Log levels configuration
- Application metrics logging
- Error tracking and alerting

### Performance Monitoring
- Database query performance
- API response time monitoring
- Memory usage tracking
- JVM metrics monitoring

## 🤝 Contributing

### Development Guidelines
1. Follow Java coding standards
2. Write comprehensive unit tests
3. Document all public APIs
4. Follow Git branching strategy
5. Code review process

### Testing
- Unit tests for all services
- Integration tests for APIs
- Database integration tests
- Performance testing

## 📞 Support

### Documentation
- API documentation available through endpoints
- Database schema documentation
- Business logic documentation
- Deployment guides

### Troubleshooting
- Common issues and solutions
- Performance optimization tips
- Database optimization guidelines
- Security best practices

## 🎯 Future Enhancements

### Planned Features
- **Mobile App Integration**: REST API ready for mobile apps
- **Advanced Analytics**: Machine learning-based insights
- **Multi-location Support**: Support for multiple petrol pumps
- **Integration APIs**: Third-party system integrations
- **Advanced Reporting**: Custom report builder
- **Workflow Management**: Approval workflows for transactions

### Technical Improvements
- **Microservices Architecture**: Break into smaller services
- **Event-Driven Architecture**: Implement event sourcing
- **Caching Layer**: Redis integration for performance
- **Message Queue**: Asynchronous processing with RabbitMQ
- **API Gateway**: Centralized API management
- **Container Orchestration**: Kubernetes deployment

---

## 📋 Summary

The Petrosoft application is a comprehensive, production-ready petrol pump management system that provides:

✅ **Complete Business Functionality**: All core petrol pump operations
✅ **Modern Technology Stack**: Latest Spring Boot and Java technologies  
✅ **Security**: Enterprise-grade authentication and authorization
✅ **Scalability**: Designed for growth and expansion
✅ **Maintainability**: Clean architecture and comprehensive documentation
✅ **Performance**: Optimized database design and efficient queries
✅ **Integration Ready**: APIs prepared for mobile and third-party integrations

The application successfully matches and extends the functionality of the reference Petrosoft India application while providing a modern, maintainable, and scalable solution for petrol pump management.
