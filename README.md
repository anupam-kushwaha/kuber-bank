<p align="center">
  <img src="logo.png" alt="Logo" width="200" height="200">
</p>

---

# 💰 Kuber Bank System

Kuber Bank is a lightweight **Java Spring Boot** banking system that supports basic banking operations including user management, account transactions, balance inquiry, and real-time email communication for key account events. The system also generates account statements and sends them as PDF files via email.

---



## 🚀 Features

### 🧑 User Management

* **Create User**: Register new customers with basic details.
* **Login**: Authenticate users securely using Spring Security (or similar mechanism).

### 💳 Account Operations

* **Balance Enquiry**: View current balance of the account.
* **Name Enquiry**: Fetch user details using account number.
* **Credit Account**: Deposit funds into an account.
* **Debit Account**: Withdraw funds from an account.
* **Transfer**: Transfer funds between accounts (with validation and transaction logging).

### 📄 Bank Statements

* **Generate Bank Statement**: Users can generate detailed account statements.
* **Email Bank Statement (PDF)**: Automatically generate and send PDF statements to the registered email.

### 📧 Email Notifications

* **Transaction Alerts**: Receive instant email alerts for any credit or debit operation.
* **PDF Statement Delivery**: Monthly or on-demand statement sent via email.

---

## 🛠️ Tech Stack

* **Java 17+**
* **Spring Boot 3.x**
* **Spring Data JPA (Hibernate)**
* **Spring Security** (optional, for login)
* **MySQL / PostgreSQL / H2** (any preferred RDBMS)
* **JavaMailSender** for email functionality
* **Thymeleaf / iText / JasperReports** (optional PDF generation libraries)

---

## 📬 Email Integration

Uses **Spring’s JavaMailSender** for sending:

* Transaction alerts (credit/debit)
* PDF statements to user's registered email

Ensure to configure your SMTP details in `application.properties`:

```properties
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your_email@example.com
spring.mail.password=your_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧾 API Endpoints (Sample)

| Method | Endpoint                            | Description               |
| ------ |-------------------------------------| ------------------------- |
| `POST` | `/api/user/create`                  | Create a new user         |
| `POST` | `/api/user/login`                   | User login                |
| `GET`  | `/api/accounts/balanceEnquiry` | Check account balance     |
| `GET`  | `/api/accounts/nameEnquiry`    | Name enquiry              |
| `POST` | `/api/accounts/creditAccount`  | Credit account            |
| `POST` | `/api/accounts/debitAccount`   | Debit account             |
| `POST` | `/api/accounts/transfer`            | Transfer between accounts |
| `GET`  | `/api/transaction/generateBankStatement`       | Generate bank statement   |
| `GET`  | `/api/transaction/emailBankStatement` | Email statement as PDF    |

---

## 🔒 Security & Validation

* Role-based access control (if implemented)
* Input validation and transaction integrity checks
* Secure password hashing (e.g., BCrypt)

---

## 🧪 Testing & Development

* Unit and integration tests with JUnit and Spring Boot Test
* Postman collection for API testing (optional)
* Docker support for easy deployment (optional)

---

## 📦 Setup Instructions

```bash
# Clone the repo
git clone https://github.com/your-username/kuber-bank.git
cd kuber-bank

# Run with Maven
./mvnw spring-boot:run
```

Set up the database and update your `application.properties` with relevant credentials and SMTP settings.

---

## ✉️ Contribution

Feel free to fork the repo and submit pull requests. Suggestions, improvements, and bug reports are welcome.

---

## 📜 License

[//]: # (This project is licensed under the MIT License - see the [LICENSE]&#40;LICENSE&#41; file for details.)

---
