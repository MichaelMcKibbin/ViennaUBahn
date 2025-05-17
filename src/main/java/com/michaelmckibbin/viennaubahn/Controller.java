package com.michaelmckibbin.viennaubahn;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Controller {

    private static final double ORIGINAL_WIDTH = 1488.0;
    private static final double ORIGINAL_HEIGHT = 993.0;
    private static final double FIT_WIDTH = 1000.0;
    private static final double X_OFFSET = 10.0;
    private static final double Y_OFFSET = 4.0;

//    public ListView routeListView;

    @FXML public Button dijkstraShortestPath;
    @FXML public Button bfsButton;
    @FXML public Button dfsRecursiveButton;
    @FXML public Button dfsIterativeButton;
    @FXML public Label waypointStatusLabel;
    @FXML public Button addWaypointButton;
    @FXML public Button treeviewButton;
    @FXML public TextField lineChangePenalty;


    @FXML private ImageView mapImageView;
    @FXML private StackPane mapContainer;
    @FXML private Pane routeLayer;
    @FXML private ComboBox<Station> startStationComboBox;
    @FXML private ComboBox<Station> endStationComboBox;
    @FXML private ComboBox<Station> waypointComboBox;
    @FXML private ListView<Station> waypointsListView;
    @FXML private Button removeWaypointButton;
    @FXML private Label stopsLabel;
    @FXML private Label timeLabel;
    @FXML private Label nodesLabel;
    @FXML private Label queueLabel;
    @FXML private Button resetButton;
    @FXML private VBox routeInfoBox;
    @FXML private VBox performanceBox;

    private Image colorMap;
    private Image greyMap;
    private Graph graph;
  //  private Set<Station> waypoints;
    private ObservableList<Station> waypoints = FXCollections.observableArrayList();
    private RouteVisualizer routeVisualizer;
    private RouteFinder routeFinder;
    private BFSRouteFinder bfsRouteFinder;
    private DFS_RecursiveRouteFinder dfsRecursiveRouteFinder;
    private DFS_IterativeRouteFinder dfsIterativeRouteFinder;
    private DijkstraRouteFinder dijkstraRouteFinder;
    private RouteMetricsDisplay metricsDisplay;
    @FXML private ListView<Station> routeListView;
    private List<List<Station>> currentRoutes;  // Store current routes
    private int currentRouteIndex = 0;          // Track which route is displayed
    @FXML public HBox nextRouteButtonsHBox;
    @FXML private Button nextRouteButton;
    @FXML private Button previousRouteButton;
    @FXML private Label routeNumberLabel;

    @FXML private HBox routeAdjustmentsHBox;
    @FXML private TextField maxPathsField;
    @FXML private TextField maxDeviationField;
    @FXML private TextField similarityLevelField;

    @FXML private ToggleGroup metricGroup;
    @FXML private RadioButton distanceRadio;
    @FXML private RadioButton timeRadio;
    @FXML private RadioButton costRadio;

    private RouteFinder currentRouteFinder;
    private List<RoutePath> foundRoutes = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("Initializing Controller...");
        long initStartTime = System.nanoTime();

        // Load images
        colorMap = new Image(getClass().getResourceAsStream(
            "/com/michaelmckibbin/viennaubahn/images/UBahn_Map_1.jpg"));
        greyMap = new Image(getClass().getResourceAsStream(
            "/com/michaelmckibbin/viennaubahn/images/UBahn_Map_1_Grey.jpg"));

        // Initialize map components
        MapInitializer mapInit = new MapInitializer(mapImageView, mapContainer,
                                                   ORIGINAL_WIDTH, ORIGINAL_HEIGHT,
                                                   FIT_WIDTH);
        mapInit.initialize();
        routeLayer = mapInit.getRouteLayer();

        List<Station> waypointsList = new ArrayList<>(waypoints);

        // Initialize RouteVisualizer with necessary components
        routeVisualizer = new RouteVisualizer(mapImageView, routeLayer, waypoints, colorMap, greyMap);

        // Initialize graph
        System.out.println("Initializing graph...");
        long graphStartTime = System.nanoTime();
        graph = new Graph();
        graph.loadFromCSV("/com/michaelmckibbin/viennaubahn/data/vienna_subway_list_1.csv");
        graph.printGraphStructure();
        graph.printTransferStations();
        long graphEndTime = System.nanoTime();
        long graphDuration = (graphEndTime - graphStartTime) / 1_000_000;
        System.out.println("Graph initialization took " + graphDuration + " ms.");
        System.out.println("Graph initialization complete.");

        // Initialize route finding components
        metricsDisplay = new RouteMetricsDisplay(stopsLabel, timeLabel,
                                               nodesLabel, queueLabel);
        bfsRouteFinder = new BFSRouteFinder(graph); // Initialize BFS
        dfsRecursiveRouteFinder = new DFS_RecursiveRouteFinder(graph);  // Initialize DFS Recursive
        ((DFS_RecursiveRouteFinder) dfsRecursiveRouteFinder).setController(this);
        dfsIterativeRouteFinder = new DFS_IterativeRouteFinder(graph);  // Initialize DFS Iterative

        dijkstraRouteFinder = new DijkstraRouteFinder(graph); // Initialize Dijkstras




        // Initialize station selection components
        List<Station> stations = new ArrayList<>(graph.getAllStations());
        stations.sort(Comparator.comparing(Station::getName));
        //startStationComboBox.getItems().addAll(stations);
        //endStationComboBox.getItems().addAll(stations);
        waypointComboBox.getItems().addAll(stations);
        populateStationComboBoxes();
        startStationComboBox.setOnAction(e -> validateSelections());
        endStationComboBox.setOnAction(e -> validateSelections());

        // Bind remove button's disabled state to waypoints list
        removeWaypointButton.disableProperty().bind(Bindings.isEmpty(waypoints));

        // Initialize waypoint management
        StationListManager stationManager = new StationListManager(
            waypointComboBox,
            waypointsListView,
            waypoints,
            removeWaypointButton
        );
        stationManager.initialize();
        setupRouteListView();

        // Add listeners to validate user input for similarity, deviation and max routes
        maxPathsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxPathsField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        maxDeviationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                maxDeviationField.setText(oldValue);
            }
        });

        similarityLevelField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                similarityLevelField.setText(oldValue);
            }
        });

        // Set up metric selection handling
        metricGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == distanceRadio) {
                ((DijkstraRouteFinder)dijkstraRouteFinder).setRouteMetric(RouteMetric.DISTANCE);
            } else if (newValue == timeRadio) {
                ((DijkstraRouteFinder)dijkstraRouteFinder).setRouteMetric(RouteMetric.TIME);
            } else if (newValue == costRadio) {
                ((DijkstraRouteFinder)dijkstraRouteFinder).setRouteMetric(RouteMetric.COST);
            }

            // If a route is already displayed, recalculate it with the new metric
            if (!foundRoutes.isEmpty()) {
                handleFindRouteDijkstra();
            }
        });

        // Set initial metric
        ((DijkstraRouteFinder)dijkstraRouteFinder).setRouteMetric(RouteMetric.DISTANCE);

