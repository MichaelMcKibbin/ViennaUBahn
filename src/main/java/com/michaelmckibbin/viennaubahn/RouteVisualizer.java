package com.michaelmckibbin.viennaubahn;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import java.util.List;
import java.util.Set;

//public class RouteVisualizer {
//
//    // Original image dimensions
//    private static final double ORIGINAL_WIDTH = 1488.0;
//    private static final double ORIGINAL_HEIGHT = 993.0;
//    private static final double FIT_WIDTH = 1000; // 800 = 181, 900 = 81, 1000 = 11
//    private static final double X_OFFSET = 10.0; // Adjust as needed
//    // experimenting with formulaic derivation of X_OFFSET based on relationship between known FIT_WIDTH and required X_OFFSET
//    //private static final double X_OFFSET = Math.max(0, (ORIGINAL_WIDTH - FIT_WIDTH) * 0.2619);
//    //private static final double X_OFFSET = (ORIGINAL_WIDTH - FIT_WIDTH) * 0.262;
//    private static final double Y_OFFSET = 4.0;
//
//    // line stroke width setting
//    private static final double LINE_STROKE_WIDTH = 8.0;
//
//    // Constants for marker sizes
//    private static final double START_STATION_MARKER_SIZE = 9;
//    private static final double INTERMEDIATE_STATION_MARKER_SIZE = 4;
//    private static final double END_STATION_MARKER_SIZE = 9.0;
//    private static final double LINE_CHANGE_STATION_MARKER_SIZE = 7;
//    private static final double WAYPOINT_STATION_MARKER_SIZE = 8.0;
//    private static final double AVOID_MARKER_SIZE = 4.0;
//
//    // Constants for marker colors
//    private static final Color START_STATION_FILL_COLOR = Color.PALEVIOLETRED;
//    private static final Color END_STATION_FILL_COLOR = Color.LIMEGREEN;
//    private static final Color LINE_CHANGE_STATION_FILL_COLOR = Color.YELLOW;
//    private static final Color INTERMEDIATE_STATION_FILL_COLOR = Color.LAWNGREEN;
//    private static final Color WAYPOINT_STATION_FILL_COLOR = Color.INDIGO;
//    private static final Color STATION_STROKE_COLOR = Color.BLACK;
//
//    private final Canvas canvas;
//    private final double lineWidth;
//    private final Color lineColor;
//
////    private void drawRoute(RoutePath routePath) {
////        setGreyMap(); // Switch to grey map
////        routeLayer.getChildren().clear();
////        List<Station> stations = routePath.getStations();
////        for (int i = 0; i < stations.size() - 1; i++) {
////            Station current = stations.get(i);
////            Station next = stations.get(i + 1);
////            // Scale coordinates and apply X offset
////            int startX = (int) Math.round(((current.getX() + X_OFFSET) / ORIGINAL_WIDTH) * mapImageView.getFitWidth());
////            int startY = (int) Math.round((current.getY() / ORIGINAL_HEIGHT) * mapImageView.getFitHeight() + Y_OFFSET);
////            int endX = (int) Math.round(((next.getX() + X_OFFSET) / ORIGINAL_WIDTH) * mapImageView.getFitWidth());
////            int endY = (int) Math.round((next.getY() / ORIGINAL_HEIGHT) * mapImageView.getFitHeight() + Y_OFFSET);
////            // Then add Y_OFFSET to the scaled coordinates
////            startY += Y_OFFSET; endY += Y_OFFSET;
////            // Debug output
////            System.out.println("Drawing line from (" + startX + "," + startY +") to (" + endX + "," + endY + ")");
////            Line line = new Line(startX, startY, endX, endY);
////            // Use the next station's line color instead of the current station's
////            try {line.setStroke(LineColor.valueOf(next.getLineColor().toUpperCase()).getColor());
////            } catch (IllegalArgumentException e) {line.setStroke(Color.BLACK); System.out.println("Unknown line color: " + next.getLineColor());  }
////            line.setStrokeWidth(LINE_STROKE_WIDTH);
////            line.setStrokeLineCap(StrokeLineCap.ROUND);
////            routeLayer.getChildren().add(line); }
////        // Draw station markers
////        stations = routePath.getStations();
////        for (int i = 0; i < stations.size(); i++) {Station station = stations.get(i);
////            int x = (int) Math.round(((station.getX() + X_OFFSET) / ORIGINAL_WIDTH) * mapImageView.getFitWidth());
////            int y = (int) Math.round((station.getY() / ORIGINAL_HEIGHT) * mapImageView.getFitHeight() + Y_OFFSET);
////            Circle marker = new Circle(x, y, 0);
////            // Check if this is a line change point (but not for the last station)
////            boolean isLineChange = false;
////            if (i < stations.size() - 1) {Station nextStation = stations.get(i + 1); isLineChange = !station.getLineColor().equals(nextStation.getLineColor()); }
////            // check if this is a waypoint
////            boolean isWaypoint = waypoints.contains(station);
////            // Set different colors and sizes for start and end stations
////            if (i == 0) {  // Start station
////                marker.setRadius(START_STATION_MARKER_SIZE); marker.setFill(START_STATION_FILL_COLOR); marker.setStroke(STATION_STROKE_COLOR); marker.setStrokeWidth(2);
////            } else if (i == stations.size() - 1) {  // End station
////                marker.setRadius(END_STATION_MARKER_SIZE); marker.setFill(END_STATION_FILL_COLOR); marker.setStroke(STATION_STROKE_COLOR); marker.setStrokeWidth(1);
////            } else if (isLineChange) {  // Line change point
////                marker.setStroke(STATION_STROKE_COLOR); marker.setStrokeWidth(1);
////            }else if (isWaypoint){
////                marker.setRadius(WAYPOINT_STATION_MARKER_SIZE); marker.setFill(WAYPOINT_STATION_FILL_COLOR); marker.setStroke(STATION_STROKE_COLOR); marker.setStrokeWidth(1);
////            } else {  // Intermediate stations
////                marker.setRadius(INTERMEDIATE_STATION_MARKER_SIZE); marker.setFill(INTERMEDIATE_STATION_FILL_COLOR); marker.setStroke(STATION_STROKE_COLOR); marker.setStrokeWidth(1);
////            }
////            routeLayer.getChildren().add(marker);
////            System.out.println("Drawing station marker at (" + x + "," + y + ") for " + station.getName() +
////                    (i == 0 ? " (Start)" : (i == stations.size() - 1 ? " (End)" : "")));
////        }
////    }
//    public RouteVisualizer(Canvas canvas, double lineWidth, Color lineColor) {
//        this.canvas = canvas;
//        this.lineWidth = lineWidth;
//        this.lineColor = lineColor;
//    }
//    public void clearRoute() {
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//    }
//    public void visualizeRoute(List<Station> route) {
//        if (route == null || route.size() < 2) return;
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.setStroke(lineColor);
//        gc.setLineWidth(lineWidth);
//        // Calculate scale factors
//        double scaleX = canvas.getWidth() / ORIGINAL_WIDTH;
//        double scaleY = canvas.getHeight() / ORIGINAL_HEIGHT;
//        System.out.println("Canvas dimensions: " + canvas.getWidth() + "x" + canvas.getHeight());
//        for (int i = 0; i < route.size() - 1; i++) {
//            Station current = route.get(i);
//            Station next = route.get(i + 1);
//            // Scale and offset the coordinates
//            double startX = (current.getX() * scaleX) + X_OFFSET;
//            double startY = (current.getY() * scaleY) + Y_OFFSET;
//            double endX = (next.getX() * scaleX) + X_OFFSET;
//            double endY = (next.getY() * scaleY) + Y_OFFSET;
//
//            System.out.println("Drawing line from (" + startX + "," + startY +
//                    ") to (" + endX + "," + endY + ")");
//            // Draw line between stations
//            gc.beginPath();
//            gc.moveTo(startX, startY);
//            gc.lineTo(endX, endY);
//            gc.stroke();
//        }
//    }
//}

