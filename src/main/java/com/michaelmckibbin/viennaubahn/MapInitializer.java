package com.michaelmckibbin.viennaubahn;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Initializes and manages the map display components for the Vienna U-Bahn application.
 * This class handles the setup and configuration of the map layers, including the base
 * map image and the transparent route overlay layer.
 *
 * <p>The class manages:</p>
 * <ul>
 *     <li>Map image scaling and proportions</li>
 *     <li>Container layout and hierarchy</li>
 *     <li>Route overlay positioning and bindings</li>
 *     <li>Mouse interaction settings</li>
 * </ul>
 *
 * <p>The map display consists of two main layers:</p>
 * <ul>
 *     <li>Base map image (ImageView)</li>
 *     <li>Transparent route overlay (Pane)</li>
 * </ul>
 *
 * Example usage:
 * <pre>
 * MapInitializer initializer = new MapInitializer(mapView, container, 1488.0, 993.0, 1000.0);
 * initializer.initialize();
 * </pre>
 *
 * @author Michael McKibbin
 * @version 1.0
 */
public class MapInitializer {
    /** The ImageView containing the base map image */
    private final ImageView mapImageView;

    /** The container pane that holds both the map and route layers */
    private final Pane mapContainer;

    /** The transparent overlay pane for drawing routes */
    private final Pane routeLayer;

    /** Original width of the map image */
    private final double ORIGINAL_WIDTH;

    /** Original height of the map image */
    private final double ORIGINAL_HEIGHT;

    /** Desired display width of the map */
    private final double FIT_WIDTH;

    /**
     * Creates a new MapInitializer with specified dimensions and components.
     *
     * @param mapImageView The ImageView that will display the map
     * @param mapContainer The container Pane that will hold all layers
     * @param originalWidth The original width of the map image
     * @param originalHeight The original height of the map image
     * @param fitWidth The desired display width of the map
     */
    public MapInitializer(ImageView mapImageView, Pane mapContainer,
                         double originalWidth, double originalHeight,
                         double fitWidth) {
        this.mapImageView = mapImageView;
        this.mapContainer = mapContainer;
        this.routeLayer = new Pane();
        this.ORIGINAL_WIDTH = originalWidth;
        this.ORIGINAL_HEIGHT = originalHeight;
        this.FIT_WIDTH = fitWidth;
    }

    /**
     * Initializes the map display by:
     * <ul>
     *     <li>Setting up the transparent route layer</li>
     *     <li>Configuring the map image dimensions and ratio</li>
     *     <li>Organizing the layer hierarchy</li>
     *     <li>Establishing size bindings between layers</li>
     * </ul>
     */
    public void initialize() {
        // Initialize the route layer
        routeLayer.setMouseTransparent(true);

        // Set initial size for ImageView
        mapImageView.setFitWidth(FIT_WIDTH);
        mapImageView.setFitHeight(mapImageView.getFitWidth() * (ORIGINAL_HEIGHT/ORIGINAL_WIDTH));
        mapImageView.setPreserveRatio(true);

        // Set up container
        mapContainer.getChildren().clear();
        mapContainer.getChildren().addAll(mapImageView, routeLayer);

        // Bind routeLayer size
        routeLayer.prefWidthProperty().bind(mapImageView.fitWidthProperty());
        routeLayer.prefHeightProperty().bind(mapImageView.fitHeightProperty());
    }

    /**
     * Returns the route overlay layer for drawing routes.
     *
     * @return The Pane used for drawing routes over the map
     */
    public Pane getRouteLayer() {
        return routeLayer;
    }
}