// listener for line change penalty changes
lineChangePenalty.textProperty().addListener((observable, oldValue, newValue) -> {
    if (!newValue.equals(oldValue)) {
        // Only update if there's an active route
        if (currentRouteFinder != null && dijkstraRouteFinder != null) {
            handleFindRouteDijkstra();  // This will handle the route finding with proper station checks
        }
    }
});



        setupLineChangePenaltyField();

        long initEndTime = System.nanoTime();
        long initDuration = (initEndTime - initStartTime) / 1_000_000;
        System.out.println("Controller initialization took " + initDuration + " ms.");
        System.out.println("\nController initialization complete./n");
    } // end of initialize section


    private double getLineChangePenalty() {
    try {
        double penalty = Double.parseDouble(lineChangePenalty.getText());
        if (penalty < 0) {
            lineChangePenalty.setText("5"); // Reset to default if negative
            return 5.0;
        }
        return penalty;
    } catch (NumberFormatException e) {
        lineChangePenalty.setText("5"); // Reset to default if invalid
        return 5.0;
    }
}

@FXML
private void handleFindRouteDijkstra() {
        try{
    System.out.println("Dijkstra Search initiated...");
    clearRouteDisplay();
    currentRouteFinder = dijkstraRouteFinder;

    // Update the line change penalty before finding route
    if (dijkstraRouteFinder != null) {
        dijkstraRouteFinder.setLineChangePenalty(getLineChangePenalty());
    }

    long startTime = System.nanoTime();
    RoutePath routePath = findAndDisplayRoute();
    long endTime = System.nanoTime();

    if (routePath != null) {
        // Add detailed path debugging information
        routePath.printPathDetails();

        // Verify path continuity and alert if there are issues
        if (!routePath.verifyPathContinuity()) {
            System.out.println("WARNING: Possible discontinuities detected in the route!");
            // Print the problematic segments
            List<Station> stations = routePath.getStations();
            for (int i = 0; i < stations.size() - 1; i++) {
                Station current = stations.get(i);
                Station next = stations.get(i + 1);
                double segmentDistance = Station.euclideanDistance(current, next);
                System.out.printf("Segment %d-%d: %s to %s, distance: %.2f%n",
                    i, i+1, current.getName(), next.getName(), segmentDistance);
                System.out.printf("  Coordinates: (%d,%d) to (%d,%d)%n",
                    current.getX(), current.getY(), next.getX(), next.getY());
            }
        }

        // Calculate, scale, and print route Euclidean distance
        double euclideanDistance = calculateEuclideanDistance(routePath.getStations());
        double scaledDistance = euclideanDistance / 55;
        System.out.printf("Route Euclidean Distance: %.3f km%n", scaledDistance);

        // Calculate 'as the crow flies' distance between start and end stations
        List<Station> stations = routePath.getStations();
        Station startStation = stations.get(0);
        Station endStation = stations.get(stations.size() - 1);
        double directDistance = Math.sqrt(
                Math.pow(endStation.getX() - startStation.getX(), 2) +
                        Math.pow(endStation.getY() - startStation.getY(), 2)
        ) / 55;  // Apply same scaling factor

        System.out.printf("Direct ('as crow flies') Distance: %.3f km%n", directDistance);

        // Print route efficiency metric
        double routeEfficiency = (directDistance / scaledDistance) * 100;
        System.out.printf("Route Efficiency: %.1f%% (100%% would be a straight line)%n",
            routeEfficiency);

        // Print detailed station-by-station coordinates
        System.out.println("\nDetailed Station Coordinates:");
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            System.out.printf("Station %d: %s at (%d,%d)%n",
                i, station.getName(), station.getX(), station.getY());
        }

        // Update the regular metrics display
        metricsDisplay.updateMetrics(
                routePath.getNumberOfStops(),
                endTime - startTime,
                routePath.getNodesVisited(),
                routePath.getMaxQueueSize()
        );
    } else {
        System.out.println("No route found!");
    }
        } catch (Exception e) {
            System.err.println("Error in handleFindRouteDijkstra: " + e.getMessage());
            e.printStackTrace();
        }
}

