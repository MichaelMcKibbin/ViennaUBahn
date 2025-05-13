package com.michaelmckibbin.viennaubahn;

import java.util.*;

//    The main differences from the recursive version are:
//    Explicit stack management instead of call stack
//    State management through SearchState objects
//    Iterative loop instead of recursive calls
//    More explicit control over the search process

public class DFS_IterativeRouteFinder implements RouteFinder {
    private final Graph graph;
    private int nodesVisited;
    private int maxQueueSize;
    private int maxPaths = 10;
    private double maxDeviation = 1.5;
    private double similarityLevel = 0.7;
    private BFSRouteFinder bfsRouteFinder;

    public DFS_IterativeRouteFinder(Graph graph) {
        this.graph = graph;
        this.nodesVisited = 0;
        this.maxQueueSize = 0;
        this.bfsRouteFinder = new BFSRouteFinder(graph);
    }

    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
        System.out.println("Starting DFS Iterative route search...");
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

        int shortestLength = shortestRoute.getStations().size();
        int maxAllowedLength = (int)(shortestLength * maxDeviation);

        System.out.println("Shortest path length through waypoints: " + shortestLength);
        System.out.println("Maximum allowed length: " + maxAllowedLength);

        List<List<Station>> foundPaths = new ArrayList<>();
        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        // Find routes between consecutive waypoints
        for (int i = 0; i < allPoints.size() - 1; i++) {
            Station segmentStart = allPoints.get(i);
            Station segmentEnd = allPoints.get(i + 1);

            List<List<Station>> segmentRoutes = findMultipleDirectRoutes(segmentStart, segmentEnd);
            if (segmentRoutes == null || segmentRoutes.isEmpty()) {
                return null;
            }

            // For the first segment, add all routes to foundPaths
            if (i == 0) {
                foundPaths.addAll(segmentRoutes);
            } else {
                // Combine existing paths with new segment routes
                List<List<Station>> newPaths = new ArrayList<>();
                for (List<Station> existingPath : foundPaths) {
                    for (List<Station> segmentRoute : segmentRoutes) {
                        List<Station> combinedPath = new ArrayList<>(existingPath);
                        combinedPath.addAll(segmentRoute.subList(1, segmentRoute.size()));
                        if (combinedPath.size() <= maxAllowedLength) {
                            newPaths.add(combinedPath);
                        }
                    }
                }
                foundPaths = newPaths;
            }
        }

        // Sort paths by length and limit to maxPaths
        foundPaths.sort((path1, path2) -> path1.size() - path2.size());
        if (foundPaths.size() > maxPaths) {
            foundPaths = foundPaths.subList(0, maxPaths);
        }

        return foundPaths;
    }

    private List<List<Station>> findMultipleDirectRoutes(Station start, Station end) {
        List<List<Station>> paths = new ArrayList<>();
        Set<Station> visited = new HashSet<>();
        Stack<SearchState> stack = new Stack<>();

        // Initialize the stack with the start state
        stack.push(new SearchState(start, new ArrayList<>(List.of(start)), visited));

        while (!stack.isEmpty() && paths.size() < maxPaths) {
            SearchState current = stack.pop();
            Station currentStation = current.station;
            List<Station> currentPath = current.path;
            Set<Station> currentVisited = current.visited;

            nodesVisited++;
            maxQueueSize = Math.max(maxQueueSize, stack.size());

            if (currentStation.equals(end)) {
                if (isPathSufficientlyDifferent(currentPath, paths)) {
                    paths.add(new ArrayList<>(currentPath));
                    System.out.println("Found path of length: " + currentPath.size());
                }
                continue;
            }

            // Get neighbors and add to stack in reverse order (to maintain similar order to recursive DFS)
            List<Station> neighbors = new ArrayList<>(graph.getNeighbors(currentStation));
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                Station neighbor = neighbors.get(i);
                if (!currentVisited.contains(neighbor)) {
                    // Create new path and visited set for this branch
                    List<Station> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    Set<Station> newVisited = new HashSet<>(currentVisited);
                    newVisited.add(neighbor);

                    stack.push(new SearchState(neighbor, newPath, newVisited));
                }
            }
        }

        return paths;
    }

    private boolean isPathSufficientlyDifferent(List<Station> newPath, List<List<Station>> existingPaths) {
        if (existingPaths.isEmpty()) {
            return true;
        }

        for (List<Station> existingPath : existingPaths) {
            Set<Station> newPathSet = new HashSet<>(newPath);
            Set<Station> existingPathSet = new HashSet<>(existingPath);
            newPathSet.retainAll(existingPathSet);

            double similarity = (double) newPathSet.size() / Math.min(newPath.size(), existingPath.size());
            if (similarity > similarityLevel) {
                return false;
            }
        }
        return true;
    }

    // Helper class to store search state
    private static class SearchState {
        Station station;
        List<Station> path;
        Set<Station> visited;

        SearchState(Station station, List<Station> path, Set<Station> visited) {
            this.station = station;
            this.path = path;
            this.visited = visited;
        }
    }

    // Setters for parameters
    public void setMaxPaths(int maxPaths) {
        this.maxPaths = maxPaths;
    }

    public void setMaxDeviation(double maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    public void setSimilarityLevel(double similarityLevel) {
        this.similarityLevel = similarityLevel;
    }
}

