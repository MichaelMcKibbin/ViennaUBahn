package com.michaelmckibbin.viennaubhan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

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

        // Print debug information
        graph.printGraphStructure();
        graph.printTransferStations();

        System.out.println("\nController initialization complete.");

    }

    // Example method for finding a path between stations
    public void findPath(String startStationName, String endStationName) {
        Station startStation = Graph.getStation(startStationName);
        Station endStation = Graph.getStation(endStationName);

        if (startStation == null || endStation == null) {
            System.out.println("Start or end station not found");
            return;
        }

        Path path = graph.bfsAlgorithm(startStation, endStation);

        if (path != null) {
            System.out.println("Number of stops: " + path.getNumberOfStops());
            for (Station station : path.getStations()) {
                System.out.println(station.getName());
            }
        } else {
            System.out.println("No path found between stations");
        }
    }



}


//    public void initialiseMap(ActionEvent actionEvent) {
//        // Check if the map is populated
//        if (isMapPopulated) {
//            System.out.println("Map already populated");
//            return;
//        }
//        String csvData = "Central Stationline,Notting Hill Gate,df002c,111,292\n" +
//                "Central Stationline,Queensway,df002c,170,278\n" +
//                "Central Stationline,Lancaster Gate,df002c,249,266\n" +
//                "Central Stationline,Marble Arch,df002c,360,249\n" +
//                "Central Stationline,Bond Street,df002c,419,242\n" +
//                "Central Stationline,Oxford Circus,df002c,471,234\n" +
//                "Central Stationline,Tottenham Court Road,df002c,539,218\n" +
//                "Central Stationline,Holborn,df002c,611,209\n" +
//                "Central Stationline,Chancery Lane,df002c,670,197\n" +
//                "Central Stationline,St. Paul's,df002c,760,238\n" +
//                "Central Stationline,Bank,df002c,816,252\n" +
//                "Central Stationline,Liverpool Street,df002c,857,204\n" +
//                "Picadilly Stationline,Earl's Court,002d73,107,473\n" +
//                "Picadilly Stationline,Gloucester Road,002d73,201,448\n" +
//                "Picadilly Stationline,South Kensington,002d73,260,452\n" +
//                "Picadilly Stationline,Knightsbridge,002d73,346,375\n" +
//                "Picadilly Stationline,Hyde Park Corner,002d73,398,362\n" +
//                "Picadilly Stationline,Green Park,002d73,462,321\n" +
//                "Picadilly Stationline,Picadilly Circus,002d73,519,288\n" +
//                "Picadilly Stationline,Leicester Square,002d73,558,272\n" +
//                "Picadilly Stationline,Covent Garden,002d73,584,256\n" +
//                "Picadilly Stationline,Holborn,002d73,611,209\n" +
//                "Picadilly Stationline,Russell Square,002d73,582,150\n" +
//                "Picadilly Stationline,King's Cross St. Pancras,002d73,586,69\n" +
//                "Bakerloo Stationline,Paddington,ab6612,249,223\n" +
//                "Bakerloo Stationline,Edgware Road,ab6612,292,182\n" +
//                "Bakerloo Stationline,Marylebone,ab6612,330,155\n" +
//                "Bakerloo Stationline,Baker Street,ab6612,369,154\n" +
//                "Bakerloo Stationline,Regent's Park,ab6612,434,150\n" +
//                "Bakerloo Stationline,Oxford Circus,ab6612,471,234\n" +
//                "Bakerloo Stationline,Picadilly Circus,ab6612,519,288\n" +
//                "Bakerloo Stationline,Charing Cross,ab6612,581,307\n" +
//                "Bakerloo Stationline,Embankment,ab6612,595,313\n" +
//                "Bakerloo Stationline,Waterloo,ab6612,649,353\n" +
//                "Bakerloo Stationline,Lambeth North,ab6612,663,403\n" +
//                "Bakerloo Stationline,Elephant & Castle,ab6612,741,450\n" +
//                "Circle Stationline,Edgware Road,f7dc00,292,181\n" +
//                "Circle Stationline,Paddington,f7dc00,249,223\n" +
//                "Circle Stationline,Bayswater,f7dc00,168,264\n" +
//                "Circle Stationline,Notting Hill Gate,f7dc00,111,292\n" +
//                "Circle Stationline,High Street Kensington,f7dc00,138,381\n" +
//                "Circle Stationline,Gloucester Road,f7dc00,201,448\n" +
//                "Circle Stationline,South Kensington,f7dc00,260,452\n" +
//                "Circle Stationline,Sloane Square,f7dc00,373,470\n" +
//                "Circle Stationline,Victoria,f7dc00,450,427\n" +
//                "Circle Stationline,St. James Park,f7dc00,523,396\n" +
//                "Circle Stationline,Westminster,f7dc00,576,380\n" +
//                "Circle Stationline,Embankment,f7dc00,595,313\n" +
//                "Circle Stationline,Temple,f7dc00,649,274\n" +
//                "Circle Stationline,Blackfriars,f7dc00,722,265\n" +
//                "Circle Stationline,Mansion House,f7dc00,782,265\n" +
//                "Circle Stationline,Cannon Street,f7dc00,804,272\n" +
//                "Circle Stationline,Tower Hill,f7dc00,894,288\n" +
//                "Circle Stationline,Aldgate,f7dc00,902,241\n" +
//                "Circle Stationline,Liverpool Street,f7dc00,857,204\n" +
//                "Circle Stationline,Moorgate,f7dc00,816,196\n" +
//                "Circle Stationline,Barbican,f7dc00,755,177\n" +
//                "Circle Stationline,Farringdon,f7dc00,707,178\n" +
//                "Circle Stationline,King's Cross St. Pancras,f7dc00,586,69\n" +
//                "Circle Stationline,Euston Square,f7dc00,508,119\n" +
//                "Circle Stationline,Great Portland Street,f7dc00,455,142\n" +
//                "Circle Stationline,Paddington,f7dc00,249,223\n" +
//                "Victoria Stationline,King's Cross St. Pancras,0076bd,586,69\n" +
//                "Victoria Stationline,Euston,0076bd,522,96\n" +
//                "Victoria Stationline,Warren Street,0076bd,491,135\n" +
//                "Victoria Stationline,Oxford Circus,0076bd,471,234\n" +
//                "Victoria Stationline,Green Park,0076bd,462,321\n" +
//                "Victoria Stationline,Victoria,0076bd,450,427\n" +
//                "Victoria Stationline,Pimlico,0076bd,524,502\n" +
//                "Victoria Stationline,Vauxhall,0076bd,576,535\n" +
//                "District Stationline,Edgware Road,0d6928,292,181\n" +
//                "District Stationline,Paddington,0d6928,249,223\n" +
//                "District Stationline,Bayswater,0d6928,168,264\n" +
//                "District Stationline,Notting Hill Gate,0d6928,111,292\n" +
//                "District Stationline,High Street Kensington,0d6928,138,381\n" +
//                "District Stationline,Earl's Court,0d6928,107,473\n" +
//                "District Stationline,Gloucester Road,0d6928,201,448\n" +
//                "District Stationline,South Kensington,0d6928,260,452\n" +
//                "District Stationline,Sloane Square,0d6928,373,470\n" +
//                "District Stationline,Victoria,0d6928,450,427\n" +
//                "District Stationline,St. James Park,0d6928,523,396\n" +
//                "District Stationline,Westminster,0d6928,576,380\n" +
//                "District Stationline,Embankment,0d6928,595,313\n" +
//                "District Stationline,Temple,0d6928,649,274\n" +
//                "District Stationline,Blackfriars,0d6928,722,265\n" +
//                "District Stationline,Mansion House,0d6928,782,265\n" +
//                "District Stationline,Cannon Street,0d6928,804,272\n" +
//                "District Stationline,Tower Hill,0d6928,894,288\n" +
//                "District Stationline,Aldgate East,0d6928,912,263\n" +
//                "District Stationline,Whitechapel,0d6928,958,263\n" +
//                "District Stationline,Stepney Green,0d6928,1010,263\n" +
//                "District Stationline,Mile End,0d6928,1060,263\n" +
//                "District Stationline,Bow Road,0d6928,1106,263\n" +
//                "District Stationline,Bromley-by-Bow,0d6928,1158,263\n" +
//                "District Stationline,West Ham,0d6928,1210,263\n" +
//                "District Stationline,Plaistow,0d6928,1262,263\n" +
//                "District Stationline,Upton Park,0d6928,1314,263\n" +
//                "District Stationline,East Ham,0d6928,1366,263\n" +
//                "District Stationline,Barking,0d6928,1418,263\n";
//        Map<String, Stationline> lines = parseCSVData(csvData);
//        // Iterate over the lines and print their stations
//        for (Map.Entry<String, Stationline> entry : lines.entrySet()) {
//            System.out.println(entry.getKey() + " - " + entry.getValue().getLineColor());
//            for (Station station : entry.getValue().getStations()) {
//                System.out.println("\t" + station.getStationName() + " (" + station.getXcoord() + "," + station.getYcoord() + ")");
//            }
//            System.out.println("\n");
//        }
//
//        populateMenuButtons(lines);
//
//
//        //After filling the map, set the boolean to true.
//        isMapPopulated = true;
//    }

