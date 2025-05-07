package com.michaelmckibbin.viennaubhan;

import javafx.fxml.Initializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;


public class Graph implements Initializable {
    // A static graph object to represent the graph
    public static Graph graph;

    // A station adjacency list to represent vertices on the graph
    private Map<Station, List<Station>> adjacencyList;

    // Map to store lines and their colors
    private Map<String, String> lineColors;

    private static Map<String, Station> stationMap = new HashMap<>();

    // Static method to get a station by name
    public static Station getStation(String stationName) {
        return stationMap.get(stationName);
    }

    // Method to register a station in the map
    public static void registerStation(Station station) {
        stationMap.put(station.getName(), station);
    }

    public Graph() {
        adjacencyList = new HashMap<>();
        lineColors = new HashMap<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (graph == null) {
            graph = this;
        }
    }

    // Add a station to the graph
    public void addStation(Station station) {
        if (!adjacencyList.containsKey(station)) {
            adjacencyList.put(station, new ArrayList<>());
        }
    }

    // Add a connection between two stations
    public void addConnection(Station station1, Station station2) {
        addStation(station1);
        addStation(station2);

        // Add bidirectional connection
        if (!adjacencyList.get(station1).contains(station2)) {
            adjacencyList.get(station1).add(station2);
        }
        if (!adjacencyList.get(station2).contains(station1)) {
            adjacencyList.get(station2).add(station1);
        }
    }

    // Add a subway line with its color
    public void addLine(String lineName, String color) {
        lineColors.put(lineName, color);
    }

    // Get color for a specific line
    public String getLineColor(String lineName) {
        return lineColors.get(lineName);
    }

    // Get all stations in the graph
    public Set<Station> getAllStations() {
        return adjacencyList.keySet();
    }

    // Get neighbors of a station
    public Set<Station> getNeighbors(Station station) {
        return new HashSet<>(adjacencyList.getOrDefault(station, new ArrayList<>()));
    }

    // Your existing BFS implementation with some improvements
    public Path bfsAlgorithm(Station start, Station end) {
        if (start == null || end == null) {
            return null;
        }

        Map<Station, Station> previous = new HashMap<>();
        Queue<Station> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(end)) {
                return buildPath(previous, start, end);
            }

            for (Station neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return null; // No path found
    }

    // Helper method to build the path from the BFS result
    private Path buildPath(Map<Station, Station> previous, Station start, Station end) {
        List<Station> pathStations = new ArrayList<>();
        Station current = end;

        while (current != null) {
            pathStations.add(0, current);
            current = previous.get(current);
        }

        return new Path(pathStations);
    }

    // Method to load the graph from CSV
    public void loadFromCSV(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            Station previousStation = null;
            String currentLine = null;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String stationName = data[0].trim();
                    String lineName = data[1].trim();
                    String lineColor = data[2].trim();
                    int x = Integer.parseInt(data[3].trim());
                    int y = Integer.parseInt(data[4].trim());

                    Station station = new Station(stationName, lineName,lineColor, x, y);
                    addStation(station);

                    // Add line color if it's a new line
                    if (!lineColors.containsKey(lineName)) {
                        addLine(lineName, lineColor);
                    }

                    // Connect stations that are consecutive on the same line
                    if (currentLine != null && currentLine.equals(lineName) && previousStation != null) {
                        addConnection(previousStation, station);
                    }

                    previousStation = station;
                    currentLine = lineName;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading graph from CSV: " + e.getMessage());
        }
    }
}




//public class    Graph implements Initializable {
//    // A static graph object to represent the graph
//    public static Graph graph;
//
//    // A station adjacency list to represent vertices on the graph
//    private Map<Station, List<Station>> adjacencyList;
//
//    // Method to get the set of neighbors of a given station
//    public Set<Station> getNeighbors(Station station) {
//        return station.getNeighborStations().keySet();
//    }
//
//    // Method to find the shortest path between two stations using BFS
//    public Path bfsAlgorithm(Station start, Station end) {
//        // Initialize a previous map, queue, and visited set
//        Map<Station, Station> previous = new HashMap<>();
//        Queue<Station> queue = new LinkedList<>();
//        Set<Station> visited = new HashSet<>();
//
//        // Add the start station to the queue and visited set
//        queue.add(start);
//        visited.add(start);
//
//        // While the queue is not empty
//        while (!queue.isEmpty()) {
//            // Get the next station from the queue
//            Station current = queue.poll();
//
//            // If we have reached the end station, build the path and return it
//            if (current.equals(end)) {
//                List<Station> path = new ArrayList<>();
//                int stops = 0;
//
//                Station pathStation = end;
//                while (pathStation != null) {
//                    path.add(0, pathStation);
//                    pathStation = previous.get(pathStation);
//                    stops++;
//                }
//
//                return new Path(path, stops - 1);
//            }
//
//            // Otherwise, get the neighbors of the current station and explore them
//            Set<Station> neighbors = getNeighbors(current);
//            for (Station neighbor : neighbors) {
//                if (!visited.contains(neighbor)) {
//                    visited.add(neighbor);
//                    queue.add(neighbor);
//                    previous.put(neighbor, current);
//                }
//            }
//        }
//
//        // If no path is found, return null
//        return null;
//    }
//
//
//    // Method to find the shortest path between two stations using Dijkstra's algorithm
//    public Path dijkstraAlgorithm(Set<Station> allStations, Station start, Station end) {
//        // Initialize data structures for distances, previous stations, and a priority queue
//        Map<Station, Double> distances = new HashMap<>();
//        Map<Station, Station> previous = new HashMap<>();
//        PriorityQueue<Station> queue = new PriorityQueue<>((a, b) -> Double.compare(distances.get(a), distances.get(b)));
//
//        // Initialize the distances map with INFINITY distance for all stations, except for the start station, which has distance 0
//        for (Station station : allStations) {
//            distances.put(station, Double.MAX_VALUE);
//        }
//        distances.put(start, 0.0);
//
//        // Start the search from the start station
//        queue.add(start);
//
//        while (!queue.isEmpty()) {
//            Station current = queue.poll();
//
//            // If we have reached the end station, build the path and return it
//            if (current.equals(end)) {
//                List<Station> path = new ArrayList<>();
//                double totalDistance = 0.0;
//
//                // Build the path and calculate the total distance by backtracking through the previous stations
//                for (Station station = end; station != null; station = previous.get(station)) {
//                    path.add(0, station);
//                    if (previous.get(station) != null) {
//                        totalDistance += distances.get(station);
//                    }
//                }
//
//                return new Path(path, totalDistance);
//            }
//
//            // Otherwise, get the neighbors of the current station and explore them
//            for (Station neighbor : getNeighbors(current)) {
//                // Calculate the distance from the start station to the neighbor through the current station
//                double distance = distances.get(current) + current.getNeighborStations().get(neighbor);
//
//                // If the calculated distance is smaller than the previously recorded distance, update it
//                if (distance < distances.get(neighbor)) {
//                    distances.put(neighbor, distance);
//                    previous.put(neighbor, current);
//                    queue.add(neighbor);
//                }
//            }
//        }
//
//        // If no path is found, return null
//        return null;
//    }
//
//
//    // Method to initialize the Graph object
//    public void initialize(URL url, ResourceBundle resourceBundle){
//        // Set the static graph object to this
//        graph = this;
//    }
//}
