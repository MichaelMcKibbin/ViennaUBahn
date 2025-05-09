package com.michaelmckibbin.viennaubhan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.util.*;
import java.util.stream.Collectors;


public class Controller {
    public static Controller maincon;
    public Button dijkstraSearchButton;
    public ListView waypointListview;
    public Button removeWaypointButton;
    public Button addWaypointButton;
    public ListView avoidListview;
    public Button removeAvoidStationButton;
    public Button addAvoidStationButton;
    public ComboBox fromComboBox;
    public ComboBox destinationComboBox;
    public Button searchButton;
    public AnchorPane rootPane;
    public TableView routesTableView;
    public Canvas canvasImage;
    public Button dfsButton;
    public Button bfsButton;
    public Button dijkstraWithPenalties;
    public Button dijkstraShortestPath;
    private Image colorMap;
    private Image greyMap;

    @FXML
    public Button clearMap;
    @FXML
    public MenuButton waypointStation;
    @FXML
    public AnchorPane mapPane;
    @FXML
    public Button bfsSearchButton;
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






    @FXML
    private StackPane mapContainer; // Wrap ImageView in a StackPane

    private Pane routeLayer; // Layer for drawing routes

    @FXML
    public ListView routeOutput;
    @FXML
    public Button initialiseMapButton;
    @FXML
    public MenuButton startStation;
    @FXML
    public MenuButton destinationStation;
    @FXML
    public MenuButton avoidStation;
    @FXML
    private VBox performanceBox;
    @FXML
    private Label performanceLabel;
    @FXML
    private ComboBox<Station> startStationComboBox;
    @FXML
    private ComboBox<Station> endStationComboBox;
    @FXML
    private VBox routeInfoBox;
    @FXML
    private ListView<String> routeListView;
    @FXML
    public Label numberOfStopsLabel;
    @FXML
    private Station selectedWaypointStation;
    private Circle firstStationRing;
    private Circle destinationStationCircle;
    private Circle firstStationCircle;
    private Station selectedDestinationStation;
    private Station firstSelectedStation;
    private Circle destinationStationOuterRing;
    private boolean isMapPopulated = false;

    @FXML
    public void dfsSearch(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDijkstraWithPenalties(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDijkstraShortestPath(ActionEvent actionEvent) {
    }
    @FXML
    public void handleAddAvoidStation(ActionEvent actionEvent) {
    }
    @FXML
    public void handleRemoveAvoidStation(ActionEvent actionEvent) {
    }
    @FXML
    public void handleAddWaypoint(ActionEvent actionEvent) {
    }
    @FXML
    public void bfsSearch(ActionEvent actionEvent) {
    }
    @FXML
    public void removeWaypoint(ActionEvent actionEvent) {
    }

    private Graph graph;  // The graph instance

    @FXML
    public void initialize() {

        System.out.println("Initializing Controller...");

        colorMap = new Image(getClass().getResourceAsStream("/com/michaelmckibbin/viennaubhan/images/UBhan_Map_1.jpg"));
        greyMap = new Image(getClass().getResourceAsStream("/com/michaelmckibbin/viennaubhan/images/UBhan_Map_1_Grey.jpg"));


        // Create and initialize the graph
        graph = new Graph();

        // Load data from CSV
        graph.loadFromCSV("/com/michaelmckibbin/viennaubhan/data/vienna_subway_list_1.csv");

        // Populate ComboBoxes with station names
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


        System.out.println("\nController initialization complete.");

    }

    private void populateStationComboBoxes() {
        // Get all stations and sort them by name
        List<Station> stations = new ArrayList<>(graph.getAllStations());
        stations.sort(Comparator.comparing(Station::getName));

        // Add stations to ComboBoxes
        startStationComboBox.getItems().addAll(stations);
        endStationComboBox.getItems().addAll(stations);

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

    @FXML
private void findRouteBfs() {
    Station startStation = startStationComboBox.getValue();
    Station endStation = endStationComboBox.getValue();

    if (startStation == null || endStation == null) {
        showAlert("Please select both start and end stations.");
        return;
    }

    Path path = graph.bfsAlgorithm(startStation, endStation);

    if (path != null) {
        displayRoute(path);
        displayPerformanceMetrics(path);
        drawRoute(path);
    } else {
        showAlert("No route found between selected stations.");
    }
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
                routeListView.getItems().add(station.getName())
        );

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