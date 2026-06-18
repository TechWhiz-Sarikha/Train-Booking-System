# 🚆 Train Ticket Booking System

A console-based **Train Ticket Booking System** built with **Java** and **Apache Derby** (embedded database — no MySQL or any external DB setup required).

Developed as a **PBL (Project Based Learning)** submission covering all 12 user stories.

---

## 📌 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (JDK 17+) |
| Database | Apache Derby (Embedded) |
| IDE | VS Code |
| Architecture | MVC (Model-View-Controller) |
| DB Access | JDBC |

---

## 📁 Project Structure

```
TrainTicketBooking/
│
├── src/
│   ├── Main.java                        # Entry point
│   ├── model/
│   │   ├── Train.java                   # Train entity
│   │   ├── Customer.java                # Customer entity
│   │   └── Booking.java                 # Booking entity
│   ├── dao/
│   │   ├── TrainDAO.java                # Train DB operations
│   │   ├── CustomerDAO.java             # Customer DB operations
│   │   └── BookingDAO.java              # Booking DB operations
│   ├── service/
│   │   ├── AdminService.java            # Admin menu & business logic
│   │   └── CustomerService.java         # Customer menu & business logic
│   └── util/
│       ├── DBConnection.java            # Derby connection + auto table creation
│       └── InputValidator.java          # Input validation utilities
│
├── lib/
│   └── derby.jar                        # Apache Derby embedded driver
│
├── bin/                                 # Compiled .class files (create before build)
│
└── .vscode/
    └── tasks.json                       # VS Code build & run tasks
```

---

## ⚙️ Setup & Run

### Prerequisites

- [JDK 17+](https://adoptium.net) installed
- [VS Code](https://code.visualstudio.com) installed
- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) installed in VS Code

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/TrainTicketBooking.git
cd TrainTicketBooking
```

### 2. Create the `bin` Folder

```bash
mkdir bin
```

### 3. Compile

**Windows:**
```bash
javac -cp lib/derby.jar -d bin src/util/DBConnection.java src/util/InputValidator.java src/model/Train.java src/model/Customer.java src/model/Booking.java src/dao/TrainDAO.java src/dao/CustomerDAO.java src/dao/BookingDAO.java src/service/AdminService.java src/service/CustomerService.java src/Main.java
```

**Linux / Mac:**
```bash
javac -cp lib/derby.jar -d bin src/util/DBConnection.java src/util/InputValidator.java src/model/Train.java src/model/Customer.java src/model/Booking.java src/dao/TrainDAO.java src/dao/CustomerDAO.java src/dao/BookingDAO.java src/service/AdminService.java src/service/CustomerService.java src/Main.java
```

### 4. Run

**Windows:**
```bash
java -cp "bin;lib/derby.jar" Main
```

**Linux / Mac:**
```bash
java -cp "bin:lib/derby.jar" Main
```

> 💡 The Derby database (`TrainBookingDB/`) is created **automatically** on first run. No SQL scripts to import, no server to start.

---

## 🔐 Login Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Customer | `user` | `user123` |

---

## 📋 Features & User Stories

### 🛠️ Admin Module

| US | Feature | Description |
|---|---|---|
| US001 | Admin Menu | Login-protected menu with 4 options |
| US002 | Train Registration | Add new trains with route, schedule, stops, fares |
| US003 | Train Details Update | Update train info with schedule conflict detection |
| US004 | Delete Train | Remove train; associated bookings auto-cancelled |

### 👤 Customer Module

| US | Feature | Description |
|---|---|---|
| US005 | Customer Menu | Login-protected menu with 8 options |
| US006 | Customer Registration | Register with name, email, phone, address, password |
| US007 | Customer Details Update | Edit personal details with validation |
| US008 | Customer Soft Delete | Deactivate account (data retained in DB) |
| US009 | Display Available Trains | Search by origin, destination, travel date |
| US010 | Train Ticket Booking | Book tickets with fare preview and confirmation |
| US011 | Ticket Cancellation | Cancel with password verification + 24hr window |
| US012 | View Booking History | View all past and active bookings |

---

## ✅ Validations Implemented

| Field | Rule |
|---|---|
| Name | Letters and spaces only — no numbers or special characters |
| Email | Standard email format (user@domain.com) |
| Phone | Exactly 10 numeric digits |
| Travel Date | Today up to next 3 months (yyyy-MM-dd) |
| Time | HH:mm format (24-hour) |
| Train Number | Unique — duplicates rejected |
| Schedule | No two trains with same departure + arrival time on same route |
| Tickets | Max 6 tickets per session per customer |
| Cancellation | Password required + must be 24+ hours before departure |
| Soft Delete | Account marked inactive; data never deleted from DB |

---

## 🗄️ Database Tables (Auto-created by Derby)

```sql
trains         -- train_number, train_name, origin, destination, departure_time,
               --   arrival_time, total_seats, available_seats, fare_sleeper, fare_ac

train_stops    -- id, train_number, stop_name, stop_order, arrive_time, depart_time

customers      -- customer_id, name, email, phone, address, password, is_active

bookings       -- booking_id, customer_id, train_number, origin, destination,
               --   travel_date, travel_class, num_tickets, total_fare, status, booked_at
```

---

## 🖥️ Sample Output

```
==========================================
   TRAIN TICKET BOOKING SYSTEM (Derby)
==========================================
[DB] Derby database initialized successfully.

========== MAIN MENU ==========
1) Admin
2) Customer
3) Exit
Select User Type: 1

========== ADMIN LOGIN ==========
Username: admin
Password: admin123

Login successful! Welcome, Admin.

========== ADMIN MENU ==========
1) Admin Train Registration
2) Train Details Update by Admin
3) Delete Train by Admin
4) Exit
Enter Choice:
```

---

## 📝 Notes

- The `TrainBookingDB/` folder is auto-generated on first run — **do not delete it** (it stores all your data)
- No external database server needed — Derby runs fully **embedded inside the JVM**
- To reset the database, simply delete the `TrainBookingDB/` folder and re-run

---