//
//    private Map<String, Stationline> parseCSVData(String csvData) {
//        Map<String, Stationline> lines = new HashMap<>();
//        // Split the csvData to get each line
//        String[] csvLines = csvData.split("\n");
//
//        // Loop through each line in the csv string
//        for (String line : csvLines) {
//            // Split the line by a comma
//            String[] values = line.split(",");
//
//            // Check for correct values
//            if (values.length != 5) {
//                System.err.println("Invalid line in CSV: " + line);
//                continue;
//            }
//
//            // Get values
//            String lineName = values[0].trim();
//            String stationName = values[1].trim();
//            String color = values[2].trim();
//            double x = Double.parseDouble(values[3].trim());
//            double y = Double.parseDouble(values[4].trim());
//
//            // Create or retrieve the Stationline object
//            Stationline lineObj = lines.get(lineName);
//            if (lineObj == null) {
//                lineObj = new Stationline(lineName, color);
//                lines.put(lineName, lineObj);
//            }
//
//            // Create or retrieve the Station object
//            Station stationObj = stations.get(stationName);
//            if (stationObj == null) {
//                stationObj = new Station(stationName, x, y);
//                stations.put(stationName, stationObj);
//            }
//
//            // Add the station to the line
//            lineObj.addStation(stationObj);
//
//
//            // Get the previous station on the line, if it exists
//            List<Station> currentLineStations = lineObj.getStations();
//            if (currentLineStations.size() > 1) {
//                Station previousStation = currentLineStations.get(currentLineStations.size() - 2);
//
//                // Calculate the distance between the current and previous stations
//                double distance = Math.sqrt(Math.pow(stationObj.getXcoord() - previousStation.getXcoord(), 2)
//                        + Math.pow(stationObj.getYcoord() - previousStation.getYcoord(), 2));
//                // Print the distances
//                System.out.println("Calculating distance between " + previousStation.getStationName() + " and " + stationObj.getStationName());
//                System.out.println("Previous station coordinates: " + previousStation.getXcoord() + ", " + previousStation.getYcoord());
//                System.out.println("Current station coordinates: " + stationObj.getXcoord() + ", " + stationObj.getYcoord());
//                System.out.println("Calculated distance: " + distance);
//
//                // Add the current station as a neighbor to the previous station, if they are not already neighbors
//                if (!previousStation.getNeighborStations().containsKey(stationObj)) {
//                    previousStation.addNeighbor(stationObj);
//                    System.out.println("Added " + stationObj.getStationName() + " as a neighbor to " + previousStation.getStationName());
//                }
//
//                // Add the previous station as a neighbor to the current station, if they are not already neighbors
//                if (!stationObj.getNeighborStations().containsKey(previousStation)) {
//                    stationObj.addNeighbor(previousStation);
//                    System.out.println("Added " + previousStation.getStationName() + " as a neighbor to " + stationObj.getStationName());
//                }
//            }
//        }
//
//        return lines;
//    }
//
//
//    private void handleMenuClick(ActionEvent event, Station station) {
//        MenuItem clickedMenuItem = (MenuItem) event.getSource();
//        MenuButton parentMenuButton = (MenuButton) clickedMenuItem.getParentPopup().getOwnerNode();
//
//        // Set the selected station as the text of the parent MenuButton
//        parentMenuButton.setText(station.getStationName());
//
//        if (parentMenuButton == startStation) {
//            firstSelectedStation = station;
//            drawFirstCircle(station);
//        } else if (parentMenuButton == destinationStation) {
//            selectedDestinationStation = station;
//            drawDestinationCircle(station);
//        }
//    }
//
//    // This method draws a line between each station on the path
//    private void drawShortestPath(Path shortestRoute) {
//        // Remove any existing lines from the mapPane
//        mapPane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);
//        // Get the list of stations in the shortest path
//        List<Station> shortestPath = shortestRoute.getPath();
//        // Draw a line between each pair of adjacent stations in the shortest path
//        for (int i = 0; i < shortestPath.size() - 1; i++) {
//            Station start = shortestPath.get(i);
//            Station end = shortestPath.get(i + 1);
//
//            drawLineBetweenStations(start, end, Color.PURPLE);
//        }
//    }
//
//    //Based on the scale of the map image and the relative coordinates, this method computes the precise coordinates of a station on the mapPane.
//    private Point2D calculateActualCoordinates(Station station) {
//        double scaleX = mapImageView.getBoundsInLocal().getWidth() / mapImageView.getImage().getWidth();
//        double scaleY = mapImageView.getBoundsInLocal().getHeight() / mapImageView.getImage().getHeight();
//
//        double actualX = station.getXcoord() * scaleX;
//        double actualY = station.getYcoord() * scaleY;
//
//        return new Point2D(actualX, actualY);
//    }
//
//    //By utilizing the station's precise coordinates, this method generates a colored circle to represent the station on the map.
//    private void createStationCircle(Circle innerCircle, Circle outerRing, Station station, Color color) {
//        // Calculate the coordinates of the station on the mapPane
//        Point2D actualCoordinates = calculateActualCoordinates(station);
//
//        // Set the radius and centre of the circle
//        innerCircle.setCenterX(actualCoordinates.getX());
//        innerCircle.setCenterY(actualCoordinates.getY());
//        innerCircle.setRadius(5);
//        innerCircle.setFill(color);
//
//        outerRing.setCenterX(actualCoordinates.getX());
//        outerRing.setCenterY(actualCoordinates.getY());
//        outerRing.setRadius(5); // Slightly larger radius for the outer ring
//        outerRing.setFill(Color.TRANSPARENT);
//        outerRing.setStroke(color);
//        outerRing.setStrokeWidth(2);
//    }
//
//
//    // this method draws the circle we have already on a starting station red
//    private void drawFirstCircle(Station station) {
//        // Remove any existing circle
//        if (firstStationCircle != null) {
//            mapPane.getChildren().removeAll(firstStationCircle, firstStationRing);
//        }
//        // Makes a new circle on the station picked
//        firstStationCircle = new Circle();
//        firstStationRing = new Circle();
//        createStationCircle(firstStationCircle, firstStationRing, station, Color.RED);
//        // puts new circle on the map pane
//        mapPane.getChildren().addAll(firstStationCircle, firstStationRing);
//    }
//
//    // This method draws an aqua circle on the selected destination
//    private void drawDestinationCircle(Station station) {
//        // Remove any existing end station circle and outer ring from the mapPane
//        if (destinationStationCircle != null) {
//            mapPane.getChildren().removeAll(destinationStationCircle, destinationStationOuterRing);
//        }
//        // Create a new circle and outer ring with the specified station and color
//        destinationStationCircle = new Circle();
//        destinationStationOuterRing = new Circle();
//        createStationCircle(destinationStationCircle, destinationStationOuterRing, station, Color.AQUA);
//        // Add the new circle and outer ring to the mapPane
//        mapPane.getChildren().addAll(destinationStationCircle, destinationStationOuterRing);
//    }
//
//    private void populateMenuButtons(Map<String, Stationline> lines) {
//        Set<Station> uniqueStationsSet = new HashSet<>();
//        // Loop through each Stationline object in the lines Map
//        for (Stationline line : lines.values()) {
//            // Loop through each Station object in the Stationline's stations
//            // Add the station to the uniqueStations set
//            uniqueStationsSet.addAll(line.getStations());
//        }
//        // Convert the Set to a List
//        List<Station> uniqueStationsList = new ArrayList<>(uniqueStationsSet);
//        // Sort list of stations alphabetically
//        uniqueStationsList.sort(Comparator.comparing(Station::getStationName));
//        // Add the stations to the menu button
//        avoidStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
//        waypointStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
//        startStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
//        destinationStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
//    }
//
//    private List<MenuItem> createStationMenuItems(List<Station> stations) {
//        List<MenuItem> stationMenuItems = new ArrayList<>();
//        for (Station station : stations) {
//            MenuItem menuItem = new MenuItem(station.getStationName());
//            menuItem.setOnAction(e -> handleMenuClick(e, station));
//            stationMenuItems.add(menuItem);
//        }
//        return stationMenuItems;
//    }
//
//
//    // This method draws a line between two stations with a given color
//    private void drawLineBetweenStations(Station start, Station end, Color color) {
//        // Calculate the actual coordinates of the start and end stations on the mapPane
//        Point2D startPoint = calculateActualCoordinates(start);
//        Point2D endPoint = calculateActualCoordinates(end);
//        // Create a new line with the calculated start and end points and set its color and width
//        javafx.scene.shape.Line line = new javafx.scene.shape.Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
//        line.setStroke(color);
//        line.setStrokeWidth(4);
//        // Add the line to the mapPane
//        mapPane.getChildren().add(line);
//    }
//
//
//    // This method is called when the user wants to perform a breadth-first search to find the shortest path between two selected stations
//    public void bfsSearch(ActionEvent actionEvent) {
//        // Determine the shortest route between the selected starting and destination stations using the Graph's findShortestPath method
//        Path shortestRoute = graph.bfsAlgorithm(firstSelectedStation, selectedDestinationStation);
//
//        // Display an error message if no path is found
//        if (shortestRoute == null) {
//            System.out.println("No path found between the selected stations");
//        } else {
//            // If a path is found, print the path and the number of stops
//            List<Station> path = shortestRoute.getPath();
//            System.out.println(path.get(0).getStationName() + " to " + path.get(path.size()-1).getStationName());
//
//            // Initialize variables to track the current and next lines
//            Stationline currentLine = null;
//            Stationline nextLine = null;
//
//            // Initialize lists to store the station path and line changes
//            List<String> stationPath = new ArrayList<>();
//            List<String> lineChanges = new ArrayList<>();
//
//            // Iterate over each station in the path
//            for (int i = 0; i < path.size() - 1; i++) {
//                Station station1 = path.get(i);
//                Station station2 = path.get(i + 1);
//
//                // Get the common lines between the two stations
//                List<Stationline> commonLines = getCommonLines(station1, station2);
//
//                // If there is no current line or the current line is not in the list of common lines,
//                // update the current line and print it
//                if (currentLine == null || !commonLines.contains(currentLine)) {
//                    // Update the current line (select the first one from the list for simplicity)
//                    nextLine = commonLines.get(0);
//
//                    // Add the line change to the list
//                    if (i != 0) { // Avoid adding a line change for the first station
//                        lineChanges.add(currentLine.getLineName() + " to " + station1.getStationName());
//                        lineChanges.add(nextLine.getLineName());
//                    } else { // Handle the initial line at the starting station
//                        lineChanges.add(nextLine.getLineName());
//                    }
//                    currentLine = nextLine;
//                }
//
//                // Add the station to the station path list
//                stationPath.add(station1.getStationName());
//            }
//
//            // Add the final station to the station path list
//            stationPath.add(path.get(path.size() - 1).getStationName());
//
//            // Add the final line change to the list
//            lineChanges.add("Take the " + currentLine.getLineName() + " to " + path.get(path.size()-1).getStationName());
//
//            // Print the station path
//            System.out.println("Shortest path: ");
//            System.out.println(String.join(" -> ", stationPath));
//
//            // Print the line changes
//            System.out.println(String.join("\n", lineChanges));
//            System.out.println("Number of stops: " + shortestRoute.getStops());
//            drawShortestPath(shortestRoute);
//
//            // Update the ListView
//            List<String> outputLines = new ArrayList<>();
//
//            // Add the BFS header indicating the starting and destination stations
//            outputLines.add("\nBFS: " + firstSelectedStation.getStationName() + " to " + selectedDestinationStation.getStationName());
//            outputLines.add("Number of stops: " + shortestRoute.getStops());
//            // Iterate over the stations in the path and add appropriate markers
//            for (int i = 0; i < path.size(); i++) {
//                if (i == 0 || i == path.size() - 1) {
//                    // Add a special marker for the first and last stations
//                    outputLines.add("** " + path.get(i).getStationName() + " **");
//                } else {
//                    // Add a downward arrow marker for intermediate stations
//                    outputLines.add("-- " + path.get(i).getStationName());
//                }
//            }
//
//            // Add the line directions header and the list of line changes
//            outputLines.add("\nStationline Directions:");
//            outputLines.addAll(lineChanges);
//
//            // Create an observable list and populate it with the output lines
//            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
//            routeOutput.setItems(items);
//        }
//    }
//
//    // This method finds the shortest path between two stations using dijkstras algorithm
//    //use high street kensington and chancery lane to see difference in the two algorithms
//    public void dijkstraSearch(ActionEvent actionEvent) {
//        if (firstSelectedStation == null || selectedDestinationStation == null) {
//            System.out.println("Please select both start and end stations");
//            return;
//        }
//
//        Set<Station> allStations = new HashSet<>(stations.values());
//        Path shortestRoute = graph.dijkstraAlgorithm(allStations, firstSelectedStation, selectedDestinationStation);
//
//        if (shortestRoute == null) {
//            System.out.println("No path found between the selected stations");
//        } else {
//            List<Station> path = shortestRoute.getPath();
//            System.out.println(path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());
//
//            Stationline currentLine = null;
//            Stationline nextLine = null;
//
//            List<String> stationPath = new ArrayList<>();
//            List<String> lineChanges = new ArrayList<>();
//
//            double totalLineChangeTime = 0.0;
//            double lineChangeTime = 20.0;
//
//            for (int i = 0; i < path.size() - 1; i++) {
//                Station station1 = path.get(i);
//                Station station2 = path.get(i + 1);
//
//                List<Stationline> commonLines = getCommonLines(station1, station2);
//
//                if (currentLine == null || !commonLines.contains(currentLine) || !currentLine.equals(nextLine)) {
//                    // Update the current line
//                    nextLine = commonLines.get(0);
//
//                    // Add the line change to the list
//                    if (i != 0) { // Avoid adding a line change for the first station
//                        lineChanges.add("Take " + currentLine.getLineName() + " to " + station1.getStationName());
//                        lineChanges.add("Change to " + nextLine.getLineName());
//                        totalLineChangeTime += lineChangeTime;  // or totalLineChangeDistance += lineChangeDistance;
//                    } else { // Handle the initial line at the starting station
//                        lineChanges.add("Start with " + nextLine.getLineName());
//                    }
//                    currentLine = nextLine;
//                }
//
//                stationPath.add(station1.getStationName());
//            }
//
//            stationPath.add(path.get(path.size() - 1).getStationName());
//            lineChanges.add("Take " + currentLine.getLineName() + " to " + path.get(path.size() - 1).getStationName());
//
//            System.out.println("Shortest path: ");
//            System.out.println(String.join(" -> ", stationPath));
//
//            System.out.println(String.join("\n", lineChanges));
//
//            DecimalFormat decimalFormat = new DecimalFormat("#.00");
//            String roundedLineChangeTime = decimalFormat.format(totalLineChangeTime);
//
//            System.out.println("Total Stationline Change Time/Distance: " + roundedLineChangeTime);
//
//            drawShortestPath(shortestRoute);
//
//            // Calculate total time
//            double totalTime = shortestRoute.getDistance() + totalLineChangeTime;
//
//            List<String> outputLines = new ArrayList<>();
//            outputLines.add("\nDijkstra: " + firstSelectedStation.getStationName() + " to " + selectedDestinationStation.getStationName());
//            outputLines.add("Total Base Time: " + decimalFormat.format(shortestRoute.getDistance()) + " seconds");
//            outputLines.add("Total Stationline Change Time: " + roundedLineChangeTime + " seconds");
//            outputLines.add("Total Time: " + decimalFormat.format(totalTime) + " seconds");
//
//            for (int i = 0; i < path.size(); i++) {
//                if (i == 0 || i == path.size() - 1) {
//                    outputLines.add("** " + path.get(i).getStationName() + " **");
//                } else {
//                    outputLines.add("-- " + path.get(i).getStationName());
//                }
//            }
//            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
//            routeOutput.setItems(items);
//
//        }
//    }
//
//    public List<Stationline> getCommonLines(Station station1, Station station2) {
//        // Initialize a new list with the lines of the first station
//        List<Stationline> commonLines = new ArrayList<>(station1.getLines());
//
//        // Keep only the lines that are also present in the lines of the second station
//        commonLines.retainAll(station2.getLines());
//
//        // Return the list of shared lines
//        return commonLines;
//    }
//
//    private Station findNearestStation(double x, double y) {
//        // Initialize variables to store the nearest station and its distance
//        Station nearestStation = null;
//        double nearestDistance = Double.MAX_VALUE;
//
//        // Iterate over all stations to find the nearest one
//        for (Station station : stations.values()) {
//            // Calculate the distance between the given coordinates and the station's coordinates
//            double distance = calculateDistance(x, y, station.getXcoord(), station.getYcoord());
//
//            // If the calculated distance is smaller than the current nearest distance,
//            // update the nearest station and nearest distance
//            if (distance < nearestDistance) {
//                nearestDistance = distance;
//                nearestStation = station;
//            }
//        }
//        return nearestStation;
//    }
//
//    private double calculateDistance(double x1, double y1, double x2, double y2) {
//        //Use Euclidean Formula to calculate the distance between two points
//        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
//    }
//
//    private void selectMenuItem(MenuButton menuButton, String stationName) {
//        // Iterate through menuButton's items list and select item with matching stationName
//        for (MenuItem item : menuButton.getItems()) {
//            if (item.getText().equals(stationName)) {
//                // Set menuButton's text to selected item's text
//                menuButton.setText(item.getText());
//                break;
//            }
//        }
//    }
//
//    public void clearMap(ActionEvent actionEvent) {
//        //clear lines
//        mapPane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);
//        //clear listview
//        routeOutput.getItems().clear();
//    }
//
//
//    public void initialize(URL url, ResourceBundle resourceBundle){
//        maincon = this; // Set the maincon to this instance of the controller
//    }
//
//    public void avoidStation(ActionEvent event) {
//    }
//
//    public void removeStation(ActionEvent event) {
//    }
//
//    public void removeWaypoint(ActionEvent event) {
//    }
//
//    public void addWaypoint(ActionEvent event) {
//    }
//
//    public void dijkstraNoLineSearch(ActionEvent event) {
//        if (firstSelectedStation == null || selectedDestinationStation == null) {
//            return;
//        }
//
//        Set<Station> allStations = new HashSet<>(stations.values());
//        Path shortestRoute = graph.dijkstraAlgorithm(allStations, firstSelectedStation, selectedDestinationStation);
//
//        if (shortestRoute == null) {
//            System.out.println("No path found");
//        } else {
//            List<Station> path = shortestRoute.getPath();
//            System.out.println(path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());
//
//            Stationline currentLine = null;
//            Stationline nextLine = null;
//
//            List<String> stationPath = new ArrayList<>();
//            List<String> lineChanges = new ArrayList<>();
//
//            for (int i = 0; i < path.size() - 1; i++) {
//                Station station1 = path.get(i);
//                Station station2 = path.get(i + 1);
//
//                List<Stationline> commonLines = getCommonLines(station1, station2);
//
//                if (currentLine == null || !commonLines.contains(currentLine) || !currentLine.equals(nextLine)) {
//                    nextLine = commonLines.get(0);
//
//                    if (i != 0) {
//                        lineChanges.add("Take the" + currentLine.getLineName() + " to " + station1.getStationName());
//                        lineChanges.add("Change line to " + nextLine.getLineName());
//                    } else {
//                        lineChanges.add("Start on  " + nextLine.getLineName());
//                    }
//
//                    currentLine = nextLine;
//                }
//
//                stationPath.add(station1.getStationName());
//            }
//
//            stationPath.add(path.get(path.size() - 1).getStationName());
//            lineChanges.add("Take " + currentLine.getLineName() + " to " + path.get(path.size() - 1).getStationName());
//
//            System.out.println("Shortest path: ");
//            System.out.println(String.join(" -> ", stationPath));
//
//            System.out.println(String.join("\n", lineChanges));
//
//            drawShortestPath(shortestRoute);
//
//            // Calculate total time
//            double totalTime = shortestRoute.getDistance();
//
//            DecimalFormat decimalFormat = new DecimalFormat("#.00");
//            List<String> outputLines = new ArrayList<>();
//            outputLines.add("\nDijkstra: " + firstSelectedStation.getStationName() + " to " + selectedDestinationStation.getStationName());
//            outputLines.add("Total Time: " + decimalFormat.format(totalTime) + " seconds");
//
//            for (int i = 0; i < path.size(); i++) {
//                if (i == 0 || i == path.size() - 1) {
//                    outputLines.add("** " + path.get(i).getStationName() + " **");
//                } else {
//                    outputLines.add("-- " + path.get(i).getStationName());
//                }
//            }
//
//            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
//            routeOutput.setItems(items);
//        }
//    }
//
//    public void search(ActionEvent actionEvent) {
//    }