public class RouteVisualizer {
    // Original dimensions and scaling constants
    private static final double ORIGINAL_WIDTH = 1488.0;
    private static final double ORIGINAL_HEIGHT = 993.0;
    private static final double FIT_WIDTH = 1000.0;
    private static final double X_OFFSET = 10.0;
    private static final double Y_OFFSET = 4.0;

    // Station marker sizes and colors
    private static final double START_STATION_MARKER_SIZE = 8.0;
    private static final double END_STATION_MARKER_SIZE = 8.0;
    private static final double WAYPOINT_STATION_MARKER_SIZE = 6.0;
    private static final double INTERMEDIATE_STATION_MARKER_SIZE = 4.0;
    private static final double LINE_STROKE_WIDTH = 3.0;

    private static final Color START_STATION_FILL_COLOR = Color.GREEN;
    private static final Color END_STATION_FILL_COLOR = Color.RED;
    private static final Color WAYPOINT_STATION_FILL_COLOR = Color.YELLOW;
    private static final Color INTERMEDIATE_STATION_FILL_COLOR = Color.WHITE;
    private static final Color STATION_STROKE_COLOR = Color.BLACK;

    private final ImageView mapImageView;
    private final Pane routeLayer;
    private final Set<Station> waypoints;

    public RouteVisualizer(ImageView mapImageView, Pane routeLayer, Set<Station> waypoints) {
        this.mapImageView = mapImageView;
        this.routeLayer = routeLayer;
        this.waypoints = waypoints;
    }

    public void drawRoute(RoutePath routePath) {
        setGreyMap(); // This method needs to be implemented or passed from Controller
        routeLayer.getChildren().clear();
        List<Station> stations = routePath.getStations();

        // Draw lines between stations
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
            System.out.println("Drawing line from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");

            Line line = new Line(startX, startY, endX, endY);

            // Use the next station's line color
            try {
                line.setStroke(LineColor.valueOf(next.getLineColor().toUpperCase()).getColor());
            } catch (IllegalArgumentException e) {
                line.setStroke(Color.BLACK);
                System.out.println("Unknown line color: " + next.getLineColor());
            }

            line.setStrokeWidth(LINE_STROKE_WIDTH);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            routeLayer.getChildren().add(line);
        }

        // Draw station markers
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

            // check if this is a waypoint
            boolean isWaypoint = waypoints.contains(station);

            // Set different colors and sizes for stations
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
                marker.setStroke(STATION_STROKE_COLOR);
                marker.setStrokeWidth(1);
            } else if (isWaypoint) {
                marker.setRadius(WAYPOINT_STATION_MARKER_SIZE);
                marker.setFill(WAYPOINT_STATION_FILL_COLOR);
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

    public void clearRoute() {
        routeLayer.getChildren().clear();
    }

    // You'll need to implement or pass this method from Controller
    private void setGreyMap() {
        // Implementation needed
    }
}
