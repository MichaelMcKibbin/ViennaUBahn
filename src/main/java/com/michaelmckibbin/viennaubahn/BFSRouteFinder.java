package com.michaelmckibbin.viennaubahn;

import java.util.*;
public class BFSRouteFinder implements RouteFinder {

    private final Graph graph;
    private int nodesVisited;
    private int maxQueueSize;

    public BFSRouteFinder(Graph graph) {
        this.graph = graph;
        this.nodesVisited = 0;
        this.maxQueueSize = 0;
    }

    @Override
    public RoutePath findRoute(Station start, Station end, List<Station> waypoints) {
        System.out.println("Starting BFS route search...");
        nodesVisited = 0;
        maxQueueSize = 0;

        try {
            List<Station> path = findRoutesWithWaypoints(start, end, waypoints);

            if (path != null) {
                System.out.println("Route found with " + path.size() + " stations");
                return new RoutePath(path, 0,  // passing 0 for timing
                        nodesVisited, maxQueueSize);
            }
            System.out.println("No route found");
            return null;
        } catch (Exception e) {
            System.err.println("Error in route finding: " + e.getMessage());
            return null;
        }
    }

    private List<Station> findRoute(Station start, Station end) {
        Queue<List<Station>> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();

        List<Station> initialPath = new ArrayList<>();
        initialPath.add(start);
        queue.add(initialPath);
        visited.add(start);

        while (!queue.isEmpty()) {
            List<Station> currentPath = queue.poll();
            Station currentStation = currentPath.get(currentPath.size() - 1);

            nodesVisited++;
            maxQueueSize = Math.max(maxQueueSize, queue.size());

            if (currentStation.equals(end)) {
                return currentPath;
            }

            for (Station neighbor : graph.getNeighbors(currentStation)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<Station> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return null;
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


}
