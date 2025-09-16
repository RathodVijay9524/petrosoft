# Petrosoft API Endpoints Documentation

## Overview
This document provides comprehensive documentation for all API endpoints in the Petrosoft application. The API follows RESTful conventions and includes role-based access control.

**Base URL**: `http://localhost:8081/api`
**Authentication**: JWT Bearer Token (where applicable)

---

## 🔐 Authentication Endpoints

### User Authentication
- **POST** `/auth/login` - User login
- **POST** `/auth/register` - User registration
- **POST** `/auth/refresh` - Refresh JWT token
- **POST** `/auth/logout` - User logout

### User Management
- **GET** `/users` - Get all users (Admin only)
- **GET** `/users/{id}` - Get user by ID
- **PUT** `/users/{id}` - Update user
- **DELETE** `/users/{id}` - Delete user (Admin only)

---

## 🛢️ Petrol Pump Operations

### Sales Management
- **GET** `/sales` - Get all sales transactions
- **GET** `/sales/{id}` - Get sales transaction by ID
- **POST** `/sales` - Create new sales transaction
- **PUT** `/sales/{id}` - Update sales transaction
- **DELETE** `/sales/{id}` - Delete sales transaction

### Pump Management
- **GET** `/pumps` - Get all pumps
- **GET** `/pumps/{id}` - Get pump by ID
- **POST** `/pumps` - Create new pump
- **PUT** `/pumps/{id}` - Update pump
- **DELETE** `/pumps/{id}` - Delete pump

### Tank Management
- **GET** `/tanks` - Get all tanks
- **GET** `/tanks/{id}` - Get tank by ID
- **POST** `/tanks` - Create new tank
- **PUT** `/tanks/{id}` - Update tank
- **DELETE** `/tanks/{id}` - Delete tank

### Shift Management
- **GET** `/shifts` - Get all shifts
- **GET** `/shifts/{id}` - Get shift by ID
- **POST** `/shifts` - Create new shift
- **PUT** `/shifts/{id}` - Update shift
- **DELETE** `/shifts/{id}` - Delete shift
- **POST** `/shifts/{id}/close` - Close shift

---

## 💰 Financial Operations

### Payment Management
- **GET** `/payments` - Get all payments
- **GET** `/payments/{id}` - Get payment by ID
- **POST** `/payments` - Create new payment
- **PUT** `/payments/{id}` - Update payment
- **DELETE** `/payments/{id}` - Delete payment

### Voucher Management
- **GET** `/vouchers` - Get all vouchers
- **GET** `/vouchers/{id}` - Get voucher by ID
- **POST** `/vouchers` - Create new voucher
- **PUT** `/vouchers/{id}` - Update voucher
- **DELETE** `/vouchers/{id}` - Delete voucher
- **POST** `/vouchers/{id}/post` - Post voucher
- **POST** `/vouchers/{id}/cancel` - Cancel voucher

### Ledger Management
- **GET** `/ledger` - Get all ledger entries
- **GET** `/ledger/{id}` - Get ledger entry by ID
- **POST** `/ledger` - Create new ledger entry
- **PUT** `/ledger/{id}` - Update ledger entry
- **DELETE** `/ledger/{id}` - Delete ledger entry

### Account Management
- **GET** `/accounts` - Get all accounts
- **GET** `/accounts/{id}` - Get account by ID
- **POST** `/accounts` - Create new account
- **PUT** `/accounts/{id}` - Update account
- **DELETE** `/accounts/{id}` - Delete account

---

## 👥 Master Data Management

### Customer Management
- **GET** `/customers` - Get all customers
- **GET** `/customers/{id}` - Get customer by ID
- **POST** `/customers` - Create new customer
- **PUT** `/customers/{id}` - Update customer
- **DELETE** `/customers/{id}` - Delete customer

