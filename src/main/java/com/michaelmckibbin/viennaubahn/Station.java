package com.michaelmckibbin.viennaubahn;

import java.util.*;

public class Station {
    private final String stationName;
    private final String lineName;
    private final String lineColor;
    private final int x;
    private final int y;

//    public Station(String stationName, String lineName, String lineColor, int x, int y) {
//        if (Graph.getStation(stationName) != null) {
//            throw new IllegalArgumentException("Station " + stationName + " already exists");
//        }
//
//        this.stationName = stationName;
//        this.lineName = lineName;
//        this.lineColor = lineColor;
//        this.x = x;
//        this.y = y;
//
//        // Register the station in the Graph
//        Graph.registerStation(this);
//    }


    public Station(String stationName, String lineName, String lineColor, int x, int y) {
    Station existingStation = Graph.getStation(stationName);
    if (existingStation != null && existingStation.getLineName().equals(lineName)) { // throw an exception if trying to create the same station on the same line twice.
        throw new IllegalArgumentException("Station " + stationName + " already exists on line " + lineName);
    }

    this.stationName = stationName;
    this.lineName = lineName;
    this.lineColor = lineColor;
    this.x = x;
    this.y = y;

    Graph.registerStation(this);
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
}
