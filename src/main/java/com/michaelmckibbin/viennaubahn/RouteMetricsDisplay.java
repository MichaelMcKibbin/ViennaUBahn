package com.michaelmckibbin.viennaubahn;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This class is responsible for displaying the metrics of a route.
 * It updates the labels on the UI with the number of stops, execution time,
 * journey duration, and number of transfers.
 */
public class RouteMetricsDisplay {
    @FXML private Label stopsLabel;
    @FXML private Label timeLabel;
    @FXML private Label durationLabel;
    @FXML private Label transfersLabel;
    @FXML private Label euclideanDistLabel;

    public RouteMetricsDisplay(Label stopsLabel, Label timeLabel,
                             Label durationLabel, Label transfersLabel) {
        this.stopsLabel = stopsLabel;
        this.timeLabel = timeLabel;
        this.durationLabel = durationLabel;
        this.transfersLabel = transfersLabel;
    }

    public void updateMetrics(int stops, long executionTimeNanos,
                            long journeyDuration, int transfers) {

        // Calculate precise time values for execution time
        double milliseconds = executionTimeNanos / 1_000_000.0;
        double microseconds = executionTimeNanos / 1_000.0;

        Platform.runLater(() -> {
            stopsLabel.setText("Stops: " + stops);

            // Display algorithm execution time
            if (milliseconds < 0.01) {
                // For very small times, show microseconds
                timeLabel.setText(String.format("Time: %.2f μs", microseconds));
            } else {
                // For larger times, show milliseconds
                timeLabel.setText(String.format("Time: %.4f ms", milliseconds));
            }

            // Display journey duration in minutes
            durationLabel.setText(String.format("Journey Duration: %d min", journeyDuration));

            // Display number of transfers
            transfersLabel.setText("Transfers: " + transfers);
        });

        // Detailed console output
        System.out.println("\nRoute Metrics:");
        System.out.println("-------------");
        System.out.println("Stops: " + stops);
        System.out.println(String.format("Execution Time: %.4f ms (%.2f μs)",
                milliseconds, microseconds));
        System.out.println("Journey Duration: " + journeyDuration + " minutes");
        System.out.println("Transfers: " + transfers);
        System.out.println("Raw execution time: " + executionTimeNanos + " ns");
        System.out.println("-------------");
    }
}
