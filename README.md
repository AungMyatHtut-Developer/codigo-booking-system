# Booking System

A Spring Boot-based booking system that allows users to book classes, manage packages, and handle waitlists. This system includes class scheduling, booking cancellation, waitlist promotion, and credit management with Redis-based concurrency control.

---

## 🚀 Tech Stack

- **Java** 17
- **Spring Boot** 3.4.5
- **Spring Data JPA**
- **Spring Security with JWT**
- **Lombok**
- **Redis** (for concurrency lock control)
- **H2 / MySQL / PostgreSQL** (any JPA-compatible DB)

---

## 🔧 Setup Instructions

### Prerequisites

- Java 17
- Maven or Gradle
- Redis installed and running locally (`localhost:6379`)

### Clone and Run

```bash
git clone https://github.com/AungMyatHtut-Developer/codigo-booking-system.git
cd booking-system
```
```
Ensure Redis is running locally before starting the application.
Run the project: ./mvnw spring-boot:run

App runs on: http://localhost:8081
```
=======================================================================================
### 🗃️ Database
Hibernate auto-generates tables based on entity definitions.

Dummy data is preloaded into the database via data.sql, including:

Class entries

Package data

The schema includes relationships for users, packages, bookings, waitlists, and classes.

Database Diagram
📎 docs/Database-Design.png
(A sample ER diagram file is included in the repo)
=======================================================================================
### 🔒 Security
JWT-based authentication is implemented using Spring Security. Users must log in to get a token for API access.
=======================================================================================
⚙️ Features
Book classes with real-time capacity validation

Prevent duplicate or overlapping bookings

Cancel class and automatically handle:

Credit refund (based on rules)

Waitlist promotion

Schedule:

Waitlist cleanup & refund when class starts

Redis-based locking to prevent overbooking due to concurrent requests

==========================================================================================
### 🧪 Testing
Dummy data is inserted at startup using data.sql
Endpoints secured via JWT
Redis is required to test concurrent booking logic

==========================================================================================
### 📌 Notes
Application runs on port 8081

Redis must be running locally for booking lock to work

If you use a different DB (e.g., PostgreSQL), update application.properties
JWT and API endpoints are modular and extendable for future use cases

===========================================================================================
### 🧑‍💻 Authors
Developed by Aung Myat Htut

===========================================================================================