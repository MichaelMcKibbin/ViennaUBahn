package com.michaelmckibbin.viennaubahn;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class RouteMetricsDisplay {
    private final Label stopsLabel;
    private final Label timeLabel;
    private final Label nodesLabel;
    private final Label queueLabel;

    public RouteMetricsDisplay(Label stopsLabel, Label timeLabel,
                             Label nodesLabel, Label queueLabel) {
        this.stopsLabel = stopsLabel;
        this.timeLabel = timeLabel;
        this.nodesLabel = nodesLabel;
        this.queueLabel = queueLabel;
    }

    public void updateMetrics(int stops, long executionTime,
                            int nodesVisited, int maxQueueSize) {
        Platform.runLater(() -> {
            stopsLabel.setText("Stops: " + stops);
            timeLabel.setText(String.format("Time: %.2f ms", executionTime / 1_000_000.0));
            nodesLabel.setText("Nodes Visited: " + nodesVisited);
            queueLabel.setText("Max Queue Size: " + maxQueueSize);
        });
    }
}


