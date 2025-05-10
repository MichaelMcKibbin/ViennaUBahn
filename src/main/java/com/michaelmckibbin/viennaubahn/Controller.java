package com.michaelmckibbin.viennaubahn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.StringConverter;

import java.util.*;
import java.util.stream.Collectors;


public class Controller {


    @FXML
    private StackPane mapContainer; // Wrap ImageView in a StackPane
    private Pane routeLayer; // Layer for drawing routes
    private Image colorMap;
    private Image greyMap;


    // Waypoints and Avoids
    @FXML
    public Button removeWaypointButton;
    @FXML
    public Button addWaypointButton;
    @FXML
    public ListView avoidListview;
    @FXML
    public ComboBox<Station> waypointComboBox;
    @FXML
    public ListView waypointsListView;
    @FXML
    public Label waypointStatusLabel;

    private ObservableList<Station> waypoints = FXCollections.observableArrayList();



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

    // Original image dimensions
    private static final double ORIGINAL_WIDTH = 1488.0;
    private static final double ORIGINAL_HEIGHT = 993.0;
    private static final double FIT_WIDTH = 1000; // 800 = 181, 900 = 81, 1000 = 11
    private static final double X_OFFSET = 10.0; // Adjust as needed
    // experimenting with formulaic derivation of X_OFFSET based on relationship between known FIT_WIDTH and required X_OFFSET
    //private static final double X_OFFSET = Math.max(0, (ORIGINAL_WIDTH - FIT_WIDTH) * 0.2619);
    //private static final double X_OFFSET = (ORIGINAL_WIDTH - FIT_WIDTH) * 0.262;
    private static final double Y_OFFSET = 4.0;

    // line stroke width setting
    private static final double LINE_STROKE_WIDTH = 8.0;

    // Constants for marker sizes
    private static final double START_STATION_MARKER_SIZE = 9;
    private static final double INTERMEDIATE_STATION_MARKER_SIZE = 4;
    private static final double END_STATION_MARKER_SIZE = 9.0;
    private static final double LINE_CHANGE_STATION_MARKER_SIZE = 7;
    private static final double WAYPOINT_MARKER_SIZE = 4.0;
    private static final double AVOID_MARKER_SIZE = 4.0;

    // Constants for marker colors
    private static final Color START_STATION_FILL_COLOR = Color.PALEVIOLETRED;
    private static final Color END_STATION_FILL_COLOR = Color.LIMEGREEN;
    private static final Color LINE_CHANGE_STATION_FILL_COLOR = Color.YELLOW;
    private static final Color INTERMEDIATE_STATION_FILL_COLOR = Color.LAWNGREEN;
    private static final Color STATION_STROKE_COLOR = Color.BLACK;


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
    @FXML
    public void handleAddAvoidStation(ActionEvent actionEvent) {
    }
    @FXML
    public void handleRemoveAvoidStation(ActionEvent actionEvent) {
    }
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

        colorMap = new Image(getClass().getResourceAsStream("/com/michaelmckibbin/viennaubahn/images/UBahn_Map_1.jpg"));
        greyMap = new Image(getClass().getResourceAsStream("/com/michaelmckibbin/viennaubahn/images/UBahn_Map_1_Grey.jpg"));


        // Create and initialize the graph
        graph = new Graph();

        // Load data from CSV
        graph.loadFromCSV("/com/michaelmckibbin/viennaubahn/data/vienna_subway_list_1.csv");

        // Populate Start Station, End Station & Waypoints ComboBoxes with station names
        populateStationComboBoxes();

        // Add listeners for station selection
        startStationComboBox.setOnAction(e -> validateSelections());
        endStationComboBox.setOnAction(e -> validateSelections());

        // Print debug information
        // add more detail if needed, like the number of stations, connections, etc.
        graph.printGraphStructure();
        graph.printTransferStations();

        // Initialize the route layer
        routeLayer = new Pane();
        routeLayer.setMouseTransparent(true); // Let clicks pass through to map

        // Set initial size for ImageView
        mapImageView.setFitWidth(FIT_WIDTH);  // or whatever width fits the window
        mapImageView.setFitHeight(mapImageView.getFitWidth() * (ORIGINAL_HEIGHT/ORIGINAL_WIDTH));
        mapImageView.setPreserveRatio(true);

        // Make sure routeLayer is added after ImageView
        mapContainer.getChildren().clear();
        mapContainer.getChildren().addAll(mapImageView, routeLayer);

