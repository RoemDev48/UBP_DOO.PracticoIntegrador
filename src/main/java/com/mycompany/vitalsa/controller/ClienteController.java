package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author RRDev
*/

public class ClienteController implements Initializable {

    @FXML private Label lblTotal;
    @FXML private Label lblActivos;
    @FXML private Label lblZonas;

    @FXML private TableView<ClienteDTO> tablaClientes;
    @FXML private TableColumn<ClienteDTO, Integer> colId;
    @FXML private TableColumn<ClienteDTO, String> colNombre;
    @FXML private TableColumn<ClienteDTO, String> colZona;
    @FXML private TableColumn<ClienteDTO, String> colUltimoPedido;
    @FXML private TableColumn<ClienteDTO, String> colEstado;
    @FXML private TableColumn<ClienteDTO, Void> colAcciones;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;

    private DatabaseController dbController;
    private ObservableList<ClienteDTO> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbController = new DatabaseController();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colZona.setCellValueFactory(new PropertyValueFactory<>("zonaNombre"));
        colUltimoPedido.setCellValueFactory(new PropertyValueFactory<>("ultimoPedido"));
        
        // Le clavamos un estilo tipo "badge" o "pastilla" a la columna Estado para que quede más lindo
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(param -> new TableCell<ClienteDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label lbl = new Label(item);
                    if ("ACTIVO".equals(item)) {
                        lbl.getStyleClass().add("badge-activo");
                    } else {
                        lbl.getStyleClass().add("badge-inactivo");
                    }
                    setGraphic(lbl);
                    setText(null);
                }
            }
        });
        
        configurarBotonesAccion();

        if (cmbFiltroEstado != null) {
            cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
            cmbFiltroEstado.setValue("Todos");
            cmbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }

        actualizarDatos();
    }

    // Arma la celda de la columna de acciones (Editar, Cambiar estado, etc)
    private void configurarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<ClienteDTO, Void>() {
            private final Button btnEditar = new Button();
            private final Button btnEstado = new Button();
            private final HBox pane = new HBox(10, btnEditar, btnEstado);

            {
                btnEditar.getStyleClass().add("action-button");
                btnEstado.getStyleClass().add("action-button");
                
                FontIcon editIcon = new FontIcon("fas-pen");
                editIcon.setIconSize(13);
                editIcon.setIconColor(Color.web("#0056b3"));
                btnEditar.setGraphic(editIcon);

                btnEditar.setOnAction(e -> {
                    ClienteDTO cliente = getTableView().getItems().get(getIndex());
                    abrirModalEdicion(cliente);
                });

                btnEstado.setOnAction(e -> {
                    ClienteDTO cliente = getTableView().getItems().get(getIndex());
                    String nuevoEstado = "ACTIVO".equals(cliente.getEstado()) ? "INACTIVO" : "ACTIVO";
                    if (dbController.cambiarEstadoCliente(cliente.getId(), nuevoEstado)) {
                        actualizarDatos();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ClienteDTO cliente = getTableView().getItems().get(getIndex());
                    FontIcon eyeIcon = new FontIcon("ACTIVO".equals(cliente.getEstado()) ? "fas-eye-slash" : "fas-eye");
                    eyeIcon.setIconSize(13);
                    eyeIcon.setIconColor(Color.web("#0056b3"));
                    btnEstado.setGraphic(eyeIcon);
                    setGraphic(pane);
                }
            }
        });
    }

    // Llama a la DB y actualiza toda la pantalla
    private void actualizarDatos() {
        // TODO: esto quizás conviene pasarlo a un hilo separado si la base crece mucho
        lblTotal.setText(String.valueOf(dbController.contarClientesTotal()));
        lblActivos.setText(String.valueOf(dbController.contarClientesActivos()));
        lblZonas.setText(String.valueOf(dbController.contarZonas()));

        List<ClienteDTO> clientes = dbController.obtenerClientes();
        masterData.setAll(clientes);
        aplicarFiltros();
    }

    // Filtra la tablita en memoria (sin pegar a la DB de nuevo)
    private void aplicarFiltros() {
        if (txtBuscar == null || cmbFiltroEstado == null) return;
        
        String textoBuscado = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estadoFiltro = cmbFiltroEstado.getValue();

        javafx.collections.transformation.FilteredList<ClienteDTO> filtrados = new javafx.collections.transformation.FilteredList<>(masterData, cliente -> {
            boolean coincideTexto = true;
            if (!textoBuscado.isEmpty()) {
                String nombre = cliente.getNombre() != null ? cliente.getNombre().toLowerCase() : "";
                String doc = cliente.getDocumento() != null ? cliente.getDocumento().toLowerCase() : "";
                String zona = cliente.getZonaNombre() != null ? cliente.getZonaNombre().toLowerCase() : "";
                
                coincideTexto = nombre.contains(textoBuscado) || 
                                doc.contains(textoBuscado) || 
                                zona.contains(textoBuscado) || 
                                String.valueOf(cliente.getId()).equals(textoBuscado);
            }

            boolean coincideEstado = true;
            if ("Activos".equals(estadoFiltro)) {
                coincideEstado = "ACTIVO".equals(cliente.getEstado());
            } else if ("Inactivos".equals(estadoFiltro)) {
                coincideEstado = "INACTIVO".equals(cliente.getEstado());
            }

            return coincideTexto && coincideEstado;
        });

        tablaClientes.setItems(filtrados);
    }

    @FXML
    public void abrirModalRegistro(ActionEvent event) {
        abrirModalEdicion(null);
    }

    // Levanta el modal para editar a un cliente que ya existe
    private void abrirModalEdicion(ClienteDTO cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/cliente/ClienteModalView.fxml"));
            Parent root = loader.load();

            ClienteModalController controller = loader.getController();
            controller.initData(dbController, cliente);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(cliente == null ? "Registrar Cliente" : "Editar Cliente");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isGuardadoExitoso()) {
                actualizarDatos();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}