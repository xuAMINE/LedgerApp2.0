

# 💳 BankApp

A modern Java Spring Boot banking application with checking and savings accounts, transaction management, and Dockerized deployment on AWS EC2. This project supports continuous deployment via GitHub Actions and includes a clean, user-friendly frontend.

---

## 🚀 Features

- 🔐 User registration & login
- 🏦 Checking and Savings accounts
- 💸 Transfer between accounts
- 📜 Transaction history
- 🐳 Docker container support
- ☁️ Deployed to AWS EC2 with GitHub Actions CI/CD
- 🌐 Custom domain and optional HTTPS with Nginx

---

## 🛠️ Technologies

- Java 17 + Spring Boot 3
- Thymeleaf templates
- Maven
- PostgreSQL
- Lombok
- Docker
- GitHub Actions
- AWS EC2
- Nginx (optional for domain + HTTPS)

---

## 📦 Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/com/example/bankapp/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── BankAppApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.yml
├── target/                      # Compiled JAR output
├── .github/workflows/deploy.yml
├── Dockerfile                   # Created dynamically on EC2
├── pom.xml
└── README.md
```

---

## ⚙️ Local Development

### ✅ Prerequisites

- Java 17
- Maven
- PostgreSQL running locally (or cloud instance)

### 🧪 Build & Run

```bash
# Build JAR
mvn clean package -DskipTests

# Run app
java -jar target/bankapp-0.0.1-SN
