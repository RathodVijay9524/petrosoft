# Petrosoft API Documentation

## Base URL
```
http://localhost:8081/api
```

## Authentication

All API endpoints (except authentication endpoints) require a valid JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

---

## Authentication Endpoints

### 1. Login
**POST** `/auth/login`

Login with username and password to receive JWT token.

**Request Body:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "message": "Login successful"
}
```

### 2. Send OTP
**POST** `/auth/send-otp`

Send OTP to user's registered phone number.

**Request Body:**
```json
{
  "phoneNumber": "+1234567890"
}
```

**Response:**
```json
{
  "message": "OTP sent successfully",
  "otpId": "12345"
}
```

### 3. Verify OTP
**POST** `/auth/verify-otp`

Verify OTP and receive JWT token.

**Request Body:**
```json
{
  "phoneNumber": "+1234567890",
  "otp": "123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "OTP verified successfully"
}
```

### 4. Forgot Password
**POST** `/auth/forgot-password`

Request password reset.

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "message": "Password reset instructions sent to your email"
}
```

---

## Account Management

### 1. Get All Accounts
**GET** `/accounts`

Retrieve all accounts in the chart of accounts.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `group` (optional): Filter by account group

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "accountCode": "1001",
      "accountName": "Cash in Hand",
      "accountGroup": "CURRENT_ASSETS",
      "isActive": true,
      "isCash": true,
      "balance": 50000.00
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### 2. Create Account
**POST** `/accounts`

Create a new account in the chart of accounts.

**Request Body:**
```json
{
  "accountCode": "1002",
  "accountName": "Bank Account",
  "accountGroup": "CURRENT_ASSETS",
  "description": "Main business bank account",
  "isActive": true,
  "isCash": false
}
```

**Response:**
```json
{
  "id": 2,
  "accountCode": "1002",
  "accountName": "Bank Account",
  "accountGroup": "CURRENT_ASSETS",
  "description": "Main business bank account",
  "isActive": true,
  "isCash": false,
  "balance": 0.00
}
```

### 3. Update Account
**PUT** `/accounts/{id}`

Update an existing account.

**Request Body:**
```json
{
  "accountName": "Updated Bank Account",
  "description": "Updated description"
}
```

### 4. Delete Account
**DELETE** `/accounts/{id}`

Delete an account (soft delete - sets isActive to false).

---

## Voucher Management

### 1. Get All Vouchers
**GET** `/vouchers`

Retrieve all vouchers with pagination.

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size
- `type` (optional): Filter by voucher type
- `status` (optional): Filter by voucher status

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "voucherNumber": "VCH001",
      "voucherType": "CUSTOMER_RECEIPT",
      "voucherDate": "2024-01-15",
      "totalAmount": 5000.00,
      "status": "POSTED",
      "entries": [
        {
          "id": 1,
          "accountId": 1,
          "accountName": "Cash in Hand",
          "debitAmount": 5000.00,
          "creditAmount": 0.00,
          "narration": "Cash received from customer"
        }
      ]
    }
  ]
}
```

### 2. Create Voucher
**POST** `/vouchers`

Create a new voucher with entries.

**Request Body:**
```json
{
  "voucherType": "CUSTOMER_RECEIPT",
  "voucherDate": "2024-01-15",
  "narration": "Cash sale receipt",
  "entries": [
    {
      "accountId": 1,
      "debitAmount": 5000.00,
      "creditAmount": 0.00,
      "narration": "Cash received"
    },
    {
      "accountId": 2,
      "debitAmount": 0.00,
      "creditAmount": 5000.00,
      "narration": "Sales revenue"
    }
  ]
}
```

### 3. Post Voucher
**POST** `/vouchers/{id}/post`

Post a draft voucher to make it final.

**Response:**
```json
{
  "message": "Voucher posted successfully",
  "voucherId": 1
}
```

---

## Sales Management

### 1. Get All Sales
**GET** `/sales`

Retrieve all sales transactions.

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size
- `pumpId` (optional): Filter by pump ID
- `dateFrom` (optional): Filter from date (YYYY-MM-DD)
- `dateTo` (optional): Filter to date (YYYY-MM-DD)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "saleNumber": "SALE001",
      "pumpId": 1,
      "quantity": 50.000,
      "rate": 95.50,
      "amount": 4775.00,
      "totalAmount": 4775.00,
      "paymentMethod": "CASH",
      "saleType": "RETAIL",
      "status": "COMPLETED",
      "transactedAt": "2024-01-15T10:30:00",
      "vehicleNumber": "ABC123",
      "driverName": "John Doe"
    }
  ]
}
```

