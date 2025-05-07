package com.michaelmckibbin.viennaubhan;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Route {
    private final StringProperty routeString;

    public Route(String routeString) {
        this.routeString = new SimpleStringProperty(routeString);
    }

    public String getRouteString() {
        return routeString.get();
    }

    public void setRouteString(String routeString) {
        this.routeString.set(routeString);
    }

    public StringProperty routeStringProperty() {
        return routeString;
    }
}