//@FXML
//private void handleFindRouteDijkstra() {
//    System.out.println("Dijkstra Search initiated...");
//    clearRouteDisplay();
//    currentRouteFinder = dijkstraRouteFinder;
//
//    // Update the line change penalty before finding route
//    if (dijkstraRouteFinder != null) {
//        dijkstraRouteFinder.setLineChangePenalty(getLineChangePenalty());
//    }
//
//    long startTime = System.nanoTime();
//    RoutePath routePath = findAndDisplayRoute();
//    long endTime = System.nanoTime();
//
//    if (routePath != null) {
//        // Calculate, scale, and print route Euclidean distance
//        double euclideanDistance = calculateEuclideanDistance(routePath.getStations());
//        double scaledDistance = euclideanDistance / 55;
//        System.out.printf("Route Euclidean Distance: %.3f km%n", scaledDistance);
//
//        // Calculate 'as the crow flies' distance between start and end stations
//        List<Station> stations = routePath.getStations();
//        Station startStation = stations.get(0);
//        Station endStation = stations.get(stations.size() - 1);
//        double directDistance = Math.sqrt(
//                Math.pow(endStation.getX() - startStation.getX(), 2) +
//                        Math.pow(endStation.getY() - startStation.getY(), 2)
//        ) / 55;  // Apply same scaling factor
//
//        System.out.printf("Direct ('as crow flies') Distance: %.3f km%n", directDistance);
//
//        // Update the regular metrics display
//        metricsDisplay.updateMetrics(
//                routePath.getNumberOfStops(),
//                endTime - startTime,
//                routePath.getNodesVisited(),
//                routePath.getMaxQueueSize()
//        );
//    }
//}



    private void setupLineChangePenaltyField() {
        // Only allow numbers and decimal point
        lineChangePenalty.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                lineChangePenalty.setText(oldValue);
            }
        });
    }


    private String getCurrentMetricName() {
        if (distanceRadio.isSelected()) return "Distance";
        if (timeRadio.isSelected()) return "Time";
        if (costRadio.isSelected()) return "Cost";
        return "Unknown";
    }
    private void clearRouteDisplay() {
        // Clear previous routes and display
        foundRoutes.clear();
        currentRouteIndex = 0;

        // Clear any visual elements showing the route
        // Update UI elements as needed
        updateRouteNavigationControls();
    }


    private RoutePath findAndDisplayRoute() {
    if (currentRouteFinder == null) {
        System.err.println("No route finder selected!");
        return null;
    }

    Station startStation = startStationComboBox.getValue();
    Station endStation = endStationComboBox.getValue();

    if (startStation == null || endStation == null) {
        System.out.println("Start or end station not selected");
        return null;
    }

    System.out.println("Finding route from " + startStation.getName() +
                      " to " + endStation.getName());

    // Clear previous routes
    foundRoutes.clear();
    currentRouteIndex = 0;
    routeListView.getItems().clear();

    long startTime = System.nanoTime();
    RoutePath routePath = currentRouteFinder.findRoute(startStation, endStation, waypoints);
    long endTime = System.nanoTime();
    long executionTime = endTime - startTime;

    if (routePath != null) {
        foundRoutes.add(routePath);
        routeVisualizer.drawRoute(routePath);

        // Update route list view
        routeListView.getItems().clear();
        routeListView.getItems().addAll(routePath.getStations());

        // Show the info boxes
        routeInfoBox.setVisible(true);
        performanceBox.setVisible(true);


        // Update metrics
        metricsDisplay.updateMetrics(
                routePath.getNumberOfStops(),
                executionTime,
                routePath.getNodesVisited(),
                routePath.getMaxQueueSize()
        );

        updateRouteNavigationControls();
    } else {
        System.out.println("No route found");
        routeInfoBox.setVisible(false);
        performanceBox.setVisible(false);
    }

        return routePath;
    }



    private void updateRouteMetrics(RoutePath routePath, long duration) {
        System.out.println("\nRoute Metrics:");
        System.out.println("-------------");
        System.out.println("Stops: " + routePath.getStations().size());
        System.out.println("Time: " + (duration / 1_000_000.0) + " ms (" +
                String.format("%.2f", duration / 1_000.0) + " μs)");
        System.out.println("Raw time: " + duration + " ns");
        System.out.println("Nodes Visited: " + routePath.getNodesVisited());
        System.out.println("Max Queue Size: " + routePath.getMaxQueueSize());
        System.out.println("-------------");
    }
    void updateRouteNavigationControls() {
        boolean hasMultipleRoutes = foundRoutes.size() > 1;
        previousRouteButton.setDisable(!hasMultipleRoutes || currentRouteIndex == 0);
        nextRouteButton.setDisable(!hasMultipleRoutes ||
                currentRouteIndex >= foundRoutes.size() - 1);
        routeNumberLabel.setText("Route " + (currentRouteIndex + 1) +
                " of " + foundRoutes.size());

    }

