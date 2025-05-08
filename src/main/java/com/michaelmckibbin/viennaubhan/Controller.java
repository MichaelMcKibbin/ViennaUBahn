package com.michaelmckibbin.viennaubhan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

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


        // Bind the ImageView size to its parent container
        mapImageView.fitWidthProperty().bind(
                mapImageView.getParent().layoutBoundsProperty().map(bounds -> bounds.getWidth() - 10) // -10 for margin
        );
        mapImageView.fitHeightProperty().bind(
                mapImageView.getParent().layoutBoundsProperty().map(bounds -> bounds.getHeight() - 10) // -10 for margin
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
    } else {
        showAlert("No route found between selected stations.");
    }
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