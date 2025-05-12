package com.michaelmckibbin.viennaubahn;

import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import java.util.List;
import java.util.Set;

public class RouteVisualizer {
    // Original dimensions and scaling constants
    private static final double ORIGINAL_WIDTH = 1488.0;
    private static final double ORIGINAL_HEIGHT = 993.0;
    private static final double FIT_WIDTH = 1000.0;
    private static final double X_OFFSET = 10.0;
    private static final double Y_OFFSET = 5.0;

    // Station marker sizes and colors
    private static final double START_STATION_MARKER_SIZE = 10.0;
    private static final double END_STATION_MARKER_SIZE = 10.0;
    private static final double WAYPOINT_STATION_MARKER_SIZE = 9.0;
    private static final double LINE_CHANGE_STATION_MARKER_SIZE = 8.0;
    private static final double INTERMEDIATE_STATION_MARKER_SIZE = 5.0;
    private static final double LINE_STROKE_WIDTH = 6.0;

    private static final Color START_STATION_FILL_COLOR = Color.PALEVIOLETRED;
    private static final Color END_STATION_FILL_COLOR = Color.LIMEGREEN;
    private static final Color WAYPOINT_STATION_FILL_COLOR = Color.INDIGO;
    private static final Color INTERMEDIATE_STATION_FILL_COLOR = Color.GREENYELLOW;
    private static final Color LINE_CHANGE_STATION_FILL_COLOR = Color.YELLOW;
    private static final Color STATION_STROKE_COLOR = Color.BLACK;

    private final ImageView mapImageView;
    private final Pane routeLayer;
    private final ObservableList<Station> waypoints;
    //private final Set<Station> waypoints;
    private final Image colorMap;
    private final Image greyMap;

    public RouteVisualizer(ImageView mapImageView, Pane routeLayer,
                           ObservableList<Station> waypoints, Image colorMap, Image greyMap) {
        this.mapImageView = mapImageView;
        this.routeLayer = routeLayer;
        this.waypoints = waypoints;
        this.colorMap = colorMap;
        this.greyMap = greyMap;
    }

    public void resetMap() {
        mapImageView.setImage(colorMap);
        clearRoute();
    }

    private void setGreyMap() {
        mapImageView.setImage(greyMap);
    }

    public void drawRoute(RoutePath routePath) {
        setGreyMap(); // from Controller
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

            // Use the next station's line color - makes line changes look better
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
                marker.setRadius(LINE_CHANGE_STATION_MARKER_SIZE);
                marker.setFill(LINE_CHANGE_STATION_FILL_COLOR);
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


}