@FXML
private void handleFindRouteDFSRecursive() {
    System.out.println("DFS Recursive Search initiated...");

    // Update parameters before search
    updateDFSParameters();

    Station start = startStationComboBox.getValue();
    Station end = endStationComboBox.getValue();

    if (start == null || end == null) {
        System.out.println("Start or end station not selected");
        return;
    }

    try {
        // Get waypoints
        List<Station> waypointsList = new ArrayList<>(waypoints);

        System.out.println("Finding DFS Recursive route from " + start.getName() + " to " + end.getName());
        if (!waypointsList.isEmpty()) {
            System.out.println("With waypoints: " + waypointsList);
        }

        // Set the root station before starting the search
        dfsRecursiveRouteFinder.setSearchTreeRoot(start);
        System.out.println("Set search tree root to: " + start.getName());

        // Start timing
        long startTime = System.nanoTime();

        // Get multiple routes
        currentRoutes = dfsRecursiveRouteFinder.findMultipleRoutes(start, end, waypointsList);

        // End timing
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        if (currentRoutes != null && !currentRoutes.isEmpty()) {
            currentRouteIndex = 0;
            displayCurrentRoute(executionTime);

            // Enable/disable navigation buttons
            updateNavigationButtons();

            // Verify search tree root is still set
            Station rootStation = dfsRecursiveRouteFinder.getSearchTree();
            System.out.println("After route finding, search tree root is: " +
                (rootStation != null ? rootStation.getName() : "null"));

            System.out.println("Route(s) found successfully!");
        } else {
            System.out.println("No route(s) found!");
            routeVisualizer.resetMap();
        }
    } catch (Exception e) {
        System.err.println("Error finding route: " + e.getMessage());
        e.printStackTrace();
        routeVisualizer.resetMap();
    }
}


    @FXML
    private void handleFindRouteDFSIterative() {
        System.out.println("DFS Iterative Search initiated...");

        // Update parameters before search
        updateDFSParameters();

        Station start = startStationComboBox.getValue();
        Station end = endStationComboBox.getValue();

        if (start == null || end == null) {
            System.out.println("Start or end station not selected");
            return;
        }

        try {
            // Get waypoints
            List<Station> waypointsList = new ArrayList<>(waypoints);

            System.out.println("Finding DFS Iterative route from " + start.getName() + " to " + end.getName());
            if (!waypointsList.isEmpty()) {
                System.out.println("With waypoints: " + waypointsList);
            }

            // Start timing
            long startTime = System.nanoTime();

//            // Find route
//            RoutePath routePath = dfsIterativeRouteFinder.findRoute(start, end, waypointsList);

            // Get multiple routes
            currentRoutes = dfsIterativeRouteFinder.findMultipleRoutes(start, end, waypointsList);


            // End timing
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
//
//            if (routePath != null) {
//                routeVisualizer.drawRoute(routePath);
//
//                // Update route list view
//                routeListView.getItems().clear();
//                routeListView.getItems().addAll(routePath.getStations());
//
//                // Update route list and metrics
//                routeInfoBox.setVisible(true);
//                performanceBox.setVisible(true);
//
//                metricsDisplay.updateMetrics(
//                        routePath.getNumberOfStops(),
//                        executionTime,
//                        routePath.getNodesVisited(),
//                        routePath.getMaxQueueSize()
//                );

            if (currentRoutes != null && !currentRoutes.isEmpty()) {
                currentRouteIndex = 0;
                displayCurrentRoute(executionTime);

                // Enable/disable navigation buttons
                updateNavigationButtons();


                System.out.println("Route found successfully!");
            } else {
                System.out.println("No route found!");
                routeVisualizer.resetMap();
            }
        } catch (Exception e) {
            System.err.println("Error finding route: " + e.getMessage());
            e.printStackTrace();
            routeVisualizer.resetMap();
        }

    }

