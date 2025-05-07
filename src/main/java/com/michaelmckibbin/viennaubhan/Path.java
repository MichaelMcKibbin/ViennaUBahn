package com.michaelmckibbin.viennaubhan;

import java.util.List;

// Path class to represent a route between stations
public class Path {
    private List<Station> stations;

    public Path(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getNumberOfStops() {
        return stations.size() - 1;
    }
}

//public class Path {
//    private List<com.michaelmckibbin.viennaubhan.Station> path; // Represents the sequence of Station objects that make up the route
//    private int stops; // The total number of stops along the route
//    private double distance; // The total distance covered by the route
//    // Constructor to initialize a Path object with a given path and number of stops
//    public Path(List<com.michaelmckibbin.viennaubhan.Station> path, int stops) {
//        this.path = path;
//        this.stops = stops;
//        this.distance = 0; // Default value for distance
//    }
//
//    // Constructor to initialize a Path object with a given path and distance
//    public Path(List<com.michaelmckibbin.viennaubhan.Station> path, double distance) {
//        this.path = path;
//        this.distance = distance;
//        this.stops = 0; // Default value for stops
//    }
//
//    // Getter method to retrieve the path of the route
//    public List<com.michaelmckibbin.viennaubhan.Station> getPath() {
//        return path;
//    }
//
//    // Getter method to retrieve the number of stops in the route
//    public int getStops() {
//        return stops;
//    }
//
//    // Setter method to set the number of stops in the route
//    public void setStops(int stops) {
//        this.stops = stops;
//    }
//
//    // Getter method to retrieve the total distance of the route
//    public double getDistance() {
//        return distance;
//    }
//}