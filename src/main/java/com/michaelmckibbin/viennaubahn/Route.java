package com.michaelmckibbin.viennaubahn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Represents a route in the Vienna U-Bahn system using JavaFX properties.
 * This class wraps a route string in a StringProperty to enable JavaFX binding
 * and property change notifications.
 *
 * <p>The route string typically contains information about:</p>
 * <ul>
 *     <li>The sequence of stations in the route</li>
 *     <li>The U-Bahn lines used</li>
 *     <li>Any transfer points between lines</li>
 * </ul>
 *
 * <p>This class provides both direct access to the route string and
 * property-based access for JavaFX bindings.</p>
 *
 * Example usage:
 * <pre>
 * Route route = new Route("U1: Stephansplatz → Karlsplatz");
 * someLabel.textProperty().bind(route.routeStringProperty());
 * </pre>
 *
 * @author Michael McKibbin
 * @version 1.0
 */
public class Route {
    /** The JavaFX property containing the route string */
    private final StringProperty routeString;

    /**
     * Creates a new Route with the specified route string.
     *
     * @param routeString The string describing the route
     */
    public Route(String routeString) {
        this.routeString = new SimpleStringProperty(routeString);
    }

    /**
     * Gets the current route string value.
     *
     * @return The current route string
     */
    public String getRouteString() {
        return routeString.get();
    }

    /**
     * Sets a new value for the route string.
     *
     * @param routeString The new route string value
     */
    public void setRouteString(String routeString) {
        this.routeString.set(routeString);
    }

    /**
     * Returns the StringProperty object for binding in JavaFX.
     *
     * @return The StringProperty containing the route string
     */
    public StringProperty routeStringProperty() {
        return routeString;
    }
}

