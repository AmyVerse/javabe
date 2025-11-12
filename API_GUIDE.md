# WiPay Banking API - Complete Guide

## Overview

WiPay is a minimal OOP banking API built with Javalin and Redis. It provides complete banking functionality with users, accounts, transfers, notifications, and reports.

## Architecture

```
src/main/java/app/
├── WiPayApi.java              (Main API - all endpoints)
├── model/
│   ├── User.java              (User entity with balance)
│   ├── Account.java           (Bank account with linked accounts)
│   ├── Bank.java              (Bank details)
│   └── Transaction.java       (Transaction history)
├── util/
│   └── IdGenerator.java       (UUID-based ID generation)
└── exception/
    └── (Custom exception classes)
```

## Features Implemented

### 1. User Database (firstname@wipay ID)

- **Create User**: `POST /api/users`
- **Get All Users**: `GET /api/users`
- **Get User**: `GET /api/users/:userId`

### 2. Bank Accounts / Linked Accounts

- **Create Account**: `POST /api/accounts`
- **Get All Accounts**: `GET /api/accounts`
- **Get User Accounts**: `GET /api/accounts/:userId`

### 3. Contacts (All Users)

- **Get Contacts**: `GET /api/contacts`
  - Returns all users with name and payment ID

### 4. Transactions with Balance Management

- **Transfer Money**: `POST /api/transfer`
  - Deducts from sender
  - Adds to receiver
  - Auto-generates notifications for both users

### 5. Notifications

- **Get Notifications**: `GET /api/notifications/:userId`
  - Shows all notifications linked to user ID
  - Triggered on transactions

### 6. Reports & Analytics

- **Get Reports**: `GET /api/reports/:userId`
  - Total transactions
  - Total sent amount
  - Total received amount
  - Current balance
  - Full transaction history

---

## API Endpoints

### Health Check

```
GET /api/ping
Response: { "status": "OK" }
```

### User Endpoints

#### Create User

```
POST /api/users
Content-Type: application/json

Request:
{
  "firstName": "John",
  "lastName": "Doe",
  "balance": 1000
}

Response (201):
{
  "id": "john@wipay",
  "firstName": "John",
  "lastName": "Doe",
  "balance": 1000,
  "createdAt": "2025-11-13T03:50:00"
}
```

#### Get All Users

```
GET /api/users

Response (200):
[
  {
    "id": "john@wipay",
    "firstName": "John",
    "lastName": "Doe",
    "balance": 1000,
    "createdAt": "2025-11-13T03:50:00"
  },
  ...
]
```

#### Get Specific User

```
GET /api/users/john@wipay

Response (200):
{
  "id": "john@wipay",
  "firstName": "John",
  "lastName": "Doe",
  "balance": 1000,
  "createdAt": "2025-11-13T03:50:00"
}
```

### Account Endpoints

#### Create Account

```
POST /api/accounts
Content-Type: application/json

Request:
{
  "userId": "john@wipay",
  "accountNumber": "ACC001",
  "bankName": "State Bank",
  "balance": 5000
}

Response (201):
{
  "id": "uuid-string",
  "userId": "john@wipay",
  "accountNumber": "ACC001",
  "bankName": "State Bank",
  "balance": 5000,
  "createdAt": "2025-11-13T03:50:00"
}
```

#### Get All Accounts

```
GET /api/accounts

Response (200):
[
  {
    "id": "uuid-string",
    "userId": "john@wipay",
    "accountNumber": "ACC001",
    "bankName": "State Bank",
    "balance": 5000,
    "createdAt": "2025-11-13T03:50:00"
  },
  ...
]
```

#### Get User's Accounts

```
GET /api/accounts/john@wipay

Response (200):
[
  {
    "id": "uuid-string",
    "userId": "john@wipay",
    "accountNumber": "ACC001",
    "bankName": "State Bank",
    "balance": 5000
  }
]
```

### Contacts Endpoint

#### Get All Contacts

