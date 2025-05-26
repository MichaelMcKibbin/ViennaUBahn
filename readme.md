
# Route Finder - Vienna U-Bahn


![Original Image](docs/images/Vienna-U-Bahn-map.png "Vienna-U-Bahn-map")

## Overview

This JavaFX application allows the user to search for and find routes between stations on a railway system.
Specifically the Vienna U-Bahn in this instance.

## How It Works

The process includes:
- Station details and attributes are loaded from a database.
- A Graph data structure is created which stores information about stations, lines, and connections between them.
- Uses an adjacency list to store the connections.
- Demonstrates the differences in various search algorithms some of which generate multiple possible routes.
-  BFS: Breadth First Search.
-  DFS: Depth First Search - Iterative.
-  DFS: Depth First Search - Recursive.
-  Dijkstra's Algorithm - Shortest Route / Least Cost.
- Colour coded route map and route stations results list.
- Various metrics calculated including:
-  Calculated distance between stations.
-  Euclidian distance, travel time, & processing time.

   <br>

## Some route examples
Using the same start and finish stations to show the different results given by the various algorithms.

### Breadth First Search
<figure>
  <img src="docs/images/BFS_indirect_route_found_example.jpg" alt="Breadth First Search" title="Breadth First Search">
  <figcaption>Breadth First Search </figcaption>
</figure>

 <br>

### Depth First Search - Recursive
<figure>
  <img src="docs/images/DFS_recursive.jpg" alt="Depth First Search - Recursive" title="Depth First Search - Recursive">
  <figcaption>Depth First Search - Recursive </figcaption>
</figure>

 <br>

### Depth First Search - Iterative
<figure>
  <img src="docs/images/DFS_iterative.jpg" alt="Depth First Search - Iterative" title="Depth First Search - Iterative">
  <figcaption>Depth First Search - Iterative </figcaption>
</figure>

 <br>

### Dijkstras Shortest Path
<figure>
  <img src="docs/images/Dijkstras_route.jpg" alt="Dijkstras Shortest Path" title="Dijkstras Shortest Path">
  <figcaption>Dijkstras Shortest Path </figcaption>
</figure>

 <br>

There are more images located in the images folder, and some videos of the app in use in the videos folder


<!--
## Features
## Technical Details
-->

## Documentation

Project-related documents can be found in the [docs folder](https://github.com/MichaelMcKibbin/ViennaUBahn/tree/master/docs).<br>
More screenshots can be found in the [images folder](https://github.com/MichaelMcKibbin/ViennaUBahn/tree/master/docs/images).<br>
Some videos of the app in use can be found in the [videos folder](https://github.com/MichaelMcKibbin/ViennaUBahn/tree/master/docs/videos).<br>