        // Bind routeLayer size to ImageView
        routeLayer.prefWidthProperty().bind(mapImageView.fitWidthProperty());
        routeLayer.prefHeightProperty().bind(mapImageView.fitHeightProperty());

        // Initialize waypoints ListView
        waypointsListView.setItems(waypoints);

        // Set up cell factory for waypoints ListView to show station names
        waypointsListView.setCellFactory(listView -> new ListCell<Station>() {
                    @Override
                    protected void updateItem(Station station, boolean empty) {
                        super.updateItem(station, empty);
                        if (empty || station == null) {
                            setText(null);
                        } else {
                            setText(station.getName() + " " + station.getLineName());
                        }
                    }
                });

        // Set string converter for waypointComboBox
        waypointComboBox.setConverter(new StringConverter<Station>() {
            @Override
            public String toString(Station station) {
                return station == null ? "" : station.getName() + " " + station.getLineName();
            }

            @Override
            public Station fromString(String string) {
                return null; // Not needed for this use case
            }
        });

        // Optional: If you also want to style the dropdown items
        waypointComboBox.setCellFactory(lv -> new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName() + " " + station.getLineName());
                }
            }
        });

        // Set up button cell (what shows when an item is selected)
        waypointComboBox.setButtonCell(new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName() + " " + station.getLineName());
                }
            }
        });

        // Enable/disable remove button based on selection
        removeWaypointButton.disableProperty().bind(
                waypointsListView.getSelectionModel().selectedItemProperty().isNull()
        );

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

    private void drawRoute(Path path) {

        // Switch to grey map
        setGreyMap();

        routeLayer.getChildren().clear();
        List<Station> stations = path.getStations();

        for (int i = 0; i < stations.size() - 1; i++) {
            Station current = stations.get(i);
            Station next = stations.get(i + 1);

            // Scale coordinates and apply X offset
            int startX = (int) Math.round(((current.getX() + X_OFFSET) / ORIGINAL_WIDTH) * mapImageView.getFitWidth());
            int startY = (int) Math.round((current.getY() / ORIGINAL_HEIGHT) * mapImageView.getFitHeight() + Y_OFFSET);
            int endX = (int) Math.round(((next.getX() + X_OFFSET) / ORIGINAL_WIDTH) * mapImageView.getFitWidth());
            int endY = (int) Math.round((next.getY() / ORIGINAL_HEIGHT) * mapImageView.getFitHeight() + Y_OFFSET);

            // Then add Y_OFFSET to the scaled coordinates
            startY += Y_OFFSET;
            endY += Y_OFFSET;

            // Debug output
            System.out.println("Drawing line from (" + startX + "," + startY +
                    ") to (" + endX + "," + endY + ")");

            Line line = new Line(startX, startY, endX, endY);
            // Use the enum to set the color

//            try {
//                line.setStroke(LineColor.valueOf(current.getLineColor().toUpperCase()).getColor());
//            } catch (IllegalArgumentException e) {
//                // Fallback color if the line color isn't found in the enum
//                line.setStroke(Color.BLACK);
//                System.out.println("Unknown line color: " + current.getLineColor());
//            }

            // Use the next station's line color instead of the current station's
            try {
                line.setStroke(LineColor.valueOf(next.getLineColor().toUpperCase()).getColor());
            } catch (IllegalArgumentException e) {
                line.setStroke(Color.BLACK);
                System.out.println("Unknown line color: " + next.getLineColor());
            }
            //line.setStroke(Color.RED);
            line.setStrokeWidth(LINE_STROKE_WIDTH);
            line.setStrokeLineCap(StrokeLineCap.ROUND);

            routeLayer.getChildren().add(line);
        }

        // Draw station markers
        stations = path.getStations();
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            int x = (int) Math.round(((station.getX() + X_OFFSET) / ORIGINAL_WIDTH) * mapImageView.getFitWidth());
            int y = (int) Math.round((station.getY() / ORIGINAL_HEIGHT) * mapImageView.getFitHeight() + Y_OFFSET);

            Circle marker = new Circle(x, y, 0);

            // Check if this is a line change point (but not for the last station)
            boolean isLineChange = false;
            if (i < stations.size() - 1) {
                Station nextStation = stations.get(i + 1);
                isLineChange = !station.getLineColor().equals(nextStation.getLineColor());
            }
            // Set different colors and sizes for start and end stations
            if (i == 0) {  // Start station
                marker.setRadius(START_STATION_MARKER_SIZE);
                marker.setFill(START_STATION_FILL_COLOR);
                marker.setStroke(STATION_STROKE_COLOR);
                marker.setStrokeWidth(2);
            } else if (i == stations.size() - 1) {  // End station
                marker.setRadius(END_STATION_MARKER_SIZE);
                marker.setFill(END_STATION_FILL_COLOR);
                marker.setStroke(STATION_STROKE_COLOR);
                marker.setStrokeWidth(1);
            } else if (isLineChange) {  // Line change point
                marker.setRadius(LINE_CHANGE_STATION_MARKER_SIZE); // Using same size as start/end stations
                marker.setFill(LINE_CHANGE_STATION_FILL_COLOR);
                marker.setStroke(STATION_STROKE_COLOR);
                marker.setStrokeWidth(1);
            } else {  // Intermediate stations
                marker.setRadius(INTERMEDIATE_STATION_MARKER_SIZE);
                marker.setFill(INTERMEDIATE_STATION_FILL_COLOR);
                marker.setStroke(STATION_STROKE_COLOR);
                marker.setStrokeWidth(1);
            }

            routeLayer.getChildren().add(marker);

                System.out.println("Drawing station marker at (" + x + "," + y + ") for " + station.getName() +
                        (i == 0 ? " (Start)" : (i == stations.size() - 1 ? " (End)" : "")));

            }
    }


    // Search methods
