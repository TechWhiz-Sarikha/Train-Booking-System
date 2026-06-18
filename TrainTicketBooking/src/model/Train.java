package model;

import java.util.List;

public class Train {
    private String trainNumber;
    private String trainName;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int totalSeats;
    private int availableSeats;
    private double fareSleeper;
    private double fareAC;
    private List<String> stops;  // intermediate stops

    public Train() {}

    public Train(String trainNumber, String trainName, String origin, String destination,
                 String departureTime, String arrivalTime, int totalSeats,
                 double fareSleeper, double fareAC) {
        this.trainNumber   = trainNumber;
        this.trainName     = trainName;
        this.origin        = origin;
        this.destination   = destination;
        this.departureTime = departureTime;
        this.arrivalTime   = arrivalTime;
        this.totalSeats    = totalSeats;
        this.availableSeats= totalSeats;
        this.fareSleeper   = fareSleeper;
        this.fareAC        = fareAC;
    }

    // Getters & Setters
    public String getTrainNumber()   { return trainNumber; }
    public void setTrainNumber(String n){ this.trainNumber = n; }

    public String getTrainName()     { return trainName; }
    public void setTrainName(String n){ this.trainName = n; }

    public String getOrigin()        { return origin; }
    public void setOrigin(String o)  { this.origin = o; }

    public String getDestination()   { return destination; }
    public void setDestination(String d){ this.destination = d; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String t){ this.departureTime = t; }

    public String getArrivalTime()   { return arrivalTime; }
    public void setArrivalTime(String t){ this.arrivalTime = t; }

    public int getTotalSeats()       { return totalSeats; }
    public void setTotalSeats(int s) { this.totalSeats = s; }

    public int getAvailableSeats()   { return availableSeats; }
    public void setAvailableSeats(int s){ this.availableSeats = s; }

    public double getFareSleeper()   { return fareSleeper; }
    public void setFareSleeper(double f){ this.fareSleeper = f; }

    public double getFareAC()        { return fareAC; }
    public void setFareAC(double f)  { this.fareAC = f; }

    public List<String> getStops()   { return stops; }
    public void setStops(List<String> s){ this.stops = s; }

    @Override
    public String toString() {
        return String.format(
            "Train No: %-10s | Name: %-25s | %s -> %s | Dep: %s | Arr: %s | Seats: %d | Sleeper: %.0f | AC: %.0f",
            trainNumber, trainName, origin, destination, departureTime, arrivalTime,
            availableSeats, fareSleeper, fareAC
        );
    }
}
