package com.michaelmckibbin.viennaubahn;

import java.util.*;

public class BFSRouteFinder implements RouteSearchStrategy{
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
    long startTime = System.nanoTime();

    List<Station> path = findRoutesWithWaypoints(start, end, waypoints);
    long endTime = System.nanoTime();

    if (path != null) {
        System.out.println("Route found with " + path.size() + " stations");
        return new RoutePath(path, endTime - startTime,
                getNodesVisited(), getMaxQueueSize());
    }
    System.out.println("No route found");
    return null;
}


    @Override
    public int getNodesVisited() {
        return nodesVisited;
    }

    @Override
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public List<Station> findRoute(Station start, Station end) {
        Queue<List<Station>> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();

        // Initialize with start station
        List<Station> initialPath = new ArrayList<>();
        initialPath.add(start);
        queue.add(initialPath);
        visited.add(start);

        this.nodesVisited = 0;
        this.maxQueueSize = 0;

        while (!queue.isEmpty()) {
            List<Station> currentPath = queue.poll();
            Station currentStation = currentPath.get(currentPath.size() - 1);

            nodesVisited++;
            maxQueueSize = Math.max(maxQueueSize, queue.size());

            if (currentStation.equals(end)) {
                return currentPath;  // Return the first path found
            }

            // Get neighbors from the Graph
            Set<Station> neighbors = graph.getNeighbors(currentStation);

            for (Station neighbor : neighbors) {
                if (!currentPath.contains(neighbor)) {
                    List<Station> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return null;  // No path found
    }

    public List<Station> findRoutesWithWaypoints(Station start, Station end, List<Station> waypoints) {
        if (waypoints.isEmpty()) {
            return findRoute(start, end);
        }

        List<Station> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        List<Station> completePath = new ArrayList<>();
        completePath.add(start);

        // Find routes between consecutive points
        for (int i = 0; i < allPoints.size() - 1; i++) {
            Station currentStart = allPoints.get(i);
            Station currentEnd = allPoints.get(i + 1);

            List<Station> currentRoute = findRoute(currentStart, currentEnd);
            if (currentRoute == null) {
                return null; // No valid route found
            }

            // Add all stations except the first one (to avoid duplicates)
            completePath.addAll(currentRoute.subList(1, currentRoute.size()));
        }

        return completePath;
    }
}
