# BE Project

Spring Boot application with Java 21 and Spring Boot 3.5.4

## Prerequisites
- Java 21
- Maven 3.6+

## Running the application

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

### Authentication Endpoints

#### POST `/register`
Register a new user and automatically create a bank account.

Request body:
```json
{
  "email": "somchai@example.com",
  "password": "password123",
  "firstname": "สมชาย",
  "lastname": "ใจดี",
  "phone": "081-234-5678",
  "birthday": "15/6/2566"
}
```

Response:
```json
{
  "id": 1,
  "email": "somchai@example.com"
}
```

#### POST `/login`
Login with email and password.

Request body:
```json
{
  "email": "somchai@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### GET `/me`
Get current user information (requires Authorization header).

Headers:
```
Authorization: Bearer <token>
```

Response:
```json
{
  "id": 1,
  "email": "somchai@example.com",
  "firstname": "สมชาย",
  "lastname": "ใจดี",
  "phone": "081-234-5678",
  "birthday": "15/6/2566"
}
```

### Money Transfer Endpoints

#### POST `/api/transfer/send`
Send money to another account (requires Authorization header).

Headers:
```
Authorization: Bearer <token>
```

Request body:
```json
{
  "toAccountNumber": "LBK002345",
  "amount": 1000.0,
  "note": "Transfer to friend"
}
```

Response:
```json
{
  "transactionId": "LBK123456",
  "status": "SUCCESS",
  "amount": 1000.0,
  "toAccountNumber": "LBK002345",
  "toAccountName": "นางสาว มารี สวยงาม",
  "timestamp": "2024-01-19T10:30:00"
}
```

#### GET `/api/transfer/account`
Get current user's account information (requires Authorization header).

Headers:
```
Authorization: Bearer <token>
```

Response:
```json
{
  "accountNumber": "LBK001234",
  "accountName": "สมชาย ใจดี",
  "balance": 15420.0,
  "membershipLevel": "Gold"
}
```

#### GET `/api/transfer/history`
Get user's transfer history (requires Authorization header).

Headers:
```
Authorization: Bearer <token>
```

Response:
```json
[
  {
    "id": 1,
    "fromAccountNumber": "LBK001234",
    "toAccountNumber": "LBK002345",
    "toAccountName": "นางสาว มารี สวยงาม",
    "amount": 1000.0,
    "status": "SUCCESS",
    "transactionId": "LBK123456",
    "createdAt": "2024-01-19T10:30:00",
    "userId": 1
  }
]
```

#### GET `/api/transfer/accounts`
Get list of all accounts for recipient selection (requires Authorization header).

Headers:
```
Authorization: Bearer <token>
```

Response:
```json
[
  {
    "id": 1,
    "accountNumber": "LBK001234",
    "accountName": "สมชาย ใจดี",
    "balance": 15420.0,
    "membershipLevel": "Gold",
    "userId": 1
  },
  {
    "id": 2,
    "accountNumber": "LBK002345",
    "accountName": "นางสาว มารี สวยงาม",
    "balance": 8500.0,
    "membershipLevel": "Gold",
    "userId": 2
  }
]
```

### General Endpoints

#### GET `/`
Returns "Hello World" JSON response.

Response:
```json
{
  "message": "Hello World"
}
```

## Features

- **User Authentication**: Register, login, and JWT token-based authentication
- **Automatic Account Creation**: Bank account automatically created upon user registration
- **Money Transfer**: Send money between accounts with real-time balance updates
- **Transaction History**: View complete transfer history
- **Account Management**: View account details and balance
- **Recipient Selection**: Get list of available accounts for transfers
- **Security**: All transfer endpoints require authentication
- **Data Persistence**: SQLite database for data storage

## Database Schema

### Users Table
- id (Primary Key)
- email (Unique)
- password (Encrypted)
- firstname, lastname, phone, birthday

### Accounts Table  
- id (Primary Key)
- account_number (Unique, format: LBK######)
- account_name
- balance (Default: 15,420.00)
- membership_level (Default: "Gold")
- user_id (Foreign Key)

### Transfers Table
- id (Primary Key)
- from_account_number
- to_account_number  
- to_account_name
- amount
- status (SUCCESS/PENDING/FAILED)
- transaction_id (Unique, format: LBK######)
- created_at
- user_id (Foreign Key)
