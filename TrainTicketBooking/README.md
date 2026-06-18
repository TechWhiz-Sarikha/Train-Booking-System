# Train Ticket Booking System — Apache Derby Edition

## Project Structure

```
TrainTicketBooking/
├── src/
│   ├── Main.java
│   ├── model/
│   │   ├── Train.java
│   │   ├── Customer.java
│   │   └── Booking.java
│   ├── dao/
│   │   ├── TrainDAO.java
│   │   ├── CustomerDAO.java
│   │   └── BookingDAO.java
│   ├── service/
│   │   ├── AdminService.java
│   │   └── CustomerService.java
│   └── util/
│       ├── DBConnection.java
│       └── InputValidator.java
├── lib/
│   └── derby.jar          ← Apache Derby embedded DB (included)
├── bin/                   ← Create this folder before compiling
└── .vscode/
    └── tasks.json
```

## Setup (VS Code)

### Step 1 — Create the bin folder
```
mkdir bin
```

Step 2 — Compile
Press `Ctrl+Shift+B` (runs the "Compile" task), OR run manually:

**Windows:**
```
javac -cp "lib/derby.jar" -d bin src/util/DBConnection.java src/util/InputValidator.java src/model/Train.java src/model/Customer.java src/model/Booking.java src/dao/TrainDAO.java src/dao/CustomerDAO.java src/dao/BookingDAO.java src/service/AdminService.java src/service/CustomerService.java src/Main.java
```

**Linux/Mac:**
Same command (paths use `/` already).


### Step 3 — Run

**Windows:**
```
java -cp "bin;lib/derby.jar" Main
```

**Linux/Mac:**
```
java -cp "bin:lib/derby.jar" Main
```

> The Derby database (`TrainBookingDB/`) is created automatically in the project folder on first run. No MySQL, no setup.

---

## Login Credentials

| Role     | Username | Password   |
|----------|----------|------------|
| Admin    | admin    | admin123   |
| Customer | user     | user123    |

---

## Features Implemented

| US    | Feature                        | Status |
|-------|--------------------------------|--------|
| US001 | Admin Menu                     | ✅     |
| US002 | Admin Train Registration       | ✅     |
| US003 | Train Details Update           | ✅     |
| US004 | Delete Train by Admin          | ✅     |
| US005 | Customer Menu                  | ✅     |
| US006 | Customer Registration          | ✅     |
| US007 | Customer Details Update        | ✅     |
| US008 | Customer Soft Delete           | ✅     |
| US009 | Display Available Trains       | ✅     |
| US010 | Train Ticket Booking           | ✅     |
| US011 | Ticket Cancellation            | ✅     |
| US012 | View Booking History           | ✅     |

## Key Validations

- Name: letters and spaces only (no numbers/symbols)
- Email: proper format check
- Phone: exactly 10 numeric digits
- Travel date: today to next 3 months
- Max 6 tickets per session
- Schedule conflict check on add/update
- Password verification before cancellation
- 24-hour cancellation window enforced
- Soft delete retains customer data (is_active = 0)
- Deleting a train auto-cancels associated bookings