### 2. Create Sale
**POST** `/sales`

Create a new sales transaction.

**Request Body:**
```json
{
  "pumpId": 1,
  "quantity": 50.000,
  "rate": 95.50,
  "paymentMethod": "CASH",
  "saleType": "RETAIL",
  "vehicleNumber": "ABC123",
  "driverName": "John Doe",
  "operatorId": 1,
  "cashierId": 1
}
```

### 3. Get Sales by Pump
**GET** `/sales/pump/{pumpId}`

Get all sales for a specific pump.

### 4. Get Sales by Date Range
**GET** `/sales/date-range`

Get sales within a date range.

**Query Parameters:**
- `startDate`: Start date (YYYY-MM-DD)
- `endDate`: End date (YYYY-MM-DD)

---

## Cash Management

### 1. Get Cash Movements
**GET** `/cash-movements`

Retrieve all cash movements.

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "pumpId": 1,
      "fromShiftId": 1,
      "toShiftId": 2,
      "amount": 10000.00,
      "movementType": "HANDOVER",
      "status": "COMPLETED",
      "notes": "Cash handover between shifts",
      "createdAt": "2024-01-15T08:00:00"
    }
  ]
}
```

### 2. Create Cash Movement
**POST** `/cash-movements`

Create a new cash movement.

**Request Body:**
```json
{
  "pumpId": 1,
  "fromShiftId": 1,
  "toShiftId": 2,
  "amount": 10000.00,
  "movementType": "HANDOVER",
  "notes": "End of shift handover"
}
```

### 3. Get Cash Collections
**GET** `/cash-collections`

Retrieve cash collection data.

### 4. Create Cash Collection
**POST** `/cash-collections`

Record cash collection for a shift.

**Request Body:**
```json
{
  "shiftId": 1,
  "cashierId": 1,
  "collectedAmount": 15000.00,
  "shortageAmount": 0.00,
  "excessAmount": 100.00,
  "notes": "Shift collection with excess"
}
```

---

## Shift Management

### 1. Get All Shifts
**GET** `/shifts`

Retrieve all shifts.

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "pumpId": 1,
      "operatorId": 1,
      "cashierId": 1,
      "shiftName": "I",
      "openedAt": "2024-01-15T06:00:00",
      "closedAt": "2024-01-15T14:00:00",
      "status": "CLOSED",
      "openingCash": 5000.00,
      "closingCash": 15000.00,
      "totalSales": 10000.00,
      "cashSales": 8000.00,
      "cardSales": 2000.00
    }
  ]
}
```

### 2. Open Shift
**POST** `/shifts/open`

Open a new shift.

**Request Body:**
```json
{
  "pumpId": 1,
  "operatorId": 1,
  "cashierId": 1,
  "shiftName": "I",
  "openingCash": 5000.00
}
```

### 3. Close Shift
**POST** `/shifts/{id}/close`

Close an existing shift.

**Request Body:**
```json
{
  "closingCash": 15000.00,
  "cashGivenToNextShift": 5000.00,
  "notes": "Shift completed successfully"
}
```

---

## Financial Reports

### 1. Trial Balance
**GET** `/reports/trial-balance`

Generate trial balance report.

**Query Parameters:**
- `asOnDate` (optional): Report date (YYYY-MM-DD, default: today)

**Response:**
```json
{
  "asOnDate": "2024-01-15",
  "accounts": [
    {
      "accountCode": "1001",
      "accountName": "Cash in Hand",
      "debitBalance": 15000.00,
      "creditBalance": 0.00
    }
  ],
  "totalDebit": 50000.00,
  "totalCredit": 50000.00,
  "isBalanced": true
}
```

### 2. Profit & Loss Statement
**GET** `/reports/profit-loss`

Generate profit and loss statement.

**Query Parameters:**
- `fromDate`: Start date (YYYY-MM-DD)
- `toDate`: End date (YYYY-MM-DD)

**Response:**
```json
{
  "fromDate": "2024-01-01",
  "toDate": "2024-01-31",
  "income": [
    {
      "accountName": "Fuel Sales",
      "amount": 500000.00
    }
  ],
  "expenses": [
    {
      "accountName": "Fuel Purchase",
      "amount": 400000.00
    }
  ],
  "totalIncome": 500000.00,
  "totalExpenses": 400000.00,
  "netProfit": 100000.00
}
```

