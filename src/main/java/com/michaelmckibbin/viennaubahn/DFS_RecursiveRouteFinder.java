package com.michaelmckibbin.viennaubahn;

import javafx.application.Platform;

import java.util.*;
import java.util.stream.Collectors;

public class DFS_RecursiveRouteFinder implements RouteFinder {
    private final Graph graph;
    private int nodesVisited;
    private int maxQueueSize;
    private Set<Station> visited;
    private List<Station> currentPath;
    private List<List<Station>> foundPaths;
    private Station searchTreeRoot;
    private Map<Station, List<Station>> searchTree = new HashMap<>();


    // Similarity, Max paths, & deviation settings
//    private static final int MAX_PATHS = 10;
//    private static final double MAX_DEVIATION = 1.5; // 1.5 = 50% longer than shortest path
//    private static final double SIMILARITY_LEVEL = 0.7; // (0.0 - 1.0) Lower value allows more similar routes. Higher value requires routes to be more different
    // Replace constants with variables
    private int maxPaths = 10;
    private double maxDeviation = 1.5;
    private double similarityLevel = 0.7;

    // Add setters for the variables
    public void setMaxPaths(int maxPaths) {
        this.maxPaths = maxPaths;
    }

    public void setMaxDeviation(double maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    public void setSimilarityLevel(double similarityLevel) {
        this.similarityLevel = similarityLevel;
    }
    //
    // end of similarity, Max paths, & deviation settings
    //

    private BFSRouteFinder bfsRouteFinder;

    public DFS_RecursiveRouteFinder(Graph graph) {
        this.graph = graph;
        this.nodesVisited = 0;
        this.maxQueueSize = 0;
        this.bfsRouteFinder = new BFSRouteFinder(graph);
    }

    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
        System.out.println("Starting DFS Recursive route search...");
        searchTreeRoot = start;  // Store the start station as the root
        System.out.println("Set search tree root to: " + start.getName());
        nodesVisited = 0;
        maxQueueSize = 0;

        try {
            List<List<Station>> paths = findMultipleRoutes(start, end, waypoints);

            if (paths != null && !paths.isEmpty()) {
                return new RoutePath(paths.get(0), 0, nodesVisited, maxQueueSize);
            }
            System.out.println("No route found");
            return null;
        } catch (Exception e) {
            System.err.println("Error in route finding: " + e.getMessage());
            return null;
        }
    }

    public void setSearchTreeRoot(Station root) {
        this.searchTreeRoot = root;
        System.out.println("Search tree root set to: " + root.getName());
    }

    public Station getSearchTree() {
        System.out.println("Getting search tree, root is: " +
                (searchTreeRoot != null ? searchTreeRoot.getName() : "null"));
        return searchTreeRoot;
    }




    public List<List<Station>> findMultipleRoutes(Station start, Station end, List<Station> waypoints) {
        if (waypoints.isEmpty()) {
            return findMultipleDirectRoutes(start, end);
        }

        // Get shortest path through waypoints using BFS for reference
        RoutePath shortestRoute = bfsRouteFinder.findRoute(start, end, waypoints);
        if (shortestRoute == null) {
            System.out.println("No route found with BFS through waypoints");
            return null;
        }

//        int shortestLength = shortestRoute.getStations().size();
//        int maxAllowedLength = (int)(shortestLength * MAX_DEVIATION);
        int shortestLength = shortestRoute.getStations().size();
        int maxAllowedLength = (int)(shortestLength * maxDeviation);



        System.out.println("Shortest path length through waypoints: " + shortestLength);
        System.out.println("Maximum allowed length: " + maxAllowedLength);

        // Initialize for full path finding
        foundPaths = new ArrayList<>();

        // Find multiple routes through waypoints
        findRoutesWithWaypoints(start, end, waypoints, maxAllowedLength);

        if (foundPaths.isEmpty()) {
            System.out.println("No valid paths found within deviation limit");
            return null;
        }

        // Sort paths by length
        foundPaths.sort((path1, path2) -> path1.size() - path2.size());

        // Print info about found paths
        for (int i = 0; i < foundPaths.size(); i++) {
            System.out.println("Path " + (i + 1) + " length: " + foundPaths.get(i).size() +
                             " (+" + (foundPaths.get(i).size() - shortestLength) + " stations)");
        }

        return foundPaths;
    }

