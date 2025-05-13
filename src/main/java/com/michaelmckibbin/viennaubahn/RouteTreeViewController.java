package com.michaelmckibbin.viennaubahn;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

import java.util.*;

/**
 * Controls the tree view visualization of U-Bahn routes in the Vienna subway system.
 * This controller manages a hierarchical display of stations and their connections,
 * preventing cycles by tracking visited stations.
 *
 * <p>The class provides functionality to:</p>
 * <ul>
 *     <li>Initialize and populate a tree structure from a root station</li>
 *     <li>Create hierarchical relationships between connected stations</li>
 *     <li>Automatically expand all branches of the tree for better visibility</li>
 *     <li>Handle cycle prevention in the station network</li>
 * </ul>
 *
 * <p>The tree view is populated using:</p>
 * <ul>
 *     <li>A root station as the starting point</li>
 *     <li>A map of stations to their connected stations</li>
 *     <li>A set of visited stations to prevent infinite loops in cyclic connections</li>
 * </ul>
 *
 * Example usage:
 * <pre>
 * RouteTreeViewController controller = ...;
 * Station rootStation = ...;
 * Map<Station, List<Station>> connectionMap = ...;
 * controller.initializeTree(rootStation, connectionMap);
 * </pre>
 *
 * @author Michael McKibbin
 * @version 1.0
 * @see Station
 * @see TreeView
 * @see TreeItem
 */
public class RouteTreeViewController {
    @FXML
    private TreeView<String> routeTreeView;
    private Set<Station> visitedStations;


    /**
     * Initializes the tree view with a root station and its connections.
     * Creates a hierarchical view of the station network, starting from the specified root.
     *
     * @param rootStation The station to use as the root of the tree
     * @param treeMap A map containing stations and their connected stations
     */
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


    /**
     * Recursively expands all nodes in the tree view.
     *
     * @param item The tree item to expand, along with all its children
     */
    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }


    /**
     * Creates a tree item for a station and recursively creates items for its children.
     * Prevents cycles by tracking visited stations.
     *
     * @param station The station to create a tree item for
     * @param treeMap Map of stations to their connected stations
     * @return A TreeItem representing the station and its children, or null if the station
     *         was already visited or is null
     */
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