### Employee Management
- **GET** `/employees` - Get all employees
- **GET** `/employees/{id}` - Get employee by ID
- **POST** `/employees` - Create new employee
- **PUT** `/employees/{id}` - Update employee
- **DELETE** `/employees/{id}` - Delete employee

### Vendor Management
- **GET** `/vendors` - Get all vendors
- **GET** `/vendors/{id}` - Get vendor by ID
- **POST** `/vendors` - Create new vendor
- **PUT** `/vendors/{id}` - Update vendor
- **DELETE** `/vendors/{id}` - Delete vendor

### Company Management
- **GET** `/companies` - Get all companies
- **GET** `/companies/{id}` - Get company by ID
- **POST** `/companies` - Create new company
- **PUT** `/companies/{id}` - Update company
- **DELETE** `/companies/{id}` - Delete company

---

## 📊 Dashboard & Analytics

### Dashboard Endpoints
- **GET** `/dashboard` - Get main dashboard data
- **GET** `/dashboard/overview` - Get dashboard overview
- **GET** `/dashboard/metrics` - Get dashboard metrics
- **GET** `/dashboard/kpis` - Get dashboard KPIs

### Analytics Endpoints

#### Sales Analytics
- **GET** `/analytics/sales/revenue-trends` - Get sales revenue trends
  - Query Parameters: `startDate`, `endDate`
- **GET** `/analytics/sales/transaction-analysis` - Get sales transaction analysis
  - Query Parameters: `startDate`, `endDate`
- **GET** `/analytics/sales/customer-segmentation` - Get customer segmentation

#### Customer Analytics
- **GET** `/analytics/customers/behavior` - Get customer behavior analysis
- **GET** `/analytics/customers/retention` - Get customer retention analysis
- **GET** `/analytics/customers/acquisition-channels` - Get customer acquisition channels

#### Financial Analytics
- **GET** `/analytics/financial/revenue-metrics` - Get financial revenue metrics
  - Query Parameters: `startDate`, `endDate`
- **GET** `/analytics/financial/payment-analysis` - Get payment analysis
  - Query Parameters: `startDate`, `endDate`
- **GET** `/analytics/financial/ledger-insights` - Get ledger insights
  - Query Parameters: `startDate`, `endDate`

#### Employee Analytics
- **GET** `/analytics/employees/performance` - Get employee performance metrics
- **GET** `/analytics/employees/productivity` - Get employee productivity analysis

#### Vendor Analytics
- **GET** `/analytics/vendors/performance` - Get vendor performance metrics
- **GET** `/analytics/vendors/outstanding-payables` - Get vendor outstanding payables

---

## 🧠 Business Intelligence

### Revenue Trends
- **GET** `/bi/revenue-trends` - Get revenue trends
  - Query Parameters: `startDate`, `endDate`, `period` (monthly/daily/weekly)

### Customer Insights
- **GET** `/bi/customer-insights` - Get customer insights

### Performance Metrics
- **GET** `/bi/performance-metrics` - Get performance metrics

### Operational Efficiency
- **GET** `/bi/operational-efficiency` - Get operational efficiency metrics

### Sales Forecasting
- **GET** `/bi/sales-forecasting` - Get sales forecasting
  - Query Parameters: `months` (default: 6)

### Inventory Optimization
- **GET** `/bi/inventory-optimization` - Get inventory optimization insights

---

## 📤 Data Export

### Export Endpoints
- **GET** `/export/sales` - Export sales data
  - Query Parameters: `startDate`, `endDate`
- **GET** `/export/customers` - Export customer data
  - Query Parameters: `startDate`, `endDate`
- **GET** `/export/financial` - Export financial data
  - Query Parameters: `startDate`, `endDate`
- **GET** `/export/employees` - Export employee data
  - Query Parameters: `startDate`, `endDate`
- **GET** `/export/vendors` - Export vendor data
  - Query Parameters: `startDate`, `endDate`