//    private void updateDijkstraParameters() { // maybe later if allowing multiple (same cost) shortest paths
//        try {
//            int maxPaths = Integer.parseInt(maxPathsField.getText());
//            double maxDeviation = Double.parseDouble(maxDeviationField.getText());
//            double similarityLevel = Double.parseDouble(similarityLevelField.getText());
//
//            // Validate ranges
//            if (maxPaths < 1) maxPaths = 1;
//            if (maxPaths > 100) maxPaths = 10;
//            if (maxDeviation < 1.0) maxDeviation = 1.0;
//            if (maxDeviation > 2.0) maxDeviation = 2.0;
//            if (similarityLevel < 0.0) similarityLevel = 0.0;
//            if (similarityLevel > 1.0) similarityLevel = 1.0;
//
//            // Update the fields with validated values
//            maxPathsField.setText(String.valueOf(maxPaths));
//            maxDeviationField.setText(String.format("%.1f", maxDeviation));
//            similarityLevelField.setText(String.format("%.1f", similarityLevel));
//
//            // Update the DFS parameters
//            dijkstraRouteFinder.setMaxPaths(maxPaths);
//            dijkstraRouteFinder.setMaxDeviation(maxDeviation);
//            dijkstraRouteFinder.setSimilarityLevel(similarityLevel);
//
//        } catch (NumberFormatException e) {
//            // Handle invalid input
//            System.err.println("Invalid input in route adjustment fields");
//        }
//    }

    private void updateDFSParameters() {
        try {
            int maxPaths = Integer.parseInt(maxPathsField.getText());
            double maxDeviation = Double.parseDouble(maxDeviationField.getText());
            double similarityLevel = Double.parseDouble(similarityLevelField.getText());

            // Validate ranges
            if (maxPaths < 1) maxPaths = 1;
            if (maxPaths > 100) maxPaths = 10;
            if (maxDeviation < 1.0) maxDeviation = 1.0;
            if (maxDeviation > 2.0) maxDeviation = 2.0;
            if (similarityLevel < 0.0) similarityLevel = 0.0;
            if (similarityLevel > 1.0) similarityLevel = 1.0;

            // Update the fields with validated values
            maxPathsField.setText(String.valueOf(maxPaths));
            maxDeviationField.setText(String.format("%.1f", maxDeviation));
            similarityLevelField.setText(String.format("%.1f", similarityLevel));

            // Update the DFS parameters
            dfsRecursiveRouteFinder.setMaxPaths(maxPaths);
            dfsRecursiveRouteFinder.setMaxDeviation(maxDeviation);
            dfsRecursiveRouteFinder.setSimilarityLevel(similarityLevel);

        } catch (NumberFormatException e) {
            // Handle invalid input
            System.err.println("Invalid input in route adjustment fields");
        }
    }

    private void displayCurrentRoute(long executionTime) {
        List<Station> currentPath = currentRoutes.get(currentRouteIndex);

        routeVisualizer.drawRoute(new RoutePath(currentPath, executionTime,
                dfsRecursiveRouteFinder.getNodesVisited(),
                dfsRecursiveRouteFinder.getMaxQueueSize()));

        routeListView.getItems().clear();
        routeListView.getItems().addAll(currentPath);

        routeInfoBox.setVisible(true);
        performanceBox.setVisible(true);

        routeNumberLabel.setText(String.format("Route %d of %d",
                currentRouteIndex + 1, currentRoutes.size()));

        metricsDisplay.updateMetrics(
                currentPath.size(),
                executionTime,
                dfsRecursiveRouteFinder.getNodesVisited(),
                dfsRecursiveRouteFinder.getMaxQueueSize()
        );
    }

    private double calculateEuclideanDistance(List<Station> stations) {
        double totalEuclideanDistance = 0.0;
        for (int i = 0; i < stations.size() - 1; i++) {
            Station current = stations.get(i);
            Station next = stations.get(i + 1);
            double dx = next.getX() - current.getX();
            double dy = next.getY() - current.getY();
            totalEuclideanDistance += Math.sqrt(dx * dx + dy * dy);
        }
        return totalEuclideanDistance;
    }

    @FXML
    private void handleNextRoute() {
        if (currentRoutes != null && currentRouteIndex < currentRoutes.size() - 1) {
            currentRouteIndex++;
            displayCurrentRoute(0);  // Pass 0 for execution time as it's already been measured
            updateNavigationButtons();
        }
    }

    @FXML
    private void handlePreviousRoute() {
        if (currentRoutes != null && currentRouteIndex > 0) {
            currentRouteIndex--;
            displayCurrentRoute(0);
            updateNavigationButtons();
        }
    }

    private void updateNavigationButtons() {
        previousRouteButton.setDisable(currentRouteIndex == 0);
        nextRouteButton.setDisable(currentRouteIndex >= currentRoutes.size() - 1);
    }



    @FXML
    private void handleRemoveWaypoint() {
        Station selectedWaypoint = waypointsListView.getSelectionModel().getSelectedItem();
        if (selectedWaypoint != null) {
            waypoints.remove(selectedWaypoint);
    //        if (waypoints.isEmpty()) {
    //            removeWaypointButton.setDisable(true);
    //        }
        }
    }

