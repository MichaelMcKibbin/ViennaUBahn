package com.michaelmckibbin.viennaubahn;

import javafx.scene.Node;
import javafx.scene.shape.Line;

import java.util.*;

public class DijkstraRouteFinder implements RouteFinder {
    private static final double TRANSFER_PENALTY = 5.0; // Penalty for line transfers
    private final Graph graph;
    private double lineChangePenalty = 1.0; // Default penalty for changing lines
    private int nodesVisited = 0;
    private int maxQueueSize = 0;
    private RouteMetric currentMetric = RouteMetric.DISTANCE; // Default metric


    // setter
    public void setRouteMetric(RouteMetric metric) {
        this.currentMetric = metric;
    }

    public DijkstraRouteFinder(Graph graph) {
        this.graph = graph;
    }

    public void setLineChangePenalty(double penalty) {
        this.lineChangePenalty = penalty;
    }


    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
    long startTime = System.nanoTime();

    List<Station> completePath = new ArrayList<>();
    Station currentStart = start;

    // Modified to match other route finders' approach
    List<Station> allPoints = new ArrayList<>();
    allPoints.add(start);           // Add start station first
    allPoints.addAll(waypoints);    // Add waypoints
    allPoints.add(end);            // Add end station last

    // Start from index 1 since we don't need to find path to the start station
    for (int i = 1; i < allPoints.size(); i++) {
        Station nextDestination = allPoints.get(i);
        Station currentSource = allPoints.get(i - 1);  // Get previous station as source

        List<Station> segmentPath = findPathSegment(currentSource, nextDestination);

        if (segmentPath == null || segmentPath.isEmpty()) {
            System.out.println("No valid path found through waypoint: " + nextDestination.getName());
            return null;
        }

        if (completePath.isEmpty()) {
            completePath.addAll(segmentPath);
        } else {
            completePath.addAll(segmentPath.subList(1, segmentPath.size()));
        }
    }

    long endTime = System.nanoTime();

    int transfers = calculateTransfers(completePath);
    long duration = calculateDuration(completePath);
    int totalStops = completePath.size() - 1;