```
GET /api/contacts

Response (200):
[
  {
    "id": "john@wipay",
    "name": "John Doe",
    "paymentId": "john@wipay"
  },
  {
    "id": "jane@wipay",
    "name": "Jane Smith",
    "paymentId": "jane@wipay"
  },
  ...
]
```

### Transfer Endpoint

#### Send Money

```
POST /api/transfer
Content-Type: application/json

Request:
{
  "fromUserId": "john@wipay",
  "toUserId": "jane@wipay",
  "amount": 500
}

Response (201):
{
  "id": "txn-uuid",
  "fromUserId": "john@wipay",
  "toUserId": "jane@wipay",
  "amount": 500,
  "timestamp": "2025-11-13T03:52:00",
  "status": "completed"
}

Effects:
- john@wipay balance: -500
- jane@wipay balance: +500
- Notifications created for both users
```

### Notifications Endpoint

#### Get User Notifications

```
GET /api/notifications/john@wipay

Response (200):
[
  {
    "message": "sent ₹500.00 to jane@wipay",
    "timestamp": "2025-11-13T03:52:00"
  },
  {
    "message": "Received ₹300.00 from jane@wipay",
    "timestamp": "2025-11-13T03:53:00"
  },
  ...
]
```

### Reports Endpoint

#### Get User Reports

```
GET /api/reports/john@wipay

Response (200):
{
  "userId": "john@wipay",
  "totalTransactions": 5,
  "totalSent": 1000,
  "totalReceived": 2500,
  "currentBalance": 3500,
  "transactions": [
    {
      "id": "txn-1",
      "fromUserId": "john@wipay",
      "toUserId": "jane@wipay",
      "amount": 500,
      "timestamp": "2025-11-13T03:52:00",
      "status": "completed"
    },
    ...
  ]
}
```

---

## Data Storage

### Redis Keys

- `wipay:users` - Hash of all users by payment ID
- `wipay:accounts` - Hash of all accounts
- `wipay:transactions` - Hash of all transactions
- `wipay:notifications:{userId}` - List of notifications for user

### JSON Format in Redis

All data is stored as JSON strings for easy serialization/deserialization with Gson.

---

## Error Responses

### 404 Not Found

```json
{
  "error": "User not found"
}
```

### 400 Bad Request

```json
{
  "error": "Insufficient balance"
}
```

---

## Running the API

### Prerequisites

- Java 17+
- Redis running locally
- Maven

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/backend-0.1.0-fat.jar
```

### Access

- Base URL: `http://localhost:8080`
- Health Check: `http://localhost:8080/api/ping`

---

## Example Usage Flow

### 1. Create Two Users

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","balance":1000}'

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith","balance":2000}'
```

### 2. View All Contacts

```bash
curl http://localhost:8080/api/contacts
```

### 3. Send Transfer

```bash
curl -X POST http://localhost:8080/api/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromUserId":"john@wipay","toUserId":"jane@wipay","amount":500}'
```

### 4. Check Notifications

```bash
curl http://localhost:8080/api/notifications/john@wipay
```

### 5. View Reports

```bash
curl http://localhost:8080/api/reports/john@wipay
```

---

## Technical Stack

- **Framework**: Javalin 6.7.0
- **Database**: Redis (Jedis client)
- **JSON**: Gson
- **Build**: Maven with Shade plugin for fat JAR
- **Server**: Jetty 11.0.25
- **Java Target**: JDK 17+

---

## Security Notes

⚠️ **This is a minimal implementation without production-level security.**

- No encryption for passwords
- No authentication/authorization
- Plain JSON storage in Redis
- For production: add proper authentication, encryption, and validation

---

## Future Enhancements

1. User authentication with JWT tokens
2. Password hashing (BCrypt)
3. Transaction status workflow (PENDING → SUCCESS/FAILED)
4. Rate limiting
5. Database persistence (PostgreSQL)
6. Email/SMS notifications
7. Mobile app integration
8. Analytics and dashboards