@FXML
private void handleResetMap() {
    handleReset(); // calls the existing reset method
}


    @FXML
private void handleFindRouteBFS() {
    System.out.println("BFS Search initiated...");

    Station start = startStationComboBox.getValue();
    Station end = endStationComboBox.getValue();

    if (start == null || end == null) {
        System.out.println("Start or end station not selected");
        return;
    }

    try {
        // Get waypoints
        List<Station> waypointsList = new ArrayList<>(waypoints);

        System.out.println("Finding BFS route from " + start.getName() + " to " + end.getName());
        if (!waypointsList.isEmpty()) {
            System.out.println("With waypoints: " + waypointsList);
        }

        // Start timing
        long startTime = System.nanoTime();

        // Find route
        RoutePath routePath = bfsRouteFinder.findRoute(start, end, waypointsList);

        // End timing
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        if (routePath != null) {
            routeVisualizer.drawRoute(routePath);

            // Update route list view
            routeListView.getItems().clear();
            routeListView.getItems().addAll(routePath.getStations());

            // Update route list and metrics
            routeInfoBox.setVisible(true);
            performanceBox.setVisible(true);

            metricsDisplay.updateMetrics(
                routePath.getNumberOfStops(),
                executionTime,  // Pass the measured execution time
                routePath.getNodesVisited(),
                routePath.getMaxQueueSize()
            );

            System.out.println("Route found successfully!");
        } else {
            System.out.println("No route found!");
            routeVisualizer.resetMap();
        }
    } catch (Exception e) {
        System.err.println("Error finding route: " + e.getMessage());
        e.printStackTrace();
        routeVisualizer.resetMap();
    }
}

