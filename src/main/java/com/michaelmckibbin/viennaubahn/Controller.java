package com.michaelmckibbin.viennaubahn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.StringConverter;

import java.util.*;
import java.util.stream.Collectors;


public class Controller {

    @FXML private Canvas routeCanvas;
    @FXML private Label stopsLabel;
    @FXML private Label timeLabel;
    @FXML private Label nodesLabel;
    @FXML private Label queueLabel;

    public Button resetButton;
    @FXML
    private StackPane mapContainer; // Wrap ImageView in a StackPane
    private Pane routeLayer; // Layer for drawing routes
    private Image colorMap;
    private Image greyMap;


    private RouteVisualizer routeVisualizer;
    private RouteMetricsDisplay metricsDisplay;
    private RouteSearchStrategy routeFinder;


    // Waypoints
    @FXML
    public Button removeWaypointButton;
    @FXML
    public Button addWaypointButton;
    @FXML
    public ComboBox<Station> waypointComboBox;
    @FXML
    public Label waypointStatusLabel;
    @FXML
    public ListView waypointsListView;
    private ObservableList<Station> waypoints = FXCollections.observableArrayList();

//    //Avoid stations
//    @FXML
//    public Button removeAvoidStationButton;
//    @FXML
//    public Button addAvoidStationButton;
//    @FXML
//    public ChoiceBox<Station> avoidStationChoiceBox;
//    @FXML
//    public ListView avoidStationsListView;
//    private ObservableList<Station> avoidStations  = FXCollections.observableArrayList();

    // ComboBoxes & Search Buttons
    @FXML
    private ComboBox<Station> startStationComboBox;
    @FXML
    private ComboBox<Station> endStationComboBox;
    @FXML
    public Button dfsButton;
    @FXML
    public Button bfsButton;
    @FXML
    public Button dijkstraWithPenalties;
    @FXML
    public Button dijkstraShortestPath;




    // Imageview & related settings
    @FXML
    public ImageView mapImageView;




    // Performance stats
    @FXML
    private VBox performanceBox;
    @FXML
    private Label performanceLabel;

    // Route information
    @FXML
    private VBox routeInfoBox;
    @FXML
    private ListView<Station> routeListView;
    @FXML
    public Label numberOfStopsLabel;



    // Waypoints & Avoids
//    @FXML
//    public void handleAddAvoidStation(ActionEvent actionEvent) {
//    }
//    @FXML
//    public void handleRemoveAvoidStation(ActionEvent actionEvent) {
//    }
    @FXML
    private void handleAddWaypoint() {
        Station selected = waypointComboBox.getValue();
        if (selected != null && !waypoints.contains(selected)) {
            waypoints.add(selected);
            waypointComboBox.setValue(null); // Clear selection

            // Add visual reminder to recalculate route!
            waypointStatusLabel.setText("Press Search to recalculate");
            waypointStatusLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");
            // If desired it's possible to automatically trigger a search.
            // easiest if only one type of search is used in the app...
            // handleFindRouteBFS();
            // handleFindRouteDFS();
            // etc...
        }
    }

    @FXML
    private void handleRemoveWaypoint() {
        int selectedIndex = waypointsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            waypoints.remove(selectedIndex);
        }
        // Add visual reminder to recalculate route!
        waypointStatusLabel.setText("Press Search to recalculate");
        waypointStatusLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");


    }


    private Graph graph;  // The graph instance


