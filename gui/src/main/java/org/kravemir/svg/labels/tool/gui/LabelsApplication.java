package org.kravemir.svg.labels.tool.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LabelsApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeFrame.fxml"));

        Scene scene = new Scene(loader.load(), 700, 450);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setTitle("Welcome to SVG Labels");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
