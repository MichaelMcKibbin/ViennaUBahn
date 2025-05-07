package com.michaelmckibbin.viennaubhan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/michaelmckibbin/viennaubhan/views/u-bann-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);
        String css = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}