package com.michaelmckibbin.viennaubahn;

/**
 * Represents the different metrics used for calculating routes in the Vienna U-Bahn system.
 * These metrics determine how the optimal path between stations is calculated.
 *
 * <p>Available metrics:</p>
 * <ul>
 *     <li>{@code DISTANCE} - Calculates routes based on physical distance between stations in kilometers</li>
 *     <li>{@code TIME} - Calculates routes based on travel time between stations in minutes</li>
 *     <li>{@code COST} - Calculates routes based on travel cost between stations in euros</li>
 * </ul>
 *
 * <p>This enum is used by the route finding algorithms to determine which
 * edge weight to use when calculating the optimal path.</p>
 *
 * Example usage:
 * <pre>
 * RouteMetric metric = RouteMetric.TIME;
 * // Use this metric in route finding algorithm to optimize for fastest route
 * </pre>
 *
 * @author Michael McKibbin
 * @version 1.0
 * @see DijkstraRouteFinder
 */
public enum RouteMetric {
    /** Optimizes route based on physical distance between stations */
    DISTANCE,

    /** Optimizes route based on travel time between stations */
    TIME,

    /** Optimizes route based on travel cost between stations */
    COST

}
