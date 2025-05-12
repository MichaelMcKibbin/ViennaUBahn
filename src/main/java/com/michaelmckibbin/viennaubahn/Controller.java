package com.michaelmckibbin.viennaubahn;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;

public class Controller {
    // Keep these constants as they're used in other parts of Controller
    private static final double ORIGINAL_WIDTH = 1488.0;
    private static final double ORIGINAL_HEIGHT = 993.0;
    private static final double FIT_WIDTH = 1000.0;
    private static final double X_OFFSET = 10.0;
    private static final double Y_OFFSET = 4.0;
    public Label performanceLabel;
    public Label numberOfStopsLabel;
//    public ListView routeListView;
    public Button dijkstraShortestPath;
    public Button dijkstraWithPenalties;
    public Button bfsButton;
    public Button dfsRecursiveButton;
    public Button dfsIterativeButton;
    public Label waypointStatusLabel;
    public Button addWaypointButton;

    @FXML
    private Canvas routeCanvas;
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
    private BFSRouteFinder bfsRouteFinder;
    private DFS_RecursiveRouteFinder dfsRouteFinder;

    private RouteMetricsDisplay metricsDisplay;
    @FXML
    private ListView<Station> routeListView;

    private List<List<Station>> currentRoutes;  // Store current routes
    private int currentRouteIndex = 0;          // Track which route is displayed

    @FXML
    private Button nextRouteButton;             // Add this to your FXML
    @FXML
    private Button previousRouteButton;         // Add this to your FXML
    @FXML
    private Label routeNumberLabel;             // Add this to your FXML


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
        dfsRouteFinder = new DFS_RecursiveRouteFinder(graph);  // Initialize DFS


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

        long initEndTime = System.nanoTime();
        long initDuration = (initEndTime - initStartTime) / 1_000_000;
        System.out.println("Controller initialization took " + initDuration + " ms.");
        System.out.println("\nController initialization complete./n");
    }

    @FXML
    private void handleDijkstraShortestPath() {

    }

    @FXML
    private void handleDijkstraWithPenalties() {

    }

    @FXML
    private void handleFindRouteDFSRecursive() {
        System.out.println("DFS Recursive Search initiated...");

        Station start = startStationComboBox.getValue();
        Station end = endStationComboBox.getValue();

        if (start == null || end == null) {
            System.out.println("Start or end station not selected");
            return;
        }

        try {
            // Get waypoints
            List<Station> waypointsList = new ArrayList<>(waypoints);

            System.out.println("Finding route from " + start.getName() + " to " + end.getName());
            if (!waypointsList.isEmpty()) {
                System.out.println("With waypoints: " + waypointsList);
            }

            // Start timing
            long startTime = System.nanoTime();

//            // Find route
//            RoutePath routePath = dfsRouteFinder.findRoute(start, end, waypointsList);

            // Get multiple routes
            currentRoutes = dfsRouteFinder.findMultipleRoutes(start, end, waypointsList);


            // End timing
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;

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
//                    routePath.getNumberOfStops(),
//                    executionTime,  // Pass the measured execution time
//                    routePath.getNodesVisited(),
//                    routePath.getMaxQueueSize()
//                );

            if (currentRoutes != null && !currentRoutes.isEmpty()) {
                currentRouteIndex = 0;
                displayCurrentRoute(executionTime);

                // Enable/disable navigation buttons
                updateNavigationButtons();

//===>
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

    private void displayCurrentRoute(long executionTime) {
        List<Station> currentPath = currentRoutes.get(currentRouteIndex);

        routeVisualizer.drawRoute(new RoutePath(currentPath, executionTime,
                dfsRouteFinder.getNodesVisited(),
                dfsRouteFinder.getMaxQueueSize()));

        routeListView.getItems().clear();
        routeListView.getItems().addAll(currentPath);

        routeInfoBox.setVisible(true);
        performanceBox.setVisible(true);

        routeNumberLabel.setText(String.format("Route %d of %d",
                currentRouteIndex + 1, currentRoutes.size()));

        metricsDisplay.updateMetrics(
                currentPath.size(),
                executionTime,
                dfsRouteFinder.getNodesVisited(),
                dfsRouteFinder.getMaxQueueSize()
        );
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
    private void handleFindRouteDFSIterative() {

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
    handleReset(); // This calls the existing reset method
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

        System.out.println("Finding route from " + start.getName() + " to " + end.getName());
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
}
