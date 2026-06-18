package service;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.TrainDAO;
import model.Booking;
import model.Customer;
import model.Train;
import util.InputValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CustomerService {

    private static final String CUSTOMER_USER = "user";
    private static final String CUSTOMER_PASS = "user123";

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TrainDAO    trainDAO    = new TrainDAO();
    private final BookingDAO  bookingDAO  = new BookingDAO();
    private final Scanner     sc;

    private Customer loggedInCustomer = null;
    private int sessionTickets = 0;

    public CustomerService(Scanner scanner) {
        this.sc = scanner;
    }

    // ─── US005 : Customer login and menu ──────────────────────────────────────
    public void start() {
        System.out.println("\n========== CUSTOMER LOGIN ==========");
        System.out.print("Username: ");
        String user = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();

        if (!CUSTOMER_USER.equals(user) || !CUSTOMER_PASS.equals(pass)) {
            System.out.println("Please Enter Correct UserName and Password.");
            return;
        }

        System.out.println("\nLogin successful! Welcome.");
        customerMenu();
    }

    private void customerMenu() {
        while (true) {
            System.out.println("\n========== CUSTOMER MENU ==========");
            System.out.println("1) Customer Registration");
            System.out.println("2) Customer Details Update");
            System.out.println("3) Customer Soft Delete");
            System.out.println("4) Display Available Trains");
            System.out.println("5) Train Ticket Booking");
            System.out.println("6) Ticket Cancellation");
            System.out.println("7) View Booking History");
            System.out.println("8) Exit");
            System.out.print("Enter Choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": registerCustomer(); break;
                case "2": updateCustomer();   break;
                case "3": softDelete();       break;
                case "4": displayTrains();    break;
                case "5": bookTicket();       break;
                case "6": cancelTicket();     break;
                case "7": viewHistory();      break;
                case "8":
                    System.out.println("Good Bye User!!. Terminating the Program.");
                    return;
                default:
                    System.out.println("You have selected an inappropriate option. Kindly select an appropriate option.");
            }
        }
    }

    // ─── US006 : Customer Registration ───────────────────────────────────────
    private void registerCustomer() {
        System.out.println("\n--- Customer Registration ---");
        try {
            System.out.print("Full Name: ");
            String name = sc.nextLine().trim();
            if (!InputValidator.isValidName(name)) {
                System.out.println("  Invalid name. Name must not contain numbers or special characters."); return;
            }

            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            if (!InputValidator.isValidEmail(email)) {
                System.out.println("  Invalid email format."); return;
            }
            if (customerDAO.emailExists(email)) {
                System.out.println("  Email already registered."); return;
            }

            System.out.print("Phone (10 digits): ");
            String phone = sc.nextLine().trim();
            if (!InputValidator.isValidPhone(phone)) {
                System.out.println("  Phone must be exactly 10 numeric digits."); return;
            }

            System.out.print("Address: ");
            String address = sc.nextLine().trim();

            System.out.print("Set Password: ");
            String password = sc.nextLine().trim();
            if (password.length() < 4) {
                System.out.println("  Password must be at least 4 characters."); return;
            }

            Customer c = new Customer(name, email, phone, address, password);
            customerDAO.register(c);
            System.out.println("  Registration successful! Please note your email for future logins.");

            // Auto-login after registration
            loggedInCustomer = customerDAO.findByEmail(email);

        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US007 : Update Customer ──────────────────────────────────────────────
    private void updateCustomer() {
        System.out.println("\n--- Customer Details Update ---");
        if (!ensureLoggedIn()) return;
        try {
            Customer c = loggedInCustomer;
            System.out.println("  Current details: " + c);
            System.out.println("  (Press Enter to keep existing value)");

            System.out.print("New Name [" + c.getName() + "]: ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                if (!InputValidator.isValidName(name)) { System.out.println("Invalid name."); return; }
                c.setName(name);
            }

            System.out.print("New Email [" + c.getEmail() + "]: ");
            String email = sc.nextLine().trim();
            if (!email.isEmpty()) {
                if (!InputValidator.isValidEmail(email)) { System.out.println("Invalid email."); return; }
                c.setEmail(email);
            }

            System.out.print("New Phone [" + c.getPhone() + "]: ");
            String phone = sc.nextLine().trim();
            if (!phone.isEmpty()) {
                if (!InputValidator.isValidPhone(phone)) { System.out.println("Phone must be 10 digits."); return; }
                c.setPhone(phone);
            }

            System.out.print("New Address [" + c.getAddress() + "]: ");
            String address = sc.nextLine().trim();
            if (!address.isEmpty()) c.setAddress(address);

            boolean ok = customerDAO.update(c);
            System.out.println(ok ? "  Details updated successfully." : "  Update failed.");

        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US008 : Soft Delete ──────────────────────────────────────────────────
    private void softDelete() {
        System.out.println("\n--- Deactivate Account ---");
        if (!ensureLoggedIn()) return;
        try {
            System.out.print("  Are you sure you want to deactivate your account? (yes/no): ");
            if (!"yes".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  Deactivation cancelled."); return;
            }
            boolean ok = customerDAO.deactivate(loggedInCustomer.getCustomerId());
            if (ok) {
                System.out.println("  Account deactivated successfully. Your data is retained.");
                loggedInCustomer = null;
            } else {
                System.out.println("  Deactivation failed.");
            }
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US009 : Display Available Trains ─────────────────────────────────────
    private void displayTrains() {
        System.out.println("\n--- Display Available Trains ---");
        try {
            System.out.print("Origin Station: ");
            String origin = sc.nextLine().trim();

            System.out.print("Destination Station: ");
            String dest = sc.nextLine().trim();

            System.out.print("Travel Date (yyyy-MM-dd): ");
            String date = sc.nextLine().trim();
            if (!InputValidator.isValidTravelDate(date)) {
                System.out.println("  Invalid date. Must be today or within the next 3 months (yyyy-MM-dd)."); return;
            }

            List<Train> trains = trainDAO.findAvailableTrains(origin, dest);
            if (trains.isEmpty()) {
                System.out.println("  No trains available for this route and date.");
            } else {
                System.out.println("\n  Available Trains:");
                System.out.println("  " + "-".repeat(120));
                for (Train t : trains) {
                    System.out.println("  " + t);
                }
                System.out.println("  " + "-".repeat(120));
            }
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US010 : Book Ticket ──────────────────────────────────────────────────
    private void bookTicket() {
        System.out.println("\n--- Train Ticket Booking ---");
        if (!ensureLoggedIn()) return;
        try {
            System.out.print("Train Number: ");
            String trainNo = sc.nextLine().trim();
            Train train = trainDAO.findByNumber(trainNo);
            if (train == null) { System.out.println("  Train not found."); return; }

            System.out.print("Origin Station: ");
            String origin = sc.nextLine().trim();

            System.out.print("Destination Station: ");
            String dest = sc.nextLine().trim();

            System.out.print("Travel Date (yyyy-MM-dd): ");
            String date = sc.nextLine().trim();
            if (!InputValidator.isValidTravelDate(date)) {
                System.out.println("  Invalid date. Must be within next 3 months."); return;
            }

            System.out.println("  Select Class:");
            System.out.println("  1) Sleeper (Rs. " + train.getFareSleeper() + " per ticket)");
            System.out.println("  2) AC      (Rs. " + train.getFareAC()      + " per ticket)");
            System.out.print("  Choice: ");
            String classChoice = sc.nextLine().trim();
            String travelClass;
            double farePerTicket;
            if ("1".equals(classChoice)) {
                travelClass = "SLEEPER"; farePerTicket = train.getFareSleeper();
            } else if ("2".equals(classChoice)) {
                travelClass = "AC"; farePerTicket = train.getFareAC();
            } else {
                System.out.println("  Invalid class selection."); return;
            }

            System.out.print("Number of Tickets: ");
            int numTickets = Integer.parseInt(sc.nextLine().trim());

            // Max 6 tickets per session
            if (sessionTickets + numTickets > 6) {
                System.out.println("  Cannot book more than 6 tickets per session. Already booked: " + sessionTickets);
                return;
            }

            if (train.getAvailableSeats() < numTickets) {
                System.out.println("  Only " + train.getAvailableSeats() + " seats available."); return;
            }

            double totalFare = farePerTicket * numTickets;

            // Show fare summary
            System.out.println("\n  ===== BOOKING SUMMARY =====");
            System.out.println("  Train    : " + train.getTrainNumber() + " - " + train.getTrainName());
            System.out.println("  Route    : " + origin + " -> " + dest);
            System.out.println("  Date     : " + date);
            System.out.println("  Class    : " + travelClass);
            System.out.println("  Tickets  : " + numTickets);
            System.out.printf ("  Total    : Rs. %.2f%n", totalFare);
            System.out.println("  ===========================");
            System.out.print("  Confirm booking? (yes/no): ");
            if (!"yes".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  Booking cancelled."); return;
            }

            Booking b = new Booking();
            b.setCustomerId (loggedInCustomer.getCustomerId());
            b.setTrainNumber(trainNo);
            b.setOrigin     (origin);
            b.setDestination(dest);
            b.setTravelDate (date);
            b.setTravelClass(travelClass);
            b.setNumTickets (numTickets);
            b.setTotalFare  (totalFare);

            int bookingId = bookingDAO.createBooking(b);
            trainDAO.updateAvailableSeats(trainNo, -numTickets);
            sessionTickets += numTickets;

            // Print ticket
            System.out.println("\n  ===== TICKET =====");
            System.out.println("  Booking ID : " + bookingId);
            System.out.println("  Passenger  : " + loggedInCustomer.getName());
            System.out.println("  Train      : " + train.getTrainNumber() + " - " + train.getTrainName());
            System.out.println("  From       : " + origin);
            System.out.println("  To         : " + dest);
            System.out.println("  Date       : " + date);
            System.out.println("  Class      : " + travelClass);
            System.out.println("  Tickets    : " + numTickets);
            System.out.printf ("  Total Fare : Rs. %.2f%n", totalFare);
            System.out.println("  Status     : CONFIRMED");
            System.out.println("  ==================");

        } catch (NumberFormatException e) {
            System.out.println("  Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US011 : Cancel Ticket ────────────────────────────────────────────────
    private void cancelTicket() {
        System.out.println("\n--- Ticket Cancellation ---");
        if (!ensureLoggedIn()) return;
        try {
            System.out.print("Enter Ticket/Booking ID: ");
            int bookingId = Integer.parseInt(sc.nextLine().trim());

            Booking b = bookingDAO.findById(bookingId);
            if (b == null) { System.out.println("  Booking not found."); return; }
            if (b.getCustomerId() != loggedInCustomer.getCustomerId()) {
                System.out.println("  This booking does not belong to your account."); return;
            }
            if ("CANCELLED".equals(b.getStatus())) {
                System.out.println("  This booking is already cancelled."); return;
            }

            // Password verification (US011 requirement)
            System.out.print("Enter your password to confirm cancellation: ");
            String pwd = sc.nextLine().trim();
            if (!loggedInCustomer.getPassword().equals(pwd)) {
                System.out.println("  Incorrect password. Cancellation denied."); return;
            }

            // 24-hour window check
            Train train = trainDAO.findByNumber(b.getTrainNumber());
            if (train != null && !BookingDAO.isWithinCancellationWindow(b.getTravelDate(), train.getDepartureTime())) {
                System.out.println("  Cancellation not allowed within 24 hours of departure."); return;
            }

            System.out.println("  Booking: " + b);
            System.out.print("  Confirm cancellation? (yes/no): ");
            if (!"yes".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  Cancellation aborted."); return;
            }

            boolean ok = bookingDAO.cancelBooking(bookingId);
            if (ok) {
                trainDAO.updateAvailableSeats(b.getTrainNumber(), b.getNumTickets());
                System.out.println("  Booking cancelled successfully. Seats have been released.");
            } else {
                System.out.println("  Cancellation failed.");
            }

        } catch (NumberFormatException e) {
            System.out.println("  Invalid booking ID.");
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US012 : View Booking History ─────────────────────────────────────────
    private void viewHistory() {
        System.out.println("\n--- Booking History ---");
        if (!ensureLoggedIn()) return;
        try {
            List<Booking> bookings = bookingDAO.getBookingHistory(loggedInCustomer.getCustomerId());
            if (bookings.isEmpty()) {
                System.out.println("  No bookings found.");
            } else {
                System.out.println("  " + "-".repeat(140));
                for (Booking b : bookings) {
                    System.out.println("  " + b);
                }
                System.out.println("  " + "-".repeat(140));
            }
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── Helper: ensure a customer is logged in / selected ───────────────────
    private boolean ensureLoggedIn() {
        if (loggedInCustomer != null) return true;
        System.out.println("  No customer account selected. Please register first (Option 1).");
        System.out.print("  Or enter your registered email to proceed: ");
        try {
            String email = sc.nextLine().trim();
            if (!email.isEmpty()) {
                Customer c = customerDAO.findByEmail(email);
                if (c != null && c.isActive()) {
                    loggedInCustomer = c;
                    System.out.println("  Welcome back, " + c.getName() + "!");
                    return true;
                } else {
                    System.out.println("  Account not found or inactive.");
                }
            }
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
        return false;
    }
}
