package com.michaelmckibbin.viennaubahn;

import java.util.List;

public interface RouteSearchStrategy {
    RoutePath findRoute(Station start, Station end, List<Station> waypoints);
    int getNodesVisited();
    int getMaxQueueSize();
}

