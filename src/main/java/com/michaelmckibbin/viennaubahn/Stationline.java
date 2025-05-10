package com.michaelmckibbin.viennaubahn;

import java.util.ArrayList;
import java.util.List;

public class Stationline {
    // The name of the line
    private String lineName;
    // The lineColor of the line
    private String lineColor;
    // A list of stations on the line
    private List<Station> stations;

    public Stationline(String lineName, String color) {
        // Initialize the line with a name and lineColor
        this.lineName = lineName;
        this.lineColor = color;
        // Initialize the list of stations as an empty ArrayList
        this.stations = new ArrayList<>();
    }

    public String getLineName() {
        // Get the name of the line
        return lineName;
    }

    public String getLineColor() {
        // Get the lineColor of the line
        return lineColor;
    }

    public List<Station> getStations() {
        // Get the list of stations on the line
        return stations;
    }

//    public void addStation(Station station) {
//        // Add a station to the line by adding it to the list of stations
//        this.stations.add(station);
//        station.addLine(this); // add this line to the station
//    }

}
