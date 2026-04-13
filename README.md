# Subscription Billing System

A full-stack SaaS subscription billing system built with Java Spring Boot for the backend and React (Vite) for the frontend.

## Features

- **User Authentication:** Session-based authentication with minimal, clean login and registration interfaces.
- **Role-Based Access Control:** Dedicated dashboards for Administrators and Customers.
- **Plan Management:** Admins can create and manage subscription plans.
- **Subscription Management:** Automated billing cycles, pro-rated upgrades, and subscription lifecycle management.
- **Payment Processing Simulation:** Handling simulated payments, tracking active subscriptions, and dealing with failed payments (dunning).

## Technology Stack

### Backend
- Java 17
- Spring Boot 4.x
- Spring Data JPA
- Spring Web / MVC
- MySQL (Database)
- jBCrypt (Password Hashing)
- Lombok

### Frontend
- React 19
- Vite
- React Router DOM
- Axios
- Vanilla CSS (Light-themed, clean UI)

## Prerequisites

- JDK 17 or higher
- Node.js 18+ and npm
- MySQL Server

## Getting Started

### 1. Database Setup

Create a new MySQL database for the project:
```sql
CREATE DATABASE subscription_billing;
```
Configure your database credentials in the `backend/src/main/resources/application.properties` file:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/subscription_billing
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 2. Backend Setup

Navigate to the `backend` directory and run the Spring Boot application:

```bash
cd backend
./mvnw spring-boot:run
```
*(Use `mvnw.cmd` on Windows if Maven is not installed globally)*
The backend server will start on `http://localhost:8080`.

### 3. Frontend Setup

Navigate to the `frontend` directory, install dependencies, and start the Vite development server:

```bash
cd frontend
npm install
npm run dev
```
The frontend application will be available at `http://localhost:5173`.

## Architecture

The project is structured into two main parts:
- **`backend/`**: Contains the Spring Boot REST API, database entities, repositories, and business logic for billing and subscriptions.
- **`frontend/`**: Contains the React single-page application built with Vite, interacting with the backend via Axios.