//
//public void initialiseMap(ActionEvent actionEvent) {
//    if (isMapPopulated) {
//        System.out.println("Map already populated");
//        return;
//    }
//
//    try {
//        // Get the resource as an InputStream
//        InputStream inputStream = getClass().getResourceAsStream("/com/michaelmckibbin/viennaubhan/data/vienna_subway_list_1.csv");
//        if (inputStream == null) {
//            System.err.println("Could not find CSV file");
//            return;
//        }
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//
//        // Create data structures to store the station information
//        Map<String, List<Station>> lineStations = new HashMap<>();
//
//        while ((line = reader.readLine()) != null) {
//            String[] data = line.split(",");
//            if (data.length == 5) {
//                String stationName = data[0].trim();
//                String lineName = data[1].trim();
//                String lineColor = data[2].trim();
//                int xCoord = Integer.parseInt(data[3].trim());
//                int yCoord = Integer.parseInt(data[4].trim());
//
//                // Create or get the list of stations for this line
//                lineStations.computeIfAbsent(lineName, k -> new ArrayList<>())
//                        .add(new Station(stationName, xCoord, yCoord));
//
//                // Here you would add code to create your visual elements
//                // Similar to what you were doing with the hardcoded data
//            }
//        }
//
//        reader.close();
//        isMapPopulated = true;
//
//    } catch (IOException e) {
//        System.err.println("Error reading CSV file: " + e.getMessage());
//    } catch (NumberFormatException e) {
//        System.err.println("Error parsing coordinates: " + e.getMessage());
//    }
//}
//
//// Find transfer stations
//private Set<String> findTransferStations(Map<String, List<Station>> lineStations) {
//    Set<String> allStations = new HashSet<>();
//    Set<String> transferStations = new HashSet<>();
//
//    for (List<Station> stations : lineStations.values()) {
//        for (Station station : stations) {
//            if (!allStations.add(station.getName())) {
//                transferStations.add(station.getName());
//            }
//        }
//    }
//    return transferStations;
//}
//
//// Draw connections between stations on the same line
//private void drawLineConnections(String lineName, List<Station> stations, String lineColor) {
//    for (int i = 0; i < stations.size() - 1; i++) {
//        Station current = stations.get(i);
//        Station next = stations.get(i + 1);
//        // Add line drawing code here
//        // This could be a method in the Graph class that takes two stations and draws line between them in the same color as the 'lineColor'.
//
//    }
//}
