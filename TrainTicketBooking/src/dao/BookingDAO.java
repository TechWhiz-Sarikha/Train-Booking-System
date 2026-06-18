package dao;

import model.Booking;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // ─── US010 : Create a booking ─────────────────────────────────────────────
    public int createBooking(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings (customer_id, train_number, origin, destination, " +
                     "travel_date, travel_class, num_tickets, total_fare, status) " +
                     "VALUES (?,?,?,?,?,?,?,?,'CONFIRMED')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, b.getCustomerId());
            ps.setString(2, b.getTrainNumber());
            ps.setString(3, b.getOrigin());
            ps.setString(4, b.getDestination());
            ps.setString(5, b.getTravelDate());
            ps.setString(6, b.getTravelClass());
            ps.setInt   (7, b.getNumTickets());
            ps.setDouble(8, b.getTotalFare());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    // ─── US011 : Cancel a booking ─────────────────────────────────────────────
    public boolean cancelBooking(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status='CANCELLED' WHERE booking_id=? AND status='CONFIRMED'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── US012 : Booking history for a customer ───────────────────────────────
    public List<Booking> getBookingHistory(int customerId) throws SQLException {
        String sql = "SELECT b.*, t.train_name FROM bookings b " +
                     "JOIN trains t ON b.train_number = t.train_number " +
                     "WHERE b.customer_id=? ORDER BY b.booked_at DESC";
        List<Booking> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ─── Find booking by ID ───────────────────────────────────────────────────
    public Booking findById(int bookingId) throws SQLException {
        String sql = "SELECT b.*, t.train_name FROM bookings b " +
                     "JOIN trains t ON b.train_number = t.train_number " +
                     "WHERE b.booking_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // ─── Count tickets booked in current session ───────────────────────────────
    public int countSessionTickets(int customerId, String sessionDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(num_tickets),0) FROM bookings " +
                     "WHERE customer_id=? AND travel_date=? AND status='CONFIRMED'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt   (1, customerId);
            ps.setString(2, sessionDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Validates 24-hour cancellation window.
     * Returns true if cancellation is allowed (departure is > 24h from now).
     */
    public static boolean isWithinCancellationWindow(String travelDate, String departureTime) {
        try {
            String dateTimeStr = travelDate + " " + departureTime;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime departure = LocalDateTime.parse(dateTimeStr, fmt);
            LocalDateTime cutoff = LocalDateTime.now().plusHours(24);
            return departure.isAfter(cutoff);
        } catch (Exception e) {
            return false;
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId  (rs.getInt   ("booking_id"));
        b.setCustomerId (rs.getInt   ("customer_id"));
        b.setTrainNumber(rs.getString("train_number"));
        b.setOrigin     (rs.getString("origin"));
        b.setDestination(rs.getString("destination"));
        b.setTravelDate (rs.getString("travel_date"));
        b.setTravelClass(rs.getString("travel_class"));
        b.setNumTickets (rs.getInt   ("num_tickets"));
        b.setTotalFare  (rs.getDouble("total_fare"));
        b.setStatus     (rs.getString("status"));
        try { b.setTrainName(rs.getString("train_name")); } catch (SQLException ignored) {}
        Timestamp ts = rs.getTimestamp("booked_at");
        if (ts != null) b.setBookedAt(ts.toString());
        return b;
    }
}
