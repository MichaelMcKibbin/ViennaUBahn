package com.michaelmckibbin.viennaubahn;


import javafx.scene.Node;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Dijkstra's algorithm implementation for finding the shortest path between two stations.
 * This class uses a priority queue to efficiently explore the graph and find the shortest path.
 */
public class DijkstraRouteFinder implements RouteFinder {
    private static final double DEFAULT_TRANSFER_PENALTY = 5.0;
    private double lineChangePenalty = DEFAULT_TRANSFER_PENALTY;
    private RouteMetric routeMetric = RouteMetric.DISTANCE;
    private int nodesVisited;
    private int maxQueueSize;
    private final Graph graph;

    // Inner class for nodes in priority queue
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

    public DijkstraRouteFinder(Graph graph) {
        this.graph = graph;
    }

    private int calculateTransfers(List<Station> path) {
        if (path.size() < 2) return 0;

        int transfers = 0;
        Station currentStation = path.get(0);
        Set<String> currentLines = currentStation.getLines();

        for (int i = 1; i < path.size(); i++) {
            Station nextStation = path.get(i);
            Set<String> nextLines = nextStation.getLines();

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

            // Base duration between stations
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
        Map<Station, Station> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getDistance));
        Set<Station> visited = new HashSet<>();

        // Initialize distances
        for (Station station : graph.getAllStations()) {
            distances.put(station, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        pq.offer(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Station currentStation = current.getStation();
            nodesVisited++;

            if (currentStation.equals(end)) {
                break;
            }

            if (visited.contains(currentStation)) {
                continue;
            }

            visited.add(currentStation);

            for (Station neighbor : graph.getNeighbors(currentStation)) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                double newDist = distances.get(currentStation) + calculateWeight(currentStation, neighbor);
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentStation);
                    pq.offer(new Node(neighbor, newDist));
                }
            }
        }
        // Reconstruct path
        List<Station> path = new ArrayList<>();
        Station current = end;

        // Check if we actually found a path to the end
        if (!previous.containsKey(end) && !end.equals(start)) {
            System.out.println("No path found between " + start.getName() + " and " + end.getName());
            return null;
        }

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return path;
    }

    private List<Station> reconstructPath(Station start, Station end, Map<Station, Station> previous) {
        List<Station> path = new ArrayList<>();
        Station current = end;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        // Verify path starts at start station
        if (path.isEmpty() || !path.get(0).equals(start)) {
            return null;
        }

        return path;
    }

    public void setRouteMetric(RouteMetric metric) {
        this.routeMetric = metric;
    }

    public void setLineChangePenalty(double penalty) {
        this.lineChangePenalty = penalty;
    }

    private double calculateWeight(Station station1, Station station2) {
        double weight;

        switch (routeMetric) {
            case TIME:
                // Time-based weight calculation
                weight = Station.euclideanDistance(station1, station2) / 30.0; // Assume 30 units per minute
                if (!sharesLine(station1, station2)) {
                    weight += lineChangePenalty;
                }
                break;

            case COST:
                // Cost-based weight calculation
                weight = Station.euclideanDistance(station1, station2) * 0.1; // Example cost calculation
                if (!sharesLine(station1, station2)) {
                    weight += lineChangePenalty * 2; // Example: transfers cost more
                }
                break;

            case DISTANCE:
            default:
                // Distance-based weight calculation (default)
                weight = Station.euclideanDistance(station1, station2);
                if (!sharesLine(station1, station2)) {
                    weight += lineChangePenalty;
                }
                break;
        }

        return weight;
    }

    private boolean sharesLine(Station station1, Station station2) {
        Set<String> lines1 = station1.getLines();
        Set<String> lines2 = station2.getLines();
        return !Collections.disjoint(lines1, lines2);
    }

    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
        long startTime = System.nanoTime();

        // Debug logging
        System.out.println("Starting route calculation:");
        System.out.println("Start: " + start.getName());
        System.out.println("End: " + end.getName());
        System.out.println("Waypoints: " + waypoints.stream()
                .map(Station::getName)
                .collect(Collectors.joining(", ")));

        List<List<Station>> foundPaths = new ArrayList<>();
        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        // Find paths between consecutive points
        for (int i = 0; i < allPoints.size() - 1; i++) {
            Station source = allPoints.get(i);
            Station target = allPoints.get(i + 1);

            System.out.println("Finding path segment from " + source.getName() + " to " + target.getName());

            List<Station> pathSegment = findPathSegment(source, target);
            if (pathSegment == null || pathSegment.isEmpty()) {
                System.out.println("Failed to find path between " + source.getName() + " and " + target.getName());
                return null;
            }

            System.out.println("Found path segment: " + pathSegment.stream()
                    .map(Station::getName)
                    .collect(Collectors.joining(" -> ")));

            foundPaths.add(pathSegment);
        }

        // Combine all paths
        List<Station> completePath = new ArrayList<>();
        for (int i = 0; i < foundPaths.size(); i++) {
            List<Station> segment = foundPaths.get(i);
            if (i == 0) {
                completePath.addAll(segment);
            } else {
                // Skip the first station of subsequent segments to avoid duplicates
                completePath.addAll(segment.subList(1, segment.size()));
            }
        }

        System.out.println("Complete path: " + completePath.stream()
                .map(Station::getName)
                .collect(Collectors.joining(" -> ")));

        long endTime = System.nanoTime();
        int transfers = calculateTransfers(completePath);
        long duration = calculateDuration(completePath);
        int totalStops = completePath.size() - 1;

        return new RoutePath(completePath, duration, transfers, totalStops);
    }
}
