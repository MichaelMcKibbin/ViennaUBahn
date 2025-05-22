package com.michaelmckibbin.viennaubahn;

import javafx.fxml.Initializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * This class represents a graph data structure for the Vienna U-Bahn network
 * of stations and connections between them.
 * This class implements the Initializable interface, and manages stations,
 * their connections, and line information.
 * It uses an adjacency list to store the connections.
 * The class also provides methods for BFS algorithm and path finding.
 */
public class Graph implements Initializable {
    // A static graph object to represent the graph
    public static Graph graph;

    /**
     * Adjacency list representing the connections (vertices) between stations.
     * Each station maps to a list of its connected stations.
     */
    private Map<Station, List<Station>> adjacencyList;

    /**
     * Maps U-Bahn line names to their corresponding colors.
     * For example, "U1" -> "RED"
     */
    private Map<String, String> lineColors;

    /**
     * Maps station names to Station objects for quick lookup.
     */
    private static Map<String, Station> stationMap = new HashMap<>();

    /**
     * Retrieves a station by its name.
     *
     * @param stationName the name of the station to retrieve
     * @return the Station object if found, null otherwise
     */
    public static Station getStation(String stationName) {
        return stationMap.get(stationName);
    }

    /**
     * Registers a new station in the station map.
     *
     * @param station the Station object to register
     */
    public static void registerStation(Station station) {
        stationMap.put(station.getName(), station);
    }

    /**
     * Constructs a new Graph instance.
     * Initializes the adjacency list and line colors map.
     */
    public Graph() {
        adjacencyList = new HashMap<>();
        lineColors = new HashMap<>();
    }


    /**
     * Initializes the graph as part of the JavaFX initialization process.
     * Creates the singleton instance if it doesn't exist.
     *
     * @param url the location used to resolve relative paths for the root object
     * @param resourceBundle the resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (graph == null) {
            graph = this;
        }
    }

    /**
     * Adds a new station to the graph.
     * If the station already exists, it will not create a duplicate entry.
     *
     * @param station the Station object to add to the graph
     */
    public void addStation(Station station) {
        if (!adjacencyList.containsKey(station)) {
            adjacencyList.put(station, new ArrayList<>());
        }
    }

    /**
     * Adds a bidirectional connection between two stations.
     *
     * @param station1 the first station to connect
     * @param station2 the second station to connect
     */
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
        return new HashSet<>(adjacencyList.keySet());
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
            System.out.println("Adding to path: " + current.getName() +
                    " at (" + current.getX() + "," + current.getY() + ")");

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
    // Add all lines for this station
    stationLines.get(station.getName()).addAll(station.getLines());
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

    public RoutePath findShortestPath(Station start, Station end) {
        System.out.println("Dijkstra Search initiated...");
        System.out.println("Finding route from " + start.getName() + " to " + end.getName());
        System.out.println("Number of stations in graph: " + adjacencyList.keySet().size());

        // Initialize data structures
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getDistance));
        Set<Station> visited = new HashSet<>();

        // Initialize distances
        for (Station station : adjacencyList.keySet()) {
            distances.put(station, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        pq.add(new Node(start, 0.0));

        // Performance metrics
        long startTime = System.nanoTime();
        int nodesVisited = 0;
        int maxQueueSize = 0;

        while (!pq.isEmpty()) {
            maxQueueSize = Math.max(maxQueueSize, pq.size());
            Node currentNode = pq.poll();
            Station current = currentNode.getStation();
            nodesVisited++;

            if (current.equals(end)) {
                // Path found, build the route
                List<Station> pathStations = new ArrayList<>();
                Station temp = end;
                while (temp != null) {
                    pathStations.add(0, temp);
                    temp = previous.get(temp);
                }

                long endTime = System.nanoTime();
                RoutePath route = new RoutePath(pathStations, endTime - startTime, nodesVisited, maxQueueSize);
                System.out.println("Path found with " + pathStations.size() + " stations");
                return route;
            }

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // Check all neighbors
            for (Station neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    double newDistance = distances.get(current) + Station.euclideanDistance(current, neighbor);

                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        previous.put(neighbor, current);
                        pq.add(new Node(neighbor, newDistance));
                    }
                }
            }
        }

        return null; // No path found
    }

    // Helper class for Dijkstra's algorithm
    private static class Node {
        private final Station station;
        private final double distance;

        public Node(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }

        public Station getStation() {
            return station;
        }

        public double getDistance() {
            return distance;
        }
    }
}