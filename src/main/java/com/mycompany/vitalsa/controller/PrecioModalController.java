package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.ProductoDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author RRDev
*/

public class PrecioModalController {

    @FXML private Label lblNombreProducto;
    @FXML private TextField txtPrecio;

    private ProductoController parentController;
    private DatabaseController dbController;
    private int productoId;

    public void setParentController(ProductoController parentController) {
        this.parentController = parentController;
        this.dbController = new DatabaseController();
    }

    public void cargarDatos(ProductoDTO producto) {
        this.productoId = producto.getId();
        this.lblNombreProducto.setText(producto.getSku() + " - " + producto.getNombre());
        this.txtPrecio.setText(producto.getPrecioUnitario().replace("$", ""));
    }

    @FXML
    public void guardar() {
        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar un precio válido.");
            return;
        }

        try {
            double nuevoPrecio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));

            if (dbController.actualizarPrecio(productoId, nuevoPrecio)) {
                parentController.actualizarDatos();
                cerrar();
            } else {
                mostrarAlerta("Error", "Ocurrió un error al actualizar el precio.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El precio ingresado no tiene un formato válido.");
        }
    }

    @FXML
    public void cerrar() {
        Stage stage = (Stage) txtPrecio.getScene().getWindow();
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