### 3. Balance Sheet
**GET** `/reports/balance-sheet`

Generate balance sheet report.

**Query Parameters:**
- `asOnDate` (optional): Report date (YYYY-MM-DD, default: today)

**Response:**
```json
{
  "asOnDate": "2024-01-15",
  "assets": [
    {
      "accountName": "Cash in Hand",
      "amount": 15000.00
    }
  ],
  "liabilities": [
    {
      "accountName": "Accounts Payable",
      "amount": 5000.00
    }
  ],
  "equity": [
    {
      "accountName": "Capital",
      "amount": 100000.00
    }
  ],
  "totalAssets": 120000.00,
  "totalLiabilitiesAndEquity": 120000.00
}
```

### 4. Cash Book
**GET** `/reports/cash-book`

Generate cash book report.

**Query Parameters:**
- `fromDate`: Start date (YYYY-MM-DD)
- `toDate`: End date (YYYY-MM-DD)
- `accountId` (optional): Specific cash account

### 5. Day Book
**GET** `/reports/day-book`

Generate day book report.

**Query Parameters:**
- `date` (optional): Report date (YYYY-MM-DD, default: today)

---

## Dashboard

### 1. Dashboard Metrics
**GET** `/dashboard/metrics`

Get real-time dashboard metrics.

**Response:**
```json
{
  "totalSales": {
    "today": 50000.00,
    "thisMonth": 1500000.00,
    "thisYear": 18000000.00
  },
  "cashFlow": {
    "openingCash": 50000.00,
    "closingCash": 75000.00,
    "cashIn": 25000.00,
    "cashOut": 0.00
  },
  "salesByPaymentMethod": {
    "CASH": 40000.00,
    "CARD": 8000.00,
    "CREDIT": 2000.00
  },
  "topSellingFuels": [
    {
      "fuelType": "Petrol",
      "quantity": 500.00,
      "amount": 47500.00
    }
  ],
  "activeShifts": 3,
  "pendingVouchers": 5
}
```

### 2. Sales Summary
**GET** `/dashboard/sales-summary`

Get sales summary for dashboard.

**Query Parameters:**
- `period` (optional): Summary period (today, week, month, year)

### 3. Cash Summary
**GET** `/dashboard/cash-summary`

Get cash flow summary for dashboard.

---

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Invalid input data",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Access denied",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Data Models

### Account Groups
- `CURRENT_ASSETS`
- `FIXED_ASSETS`
- `INVESTMENTS`
- `OTHER_ASSETS`
- `CURRENT_LIABILITIES`
- `LONG_TERM_LIABILITIES`
- `CAPITAL`
- `RESERVES_AND_SURPLUS`
- `DIRECT_INCOME`
- `INDIRECT_INCOME`
- `DIRECT_EXPENSES`
- `INDIRECT_EXPENSES`

### Voucher Types
- `CUSTOMER_RECEIPT`
- `PAYMENT_VOUCHER`
- `MISCELLANEOUS_RECEIPT`
- `JOURNAL_VOUCHER`
- `CASH_DEPOSIT`
- `CASH_WITHDRAWAL`
- `CHEQUE_RETURN_ENTRY`

### Payment Methods
- `CASH`
- `CARD`
- `CREDIT`
- `WALLET`
- `UPI`
- `NET_BANKING`

### Sale Types
- `RETAIL`
- `BULK`
- `WHOLESALE`
- `GOVERNMENT`
- `STAFF`

### Voucher Status
- `DRAFT`
- `APPROVED`
- `POSTED`
- `CANCELLED`

### Sale Status
- `PENDING`
- `COMPLETED`
- `CANCELLED`
- `REFUNDED`
- `PARTIALLY_REFUNDED`

### Shift Status
- `OPEN`
- `CLOSED`
- `SUSPENDED`
- `CANCELLED`

---

## Rate Limiting

API endpoints are rate-limited to prevent abuse:
- **Authentication endpoints**: 5 requests per minute per IP
- **Other endpoints**: 100 requests per minute per user
- **Report endpoints**: 10 requests per minute per user

---

## Versioning

Current API version: **v1**

API versioning is handled through URL path:
- Current: `/api/...`
- Future versions: `/api/v2/...`

---

## Support

For API support and documentation updates:
- Check the main README.md file
- Review the application logs for detailed error information
- Contact the development team for additional assistance
