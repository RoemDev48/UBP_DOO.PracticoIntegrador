package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.PedidoDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author RRDev
*/

public class PedidoEditModalController {

    @FXML private Label lblPedidoId;
    @FXML private Label lblCliente;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Button btnGuardar;

    private DatabaseController dbController;
    private PedidoController parentController;
    private PedidoDTO pedidoActual;

    public void initData(PedidoController parent, PedidoDTO pedido) {
        this.parentController = parent;
        this.pedidoActual = pedido;
        this.dbController = new DatabaseController();

        lblPedidoId.setText(pedido.getSku());
        lblCliente.setText(pedido.getCliente());

        cmbEstado.setItems(FXCollections.observableArrayList(
                "PENDIENTE", "CONFIRMADO", "EN_PREPARACION", "ENVIADO", "ENTREGADO", "NO_ENTREGADO"
        ));

        cmbEstado.setValue(pedido.getEstado().toUpperCase());
    }

    @FXML
    private void guardar() {
        String nuevoEstado = cmbEstado.getValue();
        if (nuevoEstado != null) {
            boolean exito = dbController.cambiarEstadoPedido(pedidoActual.getIdOriginal(), nuevoEstado);
            if (exito) {
                parentController.actualizarDatos();
                cerrar();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el estado en la base de datos.");
            }
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
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