package com.michaelmckibbin.viennaubahn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the Dijkstra algorithm to find the shortest path between two stations.
 * It uses a priority queue to efficiently select the next station to visit based on the current
 * shortest distance to that station.
 */
public class DijkstraRouteFinder implements RouteFinder {
    private final Graph graph;
    private Map<String, List<SubwayEdge>> adjacencyList;
    private Map<String, Station> stationMap;
    private RouteMetric currentMetric = RouteMetric.DISTANCE; // default metric
    //private final double LINE_CHANGE_PENALTY = 5.0; // Adjust as needed
    private double lineChangePenalty = 5.0; // default value


    public DijkstraRouteFinder(Graph graph) {
        this.graph = graph;
        this.adjacencyList = new HashMap<>();
        this.stationMap = new HashMap<>();
        loadEdgeData();
    }

    private void loadEdgeData() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            getClass().getResourceAsStream("/com/michaelmckibbin/viennaubahn/data/vienna_subway_dijkstras.csv")))) {

        String line = reader.readLine(); // Skip header
        System.out.println("Header line: " + line); // Debug
        int lineCount = 0;

        while ((line = reader.readLine()) != null) {
            lineCount++;
            String[] data = line.split(",");

            // Debug output
            System.out.println("Processing line " + lineCount + ": " + line);

            String edgeId = data[0].trim();
            String fromStationName = data[1].trim();
            String toStationName = data[2].trim();
            String lineNumber = data[3].trim();
            String lineColor = data[4].trim();

            // Debug station names
            System.out.println("Adding edge from " + fromStationName + " to " + toStationName);

            // Convert coordinates to integers (round or floor if needed)
            int x = (int)Double.parseDouble(data[5].trim());
            int y = (int)Double.parseDouble(data[6].trim());

            // Use double for measurements that might have decimal places
            double travelTime = Double.parseDouble(data[7].trim());
            double distance = Double.parseDouble(data[8].trim());
            double cost = Double.parseDouble(data[9].trim());

            // Create or get stations
            Station fromStation = stationMap.computeIfAbsent(fromStationName,
                name -> new Station(name, lineNumber, lineColor, x, y));
            Station toStation = stationMap.computeIfAbsent(toStationName,
                name -> new Station(name, lineNumber, lineColor, x, y));

            // Create edge with travel time as int if needed and include lineNumber
            SubwayEdge edge = new SubwayEdge(edgeId, fromStation, toStation,
                                           (int)travelTime, distance, cost, lineNumber);

            // Add to adjacency list and create reverse edge for bidirectional travel
            adjacencyList.computeIfAbsent(fromStationName, k -> new ArrayList<>()).add(edge);

            // Create reverse edge with same properties
            SubwayEdge reverseEdge = new SubwayEdge(edgeId + "_rev", toStation, fromStation,
                    (int)travelTime, distance, cost, lineNumber);
            adjacencyList.computeIfAbsent(toStationName, k -> new ArrayList<>()).add(reverseEdge);
        }

        // Debug output after loading
        System.out.println("Loaded " + lineCount + " edges");
        System.out.println("Number of stations: " + stationMap.size());
        System.out.println("Adjacency list size: " + adjacencyList.size());

        // Print some sample connections
        adjacencyList.forEach((station, edges) -> {
            System.out.println("Station " + station + " has " + edges.size() + " connections to: " +
                    edges.stream()
                            .map(e -> e.getToStation().getName())
                            .collect(Collectors.joining(", ")));
        });

    } catch (IOException | NumberFormatException e) {
        System.err.println("Error reading edge data: " + e.getMessage());
        e.printStackTrace();
    }
}



