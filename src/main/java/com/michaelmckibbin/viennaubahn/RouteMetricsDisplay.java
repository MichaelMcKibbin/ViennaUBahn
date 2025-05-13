package com.michaelmckibbin.viennaubahn;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RouteMetricsDisplay {
    @FXML private Label stopsLabel;
    @FXML private Label timeLabel;
    @FXML private Label nodesLabel;
    @FXML private Label queueLabel;
    @FXML private Label euclideanDistLabel;

    public RouteMetricsDisplay(Label stopsLabel, Label timeLabel,
                               Label nodesLabel, Label queueLabel) {
        this.stopsLabel = stopsLabel;
        this.timeLabel = timeLabel;
        this.nodesLabel = nodesLabel;
        this.queueLabel = queueLabel;
    }

    public void updateMetrics(int stops, long executionTimeNanos,
                              int nodesVisited, int maxQueueSize) {

        // Calculate precise time values
        double milliseconds = executionTimeNanos / 1_000_000.0;
        double microseconds = executionTimeNanos / 1_000.0;

        Platform.runLater(() -> {
            stopsLabel.setText("Stops: " + stops);
            if (milliseconds < 0.01) {
                // For very small times, show microseconds
                timeLabel.setText(String.format("Time: %.2f μs", microseconds));
            } else {
                // For larger times, show milliseconds
                timeLabel.setText(String.format("Time: %.4f ms", milliseconds));
            }
            nodesLabel.setText("Nodes Visited: " + nodesVisited);
            queueLabel.setText("Max Queue Size: " + maxQueueSize);

        });

        // Detailed console output
        System.out.println("\nRoute Metrics:");
        System.out.println("-------------");
        System.out.println("Stops: " + stops);
        System.out.println(String.format("Time: %.4f ms (%.2f μs)",
                milliseconds, microseconds));
        System.out.println("Raw time: " + executionTimeNanos + " ns");
        System.out.println("Nodes Visited: " + nodesVisited);
        System.out.println("Max Queue Size: " + maxQueueSize);
        System.out.println("-------------");
    }
}

