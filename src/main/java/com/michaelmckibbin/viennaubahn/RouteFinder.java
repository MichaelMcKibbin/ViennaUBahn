package com.michaelmckibbin.viennaubahn;

import java.util.List;

/**
 * Defines the contract for route finding algorithms in the Vienna U-Bahn system.
 * Implementations of this interface provide different strategies for finding paths
 * between stations, optionally including specified waypoints.
 *
 * <p>Route finders must be able to:</p>
 * <ul>
 *     <li>Find a path between start and end stations</li>
 *     <li>Incorporate optional waypoints in the route</li>
 *     <li>Return a complete {@link RoutePath} with all necessary route information</li>
 * </ul>
 *
 * @author Michael McKibbin
 * @version 1.0
 * @see RoutePath
 * @see Station
 * @see DijkstraRouteFinder
 */
public interface RouteFinder {
    /**
     * Finds a route between two stations, optionally passing through specified waypoints.
     *
     * @param start The starting station of the route
     * @param end The destination station of the route
     * @param waypoints List of intermediate stations that must be visited (can be empty or null)
     * @return A {@link RoutePath} containing the complete route information, or null if no valid route exists
     * @throws IllegalArgumentException if start or end stations are null
     */
    RoutePath findRoute(Station start, Station end, List<Station> waypoints);
}