//    private void loadEdgeData() {
//    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//            getClass().getResourceAsStream("/com/michaelmckibbin/viennaubahn/data/vienna_subway_dijkstras.csv")))) {
//
//        String line = reader.readLine(); // Skip header
//        System.out.println("Header line: " + line); // Debug
//        int lineCount = 0;
//
//        while ((line = reader.readLine()) != null) {
//            lineCount++;
//            String[] data = line.split(",");
//
//            // Debug output
//            System.out.println("Processing line " + lineCount + ": " + line);
//
//            String edgeId = data[0].trim();
//            String fromStationName = data[1].trim();
//            String toStationName = data[2].trim();
//            String lineNumber = data[3].trim();
//            String lineColor = data[4].trim();
//
//            // Debug station names
//            System.out.println("Adding edge from " + fromStationName + " to " + toStationName);
//
//            // Convert coordinates to integers (round or floor if needed)
//            int x = (int)Double.parseDouble(data[5].trim());
//            int y = (int)Double.parseDouble(data[6].trim());
//
//            // Use double for measurements that might have decimal places
//            double travelTime = Double.parseDouble(data[7].trim());
//            double distance = Double.parseDouble(data[8].trim());
//            double cost = Double.parseDouble(data[9].trim());
//
//            // Create or get stations
//            Station fromStation = stationMap.computeIfAbsent(fromStationName,
//                name -> new Station(name, lineNumber, lineColor, x, y));
//            Station toStation = stationMap.computeIfAbsent(toStationName,
//                name -> new Station(name, lineNumber, lineColor, x, y));
//
//            // Create edge with travel time as int if needed
//            SubwayEdge edge = new SubwayEdge(edgeId, fromStation, toStation,
//                                           (int)travelTime, distance, cost);
//
//            // Add to adjacency list and create reverse edge for bidirectional travel
//            adjacencyList.computeIfAbsent(fromStationName, k -> new ArrayList<>()).add(edge);
//
//            // Create reverse edge with same properties
//            SubwayEdge reverseEdge = new SubwayEdge(edgeId + "_rev", toStation, fromStation,
//                    (int)travelTime, distance, cost);
//            adjacencyList.computeIfAbsent(toStationName, k -> new ArrayList<>()).add(reverseEdge);
//        }
//
//        // Debug output after loading
//        System.out.println("Loaded " + lineCount + " edges");
//        System.out.println("Number of stations: " + stationMap.size());
//        System.out.println("Adjacency list size: " + adjacencyList.size());
//
//        // Print some sample connections
//        adjacencyList.forEach((station, edges) -> {
//            System.out.println("Station " + station + " has " + edges.size() + " connections to: " +
//                    edges.stream()
//                            .map(e -> e.getToStation().getName())
//                            .collect(Collectors.joining(", ")));
//        });
//
//    } catch (IOException | NumberFormatException e) {
//        System.err.println("Error reading edge data: " + e.getMessage());
//        e.printStackTrace();
//    }
//}
    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
        if (waypoints.isEmpty()) {
            return findShortestPath(start, end);
        }

        // Handle waypoints by finding paths between consecutive points
        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        List<Station> completePath = new ArrayList<>();
        int totalNodesVisited = 0;
        int maxQueueSize = 0;

        for (int i = 0; i < allPoints.size() - 1; i++) {
            RoutePath segment = findShortestPath(allPoints.get(i), allPoints.get(i + 1));
            if (segment == null) return null;

            if (i > 0) {
                // Remove first station to avoid duplicates when connecting paths
                completePath.addAll(segment.getStations().subList(1, segment.getStations().size()));
            } else {
                completePath.addAll(segment.getStations());
            }

            totalNodesVisited += segment.getNodesVisited();
            maxQueueSize = Math.max(maxQueueSize, segment.getMaxQueueSize());
        }

        return new RoutePath(completePath, 0L, totalNodesVisited, maxQueueSize);
    }

    private double getEdgeWeight(SubwayEdge edge, RouteMetric metric, String currentLine) {
        double weight = switch (metric) {
            case DISTANCE -> edge.getDistance();
            case TIME -> edge.getTravelTime();
            case COST -> edge.getCost();
        };

        // Add line change penalty if changing lines
        if (currentLine != null && !currentLine.equals(edge.getLine())) {
            weight += lineChangePenalty;
        }

        return weight;
    }

    private RoutePath findShortestPath(Station start, Station end) {
        System.out.println("Starting Dijkstra search from " + start.getName() + " to " + end.getName());
        System.out.println("Number of stations in graph: " + stationMap.size());

        Map<String, Double> distances = new HashMap<>();
        Map<String, Station> previousStations = new HashMap<>();
        Map<String, String> currentLines = new HashMap<>(); // Track current line for each station
        PriorityQueue<Station> queue = new PriorityQueue<>(
                (a, b) -> Double.compare(distances.getOrDefault(a.getName(), Double.MAX_VALUE),
                        distances.getOrDefault(b.getName(), Double.MAX_VALUE)));
        Set<String> visited = new HashSet<>();
        int nodesVisited = 0;
        int maxQueueSize = 0;

        // Initialize distances
        for (String stationName : stationMap.keySet()) {
            distances.put(stationName, Double.MAX_VALUE);
        }
        distances.put(start.getName(), 0.0);
        currentLines.put(start.getName(), null); // Start with no line
        queue.offer(start);

        while (!queue.isEmpty()) {
            maxQueueSize = Math.max(maxQueueSize, queue.size());
            Station current = queue.poll();
            nodesVisited++;

            if (current.getName().equals(end.getName())) {
                break;
            }

            if (visited.contains(current.getName())) {
                continue;
            }
            visited.add(current.getName());

            List<SubwayEdge> edges = adjacencyList.getOrDefault(current.getName(), new ArrayList<>());
            String currentLine = currentLines.get(current.getName());

            for (SubwayEdge edge : edges) {
                if (!visited.contains(edge.getToStation().getName())) {
                    double newDist = distances.get(current.getName()) +
                            getEdgeWeight(edge, currentMetric, currentLine);

                    if (newDist < distances.get(edge.getToStation().getName())) {
                        distances.put(edge.getToStation().getName(), newDist);
                        previousStations.put(edge.getToStation().getName(), current);
                        currentLines.put(edge.getToStation().getName(), edge.getLine());
                        queue.offer(edge.getToStation());
                    }
                }
            }
        }

        // Reconstruct path
        List<Station> path = new ArrayList<>();
        Station current = end;
        while (current != null) {
            path.add(0, current);
            current = previousStations.get(current.getName());
        }

        if (path.size() <= 1) {
            System.out.println("No path found - path size: " + path.size());
            return null;
        }

        System.out.println("Path found with " + path.size() + " stations");
        return new RoutePath(path, distances.get(end.getName()).longValue(), nodesVisited, maxQueueSize);
    }


    // Add setters for parameters if needed
    public void setMaxPaths(int maxPaths) {}
    public void setMaxDeviation(double maxDeviation) {}
    public void setSimilarityLevel(double similarityLevel) {}
    public void setRouteMetric(RouteMetric metric) {
        this.currentMetric = metric;
    }
    public void setLineChangePenalty(double penalty) {
        this.lineChangePenalty = penalty;
    }

    // Add a getter for stationMap
    public boolean hasStation(String stationName) {
        return stationMap.containsKey(stationName);
    }

}
