package com.mycompany.vitalsa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author RRDev
*/

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/vitalsa/view/MainView.fxml"));
        
        primaryStage.setTitle("VitalSA - Sistema de Gestión");
        primaryStage.setScene(new Scene(root, 1100, 700)); 
        primaryStage.show();
    }
}