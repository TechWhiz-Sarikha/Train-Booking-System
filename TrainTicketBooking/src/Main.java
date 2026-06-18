import service.AdminService;
import service.CustomerService;
import util.DBConnection;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   TRAIN TICKET BOOKING SYSTEM (Derby)   ");
        System.out.println("==========================================");

        // Initialize DB
        try {
            DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        AdminService    adminService    = new AdminService(sc);
        CustomerService customerService = new CustomerService(sc);

        while (true) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1) Admin");
            System.out.println("2) Customer");
            System.out.println("3) Exit");
            System.out.print("Select User Type: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": adminService.start();    break;
                case "2": customerService.start(); break;
                case "3":
                    System.out.println("Good Bye!!. Terminating the Program.");
                    DBConnection.shutdown();
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1, 2 or 3.");
            }
        }
    }
}
