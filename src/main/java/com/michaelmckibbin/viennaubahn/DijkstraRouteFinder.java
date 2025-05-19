package com.michaelmckibbin.viennaubahn;

import javafx.scene.Node;
import javafx.scene.shape.Line;

import java.util.*;

public class DijkstraRouteFinder implements RouteFinder {
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

    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
    // Validate start and end stations
    if (start == null || end == null) {
        return null;
    }

    // Handle waypoints
    if (waypoints != null && !waypoints.isEmpty()) {
        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        List<Station> completePath = new ArrayList<>();
        int totalNodesVisited = 0;
        int maxQueueSize = 0;
        long startTime = System.nanoTime(); // Start timing here

        // Find route through each waypoint in sequence
        for (int i = 0; i < allPoints.size() - 1; i++) {
            Station currentStart = allPoints.get(i);
            Station currentEnd = allPoints.get(i + 1);

            RoutePath segment = findDirectRoute(currentStart, currentEnd);

            if (segment == null) {
                System.out.println("No route found between " + currentStart.getName() +
                                 " and " + currentEnd.getName());
                return null;
            }

            // Add all stations except the last one (to avoid duplicates)
            if (i < allPoints.size() - 2) {
                completePath.addAll(segment.getStations().subList(0, segment.getStations().size() - 1));
            } else {
                // For the last segment, add all stations
                completePath.addAll(segment.getStations());
            }

            totalNodesVisited += segment.getNodesVisited();
            maxQueueSize = Math.max(maxQueueSize, segment.getMaxQueueSize());
        }

        long endTime = System.nanoTime(); // End timing here
        return new RoutePath(completePath, endTime - startTime, totalNodesVisited, maxQueueSize);
    }

    // If no waypoints, use direct route finding
    return findDirectRoute(start, end);
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


//    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
//        if (start == null || end == null) {
//            return null;
//        }
//
//        // Reset metrics
//        nodesVisited = 0;
//        maxQueueSize = 0;
//
//        // Initialize data structures
//        Map<Station, Double> distances = new HashMap<>();
//        Map<Station, Station> previous = new HashMap<>();
//        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getDistance));
//        Set<Station> visited = new HashSet<>();
//
//        // Initialize all distances to infinity
//        Set<Station> allStations = graph.getAllStations();
//        for (Station station : allStations) {
//            distances.put(station, Double.POSITIVE_INFINITY);
//        }
//
//        // Set start distance to 0 and add to queue
//        distances.put(start, 0.0);
//        pq.add(new Node(start, 0.0));
//
//        long startTime = System.nanoTime();
//
//        while (!pq.isEmpty()) {
//            maxQueueSize = Math.max(maxQueueSize, pq.size());
//            Node currentNode = pq.poll();
//            Station current = currentNode.station;
//            nodesVisited++;
//
//            if (current.equals(end)) {
//                break; // Found the destination
//            }
//
//            if (visited.contains(current)) {
//                continue;
//            }
//            visited.add(current);
//
//            // Process all neighbors
//            for (Station neighbor : graph.getNeighbors(current)) {
//                if (!visited.contains(neighbor)) {
//                    // Calculate new distance including line change penalty if applicable
//                    double segmentDistance = calculateSegmentCost(current, neighbor);
//                    double newDistance = distances.get(current) + segmentDistance;
//
//                    if (newDistance < distances.get(neighbor)) {
//                        distances.put(neighbor, newDistance);
//                        previous.put(neighbor, current);
//                        pq.add(new Node(neighbor, newDistance));
//                    }
//                }
//            }
//        }
//
//        // Build the path
//        if (!previous.containsKey(end)) {
//            return null; // No path found
//        }
//
//        List<Station> pathStations = new ArrayList<>();
//        Station current = end;
//        while (current != null) {
//            pathStations.add(0, current);
//            current = previous.get(current);
//        }
//
//        long endTime = System.nanoTime();
//        return new RoutePath(pathStations, endTime - startTime, nodesVisited, maxQueueSize);
//    }

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

    private boolean sharesLine(Station station1, Station station2) {
        return station1.getLineName().equals(station2.getLineName());
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