//    @FXML
//private void handleFindRouteBFS() {
//    Station startStation = startStationComboBox.getValue();
//    Station endStation = endStationComboBox.getValue();
//
//    if (startStation == null || endStation == null) {
//        showAlert("Please select both start and end stations.");
//        return;
//    }
//
//    Path path = graph.bfsAlgorithm(startStation, endStation);
//
//    if (path != null) {
//        displayRoute(path);
//        displayPerformanceMetrics(path);
//        drawRoute(path);
//    } else {
//        showAlert("No route found between selected stations.");
//    }
//}

    @FXML
private void handleFindRouteBFS() {
    Station startStation = startStationComboBox.getValue();
    Station endStation = endStationComboBox.getValue();

    if (startStation == null || endStation == null) {
        showAlert("Please select both start and end stations.");
        return;
    }

    // Create a list of all points in order: start -> waypoints -> end
    List<Station> routePoints = new ArrayList<>();
    routePoints.add(startStation);
    routePoints.addAll(waypoints);
    routePoints.add(endStation);

    // Find paths between consecutive points and combine them
    Path completePath = null;

    try {
        // Start with first segment
        completePath = graph.bfsAlgorithm(routePoints.get(0), routePoints.get(1));

        // For each additional waypoint, find path and combine
        for (int i = 1; i < routePoints.size() - 1; i++) {
            Path nextSegment = graph.bfsAlgorithm(routePoints.get(i), routePoints.get(i + 1));
            if (nextSegment == null) {
                showAlert("No route found between " + routePoints.get(i).getName() +
                         " and " + routePoints.get(i + 1).getName());
                return;
            }
            // Remove duplicate station at connection point and combine paths
            nextSegment.getStations().remove(0); // Remove first station of next segment
            completePath.getStations().addAll(nextSegment.getStations());
            completePath.setNumberOfStops(completePath.getNumberOfStops() +
                                        nextSegment.getNumberOfStops());
        }

        if (completePath != null) {
            displayRoute(completePath);
            displayPerformanceMetrics(completePath);
            drawRoute(completePath);
        } else {
            showAlert("No route found between selected stations.");
        }
    } catch (Exception e) {
        showAlert("Error finding route: " + e.getMessage());
    }

        // After the route is found, clear the waypointStatusLabel text
        waypointStatusLabel.setText("");
        waypointStatusLabel.setStyle("");
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


private void displayPerformanceMetrics(Path path) {
    // Create performance information string
    String performanceInfo = String.format("""
        Performance Metrics:
        • Execution time: %d ms
        • Nodes visited: %d
        • Maximum queue size: %d
        • Number of stops: %d""",
        path.getExecutionTimeMillis(),
        path.getNodesVisited(),
        path.getMaxQueueSize(),
        path.getNumberOfStops());

    // Update UI
    performanceLabel.setText(performanceInfo);
    performanceBox.setVisible(true);
}

    private void displayRoute(Path path) {
    // Clear previous route
    routeListView.getItems().clear();

    // Add stations to the list view
    path.getStations().forEach(station ->
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
    numberOfStopsLabel.setText("Total stops: " + path.getNumberOfStops());

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




}