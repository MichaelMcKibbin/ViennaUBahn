package com.michaelmckibbin.viennaubahn;

import java.util.*;

// RoutePath class to represent a route between stations
public class RoutePath {
    private final List<Station> stations;
    private final long executionTimeNanos;
    private final int nodesVisited;
    private final int maxQueueSize;
    private int numberOfStops;

    // Constructor
    public RoutePath(List<Station> stations, long executionTimeNanos, int nodesVisited, int maxQueueSize) {
        this.stations = stations;
        this.executionTimeNanos = executionTimeNanos;
        this.nodesVisited = nodesVisited;
        this.maxQueueSize = maxQueueSize;
    }

    public void addStation(Station station) {
        stations.add(station);
        numberOfStops++;
    }

    public void setNumberOfStops(int stops) {
        this.numberOfStops = stops;
    }


    // Getters
    public List<Station> getStations() {
        return stations;
    }

    public long getExecutionTimeMillis() {
        return executionTimeNanos / 1_000_000; // Convert to milliseconds
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public int getNumberOfStops() {
        return stations.size() - 1;
    }


}