@FXML
public void initialize() {
    System.out.println("Initializing Controller...");

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

    // Set up canvas and route visualizer
    routeCanvas.widthProperty().bind(mapContainer.widthProperty());
    routeCanvas.heightProperty().bind(mapContainer.heightProperty());
    routeVisualizer = new RouteVisualizer(mapImageView, routeLayer, waypoints);
}



    // Initialize graph
    graph = new Graph();
    graph.loadFromCSV("/com/michaelmckibbin/viennaubahn/data/vienna_subway_list_1.csv");
    graph.printGraphStructure();
    graph.printTransferStations();

    // Initialize route finding components
    metricsDisplay = new RouteMetricsDisplay(stopsLabel, timeLabel,
                                           nodesLabel, queueLabel);
    routeFinder = new BFSRouteFinder(graph);

    // Initialize station selection components
    populateStationComboBoxes();
    startStationComboBox.setOnAction(e -> validateSelections());
    endStationComboBox.setOnAction(e -> validateSelections());

    // Initialize waypoint management
    StationListManager stationManager = new StationListManager(
        waypointComboBox,
        waypointsListView,
        waypoints,
        removeWaypointButton
    );
    stationManager.initialize();

    System.out.println("\nController initialization complete.");
}





    private void populateStationComboBoxes() {
        // Get all stations and sort them by name
        List<Station> stations = new ArrayList<>(graph.getAllStations());
        stations.sort(Comparator.comparing(Station::getName));

        // Add stations to ComboBoxes
        startStationComboBox.getItems().addAll(stations);
        endStationComboBox.getItems().addAll(stations);
        waypointComboBox.getItems().addAll(stations);
        //  avoidStationChoiceBox.getItems().addAll(stations);

        // Set cell factory to display station names
        setCellFactory(startStationComboBox);
        setCellFactory(endStationComboBox);
    }

    // Add method to set up how stations are displayed in the ComboBox
    private void setCellFactory(ComboBox<Station> comboBox) {
        // Define how to display stations in the ComboBox
        comboBox.setCellFactory(cell -> new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName());
                }
            }
        });

        // Define how to display selected station in the ComboBox
        comboBox.setButtonCell(new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName());
                }
            }
        });
    }



    private void setupAutoComplete(ComboBox<String> comboBox) {
        // Enable editing in the ComboBox
        comboBox.setEditable(true);

        // Add auto-complete functionality
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                comboBox.show();
                return;
            }

            // Filter items based on input
            String input = newValue.toLowerCase();
            List<String> filteredItems = comboBox.getItems().stream()
                    .filter(item -> item.toLowerCase().contains(input))
                    .collect(Collectors.toList());

            // Update ComboBox items
            if (!filteredItems.isEmpty()) {
                comboBox.getItems().setAll(filteredItems);
                if (!comboBox.isShowing()) {
                    comboBox.show();
                }
            }
        });
    }

    private void validateSelections() {
        Station startStation = startStationComboBox.getValue();
        Station endStation = endStationComboBox.getValue();

        // Prevent selecting same station
        if (startStation != null && startStation.equals(endStation)) {
            endStationComboBox.setValue(null);
        }
    }

    // Method to switch maps
    private void setGreyMap() {
        mapImageView.setImage(greyMap);
    }

    private void setColorMap() {
        mapImageView.setImage(colorMap);
    }





