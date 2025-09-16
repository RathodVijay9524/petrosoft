# Petrosoft Deployment Guide

## Overview

This guide provides comprehensive instructions for deploying the Petrosoft application in different environments, from development to production.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Development Environment](#development-environment)
3. [Production Deployment](#production-deployment)
4. [Database Setup](#database-setup)
5. [Configuration Management](#configuration-management)
6. [Monitoring & Logging](#monitoring--logging)
7. [Security Considerations](#security-considerations)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### System Requirements

**Minimum Requirements:**
- CPU: 2 cores
- RAM: 4GB
- Storage: 20GB SSD
- Network: 100 Mbps

**Recommended Requirements:**
- CPU: 4+ cores
- RAM: 8GB+
- Storage: 50GB+ SSD
- Network: 1 Gbps

### Software Requirements

**Required Software:**
- Java 24 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher (for development)
- Git (for version control)

**Optional Software:**
- Docker & Docker Compose
- Nginx (for reverse proxy)
- Redis (for caching)
- ELK Stack (for logging)

---

## Development Environment

### 1. Local Development Setup

**Step 1: Clone Repository**
```bash
git clone <repository-url>
cd petrosoft
```

**Step 2: Install Dependencies**
```bash
# Install Java 24 (if not already installed)
# Download from: https://adoptium.net/

# Install Maven (if not already installed)
# Download from: https://maven.apache.org/download.cgi

# Install MySQL 8.0
# Download from: https://dev.mysql.com/downloads/mysql/
```

**Step 3: Database Setup**
```sql
-- Create database
CREATE DATABASE petrosoftdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional, for security)
CREATE USER 'petrosoft_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON petrosoftdb.* TO 'petrosoft_user'@'localhost';
FLUSH PRIVILEGES;
```

**Step 4: Configuration**
Create `application-dev.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/petrosoftdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=petrosoft_user
spring.datasource.password=secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8081
server.servlet.context-path=/

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-change-this-in-production
jwt.expiration=86400000

# Logging
logging.level.com.vijay.petrosoft=DEBUG
logging.level.org.springframework.security=DEBUG

# Twilio SMS (Optional)
twilio.account.sid=${TWILIO_ACCOUNT_SID:}
twilio.auth.token=${TWILIO_AUTH_TOKEN:}
twilio.phone.number=${TWILIO_PHONE_NUMBER:}

# Email Configuration (Optional)
spring.mail.host=${SMTP_HOST:}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME:}
spring.mail.password=${SMTP_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Step 5: Run Application**
```bash
# Set development profile
export SPRING_PROFILES_ACTIVE=dev

# Run application
mvn spring-boot:run

# Or build and run
mvn clean package
java -jar target/petrosoft-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 2. IDE Configuration

**IntelliJ IDEA:**
1. Import as Maven project
2. Set Project SDK to Java 24
3. Configure Run Configuration:
   - Main class: `com.vijay.petrosoft.PetrosoftApplication`
   - VM options: `-Dspring.profiles.active=dev`
   - Program arguments: `--server.port=8081`

**Eclipse:**
1. Import as Existing Maven Project
2. Right-click project → Run As → Java Application
3. Configure main class: `com.vijay.petrosoft.PetrosoftApplication`

**VS Code:**
1. Install Java Extension Pack
2. Open project folder
3. Use Spring Boot Dashboard to run application

---

## Production Deployment

### 1. Build for Production

**Create Production Build:**
```bash
# Clean and package
mvn clean package -Pproduction

# Verify JAR file
ls -la target/petrosoft-0.0.1-SNAPSHOT.jar
```

### 2. Production Configuration

**Create `application-prod.properties`:**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:petrosoftdb}?useSSL=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME:petrosoft_user}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Configuration
server.port=${SERVER_PORT:8081}
server.servlet.context-path=/
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Logging Configuration
logging.level.com.vijay.petrosoft=INFO
logging.level.org.springframework.security=WARN
logging.level.org.hibernate.SQL=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=/var/log/petrosoft/application.log
logging.file.max-size=100MB
logging.file.max-history=30

# Management Endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.endpoint.info.enabled=true

# Twilio SMS Configuration
twilio.account.sid=${TWILIO_ACCOUNT_SID}
twilio.auth.token=${TWILIO_AUTH_TOKEN}
twilio.phone.number=${TWILIO_PHONE_NUMBER}

# Email Configuration
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Payment Gateway (Optional)
razorpay.enabled=${RAZORPAY_ENABLED:false}
razorpay.key.id=${RAZORPAY_KEY_ID:}
razorpay.key.secret=${RAZORPAY_KEY_SECRET:}
```

### 3. Environment Variables

**Required Environment Variables:**
```bash
# Database
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=petrosoftdb
export DB_USERNAME=petrosoft_user
export DB_PASSWORD=your-secure-password

# JWT
export JWT_SECRET=your-super-secure-jwt-secret-key

# External Services (Optional)
export TWILIO_ACCOUNT_SID=your-twilio-sid
export TWILIO_AUTH_TOKEN=your-twilio-token
export TWILIO_PHONE_NUMBER=your-twilio-phone
export SMTP_HOST=smtp.gmail.com
export SMTP_USERNAME=your-email
export SMTP_PASSWORD=your-app-password
```

### 4. Deployment Options

#### Option A: Direct JAR Deployment

**Step 1: Prepare Server**
```bash
# Create application directory
sudo mkdir -p /opt/petrosoft
sudo chown $USER:$USER /opt/petrosoft

# Create logs directory
sudo mkdir -p /var/log/petrosoft
sudo chown $USER:$USER /var/log/petrosoft
```

**Step 2: Deploy Application**
```bash
# Copy JAR file
cp target/petrosoft-0.0.1-SNAPSHOT.jar /opt/petrosoft/

# Create startup script
cat > /opt/petrosoft/start.sh << 'EOF'
#!/bin/bash
export SPRING_PROFILES_ACTIVE=prod
export JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC"
java $JAVA_OPTS -jar petrosoft-0.0.1-SNAPSHOT.jar
EOF

chmod +x /opt/petrosoft/start.sh
```

**Step 3: Create Systemd Service**
```bash
sudo cat > /etc/systemd/system/petrosoft.service << 'EOF'
[Unit]
Description=Petrosoft Application
After=network.target mysql.service

[Service]
Type=simple
User=petrosoft
Group=petrosoft
WorkingDirectory=/opt/petrosoft
ExecStart=/opt/petrosoft/start.sh
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=petrosoft

[Install]
WantedBy=multi-user.target
EOF

# Create user
sudo useradd -r -s /bin/false petrosoft
sudo chown -R petrosoft:petrosoft /opt/petrosoft
sudo chown -R petrosoft:petrosoft /var/log/petrosoft

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable petrosoft
sudo systemctl start petrosoft
```

#### Option B: Docker Deployment

**Create Dockerfile:**
```dockerfile
FROM openjdk:24-jdk-slim

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create application directory
WORKDIR /app

# Copy JAR file
COPY target/petrosoft-0.0.1-SNAPSHOT.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Create docker-compose.yml:**
```yaml
version: '3.8'

services:
  petrosoft:
    build: .
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_NAME=petrosoftdb
      - DB_USERNAME=petrosoft_user
      - DB_PASSWORD=secure_password
      - JWT_SECRET=your-jwt-secret
    depends_on:
      mysql:
        condition: service_healthy
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: petrosoftdb
      MYSQL_USER: petrosoft_user
      MYSQL_PASSWORD: secure_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10
    restart: unless-stopped

volumes:
  mysql_data:
```

**Deploy with Docker Compose:**
```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f petrosoft

# Stop services
docker-compose down
```

---

## Database Setup

### 1. Production Database Configuration

**MySQL Configuration (`/etc/mysql/mysql.conf.d/mysqld.cnf`):**
```ini
[mysqld]
# Basic Settings
user = mysql
default-storage-engine = InnoDB
socket = /var/run/mysqld/mysqld.sock
pid-file = /var/run/mysqld/mysqld.pid

# Network
bind-address = 0.0.0.0
port = 3306

# Character Set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# InnoDB Settings
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_file_per_table = 1

# Connection Settings
max_connections = 200
max_connect_errors = 10000

# Query Cache
query_cache_type = 1
query_cache_size = 64M

# Logging
log-error = /var/log/mysql/error.log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# Security
local-infile = 0
```

### 2. Database Backup Strategy

**Automated Backup Script:**
```bash
#!/bin/bash
# /opt/scripts/backup-database.sh

BACKUP_DIR="/opt/backups/mysql"
DB_NAME="petrosoftdb"
DB_USER="backup_user"
DB_PASS="backup_password"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Create backup directory
mkdir -p $BACKUP_DIR

# Create backup
mysqldump -u $DB_USER -p$DB_PASS \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    $DB_NAME > $BACKUP_DIR/petrosoft_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/petrosoft_$DATE.sql

# Remove old backups
find $BACKUP_DIR -name "petrosoft_*.sql.gz" -mtime +$RETENTION_DAYS -delete

echo "Backup completed: petrosoft_$DATE.sql.gz"
```

**Cron Job for Automated Backups:**
```bash
# Add to crontab (crontab -e)
0 2 * * * /opt/scripts/backup-database.sh >> /var/log/backup.log 2>&1
```

---

## Configuration Management

### 1. Environment-Specific Configuration

**Development Environment:**
```properties
# application-dev.properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.com.vijay.petrosoft=DEBUG
```

**Production Environment:**
```properties
# application-prod.properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.vijay.petrosoft=INFO
```

### 2. External Configuration

**Using Environment Variables:**
```bash
# Database
export DB_HOST=prod-db-server
export DB_PASSWORD=super-secure-password

# External Services
export TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxx
export SMTP_PASSWORD=app-specific-password
```

**Using Configuration Files:**
```bash
# Create external config file
cat > /opt/petrosoft/application-external.properties << 'EOF'
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
twilio.account.sid=${TWILIO_ACCOUNT_SID}
EOF

# Reference in startup
java -jar app.jar --spring.config.location=classpath:/application.properties,file:/opt/petrosoft/application-external.properties
```

---

## Monitoring & Logging

### 1. Application Monitoring

**Health Check Endpoint:**
```bash
# Check application health
curl http://localhost:8081/actuator/health

# Response
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 250790191104,
        "threshold": 10485760
      }
    }
  }
}
```

**Metrics Endpoint:**
```bash
# Get application metrics
curl http://localhost:8081/actuator/metrics

# Specific metrics
curl http://localhost:8081/actuator/metrics/jvm.memory.used
curl http://localhost:8081/actuator/metrics/http.server.requests
```

### 2. Log Management

**Log Rotation Configuration:**
```properties
# application-prod.properties
logging.file.name=/var/log/petrosoft/application.log
logging.file.max-size=100MB
logging.file.max-history=30
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

**Log Rotation Script:**
```bash
# /etc/logrotate.d/petrosoft
/var/log/petrosoft/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 petrosoft petrosoft
    postrotate
        systemctl reload petrosoft
    endscript
}
```

### 3. Monitoring Setup

**Prometheus Configuration (Optional):**
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'petrosoft'
    static_configs:
      - targets: ['localhost:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
```

---

## Security Considerations

### 1. Network Security

**Firewall Configuration:**
```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8081/tcp  # Application (if direct access)
sudo ufw enable
```

**Nginx Reverse Proxy:**
```nginx
# /etc/nginx/sites-available/petrosoft
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /actuator/ {
        deny all;
    }
}
```

### 2. Application Security

**Security Headers:**
```properties
# application-prod.properties
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict
```

**Database Security:**
```sql
-- Create dedicated application user
CREATE USER 'petrosoft_app'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON petrosoftdb.* TO 'petrosoft_app'@'localhost';

-- Remove unnecessary privileges
REVOKE ALL PRIVILEGES ON *.* FROM 'petrosoft_app'@'localhost';
FLUSH PRIVILEGES;
```

### 3. SSL/TLS Configuration

**Generate SSL Certificate:**
```bash
# Using Let's Encrypt (recommended)
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com

# Or self-signed certificate
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/ssl/private/petrosoft.key \
    -out /etc/ssl/certs/petrosoft.crt
```

---

## Troubleshooting

### 1. Common Issues

**Application Won't Start:**
```bash
# Check logs
sudo journalctl -u petrosoft -f

# Check Java version
java -version

# Check port availability
sudo netstat -tulpn | grep :8081
```

**Database Connection Issues:**
```bash
# Test database connection
mysql -h localhost -u petrosoft_user -p petrosoftdb

# Check MySQL status
sudo systemctl status mysql

# Check MySQL logs
sudo tail -f /var/log/mysql/error.log
```

**Memory Issues:**
```bash
# Check memory usage
free -h
top -p $(pgrep java)

# Adjust JVM settings
export JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC"
```

### 2. Performance Tuning

**JVM Tuning:**
```bash
# Production JVM settings
export JAVA_OPTS="-server \
    -Xms2g \
    -Xmx4g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat"
```

**Database Tuning:**
```sql
-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- Optimize tables
OPTIMIZE TABLE accounts, vouchers, sales;
```

### 3. Backup and Recovery

**Database Recovery:**
```bash
# Restore from backup
gunzip petrosoft_20240115_020000.sql.gz
mysql -u root -p petrosoftdb < petrosoft_20240115_020000.sql
```

**Application Rollback:**
```bash
# Stop current version
sudo systemctl stop petrosoft

# Restore previous version
cp /opt/petrosoft/backup/petrosoft-previous.jar /opt/petrosoft/petrosoft-0.0.1-SNAPSHOT.jar

# Start application
sudo systemctl start petrosoft
```

---

## Maintenance

### 1. Regular Maintenance Tasks

**Daily Tasks:**
- Monitor application logs
- Check disk space
- Verify backup completion
- Review security logs

**Weekly Tasks:**
- Update system packages
- Review performance metrics
- Clean old log files
- Database optimization

**Monthly Tasks:**
- Security updates
- Performance review
- Capacity planning
- Disaster recovery testing

### 2. Update Procedure

**Application Updates:**
```bash
# 1. Backup current version
cp /opt/petrosoft/petrosoft-0.0.1-SNAPSHOT.jar /opt/petrosoft/backup/

# 2. Stop application
sudo systemctl stop petrosoft

# 3. Deploy new version
cp new-version.jar /opt/petrosoft/petrosoft-0.0.1-SNAPSHOT.jar

# 4. Start application
sudo systemctl start petrosoft

# 5. Verify deployment
curl http://localhost:8081/actuator/health
```

---

This deployment guide provides comprehensive instructions for deploying the Petrosoft application in various environments. Follow the appropriate section based on your deployment requirements and environment.
