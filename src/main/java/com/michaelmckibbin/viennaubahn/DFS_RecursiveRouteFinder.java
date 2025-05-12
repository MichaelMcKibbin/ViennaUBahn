package com.michaelmckibbin.viennaubahn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFS_RecursiveRouteFinder implements RouteFinder {
    private final Graph graph;
    private int nodesVisited;
    private int maxQueueSize;
    private Set<Station> visited;
    private List<Station> currentPath;
    private List<Station> bestPath;

    public DFS_RecursiveRouteFinder(Graph graph) {
        this.graph = graph;
        this.nodesVisited = 0;
        this.maxQueueSize = 0;
    }

    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
        System.out.println("Starting DFS Recursive route search...");
        nodesVisited = 0;
        maxQueueSize = 0;

        try {
            List<Station> path = findRoutesWithWaypoints(start, end, waypoints);

            if (path != null) {
                System.out.println("Route found with " + path.size() + " stations");
                return new RoutePath(path, 0, nodesVisited, maxQueueSize);
            }
            System.out.println("No route found");
            return null;
        } catch (Exception e) {
            System.err.println("Error in route finding: " + e.getMessage());
            return null;
        }
    }

    private List<Station> findRoutesWithWaypoints(Station start, Station end, List<Station> waypoints) {
        if (waypoints.isEmpty()) {
            return findRoute(start, end);
        }

        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        List<Station> completePath = new ArrayList<>();
        completePath.add(start);

        System.out.println("Processing " + (allPoints.size() - 1) + " route segments...");

        for (int i = 0; i < allPoints.size() - 1; i++) {
            Station currentStart = allPoints.get(i);
            Station currentEnd = allPoints.get(i + 1);

            System.out.println("Finding route segment " + (i + 1) +
                    " from " + currentStart.getName() +
                    " to " + currentEnd.getName());

            List<Station> currentRoute = findRoute(currentStart, currentEnd);
            if (currentRoute == null) {
                System.out.println("Failed to find route segment " + (i + 1));
                return null;
            }

            completePath.addAll(currentRoute.subList(1, currentRoute.size()));
        }

        return completePath;
    }

    private List<Station> findRoute(Station start, Station end) {
        visited = new HashSet<>();
        currentPath = new ArrayList<>();
        bestPath = null;

        // Start the recursive DFS
        dfsRecursive(start, end);

        return bestPath;
    }

    private void dfsRecursive(Station current, Station end) {
        nodesVisited++;
        visited.add(current);
        currentPath.add(current);

        // Update maxQueueSize (for consistency with other implementations)
        maxQueueSize = Math.max(maxQueueSize, currentPath.size());

        // If we've found the destination
        if (current.equals(end)) {
            // If this is the first valid path found or it's shorter than the current best
            if (bestPath == null || currentPath.size() < bestPath.size()) {
                bestPath = new ArrayList<>(currentPath);
            }
        } else {
            // Explore neighbors
            Set<Station> neighbors = graph.getNeighbors(current);
            for (Station neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    dfsRecursive(neighbor, end);
                }
            }
        }

        // Backtrack
        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}