@FXML
private void handleFindRouteBFS() {
    System.out.println("BFS Search initiated..."); // Debug log

    Station start = startStationComboBox.getValue();
    Station end = endStationComboBox.getValue();

    if (start == null || end == null) {
        System.out.println("Start or end station not selected");
        return;
    }

    try {

        // Switch to grey map
        mapImageView.setImage(greyMap);

        // Clear previous route
        routeVisualizer.clearRoute();

        // Make route info visible
        routeInfoBox.setVisible(true);
        performanceBox.setVisible(true);

        // Get waypoints
        List<Station> waypointsList = new ArrayList<>(waypoints);

        System.out.println("Finding route from " + start.getName() + " to " + end.getName());
        if (!waypointsList.isEmpty()) {
            System.out.println("With waypoints: " + waypointsList);
        }

        // Find route
        RoutePath routePath = routeFinder.findRoute(start, end, waypointsList);

        if (routePath != null) {

            // Debug print station coordinates
            System.out.println("Drawing route with following coordinates:");
            for (Station station : routePath.getStations()) {
                System.out.println(station.getName() + " at (" + station.getX() + "," + station.getY() + ")");
            }

            // Display route on map
            routeVisualizer.visualizeRoute(routePath.getStations());

            // Update route list
            routeListView.getItems().clear();
            routeListView.getItems().addAll(routePath.getStations());

            // Update metrics
            metricsDisplay.updateMetrics(
                routePath.getNumberOfStops(),
                routePath.getExecutionTimeMillis(),
                routePath.getNodesVisited(),
                routePath.getMaxQueueSize()
            );

            System.out.println("Route found successfully!");
        } else {
            System.out.println("No route found!");
            mapImageView.setImage(colorMap);  // Switch back to color map if no route found
        }
    } catch (Exception e) {
        System.err.println("Error finding route: " + e.getMessage());
        e.printStackTrace();
        mapImageView.setImage(colorMap);  // Switch back to color map on error
    }
}


    private Path createVisualPath(List<Station> stations) {
        Path path = new Path();
        if (stations.isEmpty()) return path;

        Station first = stations.get(0);
        MoveTo moveTo = new MoveTo(first.getX(), first.getY());
        path.getElements().add(moveTo);

        for (int i = 1; i < stations.size(); i++) {
            Station station = stations.get(i);
            LineTo lineTo = new LineTo(station.getX(), station.getY());
            path.getElements().add(lineTo);
        }

        return path;
    }






    @FXML
    public void handleFindRouteDFS(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDijkstraWithPenalties(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDijkstraShortestPath(ActionEvent actionEvent) {
    }

    // Add method to scale coordinates to match the image size
    private double scaleX(double x) {
        Image img = mapImageView.getImage();
        if (img == null) return x;

        double imageWidth = img.getWidth();
        double displayWidth = mapImageView.getFitWidth();
        return (x / imageWidth) * displayWidth;
    }

    private double scaleY(double y) {
        Image img = mapImageView.getImage();
        if (img == null) return y;

        double imageHeight = img.getHeight();
        double displayHeight = mapImageView.getFitHeight();
        return (y / imageHeight) * displayHeight;
    }


    private void displayPerformanceMetrics(RoutePath routePath) {
    // Create performance information string
    String performanceInfo = String.format("""
        Performance Metrics:
        • Execution time: %d ms
        • Nodes visited: %d
        • Maximum queue size: %d
        • Number of stops: %d""",
        routePath.getExecutionTimeMillis(),
        routePath.getNodesVisited(),
        routePath.getMaxQueueSize(),
        routePath.getNumberOfStops());

    // Update UI
    performanceLabel.setText(performanceInfo);
    performanceBox.setVisible(true);
    }

    private void displayRoute(RoutePath routePath) {
    // Clear previous route
    routeListView.getItems().clear();

    // Add stations to the list view
    routePath.getStations().forEach(station ->
            routeListView.getItems().add(station)  // Add the entire station object instead of just the name
    );

    // Set custom cell factory
    routeListView.setCellFactory(listView -> new ListCell<Station>() {
        @Override
        protected void updateItem(Station station, boolean empty) {
            super.updateItem(station, empty);

            if (empty || station == null) {
                setText(null);
                setStyle(null);
            } else {
                // Combine station name and line name
                setText(station.getName() + " " + station.getLineName());

                // Set background color using the line color
                // Assuming getLineColor() returns a valid color string
                setStyle("-fx-background-color: " + station.getLineColor() + ";" +
                        "-fx-text-fill: white;"); // White text for better contrast
            }
        }
    });

    // Update number of stops
    numberOfStopsLabel.setText("Total stops: " + routePath.getNumberOfStops());

    // Show route info
    routeInfoBox.setVisible(true);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Route Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleResetMap() {
        // Clear the route layer
        routeLayer.getChildren().clear();

        // Clear any selected stations
        startStationComboBox.setValue(null);
        endStationComboBox.setValue(null);

        // Clear waypoints
        waypoints.clear();
        waypointsListView.getItems().clear();

        // Reset any performance/route information
        if (performanceLabel != null) {
            performanceLabel.setText("");
        }
        if (routeInfoBox != null) {
            routeInfoBox.setVisible(false);
        }

        // Display the color map
        setColorMap();
    }


}