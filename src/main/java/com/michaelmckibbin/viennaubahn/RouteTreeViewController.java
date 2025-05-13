package com.michaelmckibbin.viennaubahn;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

import java.util.*;

public class RouteTreeViewController {
    @FXML
    private TreeView<String> routeTreeView;
    private Set<Station> visitedStations;

    public void initializeTree(Station rootStation, Map<Station, List<Station>> treeMap) {
        System.out.println("\nInitializing tree with root: " + rootStation.getName());
        System.out.println("Tree map contains entries for:");
        treeMap.keySet().forEach(station -> System.out.println("  " + station.getName()));

        visitedStations = new HashSet<>();
        TreeItem<String> rootItem = createTreeItem(rootStation, treeMap);
        rootItem.setExpanded(true);
        routeTreeView.setRoot(rootItem);

        // Expand all tree items
        expandTreeView(rootItem);
    }
    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    private TreeItem<String> createTreeItem(Station station, Map<Station, List<Station>> treeMap) {
        if (station == null || visitedStations.contains(station)) {
            System.out.println("Skipping " + (station == null ? "null" : station.getName() + " (visited)"));
            return null;
        }

        visitedStations.add(station);
        TreeItem<String> item = new TreeItem<>(station.toString());
        System.out.println("Creating tree item for: " + station.getName());

        List<Station> children = treeMap.get(station);
        System.out.println("Children for " + station.getName() + ": " +
            (children == null ? "null" : children.size()));

        if (children != null) {
            for (Station child : children) {
                System.out.println("Processing child: " + child.getName() + " for parent: " + station.getName());
                if (!visitedStations.contains(child)) {
                    TreeItem<String> childItem = createTreeItem(child, treeMap);
                    if (childItem != null) {
                        item.getChildren().add(childItem);
                        System.out.println("Added " + child.getName() + " to " + station.getName());
                    }
                }
            }
        }

        return item;
    }
}