    return new RoutePath(
            completePath,    // List<Station> stations
            duration,        // long duration
            transfers,       // int transfers
            totalStops      // int totalStops
    );
}


    private int calculateTransfers(List<Station> path) {
        if (path.size() < 2) return 0;

        int transfers = 0;
        Station currentStation = path.get(0);
        Set<String> currentLines = currentStation.getLines();

        for (int i = 1; i < path.size(); i++) {
            Station nextStation = path.get(i);
            Set<String> nextLines = nextStation.getLines();

            // Check if there's any common line between current and next station
            if (Collections.disjoint(currentLines, nextLines)) {
                transfers++;
                currentLines = nextLines;
            }
        }
        return transfers;
    }

    private long calculateDuration(List<Station> path) {
        if (path.size() < 2) return 0;

        long totalDuration = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Station current = path.get(i);
            Station next = path.get(i + 1);

            // Base duration between stations (using distance)
            double distance = Station.euclideanDistance(current, next);
            long baseDuration = Math.round(distance / 30.0); // Assume 30 units per minute

            // Add transfer penalty if changing lines
            if (!sharesLine(current, next)) {
                totalDuration += baseDuration + 5; // 5 minutes transfer penalty
            } else {
                totalDuration += baseDuration;
            }
        }
        return totalDuration;
    }

    private List<Station> findPathSegment(Station start, Station end) {
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previousStations = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(
                Comparator.comparingDouble(distances::get));
        Set<Station> visited = new HashSet<>();

        // Initialize distances
        for (Station station : graph.getAllStations()) { // Changed from getStations() to getAllStations()
            distances.put(station, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(end)) {
                break;
            }

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            for (Station neighbor : graph.getNeighbors(current)) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                double distance = distances.get(current) + calculateWeight(current, neighbor);

                if (distance < distances.get(neighbor)) {
                    distances.put(neighbor, distance);
                    previousStations.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return reconstructPath(start, end, previousStations);
    }

    private List<Station> reconstructPath(Station start, Station end,
                                          Map<Station, Station> previousStations) {
        List<Station> path = new ArrayList<>();
        Station current = end;

        while (current != null) {
            path.add(0, current);
            current = previousStations.get(current);
        }

        // Verify that the path starts at the start station
        if (path.isEmpty() || !path.get(0).equals(start)) {
            return null;
        }

        return path;
    }

    private double calculateWeight(Station station1, Station station2) {
        double distance = Station.euclideanDistance(station1, station2);
        // Add penalty for transfers between lines
        if (!sharesLine(station1, station2)) {
            distance += TRANSFER_PENALTY;
        }
        return distance;
    }

    private boolean sharesLine(Station station1, Station station2) {
        Set<String> lines1 = station1.getLines();
        Set<String> lines2 = station2.getLines();
        return !Collections.disjoint(lines1, lines2);
    }



    private RoutePath findDirectRoute(Station start, Station end) {
    // Reset metrics
    nodesVisited = 0;
    maxQueueSize = 0;
    long startTime = System.nanoTime(); // Start timing here

    // Initialize data structures
    Map<Station, Double> distances = new HashMap<>();
    Map<Station, Station> previous = new HashMap<>();
    PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getDistance));
    Set<Station> visited = new HashSet<>();

    // Initialize all distances to infinity
    Set<Station> allStations = graph.getAllStations();
    for (Station station : allStations) {
        distances.put(station, Double.POSITIVE_INFINITY);
    }

    // Set start distance to 0 and add to queue
    distances.put(start, 0.0);
    pq.add(new Node(start, 0.0));



    while (!pq.isEmpty()) {
        maxQueueSize = Math.max(maxQueueSize, pq.size());
        Node currentNode = pq.poll();
        Station current = currentNode.station;
        nodesVisited++;

        if (current.equals(end)) {
            break; // Found the destination
        }

        if (visited.contains(current)) {
            continue;
        }
        visited.add(current);

        // Process all neighbors
        for (Station neighbor : graph.getNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                // Calculate new distance including line change penalty if applicable
                double segmentDistance = calculateSegmentCost(current, neighbor);
                double newDistance = distances.get(current) + segmentDistance;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    pq.add(new Node(neighbor, newDistance));
                }
            }
        }
    }

    // Build the path
    if (!previous.containsKey(end)) {
        return null; // No path found
    }

    List<Station> pathStations = new ArrayList<>();
    Station current = end;
    while (current != null) {
        pathStations.add(0, current);
        current = previous.get(current);
    }

    double totalDistance = distances.get(end); // Get the actual distance to end
    long endTime = System.nanoTime(); // End timing here
    return new RoutePath(pathStations, endTime - startTime, nodesVisited, maxQueueSize);
}

    // Modify calculateSegmentCost to use the selected metric
    private double calculateSegmentCost(Station from, Station to) {
        double cost;

        switch (currentMetric) {
            case DISTANCE:
                cost = Station.euclideanDistance(from, to);
                break;
            case TIME:
                // Calculate time-based cost (you might need to adjust this based on your needs)
                cost = Station.euclideanDistance(from, to) / getAverageSpeed();
                break;
            case COST:
                // Calculate monetary cost (you might need to adjust this based on your needs)
                cost = calculateTravelCost(from, to);
                break;
            default:
                cost = Station.euclideanDistance(from, to);
        }

        // Apply line change penalty if stations are on different lines
        if (!sharesLine(from, to)) {
            cost *= lineChangePenalty;
        }

        return cost;
    }

    // Helper methods for different metrics
    private double getAverageSpeed() {
        // Return average speed in appropriate units (e.g., km/h)
        return 30.0; // Example value, adjust as needed
    }

    private double calculateTravelCost(Station from, Station to) {
        // Calculate monetary cost between stations
        // This could be based on distance, zones, or other factors
        double baseRate = 2.40; // Example base fare
        double distanceRate = 0.20; // Example rate per km
        double distance = Station.euclideanDistance(from, to);
        return baseRate + (distance * distanceRate);
    }


    private static class Node {
        private final Station station;
        private final double distance;

        public Node(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }

        public double getDistance() {
            return distance;
        }
    }


public List<RoutePath> findMultipleRoutes(Station start, Station end, List<Station> waypoints) {
    RoutePath route = findRoute(start, end, waypoints);
    return route != null ? Collections.singletonList(route) : Collections.emptyList();
}
}
