package dao;

import model.Train;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    // ─── US002 : Add a new train ───────────────────────────────────────────────
    public boolean addTrain(Train train) throws SQLException {
        // Check duplicate train number
        if (existsByNumber(train.getTrainNumber())) {
            System.out.println("  [Error] Train number " + train.getTrainNumber() + " already exists.");
            return false;
        }
        // Schedule conflict check for ADD
        if (hasScheduleConflict(train.getTrainNumber(), train.getOrigin(),
                                train.getDestination(), train.getDepartureTime(),
                                train.getArrivalTime())) {
            System.out.println("  [Error] Schedule conflict: another train already has the same " +
                               "departure & arrival time for the same route.");
            return false;
        }
        String sql = "INSERT INTO trains (train_number, train_name, origin, destination, " +
                     "departure_time, arrival_time, total_seats, available_seats, fare_sleeper, fare_ac) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, train.getTrainNumber());
            ps.setString(2, train.getTrainName());
            ps.setString(3, train.getOrigin());
            ps.setString(4, train.getDestination());
            ps.setString(5, train.getDepartureTime());
            ps.setString(6, train.getArrivalTime());
            ps.setInt   (7, train.getTotalSeats());
            ps.setInt   (8, train.getTotalSeats());
            ps.setDouble(9, train.getFareSleeper());
            ps.setDouble(10, train.getFareAC());
            ps.executeUpdate();
        }
        // Insert stops
        if (train.getStops() != null && !train.getStops().isEmpty()) {
            insertStops(train.getTrainNumber(), train.getStops());
        }
        return true;
    }

    // ─── US003 : Update train details ──────────────────────────────────────────
    public boolean updateTrain(Train train) throws SQLException {
        // Schedule conflict (exclude self)
        if (hasScheduleConflictExcludeSelf(train.getTrainNumber(), train.getOrigin(),
                                           train.getDestination(), train.getDepartureTime(),
                                           train.getArrivalTime())) {
            System.out.println("  [Error] Schedule conflict detected with another train.");
            return false;
        }
        String sql = "UPDATE trains SET train_name=?, origin=?, destination=?, departure_time=?, " +
                     "arrival_time=?, total_seats=?, fare_sleeper=?, fare_ac=? " +
                     "WHERE train_number=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, train.getTrainName());
            ps.setString(2, train.getOrigin());
            ps.setString(3, train.getDestination());
            ps.setString(4, train.getDepartureTime());
            ps.setString(5, train.getArrivalTime());
            ps.setInt   (6, train.getTotalSeats());
            ps.setDouble(7, train.getFareSleeper());
            ps.setDouble(8, train.getFareAC());
            ps.setString(9, train.getTrainNumber());
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // ─── US004 : Delete train (cascade cancels bookings) ──────────────────────
    public boolean deleteTrain(String trainNumber) throws SQLException {
        // Mark associated confirmed bookings as CANCELLED
        String cancelBookings = "UPDATE bookings SET status='CANCELLED' WHERE train_number=? AND status='CONFIRMED'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(cancelBookings)) {
            ps.setString(1, trainNumber);
            ps.executeUpdate();
        }
        // Delete stops (FK cascade handles it, but being explicit)
        String deleteStops = "DELETE FROM train_stops WHERE train_number=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(deleteStops)) {
            ps.setString(1, trainNumber);
            ps.executeUpdate();
        }
        // Delete train
        String sql = "DELETE FROM trains WHERE train_number=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, trainNumber);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Find by train number ──────────────────────────────────────────────────
    public Train findByNumber(String trainNumber) throws SQLException {
        String sql = "SELECT * FROM trains WHERE train_number=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, trainNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // ─── US009 : Available trains by route & date ─────────────────────────────
    public List<Train> findAvailableTrains(String origin, String destination) throws SQLException {
        String sql = "SELECT * FROM trains WHERE UPPER(origin)=UPPER(?) AND UPPER(destination)=UPPER(?) AND available_seats > 0";
        List<Train> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, origin);
            ps.setString(2, destination);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ─── Seat update (for booking / cancellation) ─────────────────────────────
    public boolean updateAvailableSeats(String trainNumber, int delta) throws SQLException {
        String sql = "UPDATE trains SET available_seats = available_seats + ? WHERE train_number=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt   (1, delta);
            ps.setString(2, trainNumber);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    public boolean existsByNumber(String trainNumber) throws SQLException {
        String sql = "SELECT 1 FROM trains WHERE train_number=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, trainNumber);
            return ps.executeQuery().next();
        }
    }

    private boolean hasScheduleConflict(String excludeNumber, String origin, String destination,
                                        String depTime, String arrTime) throws SQLException {
        String sql = "SELECT 1 FROM trains WHERE UPPER(origin)=UPPER(?) AND UPPER(destination)=UPPER(?) " +
                     "AND departure_time=? AND arrival_time=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, origin); ps.setString(2, destination);
            ps.setString(3, depTime); ps.setString(4, arrTime);
            return ps.executeQuery().next();
        }
    }

    private boolean hasScheduleConflictExcludeSelf(String trainNumber, String origin, String destination,
                                                   String depTime, String arrTime) throws SQLException {
        String sql = "SELECT 1 FROM trains WHERE UPPER(origin)=UPPER(?) AND UPPER(destination)=UPPER(?) " +
                     "AND departure_time=? AND arrival_time=? AND train_number<>?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, origin); ps.setString(2, destination);
            ps.setString(3, depTime); ps.setString(4, arrTime);
            ps.setString(5, trainNumber);
            return ps.executeQuery().next();
        }
    }

    private void insertStops(String trainNumber, List<String> stops) throws SQLException {
        String sql = "INSERT INTO train_stops (train_number, stop_name, stop_order) VALUES (?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < stops.size(); i++) {
                ps.setString(1, trainNumber);
                ps.setString(2, stops.get(i));
                ps.setInt   (3, i + 1);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Train mapRow(ResultSet rs) throws SQLException {
        Train t = new Train();
        t.setTrainNumber  (rs.getString("train_number"));
        t.setTrainName    (rs.getString("train_name"));
        t.setOrigin       (rs.getString("origin"));
        t.setDestination  (rs.getString("destination"));
        t.setDepartureTime(rs.getString("departure_time"));
        t.setArrivalTime  (rs.getString("arrival_time"));
        t.setTotalSeats   (rs.getInt   ("total_seats"));
        t.setAvailableSeats(rs.getInt  ("available_seats"));
        t.setFareSleeper  (rs.getDouble("fare_sleeper"));
        t.setFareAC       (rs.getDouble("fare_ac"));
        return t;
    }
}
