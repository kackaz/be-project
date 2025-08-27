# C4 Architecture Diagram

This document contains the C4 architecture diagrams for the BE Project using Mermaid notation, showing the system architecture at different levels.

## System Context Diagram (Level 1)

```mermaid
C4Context
    title System Context Diagram for LBK Banking System

    Person(customer, "Bank Customer", "A customer who wants to manage their bank account and transfer money")
    Person(admin, "Bank Administrator", "A bank admin who manages the system")
    
    System(bankingSystem, "LBK Banking System", "Allows customers to manage accounts, transfer money, and view transaction history")
    
    System_Ext(database, "SQLite Database", "Stores user accounts, bank accounts, and transaction data")
    System_Ext(jwtService, "JWT Authentication", "Handles user authentication and authorization")
    
    Rel(customer, bankingSystem, "Uses", "HTTPS/JSON")
    Rel(admin, bankingSystem, "Administers", "HTTPS/JSON")
    Rel(bankingSystem, database, "Reads from and writes to", "JDBC")
    Rel(bankingSystem, jwtService, "Validates tokens", "JWT")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

## Container Diagram (Level 2)

```mermaid
C4Container
    title Container Diagram for LBK Banking System

    Person(customer, "Bank Customer", "A customer using the banking system")
    
    Container_Boundary(c1, "LBK Banking System") {
        Container(webApi, "Banking API", "Spring Boot", "Provides banking functionality via JSON/REST API")
        Container(authService, "Authentication Service", "Spring Security + JWT", "Handles user authentication and authorization")
        Container(transferService, "Transfer Service", "Java", "Handles money transfer operations")
        Container(accountService, "Account Service", "Java", "Manages bank account operations")
    }
    
    ContainerDb(database, "Database", "SQLite", "Stores user accounts, bank accounts, and transactions")
    
    Rel(customer, webApi, "Makes API calls to", "HTTPS")
    Rel(webApi, authService, "Validates users using")
    Rel(webApi, transferService, "Makes transfer requests using")
    Rel(webApi, accountService, "Manages accounts using")
    Rel(authService, database, "Reads from and writes to", "JDBC")
    Rel(transferService, database, "Reads from and writes to", "JDBC")
    Rel(accountService, database, "Reads from and writes to", "JDBC")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

## Component Diagram (Level 3) - Authentication Module

```mermaid
C4Component
    title Component Diagram for Authentication Module

    Person(customer, "Bank Customer")
    
    Container_Boundary(c1, "Banking API - Authentication Module") {
        Component(authController, "Authentication Controller", "Spring REST Controller", "Handles authentication HTTP requests")
        Component(authService, "Authentication Service", "Spring Service", "Manages user registration, login, and profile")
        Component(userRepository, "User Repository", "Spring Repository", "Provides user data access")
        Component(jwtUtil, "JWT Utility", "JWT Library", "Generates and validates JWT tokens")
        Component(passwordEncoder, "Password Encoder", "BCrypt", "Encrypts and validates passwords")
    }
    
    Container_Boundary(c2, "Transfer Module") {
        Component(transferService, "Transfer Service", "Spring Service", "Handles money transfers")
        Component(accountRepository, "Account Repository", "Spring Repository", "Provides account data access")
    }
    
    ContainerDb(database, "SQLite Database", "Database", "Stores all application data")
    
    Rel(customer, authController, "Makes requests to", "HTTPS")
    Rel(authController, authService, "Uses")
    Rel(authService, userRepository, "Uses")
    Rel(authService, accountRepository, "Creates accounts using")
    Rel(authService, jwtUtil, "Generates tokens using")
    Rel(authService, passwordEncoder, "Encrypts passwords using")
    Rel(userRepository, database, "Reads from and writes to", "JDBC")
    Rel(accountRepository, database, "Reads from and writes to", "JDBC")
    Rel(authService, transferService, "Notifies for account creation")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

## Component Diagram (Level 3) - Transfer Module

```mermaid
C4Component
    title Component Diagram for Transfer Module

    Person(customer, "Bank Customer")
    
    Container_Boundary(c1, "Banking API - Transfer Module") {
        Component(transferController, "Transfer Controller", "Spring REST Controller", "Handles transfer HTTP requests")
        Component(transferService, "Transfer Service", "Spring Service", "Processes money transfers and account operations")
        Component(accountRepository, "Account Repository", "Spring Repository", "Provides account data access")
        Component(transferRepository, "Transfer Repository", "Spring Repository", "Provides transfer transaction data access")
        Component(jwtValidator, "JWT Validator", "JWT Library", "Validates authentication tokens")
    }
    
    Container_Boundary(c2, "Authentication Module") {
        Component(authService, "Authentication Service", "Spring Service", "Provides user authentication")
    }
    
    ContainerDb(database, "SQLite Database", "Database", "Stores accounts and transfer data")
    
    Rel(customer, transferController, "Makes transfer requests to", "HTTPS")
    Rel(transferController, jwtValidator, "Validates tokens using")
    Rel(transferController, transferService, "Processes transfers using")
    Rel(transferService, accountRepository, "Manages accounts using")
    Rel(transferService, transferRepository, "Records transfers using")
    Rel(accountRepository, database, "Reads from and writes to", "JDBC")
    Rel(transferRepository, database, "Reads from and writes to", "JDBC")
    Rel(transferController, authService, "Gets user info from")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

## Architecture Overview

### System Components

**🏦 LBK Banking System**
- **Technology Stack**: Spring Boot 3.5.4, Java 21
- **Database**: SQLite with JDBC
- **Authentication**: JWT-based token authentication
- **API Style**: REST API with JSON

**📋 Core Modules:**

1. **Authentication Module**
   - User registration and login
   - JWT token generation and validation
   - Password encryption with BCrypt
   - Automatic bank account creation

2. **Transfer Module**
   - Money transfer between accounts
   - Account balance management
   - Transaction history tracking
   - Transfer validation and processing

3. **Account Module**
   - Bank account management
   - Balance inquiries
   - Account information updates
   - Membership level management

### Key Architectural Patterns

**🏗️ Layered Architecture:**
- **Controller Layer**: REST endpoints (`@RestController`)
- **Service Layer**: Business logic (`@Service`)
- **Repository Layer**: Data access (`@Repository`)
- **Model Layer**: Data entities and DTOs

**🔐 Security Architecture:**
- JWT-based stateless authentication
- Bearer token validation for protected endpoints
- Password encryption using BCrypt
- Request/Response validation

**💾 Data Architecture:**
- SQLite database with JDBC connectivity
- Auto-increment primary keys
- Foreign key relationships
- Transaction logging for audit trails

### Integration Points

**🔗 Inter-Module Communication:**
- Authentication service creates bank accounts automatically
- Transfer service validates user authentication
- All modules share the same database instance
- JWT tokens are validated across all protected endpoints

**📡 External Interfaces:**
- REST API endpoints for client applications
- Database JDBC connections
- JWT token validation service
- Password encryption service

This C4 architecture provides a comprehensive view of the LBK Banking System, showing how the different components interact and the overall system structure at multiple levels of detail.