### Report Export
- **GET** `/export/reports/{reportType}` - Export specific report
  - Path Parameters: `reportType` (sales/customer/financial/inventory/performance)
  - Query Parameters: `startDate`, `endDate`

---

## 🧪 Sample Data & Testing

### Sample Data Management
- **POST** `/sample-data/create` - Create sample data
- **GET** `/sample-data/status` - Get sample data status
- **DELETE** `/sample-data/clear` - Clear sample data

---

## 🔧 System & Configuration

### Performance Testing
- **GET** `/performance/health` - Get performance health status
- **POST** `/performance/load-test` - Run load test
- **POST** `/performance/stress-test` - Run stress test
- **POST** `/performance/benchmark` - Run benchmark test

### Security Audit
- **GET** `/security/health` - Get security health status
- **POST** `/security/audit` - Run security audit
- **GET** `/security/recommendations` - Get security recommendations

### System Health
- **GET** `/actuator/health` - Get system health
- **GET** `/actuator/info` - Get system information
- **GET** `/actuator/metrics` - Get system metrics

---

## 📋 API Documentation

### Swagger UI
- **GET** `/swagger-ui.html` - Access Swagger UI documentation
- **GET** `/v3/api-docs` - Get OpenAPI 3.0 specification

### API Information
- **GET** `/api-docs/info` - Get API documentation information
- **GET** `/api-docs/status` - Get API status

---

## 🔒 Role-Based Access Control

### Roles
- **ADMIN**: Full access to all endpoints
- **MANAGER**: Access to most operational endpoints
- **OPERATOR**: Limited access to basic operations
- **CASHIER**: Access to sales and payment operations

### Security Headers
All endpoints require proper authentication headers:
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

---

## 📝 Request/Response Examples

### Sales Transaction Creation
```http
POST /api/sales
Authorization: Bearer <token>
Content-Type: application/json

{
  "customerId": 1,
  "pumpId": 1,
  "fuelType": "PETROL",
  "quantity": 50.0,
  "pricePerLiter": 95.50,
  "totalAmount": 4775.00,
  "paymentMethod": "CASH"
}
```

### Dashboard Overview Response
```json
{
  "totalPumps": 5,
  "totalCustomers": 150,
  "totalSales": 1250,
  "totalRevenue": 125000.00,
  "activeSubscriptions": 3,
  "totalPayments": 850,
  "lastUpdated": "2024-09-17T00:52:26.081"
}
```

### Analytics Revenue Trends Response
```json
{
  "totalRevenue": 125000.00,
  "averageDailyRevenue": 8500.00,
  "monthlyGrowthRate": "5.25%",
  "dailySales": [
    {
      "date": "2024-09-17",
      "revenue": 8500.00
    }
  ]
}
```

---

## 🚀 Error Handling

### Standard Error Response
```json
{
  "timestamp": "2024-09-17T00:52:26.081",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/sales"
}
```

### Common HTTP Status Codes
- **200**: Success
- **201**: Created
- **400**: Bad Request
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **500**: Internal Server Error

---

## 📊 Performance Metrics

### Response Time Benchmarks
- Dashboard endpoints: 5-10ms
- Analytics endpoints: 8-31ms
- Export endpoints: 9-34ms
- CRUD operations: 10-50ms

### Rate Limiting
- Default: 100 requests per minute per user
- Admin: 1000 requests per minute
- Analytics: 50 requests per minute

---

## 🔄 Versioning

### API Versioning Strategy
- Current Version: v1
- Version Header: `Accept: application/vnd.petrosoft.v1+json`
- URL Versioning: `/api/v1/endpoint`

---

## 📞 Support & Contact

For API support and questions:
- **Development Team**: [Your Team Contact]
- **Documentation**: This file
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **GitHub Repository**: [Your Repository URL]

---

## 📅 Last Updated
**Date**: September 17, 2024
**Version**: 1.0.0
**Author**: Development Team

---

*This documentation is automatically generated and should be updated with any API changes.*
