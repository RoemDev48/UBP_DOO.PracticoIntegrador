package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.ClienteDTO;
import com.mycompany.vitalsa.dto.ZonaDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author RRDev
*/

public class ClienteModalController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<String> cmbTipoCliente;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtNumeracion;
    @FXML private ComboBox<ZonaDTO> cmbZona;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbTipoTelefono;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private DatabaseController dbController;
    private ClienteDTO clienteEdicion;
    private boolean guardadoExitoso = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbTipoCliente.setItems(FXCollections.observableArrayList("PARTICULAR", "EMPRESA"));
        cmbTipoTelefono.setItems(FXCollections.observableArrayList("CELULAR", "FIJO"));

        BooleanBinding formIncompleto = Bindings.createBooleanBinding(() ->
            cmbTipoCliente.getValue() == null ||
            txtNombre.getText().trim().isEmpty() ||
            txtDocumento.getText().trim().isEmpty() ||
            txtDireccion.getText().trim().isEmpty() ||
            txtNumeracion.getText().trim().isEmpty() ||
            cmbZona.getValue() == null ||
            txtTelefono.getText().trim().isEmpty() ||
            cmbTipoTelefono.getValue() == null,
            cmbTipoCliente.valueProperty(),
            txtNombre.textProperty(),
            txtDocumento.textProperty(),
            txtDireccion.textProperty(),
            txtNumeracion.textProperty(),
            cmbZona.valueProperty(),
            txtTelefono.textProperty(),
            cmbTipoTelefono.valueProperty()
        );
        btnGuardar.disableProperty().bind(formIncompleto);
    }

    // Recibe los datos desde la tabla principal (si es null, es un alta nueva)
    public void initData(DatabaseController db, ClienteDTO cliente) {
        this.dbController = db;
        this.clienteEdicion = cliente;

        List<ZonaDTO> zonas = dbController.obtenerZonas();
        cmbZona.setItems(FXCollections.observableArrayList(zonas));

        if (cliente != null) {
            lblTitulo.setText("Editar Cliente");
            btnGuardar.setText("Actualizar");

            cmbTipoCliente.setValue(cliente.getTipo());
            txtNombre.setText(cliente.getNombre());
            txtDocumento.setText(cliente.getDocumento());
            txtDireccion.setText(cliente.getDireccion());
            txtNumeracion.setText(String.valueOf(cliente.getNumeracion()));
            txtTelefono.setText(cliente.getTelefono());
            cmbTipoTelefono.setValue(cliente.getTipoTelefono());

            for (ZonaDTO z : zonas) {
                if (z.getId() == cliente.getZonaId()) {
                    cmbZona.setValue(z);
                    break;
                }
            }
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    // Le da de alta al cliente en la BD o lo actualiza si ya existía
    @FXML
    public void guardarCliente(ActionEvent event) {
        String tipo = cmbTipoCliente.getValue();
        String nombre = txtNombre.getText().trim();
        String doc = txtDocumento.getText().trim();
        String dir = txtDireccion.getText().trim();
        String tel = txtTelefono.getText().trim();
        String tipoTel = cmbTipoTelefono.getValue();
        int numCalle;
        
        try {
            numCalle = Integer.parseInt(txtNumeracion.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "El número de calle debe ser numérico.");
            return;
        }

        int zonaId = cmbZona.getValue().getId();

        if (clienteEdicion == null) {
            // Alta
            boolean exito = dbController.insertarCliente(tipo, nombre, doc, dir, numCalle, zonaId, tel, tipoTel);
            if (exito) {
                guardadoExitoso = true;
                cerrar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Ocurrió un error al guardar el cliente.");
            }
        } else {
            // Edición
            boolean exito = dbController.actualizarCliente(clienteEdicion.getId(), tipo, nombre, doc, dir, numCalle, zonaId, tel, tipoTel);
            if (exito) {
                guardadoExitoso = true;
                cerrar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Ocurrió un error al actualizar el cliente.");
            }
        }
    }

    @FXML
    public void cancelar(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}