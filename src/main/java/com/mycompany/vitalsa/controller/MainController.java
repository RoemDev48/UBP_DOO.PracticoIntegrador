package com.mycompany.vitalsa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author RRDev
*/

/**
 * Controlador principal de VitalSA (Top-Bar Navigation y Panel Central).
 * Maneja qué pantalla se está viendo en cada momento.
 */
public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private VBox welcomePane;
    @FXML private Button btnVolver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Al iniciar la app, aseguramos de que se vea el menú de botones gigantes y ocultamos el botón "Volver"
        volverAlInicio();
    }

    @FXML
    private void handleClientes() { loadView("/com/mycompany/vitalsa/view/cliente/ClienteListView.fxml"); }

    @FXML
    private void handleProductos() { loadView("/com/mycompany/vitalsa/view/producto/ProductoListView.fxml"); }

    @FXML
    private void handlePedidos() { loadView("/com/mycompany/vitalsa/view/pedido/PedidoListView.fxml"); }

    @FXML
    private void handleFacturacion() { loadView("/com/mycompany/vitalsa/view/facturacion/FacturacionListView.fxml"); }

    @FXML
    private void handleLogistica() { loadView("/com/mycompany/vitalsa/view/logistica/LogisticaListView.fxml"); }

    // Vuelve al menú de inicio y limpia el panel para que no consuma ram
    @FXML
    private void volverAlInicio() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(welcomePane);
        btnVolver.setVisible(false);
    }

    // Mata el programa (cierra todo de golpe)
    @FXML
    private void handleCerrarSesion() {
        javafx.application.Platform.exit();
        System.exit(0);
    }

    // Magia para cargar los distintos FXML dinámicamente en el centro de la pantalla
    // Así no abrimos mil ventanas distintas
    private void loadView(String fxmlPath) {
        try {
            URL viewUrl = getClass().getResource(fxmlPath);
            if (viewUrl == null) {
                throw new IllegalArgumentException("Vista no encontrada: " + fxmlPath);
            }
            Node view = FXMLLoader.load(viewUrl);
            
            // Reemplaza las tarjetas con la nueva vista
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Muestra el botón de volver
            btnVolver.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}