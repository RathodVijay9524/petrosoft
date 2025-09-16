# Petrosoft Database Schema Documentation

## Overview

The Petrosoft application uses MySQL 8.0 as its primary database. This document provides comprehensive documentation of the database schema, including table structures, relationships, indexes, and constraints.

## Database Configuration

- **Database Name**: `petrosoftdb`
- **Character Set**: `utf8mb4`
- **Collation**: `utf8mb4_unicode_ci`
- **Engine**: InnoDB
- **Timezone**: UTC

---

## Core Tables

### 1. Users Table

**Purpose**: Stores user authentication and profile information.

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
);
```

**Fields:**
- `id`: Primary key
- `username`: Unique username for login
- `email`: Unique email address
- `password`: Encrypted password (BCrypt)
- `first_name`, `last_name`: User's full name
- `phone`: Contact phone number
- `role`: User role (ADMIN, MANAGER, OPERATOR, CASHIER)
- `is_active`: Account status
- `last_login_at`: Last login timestamp
- Audit fields: `created_at`, `updated_at`, `created_by`, `updated_by`

---

### 2. Accounts Table

**Purpose**: Chart of accounts for double-entry bookkeeping.

```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_code VARCHAR(20) NOT NULL UNIQUE,
    account_name VARCHAR(100) NOT NULL,
    account_group ENUM(
        'CURRENT_ASSETS', 'FIXED_ASSETS', 'INVESTMENTS', 'OTHER_ASSETS',
        'CURRENT_LIABILITIES', 'LONG_TERM_LIABILITIES',
        'CAPITAL', 'RESERVES_AND_SURPLUS',
        'DIRECT_INCOME', 'INDIRECT_INCOME',
        'DIRECT_EXPENSES', 'INDIRECT_EXPENSES'
    ) NOT NULL,
    parent_account_id BIGINT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_cash BOOLEAN NOT NULL DEFAULT FALSE,
    opening_balance DECIMAL(15,2) DEFAULT 0.00,
    current_balance DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_account_code (account_code),
    INDEX idx_account_group (account_group),
    INDEX idx_parent_account (parent_account_id),
    INDEX idx_active (is_active),
    FOREIGN KEY (parent_account_id) REFERENCES accounts(id) ON DELETE SET NULL
);
```

**Fields:**
- `account_code`: Unique account identifier
- `account_name`: Descriptive account name
- `account_group`: Account classification
- `parent_account_id`: Parent account for hierarchical structure
- `is_cash`: Flag for cash accounts
- `opening_balance`: Initial account balance
- `current_balance`: Current account balance

---

### 3. Vouchers Table

**Purpose**: Stores financial transaction vouchers.

```sql
CREATE TABLE vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_number VARCHAR(50) NOT NULL UNIQUE,
    voucher_type ENUM(
        'CUSTOMER_RECEIPT', 'PAYMENT_VOUCHER', 'MISCELLANEOUS_RECEIPT',
        'JOURNAL_VOUCHER', 'CASH_DEPOSIT', 'CASH_WITHDRAWAL',
        'CHEQUE_RETURN_ENTRY'
    ) NOT NULL,
    voucher_date DATE NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    narration TEXT,
    status ENUM('DRAFT', 'APPROVED', 'POSTED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    reference_number VARCHAR(100),
    pump_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_voucher_number (voucher_number),
    INDEX idx_voucher_type (voucher_type),
    INDEX idx_voucher_date (voucher_date),
    INDEX idx_status (status),
    INDEX idx_pump_id (pump_id)
);
```

**Fields:**
- `voucher_number`: Unique voucher identifier
- `voucher_type`: Type of financial transaction
- `voucher_date`: Transaction date
- `total_amount`: Total voucher amount
- `narration`: Transaction description
- `status`: Voucher processing status
- `reference_number`: External reference
- `pump_id`: Associated petrol pump

---

### 4. Voucher Entries Table

**Purpose**: Individual debit/credit entries within vouchers.

```sql
CREATE TABLE voucher_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    entry_type ENUM('DEBIT', 'CREDIT') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    narration TEXT,
    party_name VARCHAR(100),
    party_id BIGINT NULL,
    additional_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_voucher_id (voucher_id),
    INDEX idx_account_id (account_id),
    INDEX idx_entry_type (entry_type),
    INDEX idx_party_id (party_id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
```

**Fields:**
- `voucher_id`: Reference to parent voucher
- `account_id`: Account being debited/credited
- `entry_type`: DEBIT or CREDIT
- `amount`: Transaction amount
- `narration`: Entry description
- `party_name`: Third party name
- `party_id`: Reference to party entity
- `additional_info`: JSON for custom fields

---

### 5. Ledger Entries Table

**Purpose**: General ledger entries for account tracking.

```sql
CREATE TABLE ledger_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    voucher_id BIGINT NULL,
    transaction_date TIMESTAMP NOT NULL,
    entry_type ENUM('DEBIT', 'CREDIT') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    narration TEXT,
    reference_number VARCHAR(100),
    pump_id BIGINT NULL,
    party_id BIGINT NULL,
    reconciled_by BIGINT NULL,
    is_reconciled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_account_id (account_id),
    INDEX idx_voucher_id (voucher_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_entry_type (entry_type),
    INDEX idx_pump_id (pump_id),
    INDEX idx_reconciled (is_reconciled),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE SET NULL
);
```

**Fields:**
- `account_id`: Account for this entry
- `voucher_id`: Source voucher (if applicable)
- `transaction_date`: Date of transaction
- `entry_type`: DEBIT or CREDIT
- `amount`: Transaction amount
- `balance_after`: Account balance after this entry
- `narration`: Entry description
- `is_reconciled`: Reconciliation status

---

### 6. Sales Table

**Purpose**: Fuel sales transactions.

```sql
CREATE TABLE sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pump_id BIGINT NOT NULL,
    shift_id BIGINT NULL,
    nozzle_id BIGINT NULL,
    fuel_type_id BIGINT NULL,
    customer_id BIGINT NULL,
    sale_number VARCHAR(50) NOT NULL UNIQUE,
    quantity DECIMAL(10,3) NOT NULL,
    rate DECIMAL(8,2) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT 0.00,
    tax_amount DECIMAL(15,2) DEFAULT 0.00,
    total_amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'CREDIT', 'WALLET', 'UPI', 'NET_BANKING') NOT NULL,
    sale_type ENUM('RETAIL', 'BULK', 'WHOLESALE', 'GOVERNMENT', 'STAFF') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED') DEFAULT 'COMPLETED',
    transacted_at TIMESTAMP NOT NULL,
    operator_id BIGINT NULL,
    cashier_id BIGINT NULL,
    vehicle_number VARCHAR(20),
    driver_name VARCHAR(100),
    notes TEXT,
    card_last_four VARCHAR(4),
    card_type VARCHAR(20),
    transaction_reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_pump_id (pump_id),
    INDEX idx_shift_id (shift_id),
    INDEX idx_sale_number (sale_number),
    INDEX idx_transacted_at (transacted_at),
    INDEX idx_payment_method (payment_method),
    INDEX idx_status (status),
    INDEX idx_customer_id (customer_id),
    INDEX idx_vehicle_number (vehicle_number),
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE SET NULL
);
```

**Fields:**
- `pump_id`: Petrol pump identifier
- `shift_id`: Associated shift
- `sale_number`: Unique sale identifier
- `quantity`: Fuel quantity sold
- `rate`: Price per unit
- `amount`: Base amount
- `discount_amount`: Applied discount
- `tax_amount`: Applied tax
- `total_amount`: Final amount
- `payment_method`: Payment type
- `sale_type`: Sale category
- `vehicle_number`: Customer vehicle number
- `driver_name`: Driver name

---

### 7. Shifts Table

**Purpose**: Shift management and cash tracking.

```sql
CREATE TABLE shifts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pump_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    cashier_id BIGINT NULL,
    shift_name VARCHAR(50) NOT NULL,
    opened_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP NULL,
    status ENUM('OPEN', 'CLOSED', 'SUSPENDED', 'CANCELLED') NOT NULL DEFAULT 'OPEN',
    opening_cash DECIMAL(15,2),
    closing_cash DECIMAL(15,2),
    total_sales DECIMAL(15,2) DEFAULT 0.00,
    cash_sales DECIMAL(15,2) DEFAULT 0.00,
    card_sales DECIMAL(15,2) DEFAULT 0.00,
    credit_sales DECIMAL(15,2) DEFAULT 0.00,
    cash_collected DECIMAL(15,2),
    cash_given_to_next_shift DECIMAL(15,2),
    balance_with_shift_incharge DECIMAL(15,2),
    balance_with_cashier DECIMAL(15,2),
    notes TEXT,
    closed_by BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_pump_id (pump_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_cashier_id (cashier_id),
    INDEX idx_opened_at (opened_at),
    INDEX idx_status (status),
    INDEX idx_shift_name (shift_name)
);
```

**Fields:**
- `pump_id`: Associated petrol pump
- `operator_id`: Shift operator
- `cashier_id`: Shift cashier
- `shift_name`: Shift identifier (I, II, III)
- `opened_at`, `closed_at`: Shift timing
- `status`: Current shift status
- `opening_cash`, `closing_cash`: Cash amounts
- `total_sales`: Total sales amount
- `cash_sales`, `card_sales`, `credit_sales`: Sales by payment method

---

### 8. Cash Movements Table

**Purpose**: Cash flow tracking between shifts and locations.

```sql
CREATE TABLE cash_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pump_id BIGINT NOT NULL,
    from_shift_id BIGINT NULL,
    to_shift_id BIGINT NULL,
    amount DECIMAL(15,2) NOT NULL,
    movement_type ENUM('HANDOVER', 'DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'COLLECTION') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    movement_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_pump_id (pump_id),
    INDEX idx_from_shift_id (from_shift_id),
    INDEX idx_to_shift_id (to_shift_id),
    INDEX idx_movement_type (movement_type),
    INDEX idx_movement_date (movement_date),
    INDEX idx_status (status),
    FOREIGN KEY (from_shift_id) REFERENCES shifts(id) ON DELETE SET NULL,
    FOREIGN KEY (to_shift_id) REFERENCES shifts(id) ON DELETE SET NULL
);
```

**Fields:**
- `pump_id`: Associated petrol pump
- `from_shift_id`, `to_shift_id`: Source and destination shifts
- `amount`: Movement amount
- `movement_type`: Type of cash movement
- `status`: Movement status
- `movement_date`: Date of movement

---

### 9. Currency Denominations Table

**Purpose**: Currency note and coin tracking.

```sql
CREATE TABLE currency_denominations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift_id BIGINT NOT NULL,
    denomination_type ENUM('NOTE', 'COIN') NOT NULL,
    value DECIMAL(8,2) NOT NULL,
    count INTEGER NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_shift_id (shift_id),
    INDEX idx_denomination_type (denomination_type),
    INDEX idx_value (value),
    INDEX idx_recorded_at (recorded_at),
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE CASCADE
);
```

**Fields:**
- `shift_id`: Associated shift
- `denomination_type`: NOTE or COIN
- `value`: Denomination value
- `count`: Number of notes/coins
- `total_amount`: Total amount for this denomination
- `recorded_at`: Recording timestamp

---

### 10. Cash Collections Table

**Purpose**: Shift cash collection management.

```sql
CREATE TABLE cash_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift_id BIGINT NOT NULL,
    cashier_id BIGINT NOT NULL,
    collected_amount DECIMAL(15,2) NOT NULL,
    shortage_amount DECIMAL(15,2) DEFAULT 0.00,
    excess_amount DECIMAL(15,2) DEFAULT 0.00,
    status ENUM('PENDING', 'VERIFIED', 'DISPUTED') NOT NULL DEFAULT 'PENDING',
    collection_date TIMESTAMP NOT NULL,
    verified_by BIGINT NULL,
    verification_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_shift_id (shift_id),
    INDEX idx_cashier_id (cashier_id),
    INDEX idx_collection_date (collection_date),
    INDEX idx_status (status),
    FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE CASCADE
);
```

**Fields:**
- `shift_id`: Associated shift
- `cashier_id`: Collection cashier
- `collected_amount`: Total collected amount
- `shortage_amount`: Cash shortage
- `excess_amount`: Cash excess
- `status`: Collection status
- `collection_date`: Collection timestamp

---

### 11. Notifications Table

**Purpose**: Notification queue and history.

```sql
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    pump_id BIGINT NULL,
    type ENUM('EMAIL', 'SMS', 'PUSH') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    recipient_email VARCHAR(255),
    recipient_phone VARCHAR(20),
    status ENUM('PENDING', 'SENT', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    error_message TEXT,
    template_name VARCHAR(100),
    template_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    INDEX idx_user_id (user_id),
    INDEX idx_pump_id (pump_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_retry_count (retry_count),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```

**Fields:**
- `user_id`: Target user (if applicable)
- `pump_id`: Associated petrol pump
- `type`: Notification type (EMAIL, SMS, PUSH)
- `title`: Notification title
- `message`: Notification content
- `recipient_email`, `recipient_phone`: Recipient details
- `status`: Notification status
- `scheduled_at`: Scheduled sending time
- `sent_at`: Actual sending time
- `retry_count`, `max_retries`: Retry logic
- `template_name`: Email/SMS template
- `template_data`: Template variables (JSON)

---

## Supporting Tables

### 12. Fuel Types Table

```sql
CREATE TABLE fuel_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    current_rate DECIMAL(8,2) NOT NULL,
    unit VARCHAR(10) NOT NULL DEFAULT 'LITER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 13. Customers Table

```sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    credit_limit DECIMAL(15,2) DEFAULT 0.00,
    current_balance DECIMAL(15,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_customer_code (customer_code),
    INDEX idx_name (name),
    INDEX idx_phone (phone)
);
```

### 14. Pumps Table

```sql
CREATE TABLE pumps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pump_name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_pump_name (pump_name),
    INDEX idx_active (is_active)
);
```

### 15. Nozzles Table

```sql
CREATE TABLE nozzles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pump_id BIGINT NOT NULL,
    nozzle_number VARCHAR(20) NOT NULL,
    fuel_type_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_pump_id (pump_id),
    INDEX idx_fuel_type_id (fuel_type_id),
    FOREIGN KEY (pump_id) REFERENCES pumps(id) ON DELETE CASCADE,
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(id)
);
```

---

## Database Views

### 1. Account Balance View

```sql
CREATE VIEW account_balances AS
SELECT 
    a.id,
    a.account_code,
    a.account_name,
    a.account_group,
    COALESCE(SUM(CASE WHEN le.entry_type = 'DEBIT' THEN le.amount ELSE 0 END), 0) AS total_debits,
    COALESCE(SUM(CASE WHEN le.entry_type = 'CREDIT' THEN le.amount ELSE 0 END), 0) AS total_credits,
    COALESCE(SUM(CASE WHEN le.entry_type = 'DEBIT' THEN le.amount ELSE -le.amount END), 0) AS balance
FROM accounts a
LEFT JOIN ledger_entries le ON a.id = le.account_id
WHERE a.is_active = TRUE
GROUP BY a.id, a.account_code, a.account_name, a.account_group;
```

### 2. Sales Summary View

```sql
CREATE VIEW sales_summary AS
SELECT 
    DATE(transacted_at) AS sale_date,
    pump_id,
    COUNT(*) AS total_transactions,
    SUM(quantity) AS total_quantity,
    SUM(total_amount) AS total_amount,
    SUM(CASE WHEN payment_method = 'CASH' THEN total_amount ELSE 0 END) AS cash_sales,
    SUM(CASE WHEN payment_method = 'CARD' THEN total_amount ELSE 0 END) AS card_sales,
    SUM(CASE WHEN payment_method = 'CREDIT' THEN total_amount ELSE 0 END) AS credit_sales
FROM sales
WHERE status = 'COMPLETED'
GROUP BY DATE(transacted_at), pump_id;
```

### 3. Shift Summary View

```sql
CREATE VIEW shift_summary AS
SELECT 
    s.id,
    s.pump_id,
    s.shift_name,
    s.opened_at,
    s.closed_at,
    s.status,
    s.opening_cash,
    s.closing_cash,
    s.total_sales,
    COUNT(st.id) AS total_transactions,
    COALESCE(SUM(st.quantity), 0) AS total_fuel_sold
FROM shifts s
LEFT JOIN sales st ON s.id = st.shift_id AND st.status = 'COMPLETED'
GROUP BY s.id, s.pump_id, s.shift_name, s.opened_at, s.closed_at, s.status, s.opening_cash, s.closing_cash, s.total_sales;
```

---

## Indexes and Performance

### Primary Indexes
- All tables have primary key indexes on `id` columns
- Unique indexes on business keys (account_code, voucher_number, sale_number)

### Foreign Key Indexes
- All foreign key columns have indexes for join performance
- Cascade and set null constraints properly configured

### Composite Indexes
```sql
-- Sales table composite indexes
CREATE INDEX idx_sales_date_pump ON sales(transacted_at, pump_id);
CREATE INDEX idx_sales_shift_status ON sales(shift_id, status);
CREATE INDEX idx_sales_payment_date ON sales(payment_method, transacted_at);

-- Ledger entries composite indexes
CREATE INDEX idx_ledger_account_date ON ledger_entries(account_id, transaction_date);
CREATE INDEX idx_ledger_reconciled_date ON ledger_entries(is_reconciled, transaction_date);

-- Notifications composite indexes
CREATE INDEX idx_notifications_status_retry ON notifications(status, retry_count);
CREATE INDEX idx_notifications_scheduled_status ON notifications(scheduled_at, status);
```

---

## Constraints and Business Rules

### Data Integrity Constraints
1. **Account Balance**: Balance must be calculated from ledger entries
2. **Voucher Balance**: Total debits must equal total credits
3. **Sales Amount**: total_amount = amount - discount_amount + tax_amount
4. **Shift Cash**: closing_cash >= 0
5. **Notification Retry**: retry_count <= max_retries

### Referential Integrity
- All foreign keys have proper constraints
- Cascade deletes where appropriate
- Set null for optional references

---

## Backup and Recovery

### Backup Strategy
```sql
-- Full database backup
mysqldump --single-transaction --routines --triggers --events petrosoftdb > backup.sql

-- Table-specific backup
mysqldump --single-transaction petrosoftdb sales vouchers > transactions_backup.sql

-- Incremental backup (binary logs)
mysqlbinlog mysql-bin.000001 > incremental_backup.sql
```

### Recovery Procedures
```sql
-- Full database restore
mysql petrosoftdb < backup.sql

-- Point-in-time recovery
mysqlbinlog --start-datetime="2024-01-15 10:00:00" mysql-bin.000001 | mysql petrosoftdb
```

---

## Maintenance Queries

### Performance Monitoring
```sql
-- Check table sizes
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'petrosoftdb'
ORDER BY (data_length + index_length) DESC;

-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- Analyze table performance
ANALYZE TABLE sales, vouchers, ledger_entries;
```

### Data Cleanup
```sql
-- Archive old notifications
DELETE FROM notifications 
WHERE status = 'SENT' 
AND created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- Optimize tables
OPTIMIZE TABLE sales, vouchers, ledger_entries, notifications;
```

---

This database schema provides a robust foundation for the Petrosoft application, supporting all business requirements while maintaining data integrity and performance.