private void setupRouteListView() {
    routeListView.setCellFactory(listView -> new ListCell<Station>() {
        @Override
        protected void updateItem(Station station, boolean empty) {
            super.updateItem(station, empty);

            if (empty || station == null) {
                setText(null);
                setStyle(null);
            } else {
                setText(station.getName() + " (" + station.getLineName() + ")");

                String lineColor = station.getLineColor().toUpperCase();
                String backgroundColor;
                String textColor = "white"; // default text color

                switch (lineColor) {
                    case "RED":
                        backgroundColor = "rgba(255, 0, 0, 0.75)";
                        break;
                    case "PURPLE":
                        backgroundColor = "rgba(128, 0, 128, 0.75)";
                        break;
                    case "ORANGE":
                        backgroundColor = "rgba(255, 165, 0, 0.75)";
                        break;
                    case "BROWN":
                        backgroundColor = "rgba(165, 42, 42, 0.75)";
                        break;
                    case "GREEN":
                        backgroundColor = "rgba(0, 128, 0, 0.75)";
                        break;
                    default:
                        backgroundColor = "white";
                }

                setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s;",
                    backgroundColor, textColor));
            }
        }
    });
}




    @FXML
private void handleAddWaypoint() {
    Station selectedStation = waypointComboBox.getValue();
    if (selectedStation != null && !waypoints.contains(selectedStation)) {
        waypoints.add(selectedStation);
        waypointComboBox.setValue(null);
        //removeWaypointButton.setDisable(false);
    }
}


    @FXML
    private void handleReset() {
        routeVisualizer.resetMap();
        routeInfoBox.setVisible(false);
        performanceBox.setVisible(false);
        waypoints.clear();
        waypointsListView.getItems().clear();
        startStationComboBox.setValue(null);
        endStationComboBox.setValue(null);
        routeListView.getItems().clear();
    }

    private void populateStationComboBoxes() {
        List<Station> stations = new ArrayList<>(graph.getAllStations());
        stations.sort(Comparator.comparing(Station::getName));
        startStationComboBox.getItems().addAll(stations);
        endStationComboBox.getItems().addAll(stations);
    }

    private void validateSelections() {
        Station start = startStationComboBox.getValue();
        Station end = endStationComboBox.getValue();
        if (start != null && end != null && start.equals(end)) {
            endStationComboBox.setValue(null);
        }
    }

@FXML
private void showTreeView() {
    try {
        System.out.println("Opening tree view...");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/michaelmckibbin/viennaubahn/views/route-tree-view.fxml"));
        Parent root = fxmlLoader.load();

        RouteTreeViewController treeController = fxmlLoader.getController();

        if (dfsRecursiveRouteFinder != null) {
            Station rootStation = dfsRecursiveRouteFinder.getSearchTree();
            Map<Station, List<Station>> treeMap = dfsRecursiveRouteFinder.getSearchTreeMap();
            treeController.initializeTree(rootStation, treeMap);
        }

        Stage stage = new Stage();
        stage.setTitle("Search Tree View");
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Set initial window size
        stage.setWidth(400);  // Adjust width as needed
        stage.setHeight(600); // Adjust height as needed

        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}






}
