package com.michaelmckibbin.viennaubahn;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;

/**
 * Manages the list of waypoints in the application.
 * This class handles the setup and management of the waypoint list view,
 * including the removal of waypoints and the setup of various components
 * related to the waypoint list.
 */
public class StationListManager {
    private final ComboBox<Station> waypointComboBox;
    private final ListView<Station> waypointsListView;
    private final ObservableList<Station> waypoints;
    private final Button removeWaypointButton;

    public StationListManager(ComboBox<Station> waypointComboBox,
                            ListView<Station> waypointsListView,
                            ObservableList<Station> waypoints,
                              Button removeWaypointButton) {
        this.waypointComboBox = waypointComboBox;
        this.waypointsListView = waypointsListView;
        this.waypoints = waypoints;
        this.removeWaypointButton = removeWaypointButton;
    }

    public void initialize() {
        waypointsListView.setItems(waypoints);
        setupCellFactory();
        setupComboBoxConverter();
        setupComboBoxCellFactory();
        setupComboBoxButtonCell();
        setupRemoveButton();
    }

    private void setupCellFactory() {
        waypointsListView.setCellFactory(listView -> new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName() + " " + station.getLinesAsString());
                }
            }
        });
    }

    private void setupComboBoxConverter() {
        waypointComboBox.setConverter(new StringConverter<Station>() {
            @Override
            public String toString(Station station) {
                return station == null ? "" :
                        station.getName() + " " + station.getLinesAsString();
            }

            @Override
            public Station fromString(String string) {
                return null;
            }
        });
    }

    private void setupComboBoxCellFactory() {
        waypointComboBox.setCellFactory(lv -> new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName() + " " + station.getLinesAsString());
                }
            }
        });
    }

    private void setupComboBoxButtonCell() {
        waypointComboBox.setButtonCell(new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                } else {
                    setText(station.getName() + " " + station.getLinesAsString());
                }
            }
        });
    }

    private void setupRemoveButton() {
        removeWaypointButton.disableProperty().bind(
                waypointsListView.getSelectionModel().selectedItemProperty().isNull()
        );
    }

}

