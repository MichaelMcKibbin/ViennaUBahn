package com.michaelmckibbin.viennaubahn;

import java.util.List;

/**
 * Interface for route search strategies.
 * This interface defines the contract for different algorithms
 * used to find routes between stations in Vienna's U-Bahn system.
 */
public interface RouteSearchStrategy {
    RoutePath findRoute(Station start, Station end, List<Station> waypoints);
    int getNodesVisited();
    int getMaxQueueSize();
}

