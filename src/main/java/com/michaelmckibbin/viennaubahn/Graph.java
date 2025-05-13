package com.michaelmckibbin.viennaubahn;

import javafx.fxml.Initializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * This class represents a graph of stations and connections between them.
 * It uses an adjacency list to store the connections and provides methods
 * to add stations, connections, and lines.
 */
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

    // BFS algorithm with debug output
    public RoutePath bfsAlgorithm(Station start, Station end) {
        if (start == null || end == null) {
            return null;
        }

        // Performance metrics
        long startTime = System.nanoTime();
        int nodesVisited = 0;
        int maxQueueSize = 0;


        // Queue for BFS traversal
        Queue<Station> queue = new LinkedList<>();
        // Keep track of visited stations to avoid cycles
        Set<Station> visited = new HashSet<>();
        // Store the path information (which station we came from)
        Map<Station, Station> previous = new HashMap<>();

        // Start BFS from the start station
        queue.add(start);
        visited.add(start);

        System.out.println("\nStarting BFS from: " + start.getName());
        int level = 0;
        int currentLevelSize = 1;
        int nextLevelSize = 0;

        while (!queue.isEmpty()) {
            // Track maximum queue size
            maxQueueSize = Math.max(maxQueueSize, queue.size());

            Station current = queue.poll();  // Get next station
            nodesVisited++;
            currentLevelSize--;
            System.out.println("Exploring: " + current.getName() + " (Level " + level + ")");


            // If found destination, build and return the path
            if (current.equals(end)) {
                System.out.println("Found destination!");
                long endTime = System.nanoTime();
                return buildPath(previous, start, end, endTime - startTime, nodesVisited, maxQueueSize);
            }

            // Explore all neighboring stations
            for (Station neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    System.out.println("  → Adding neighbor: " + neighbor.getName());
                    visited.add(neighbor);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                    nextLevelSize++;

                }
            }
            if (currentLevelSize == 0) {
                level++;
                currentLevelSize = nextLevelSize;
                nextLevelSize = 0;
                System.out.println("\nMoving to level " + level);
            }
        }

        return null; // No path found
    }

    // Helper method to build the path from the BFS result
    private RoutePath buildPath(Map<Station, Station> previous, Station start, Station end,
                                long executionTimeNanos, int nodesVisited, int maxQueueSize) {
        List<Station> pathStations = new ArrayList<>();
        Station current = end;

        // Work backwards from end to start
        while (current != null) {
            pathStations.add(0, current);  // Add to front of list
            current = previous.get(current);
        }

        return new RoutePath(pathStations, executionTimeNanos, nodesVisited, maxQueueSize);
    }

    // Debug method to print all loaded stations and their connections
    public void printGraphStructure() {
        System.out.println("\n=== Graph Structure ===");
        System.out.println("Total stations: " + adjacencyList.size());
        System.out.println("\nLines and Colors:");
        lineColors.forEach((line, color) ->
                System.out.println("Line: " + line + " | Color: " + color));

        System.out.println("\nStations and Connections:");
        adjacencyList.forEach((station, connections) -> {
            System.out.println("\nStation: " + station.getName());
            System.out.println("Location: (" + station.getX() + ", " + station.getY() + ")");
            System.out.println("Connected to:");
            connections.forEach(connected ->
                    System.out.println("  → " + connected.getName()));
        });
    }

    // Debug method to print transfer stations
    public void printTransferStations() {
        System.out.println("\n=== Transfer Stations ===");
        Map<String, Set<String>> stationLines = new HashMap<>();

        // Group stations by their lines
        adjacencyList.keySet().forEach(station -> {
            if (!stationLines.containsKey(station.getName())) {
                stationLines.put(station.getName(), new HashSet<>());
            }
            stationLines.get(station.getName()).add(station.getLineName());
        });

        // Print stations that appear on multiple lines
        stationLines.forEach((stationName, lines) -> {
            if (lines.size() > 1) {
                System.out.println("\nTransfer Station: " + stationName);
                System.out.println("Connected Lines: " + String.join(", ", lines));
            }
        });
    }

    // Modify loadFromCSV method to include debug output
    public void loadFromCSV(String resourcePath) {
        int lineCount = 0;
        Set<String> uniqueStations = new HashSet<>();

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            System.out.println("\n=== Loading CSV Data ===");
            System.out.println("Resource RoutePath: " + resourcePath);

            String line;
            Station previousStation = null;
            String currentLine = null;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",");
                if (data.length == 5) {
                    String stationName = data[0].trim();
                    String lineName = data[1].trim();
                    String lineColor = data[2].trim();
                    int x = Integer.parseInt(data[3].trim());
                    int y = Integer.parseInt(data[4].trim());

                    uniqueStations.add(stationName);

                    Station station = new Station(stationName, lineName, lineColor, x, y);
                    addStation(station);

                    if (!lineColors.containsKey(lineName)) {
                        addLine(lineName, lineColor);
                        System.out.println("Added new line: " + lineName + " (Color: " + lineColor + ")");
                    }

                    if (currentLine != null && currentLine.equals(lineName) && previousStation != null) {
                        addConnection(previousStation, station);
                    }

                    previousStation = station;
                    currentLine = lineName;
                }
            }

            System.out.println("\nCSV Loading Summary:");
            System.out.println("Total lines processed: " + lineCount);
            System.out.println("Unique stations found: " + uniqueStations.size());
            System.out.println("Number of subway lines: " + lineColors.size());

        } catch (IOException e) {
            System.err.println("Error loading graph from CSV: " + e.getMessage());
        }
    }
}