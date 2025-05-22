package com.michaelmckibbin.viennaubahn;

import java.util.*;

/**
 * Represents a station in the Vienna subway system.
 * Each station has a name, line, color, and coordinates.
 * Stations can be connected to other stations, forming a graph structure.
 */
public class Station {
    private final String stationName;
    private final Set<String> lineNames;

    private final Map<String, String> lineColors; // Map line names to colors

    private final int x;
    private final int y;
    private List<Station> connectedStations = new ArrayList<>();

    public Station(String stationName, String lineName, String lineColor, int x, int y) {
        this.stationName = stationName;
        this.lineNames = new HashSet<>();
        this.lineColors = new HashMap<>();
        addLine(lineName, lineColor);
        this.x = x;
        this.y = y;

        Graph.registerStation(this);
    }

    public void addLine(String lineName, String lineColor) {
        lineNames.add(lineName);
        lineColors.put(lineName, lineColor);
    }

    public Set<String> getLineNames() {
        return Collections.unmodifiableSet(lineNames);
    }

    public String getLineColor(String lineName) {
        return lineColors.get(lineName);
    }

    public static double euclideanDistance(Station a, Station b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void addConnection(Station station) {
        if (!connectedStations.contains(station)) {
            connectedStations.add(station);
            System.out.println("Added connection: " + stationName + " -> " + station.getName());
        }
    }

    public List<Station> getConnectedStations() {
        System.out.println("Getting connections for station: " + stationName);
        if (connectedStations.isEmpty()) {
            System.out.println("No connections for " + stationName);
        } else {
            System.out.println("Number of connections: " + connectedStations.size());
            for (Station s : connectedStations) {
                System.out.println("  Connected to: " + s.getName() + " (Lines: " +
                        String.join(", ", s.getLines()) + ")");
            }
        }
        return connectedStations;
    }


    /**
     * Gets all lines this station belongs to
     * @return Set of line identifiers
     */
    public Set<String> getLines() {
        return Collections.unmodifiableSet(lineNames);
    }
    /**
     * Gets the primary line (first line) of this station
     * @return the line identifier
     */
    public String getLine() {
        return lineNames.iterator().next();
    }

    // getters
    public String getName() { return stationName; }
    public String getLinesAsString() {
        return String.join(", ", lineNames);
    }
    public String getLineColor() { return String.join(", ", lineColors.values()); }
    public String getLineAndColor() {
        StringBuilder sb = new StringBuilder();
        for (String line : lineNames) {
            sb.append(line).append(" (").append(lineColors.get(line)).append("), ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }
    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(getName(), station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return stationName + " (Lines: " + getLinesAsString() + " - " + getLineColor() + ")";
    }
}
