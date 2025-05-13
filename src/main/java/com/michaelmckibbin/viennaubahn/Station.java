package com.michaelmckibbin.viennaubahn;

import java.util.*;

public class Station {
    private final String stationName;
    private final String lineName;
    private final String lineColor;
    private final int x;
    private final int y;
    private List<Station> connectedStations = new ArrayList<>();

    public Station(String stationName, String lineName, String lineColor, int x, int y) {
        Station existingStation = Graph.getStation(stationName);
        if (existingStation != null && existingStation.getLineName().equals(lineName)) {
            throw new IllegalArgumentException("Station " + stationName + " already exists on line " + lineName);
        }

        this.stationName = stationName;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.x = x;
        this.y = y;

        Graph.registerStation(this);
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
                System.out.println("  Connected to: " + s.getName() + " (" + s.getLineName() + ")");
            }
        }
        return connectedStations;
    }

    // getters
    public String getName() { return stationName; }
    public String getLineName() { return lineName; }
    public String getLineColor() { return lineColor; }
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
        return stationName + " (" + lineName + ")";
    }
}
