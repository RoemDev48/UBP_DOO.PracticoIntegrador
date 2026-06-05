package com.mycompany.vitalsa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author RRDev
*/

public class ProductoModalController {

    @FXML private TextField txtCategoria;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;

    private ProductoController parentController;
    private DatabaseController dbController;

    public void setParentController(ProductoController parentController) {
        this.parentController = parentController;
        this.dbController = new DatabaseController();
    }

    @FXML
    public void guardar() {
        if (txtCategoria.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || 
            txtPrecio.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (dbController.insertarProducto(txtCategoria.getText().trim(), txtNombre.getText().trim(), precio, stock)) {
                parentController.actualizarDatos();
                cerrar();
            } else {
                mostrarAlerta("Error", "Ocurrió un error al guardar el producto.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio o stock con formato inválido.");
        }
    }

    @FXML
    public void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}