<div align="center">

  <!-- Hero Banner -->
  <img src="src/main/resources/static/images/logo.png" alt="NexaSupply Logo" width="200" style="border-radius: 1px; margin-bottom: 0px;" />
  
  # ⚙️ NexaSupply — Backend RESTful API Engine
  ### ⚡ Smart B2B Wholesale & Inventory Distribution Service ⚡

  <p align="center">
    <b>High-performance, secure, and resilient Spring Boot 3 enterprise backend engine powering supply chain logistics, automated credit limits, real-time AI assistance, and Jasper PDF invoicing.</b>
  </p>

  <!-- Tech Stack Badges -->
  <p align="center">
    <a href="https://spring.io/projects/spring-boot">
      <img src="https://img.shields.io/badge/Framework-Spring%20Boot%203.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
    </a>
    <a href="https://www.oracle.com/java/">
      <img src="https://img.shields.io/badge/JDK-Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
    </a>
    <a href="https://www.mysql.com/">
      <img src="https://img.shields.io/badge/Database-Aiven%20Cloud%20MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
    </a>
    <a href="https://jwt.io/">
      <img src="https://img.shields.io/badge/Security-Spring%20Security%20%2b%20JWT-E6155E?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
    </a>
    <a href="https://community.jaspersoft.com/">
      <img src="https://img.shields.io/badge/Reporting-Jaspersoft%207.0-025E8A?style=for-the-badge&logo=jasperreports&logoColor=white" alt="JasperReports" />
    </a>
    <a href="https://ai.google.dev/">
      <img src="https://img.shields.io/badge/AI Engine-Google%20Gemini%20API-8E44AD?style=for-the-badge&logo=googlegemini&logoColor=white" alt="Gemini AI" />
    </a>
  </p>

  <!-- Key Metrics -->
  <p align="center">
    <img src="https://img.shields.io/badge/Architecture-Clean%20%2f%20Layered-2E8B57?style=flat-square&logo=architecture" alt="Architecture" />
    <img src="https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apachemaven" alt="Maven" />
    <img src="https://img.shields.io/badge/Status-Production--Ready-brightgreen?style=flat-square" alt="Status" />
    <img src="https://img.shields.io/badge/License-Academic%20Project-blue?style=flat-square" alt="License" />
  </p>

  <hr width="80%" />
</div>

---

## 📌 Technical Overview

The **NexaSupply Backend** service functions as the core API server for the NexaSupply ecosystem. Built with **Java 21** and **Spring Boot 3**, it delivers enterprise-level transaction safety, stateless dynamic authentication using **Spring Security & JWT**, background processing pipelines (`@Async`), dynamic Jasper PDF invoice compilation, and intelligent rule-assisted query processing powered by the **Google Gemini API**.

---

## 🏗️ Architectural Package Breakdown

The application follows a clean, modular, and decoupled **Layered Architecture**:

```text
lk.ijse.NexaSupply
├── 📂 constant          # Shared System Enums & Generic Response Contracts (CommonResponse)
├── 📂 controller        # REST Controllers exposing business logic endpoints
├── 📂 dto               # Encapsulated Data Transfer Objects organized by domain
│   ├── auth / chatbot / dashboard / driver / order / payment
│   └── product / report / restock / shipment / supplier / system
├── 📂 entity            # JPA Database Domain Models & Mappings
├── 📂 enumeration       # System State Enums (Roles, Statuses, Units, Ledger Types)
├── 📂 exception         # Centralized Global Exception Handlers (AppExceptionHandler)
├── 📂 repository        # Spring Data JPA Interfaces for Database Operations
├── 📂 security          # Custom UserDetails, JWT Authentication Filters & Configs
├── 📂 service           # Business Logic Interfaces & Implementation Details
└── 📂 util              # Helper Utilities & Security Context Wrappers
```

---

## 🗄️ Relational Database Schema & Entities

The persistence layer integrates Spring Data JPA with an **Aiven Cloud MySQL Server** hosting 16 core domain entities:

| Category | Domain Entities |
| :--- | :--- |
| **User & Access** | `User`, `PasswordResetOtp`, `AuditLog`, `Notification` |
| **Product & Inventory** | `Product`, `Category`, `Supplier`, `Restock`, `RestockDetail` |
| **Sales & Operations** | `Order`, `OrderProduct`, `Payment`, `CreditLedger` |
| **Logistics & Support** | `Driver`, `Shipment`, `ChatLog` |

---

## 🛠️ Technology Stack & Dependencies

* **Language & Framework:** Java 21 (LTS), Spring Boot 3.x Starter Web MVC
* **Security Layer:** Spring Security with stateless JJWT (`jjwt-api 0.12.3`)
* **Persistence & ORM:** Spring Data JPA + Hibernate ORM
* **Cloud Database:** MySQL Connector/J connected to **Aiven Cloud MySQL**
* **Reporting Engine:** Jaspersoft Framework (`jasperreports 7.0.3` PDF rendering)
* **AI & Intelligence:** Google Gemini API Integration (`AiToolsService`, `ChatService`)
* **Utilities:** Project Lombok, Jakarta Bean Validation, JavaMailSender

---

## 🌟 Key Engineering Capabilities

* **Stateless JWT Security & RBAC:** Multi-role access management (`ROLE_ADMIN`, `ROLE_RETAILER`) using custom authentication filters and annotation-driven security.
* **Smart Financial Ledger Management:** Automated credit limit evaluations and real-time ledger balance updates (`CreditLedgerService`).
* **AI-Assisted Customer Support:** Integrated Google Gemini REST services to process natural language queries and context-aware system support.
* **Dynamic Jasper Reports Generation:** Programmatic PDF invoicing and shipping order generation.
* **Asynchronous Mail Dispatches:** Non-blocking background workers handling notification emails, transactional receipts, and OTP verification codes.

---

## ⚙️ Configuration & Execution

### 1. Application Properties
Configure your credentials in `src/main/resources/application.properties`:

```properties
# Cloud Data Source Configuration
spring.datasource.url=jdbc:mysql://<aiven-cloud-host>:<port>/<db_name>?ssl-mode=REQUIRED
spring.datasource.username=<cloud-db-username>
spring.datasource.password=<cloud-db-password>

# Mail & SMTP Setup
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<system-email>
spring.mail.password=<app-password>

# AI Integration
gemini.api.key=<google-gemini-api-key>

# JWT Credentials
jwt.secret=<256-bit-jwt-secret-key>
```

### 2. Build & Launch

```bash
# Compile and package application
mvn clean package -DskipTests

# Run Spring Boot service
mvn spring-boot:run
```

The REST API engine will initialize on `http://localhost:8080`.

---

## 🎓 Academic Credentials

* **Coursework:** Final Comprehensive Software Project
* **Module:** Advanced API Development (AAD)
* **Institution:** Institute of Software Engineering (IJSE)
* **Developer:** Induni Palliyaguru
