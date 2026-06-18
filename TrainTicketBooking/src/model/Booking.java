package model;

public class Booking {
    private int bookingId;
    private int customerId;
    private String trainNumber;
    private String origin;
    private String destination;
    private String travelDate;
    private String travelClass;   // SLEEPER or AC
    private int numTickets;
    private double totalFare;
    private String status;        // CONFIRMED or CANCELLED
    private String bookedAt;
    private String trainName;     // joined display

    public Booking() {}

    public int getBookingId()               { return bookingId; }
    public void setBookingId(int id)        { this.bookingId = id; }

    public int getCustomerId()              { return customerId; }
    public void setCustomerId(int id)       { this.customerId = id; }

    public String getTrainNumber()          { return trainNumber; }
    public void setTrainNumber(String t)    { this.trainNumber = t; }

    public String getOrigin()               { return origin; }
    public void setOrigin(String o)         { this.origin = o; }

    public String getDestination()          { return destination; }
    public void setDestination(String d)    { this.destination = d; }

    public String getTravelDate()           { return travelDate; }
    public void setTravelDate(String d)     { this.travelDate = d; }

    public String getTravelClass()          { return travelClass; }
    public void setTravelClass(String c)    { this.travelClass = c; }

    public int getNumTickets()              { return numTickets; }
    public void setNumTickets(int n)        { this.numTickets = n; }

    public double getTotalFare()            { return totalFare; }
    public void setTotalFare(double f)      { this.totalFare = f; }

    public String getStatus()               { return status; }
    public void setStatus(String s)         { this.status = s; }

    public String getBookedAt()             { return bookedAt; }
    public void setBookedAt(String b)       { this.bookedAt = b; }

    public String getTrainName()            { return trainName; }
    public void setTrainName(String n)      { this.trainName = n; }

    @Override
    public String toString() {
        return String.format(
            "BookingID: %-5d | Train: %-10s %-20s | %s -> %s | Date: %-12s | Class: %-7s | Tickets: %d | Fare: %.2f | Status: %s",
            bookingId, trainNumber, (trainName != null ? trainName : ""),
            origin, destination, travelDate, travelClass, numTickets, totalFare, status
        );
    }
}
