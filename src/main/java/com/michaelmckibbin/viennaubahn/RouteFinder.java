package com.michaelmckibbin.viennaubahn;

import java.util.List;

public interface RouteFinder {
    RoutePath findRoute(Station start, Station end, List<Station> waypoints);
}

