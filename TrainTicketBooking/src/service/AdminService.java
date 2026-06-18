package service;

import dao.TrainDAO;
import model.Train;
import util.InputValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminService {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private final TrainDAO trainDAO = new TrainDAO();
    private final Scanner  sc;

    public AdminService(Scanner scanner) {
        this.sc = scanner;
    }

    // ─── US001 : Admin login and menu ─────────────────────────────────────────
    public void start() {
        System.out.println("\n========== ADMIN LOGIN ==========");
        System.out.print("Username: ");
        String user = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();

        if (!ADMIN_USER.equals(user) || !ADMIN_PASS.equals(pass)) {
            System.out.println("Please Enter Correct UserName and Password.");
            return;
        }

        System.out.println("\nLogin successful! Welcome, Admin.");
        adminMenu();
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1) Admin Train Registration");
            System.out.println("2) Train Details Update by Admin");
            System.out.println("3) Delete Train by Admin");
            System.out.println("4) Exit");
            System.out.print("Enter Choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": registerTrain();  break;
                case "2": updateTrain();    break;
                case "3": deleteTrain();    break;
                case "4":
                    System.out.println("Good Bye User!!. Terminating the Program.");
                    return;
                default:
                    System.out.println("You have selected an inappropriate option. Kindly select an appropriate option.");
            }
        }
    }

    // ─── US002 : Register Train ───────────────────────────────────────────────
    private void registerTrain() {
        System.out.println("\n--- Admin Train Registration ---");
        try {
            System.out.print("Train Number: ");
            String trainNo = sc.nextLine().trim();
            if (!InputValidator.isValidTrainNumber(trainNo)) {
                System.out.println("Invalid train number."); return;
            }

            System.out.print("Train Name: ");
            String trainName = sc.nextLine().trim();
            if (trainName.isEmpty()) { System.out.println("Train name cannot be empty."); return; }

            System.out.print("Origin Station: ");
            String origin = sc.nextLine().trim();

            System.out.print("Destination Station: ");
            String dest = sc.nextLine().trim();

            System.out.print("Departure Time (HH:mm): ");
            String depTime = sc.nextLine().trim();
            if (!InputValidator.isValidTime(depTime)) { System.out.println("Invalid time format. Use HH:mm."); return; }

            System.out.print("Arrival Time (HH:mm): ");
            String arrTime = sc.nextLine().trim();
            if (!InputValidator.isValidTime(arrTime)) { System.out.println("Invalid time format. Use HH:mm."); return; }

            System.out.print("Total Seats: ");
            int seats = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Sleeper Fare (per ticket): ");
            double fareSleeper = Double.parseDouble(sc.nextLine().trim());

            System.out.print("AC Fare (per ticket): ");
            double fareAC = Double.parseDouble(sc.nextLine().trim());

            System.out.print("Intermediate Stops (comma-separated, or press Enter to skip): ");
            String stopsInput = sc.nextLine().trim();
            List<String> stops = new ArrayList<>();
            if (!stopsInput.isEmpty()) {
                for (String s : stopsInput.split(",")) stops.add(s.trim());
            }

            Train train = new Train(trainNo, trainName, origin, dest, depTime, arrTime, seats, fareSleeper, fareAC);
            train.setStops(stops);

            boolean success = trainDAO.addTrain(train);
            if (success) System.out.println("  Train registered successfully.");

        } catch (NumberFormatException e) {
            System.out.println("  Invalid numeric input. Please try again.");
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US003 : Update Train ─────────────────────────────────────────────────
    private void updateTrain() {
        System.out.println("\n--- Train Details Update ---");
        try {
            System.out.print("Enter Train Number to search: ");
            String trainNo = sc.nextLine().trim();
            Train existing = trainDAO.findByNumber(trainNo);
            if (existing == null) { System.out.println("  Train not found."); return; }

            System.out.println("  Current details: " + existing);
            System.out.println("  (Press Enter to keep existing value)");

            System.out.print("New Train Name [" + existing.getTrainName() + "]: ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) existing.setTrainName(name);

            System.out.print("New Origin [" + existing.getOrigin() + "]: ");
            String origin = sc.nextLine().trim();
            if (!origin.isEmpty()) existing.setOrigin(origin);

            System.out.print("New Destination [" + existing.getDestination() + "]: ");
            String dest = sc.nextLine().trim();
            if (!dest.isEmpty()) existing.setDestination(dest);

            System.out.print("New Departure Time [" + existing.getDepartureTime() + "]: ");
            String depTime = sc.nextLine().trim();
            if (!depTime.isEmpty()) {
                if (!InputValidator.isValidTime(depTime)) { System.out.println("Invalid time."); return; }
                existing.setDepartureTime(depTime);
            }

            System.out.print("New Arrival Time [" + existing.getArrivalTime() + "]: ");
            String arrTime = sc.nextLine().trim();
            if (!arrTime.isEmpty()) {
                if (!InputValidator.isValidTime(arrTime)) { System.out.println("Invalid time."); return; }
                existing.setArrivalTime(arrTime);
            }

            System.out.print("New Total Seats [" + existing.getTotalSeats() + "]: ");
            String seatsStr = sc.nextLine().trim();
            if (!seatsStr.isEmpty()) existing.setTotalSeats(Integer.parseInt(seatsStr));

            System.out.print("New Sleeper Fare [" + existing.getFareSleeper() + "]: ");
            String sl = sc.nextLine().trim();
            if (!sl.isEmpty()) existing.setFareSleeper(Double.parseDouble(sl));

            System.out.print("New AC Fare [" + existing.getFareAC() + "]: ");
            String ac = sc.nextLine().trim();
            if (!ac.isEmpty()) existing.setFareAC(Double.parseDouble(ac));

            boolean ok = trainDAO.updateTrain(existing);
            System.out.println(ok ? "  Train details updated successfully." : "  Update failed.");

        } catch (NumberFormatException e) {
            System.out.println("  Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // ─── US004 : Delete Train ─────────────────────────────────────────────────
    private void deleteTrain() {
        System.out.println("\n--- Delete Train ---");
        try {
            System.out.print("Enter Train Number to delete: ");
            String trainNo = sc.nextLine().trim();
            Train existing = trainDAO.findByNumber(trainNo);
            if (existing == null) { System.out.println("  Train not found."); return; }

            System.out.println("  Found: " + existing);
            System.out.print("  Confirm delete? (yes/no): ");
            if (!"yes".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  Deletion cancelled."); return;
            }

            boolean ok = trainDAO.deleteTrain(trainNo);
            System.out.println(ok ? "  Train deleted. Associated bookings have been cancelled."
                                  : "  Deletion failed.");
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }
}