    private void findRoutesWithWaypoints(Station start, Station end, List<Station> waypoints, int maxLength) {
        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        // Try different combinations of paths between waypoints
        findWaypointCombinations(allPoints, new ArrayList<>(), 0, new ArrayList<>(), maxLength);
    }

    private void findWaypointCombinations(List<Station> points, List<Station> currentFullPath,
                                        int currentIndex, List<List<Station>> segmentPaths,
                                        int maxLength) {
        // Stop if we've found enough paths or current path is too long
//        if (foundPaths.size() >= MAX_PATHS ||
//            (currentFullPath.size() > 0 && currentFullPath.size() > maxLength)) {
//            return;
//        }
        if (foundPaths.size() >= maxPaths ||
                (currentFullPath.size() > 0 && currentFullPath.size() > maxLength)) {
            return;
        }

        // If we've processed all points
        if (currentIndex >= points.size() - 1) {
            if (!currentFullPath.isEmpty() && isPathSufficientlyDifferent(currentFullPath)) {
                foundPaths.add(new ArrayList<>(currentFullPath));
                System.out.println("Found valid path through waypoints, length: " + currentFullPath.size());
            }
            return;
        }

        // Get current segment's start and end
        Station segmentStart = points.get(currentIndex);
        Station segmentEnd = points.get(currentIndex + 1);

        // Find multiple routes for this segment
        List<List<Station>> segmentRoutes = findMultipleDirectRoutes(segmentStart, segmentEnd);
        if (segmentRoutes == null || segmentRoutes.isEmpty()) {
            return;
        }

        // Try each route for this segment
        for (List<Station> segmentRoute : segmentRoutes) {
            List<Station> newFullPath = new ArrayList<>(currentFullPath);

            // Add segment route (skip first station if not first segment to avoid duplicates)
            if (currentFullPath.isEmpty()) {
                newFullPath.addAll(segmentRoute);
            } else {
                newFullPath.addAll(segmentRoute.subList(1, segmentRoute.size()));
            }

            // Only continue if we're still within length limit
            if (newFullPath.size() <= maxLength) {
                List<List<Station>> newSegmentPaths = new ArrayList<>(segmentPaths);
                newSegmentPaths.add(segmentRoute);

                // Recurse to next segment
                findWaypointCombinations(points, newFullPath, currentIndex + 1,
                                      newSegmentPaths, maxLength);
            }
        }
    }

    private List<List<Station>> findMultipleDirectRoutes(Station start, Station end) {
        visited = new HashSet<>();
        currentPath = new ArrayList<>();
        List<List<Station>> segmentPaths = new ArrayList<>();

        // Clear the search tree before starting new search
        searchTree.clear();

        // Find direct routes between two points
        dfsRecursive(start, end, Integer.MAX_VALUE, segmentPaths);

        if (segmentPaths.isEmpty()) {
            return null;
        }

        // Sort segment paths by length
        segmentPaths.sort((path1, path2) -> path1.size() - path2.size());

//        // Return at most MAX_PATHS paths for this segment
//        return segmentPaths.subList(0, Math.min(MAX_PATHS, segmentPaths.size()));
//    }
//
//    private void dfsRecursive(Station current, Station end, int maxLength, List<List<Station>> segmentPaths) {
//        if (segmentPaths.size() >= MAX_PATHS || currentPath.size() > maxLength) {
//            return;
//        }

        // Return at most maxPaths paths for this segment
        return segmentPaths.subList(0, Math.min(maxPaths, segmentPaths.size()));
    }

