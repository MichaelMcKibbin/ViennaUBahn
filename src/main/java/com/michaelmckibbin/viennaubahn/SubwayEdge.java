package com.michaelmckibbin.viennaubahn;

/**
 * Represents an edge in the graph, connecting two stations.
 * This class encapsulates the properties of an edge, including the travel time,
 * distance, cost, and the line used for the travel.
 */
public class SubwayEdge {
    private final String edgeId;
    private final Station fromStation;
    private final Station toStation;
    private final int travelTime;      // Cast to int if needed
    private final double distance;     // Keep as double
    private final double cost;         // Keep as double
    private final String line;


    public SubwayEdge(String edgeId, Station fromStation, Station toStation,
                      int travelTime, double distance, double cost, String line) {
        this.edgeId = edgeId;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.travelTime = travelTime;
        this.distance = distance;
        this.cost = cost;
        this.line = line;
    }

    // Getters
    public Station getFromStation() { return fromStation; }
    public Station getToStation() { return toStation; }
    public int getTravelTime() { return travelTime; }
    public double getDistance() { return distance; }
    public double getCost() { return cost; }
    public String getLine() { return line; }
}
