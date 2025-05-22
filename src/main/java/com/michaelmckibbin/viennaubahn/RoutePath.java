package com.michaelmckibbin.viennaubahn;

import java.util.*;

// RoutePath class to represent a route between stations
public class RoutePath {
    private final List<Station> stations;
    private final long duration;
    private final int transfers;
    private final int totalStops;

    // Your existing constructor
    public RoutePath(List<Station> stations, long duration, int transfers, int totalStops) {
        this.stations = stations;
        this.duration = duration;
        this.transfers = calculateTransfers(stations); // Calculate transfers automatically
        this.totalStops = totalStops;
        validatePath(); // Add validation on construction
    }

    // Add this method to validate the path
    private void validatePath() {
        if (stations == null || stations.isEmpty()) {
            throw new IllegalStateException("Path cannot be empty");
        }

        // Validate that no station has invalid coordinates
        for (Station station : stations) {
            if (station == null) {
                throw new IllegalStateException("Path contains null station");
            }
            if (!isValidCoordinate(station.getX()) || !isValidCoordinate(station.getY())) {
                throw new IllegalStateException("Invalid coordinates for station: " + station.getName() +
                        " at (" + station.getX() + "," + station.getY() + ")");
            }
        }
    }

    private boolean isValidCoordinate(double coord) {
        return !Double.isNaN(coord) && !Double.isInfinite(coord) && coord >= 0;
    }


    public void printPathDetails() {
    try {
        System.out.println("Path Details:");
        System.out.println("Number of stations: " + stations.size());
        System.out.println("Total distance: " + calculateEuclideanDistance());

        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            // Use simple string formatting to avoid potential type conversion issues
            System.out.println("Station " + i + ": " + station.getName() +
                             " at (" + station.getX() + "," + station.getY() + ")");

            if (i > 0) {
                Station prev = stations.get(i-1);
                double segmentDistance = Station.euclideanDistance(prev, station);
                System.out.println("  Distance from previous: " + segmentDistance);
            }
        }
    } catch (Exception e) {
        System.err.println("Error printing path details: " + e.getMessage());
        e.printStackTrace();
    }
}

    // Add this method to verify path continuity
    public boolean verifyPathContinuity() {
        if (stations.size() < 2) return true;

        for (int i = 0; i < stations.size() - 1; i++) {
            Station current = stations.get(i);
            Station next = stations.get(i + 1);
            double distance = Station.euclideanDistance(current, next);

            // You might need to adjust this threshold based on your map scale
            if (distance > 100) { // arbitrary threshold, adjust as needed
                System.out.printf("Warning: Large gap detected between %s and %s: %.2f units%n",
                        current.getName(), next.getName(), distance);
                return false;
            }
        }
        return true;
    }


    public double calculateEuclideanDistance() {
        double totalEuclideanDistance = 0.0;
        for (int i = 0; i < stations.size() - 1; i++) {
            Station current = stations.get(i);
            Station next = stations.get(i + 1);
            totalEuclideanDistance += Station.euclideanDistance(current, next);
        }
        return totalEuclideanDistance;
    }

    /**
     * Calculates the number of transfers needed in the route
     * @param path List of stations in the route
     * @return Number of transfers
     */
    private int calculateTransfers(List<Station> path) {
        if (path.size() < 2) return 0;

        int transfers = 0;
        Station currentStation = path.get(0);
        Set<String> currentLines = currentStation.getLines();

        for (int i = 1; i < path.size(); i++) {
            Station nextStation = path.get(i);
            Set<String> nextLines = nextStation.getLines();

            // Check if there's any common line between current and next station
            if (Collections.disjoint(currentLines, nextLines)) {
                transfers++;
                currentLines = nextLines;
            }
        }
        return transfers;
    }


    // Getters
    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public long getDuration() {
        return duration;
    }

    public int getTransfers() {
        return transfers;
    }

    public int getNumberOfStops() {
        return totalStops;
    }


}
