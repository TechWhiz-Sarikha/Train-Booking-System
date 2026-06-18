package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String DRIVER   = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_URL   = "jdbc:derby:TrainBookingDB;create=true";
    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
                initializeDatabase(connection);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Derby driver not found. Ensure derby.jar is in classpath.", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {

            // ---------- TRAINS ----------
            try {
                st.executeUpdate(
                    "CREATE TABLE trains (" +
                    "  train_number   VARCHAR(10)  PRIMARY KEY," +
                    "  train_name     VARCHAR(100) NOT NULL," +
                    "  origin         VARCHAR(100) NOT NULL," +
                    "  destination    VARCHAR(100) NOT NULL," +
                    "  departure_time VARCHAR(10)  NOT NULL," +
                    "  arrival_time   VARCHAR(10)  NOT NULL," +
                    "  total_seats    INT          DEFAULT 100," +
                    "  available_seats INT         DEFAULT 100," +
                    "  fare_sleeper   DOUBLE       DEFAULT 500.0," +
                    "  fare_ac        DOUBLE       DEFAULT 1200.0" +
                    ")"
                );
            } catch (SQLException e) { /* table exists */ }

            // ---------- TRAIN STOPS ----------
            try {
                st.executeUpdate(
                    "CREATE TABLE train_stops (" +
                    "  id           INT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "  train_number VARCHAR(10)  NOT NULL," +
                    "  stop_name    VARCHAR(100) NOT NULL," +
                    "  stop_order   INT          NOT NULL," +
                    "  arrive_time  VARCHAR(10)," +
                    "  depart_time  VARCHAR(10)," +
                    "  FOREIGN KEY (train_number) REFERENCES trains(train_number) ON DELETE CASCADE" +
                    ")"
                );
            } catch (SQLException e) { /* table exists */ }

            // ---------- CUSTOMERS ----------
            try {
                st.executeUpdate(
                    "CREATE TABLE customers (" +
                    "  customer_id INT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "  name        VARCHAR(100) NOT NULL," +
                    "  email       VARCHAR(150) NOT NULL UNIQUE," +
                    "  phone       VARCHAR(10)  NOT NULL," +
                    "  address     VARCHAR(255)," +
                    "  password    VARCHAR(100) NOT NULL," +
                    "  is_active   SMALLINT     DEFAULT 1" +
                    ")"
                );
            } catch (SQLException e) { /* table exists */ }

            // ---------- BOOKINGS ----------
            try {
                st.executeUpdate(
                    "CREATE TABLE bookings (" +
                    "  booking_id   INT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "  customer_id  INT          NOT NULL," +
                    "  train_number VARCHAR(10)  NOT NULL," +
                    "  origin       VARCHAR(100) NOT NULL," +
                    "  destination  VARCHAR(100) NOT NULL," +
                    "  travel_date  VARCHAR(20)  NOT NULL," +
                    "  travel_class VARCHAR(20)  NOT NULL," +
                    "  num_tickets  INT          NOT NULL," +
                    "  total_fare   DOUBLE       NOT NULL," +
                    "  status       VARCHAR(20)  DEFAULT 'CONFIRMED'," +
                    "  booked_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP," +
                    "  FOREIGN KEY (customer_id)  REFERENCES customers(customer_id)," +
                    "  FOREIGN KEY (train_number) REFERENCES trains(train_number)" +
                    ")"
                );
            } catch (SQLException e) { /* table exists */ }

            System.out.println("[DB] Derby database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[DB] Initialization error: " + e.getMessage());
        }
    }

    public static void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            // Derby always throws on shutdown — this is normal
        }
    }
}
