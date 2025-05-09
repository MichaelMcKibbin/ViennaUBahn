package com.michaelmckibbin.viennaubhan;

import javafx.scene.paint.Color;

public enum LineColor {
    RED(Color.RED),
    PURPLE(Color.PURPLE),
    ORANGE(Color.ORANGE),
    BROWN(Color.BROWN),
    GREEN(Color.GREEN);

    private final Color color;

    LineColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}