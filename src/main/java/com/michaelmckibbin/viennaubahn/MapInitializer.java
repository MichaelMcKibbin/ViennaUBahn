package com.michaelmckibbin.viennaubahn;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MapInitializer {
    private final ImageView mapImageView;
    private final Pane mapContainer;
    private final Pane routeLayer;
    private final double ORIGINAL_WIDTH;
    private final double ORIGINAL_HEIGHT;
    private final double FIT_WIDTH;

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

    public Pane getRouteLayer() {
        return routeLayer;
    }
}