    private void dfsRecursive(Station current, Station end, int maxLength, List<List<Station>> segmentPaths) {
        if (segmentPaths.size() >= maxPaths || currentPath.size() > maxLength) {
            return;
        }

        nodesVisited++;
        visited.add(current);
        currentPath.add(current);

        maxQueueSize = Math.max(maxQueueSize, currentPath.size());

        if (current.equals(end)) {
            segmentPaths.add(new ArrayList<>(currentPath));
            // Add the current path to the search tree
            for (int i = 0; i < currentPath.size() - 1; i++) {
                Station from = currentPath.get(i);
                Station to = currentPath.get(i + 1);
                searchTree.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
                System.out.println("Added path connection: " + from.getName() + " -> " + to.getName());
            }
        } else {
            Set<Station> neighbors = graph.getNeighbors(current);
            for (Station neighbor : neighbors) {
                if (!visited.contains(neighbor) && segmentPaths.size() < maxPaths) {
                    // Add this explored connection to the search tree
                    searchTree.computeIfAbsent(current, k -> new ArrayList<>()).add(neighbor);
                    System.out.println("Added explored connection: " + current.getName() + " -> " + neighbor.getName());
                    dfsRecursive(neighbor, end, maxLength, segmentPaths);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    // clear the search tree before new searches
    public void clearSearchTree() {
        searchTree.clear();
    }

//
//    private void dfsRecursive(Station current, Station end, int maxLength, List<List<Station>> segmentPaths) {
//        if (segmentPaths.size() >= maxPaths || currentPath.size() > maxLength) {
//            return;
//        }
//
//        nodesVisited++;
//        visited.add(current);
//        currentPath.add(current);
//
//        maxQueueSize = Math.max(maxQueueSize, currentPath.size());
//
//        if (current.equals(end)) {
//            segmentPaths.add(new ArrayList<>(currentPath));
//        } else {
//            Set<Station> neighbors = graph.getNeighbors(current);
//            for (Station neighbor : neighbors) {
////                if (!visited.contains(neighbor) && segmentPaths.size() < MAX_PATHS) {
////                    dfsRecursive(neighbor, end, maxLength, segmentPaths);
////                }
//                if (!visited.contains(neighbor) && segmentPaths.size() < maxPaths) {
//                    dfsRecursive(neighbor, end, maxLength, segmentPaths);
//                }
//            }
//        }
//
//        currentPath.remove(currentPath.size() - 1);
//        visited.remove(current);
//    }

    private boolean isPathSufficientlyDifferent(List<Station> newPath) {
        if (foundPaths.isEmpty()) {
            return true;
        }

        for (List<Station> existingPath : foundPaths) {
            Set<Station> newPathSet = new HashSet<>(newPath);
            Set<Station> existingPathSet = new HashSet<>(existingPath);
            newPathSet.retainAll(existingPathSet);

            // percentage similar allowed
            double similarity = (double) newPathSet.size() / Math.min(newPath.size(), existingPath.size());
            //if (similarity > SIMILARITY_LEVEL) { // When constant used
            if (similarity > similarityLevel) { // When user adjusted variable used
                return false;
            }
        }
        return true;
    }

    public List<Station> getExploredConnections(Station station) {
        List<Station> connections = searchTree.getOrDefault(station, new ArrayList<>());
        System.out.println("Getting explored connections for " + station.getName() +
                ": " + connections.size() + " connections");
        connections.forEach(s -> System.out.println("  -> " + s.getName()));
        return connections;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }





    // Make sure there's a reference to the controller
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

public Map<Station, List<Station>> getSearchTreeMap() {
    System.out.println("\nDumping search tree contents:");
    searchTree.forEach((station, connections) -> {
        System.out.println(station.getName() + " connects to:");
        connections.forEach(conn -> System.out.println("  -> " + conn.getName()));
    });
    return new HashMap<>(searchTree);
}




}
