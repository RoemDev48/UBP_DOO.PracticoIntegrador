package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.PedidoDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author RRDev
*/

public class PedidoController implements Initializable {

    @FXML private Label lblTotal;
    @FXML private Label lblPendientes;
    @FXML private Label lblTransito;
    @FXML private Label lblEntregados;
    
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TextField txtBuscar;
    
    private ObservableList<PedidoDTO> masterData = FXCollections.observableArrayList();

    @FXML private TableView<PedidoDTO> tablaPedidos;
    @FXML private TableColumn<PedidoDTO, String> colId;
    @FXML private TableColumn<PedidoDTO, String> colCliente;
    @FXML private TableColumn<PedidoDTO, String> colFecha;
    @FXML private TableColumn<PedidoDTO, String> colEstado;
    @FXML private TableColumn<PedidoDTO, String> colTotal;
    @FXML private TableColumn<PedidoDTO, Void> colAcciones;

    private DatabaseController dbController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbController = new DatabaseController();

        // Seteo inicial de las columnas de la tabla principal
        colId.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Le clavamos un estilo tipo "badge" o "pastilla" a la columna Estado para que quede más lindo
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(param -> new TableCell<PedidoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label lbl = new Label(item);
                    String estadoFormateado = item.toUpperCase();
                    if (estadoFormateado.equals("EN PREPARACION") || estadoFormateado.equals("CONFIRMADO")) {
                        lbl.getStyleClass().add("badge-pedido-preparacion");
                        lbl.setText("EN PREPARACIÓN");
                    } else if (estadoFormateado.equals("ENVIADO")) {
                        lbl.getStyleClass().add("badge-pedido-enviado");
                    } else if (estadoFormateado.equals("ENTREGADO")) {
                        lbl.getStyleClass().add("badge-pedido-entregado");
                    } else if (estadoFormateado.equals("CANCELADO")) {
                        lbl.getStyleClass().add("badge-pedido-cancelado");
                    } else { // PENDIENTE por descarte
                        lbl.getStyleClass().add("badge-pedido-pendiente");
                    }
                    
                    setGraphic(lbl);
                    setText(null);
                }
            }
        });
        
        configurarFiltro();
        configurarBotonesAccion();
        actualizarDatos();
    }
    
    private void configurarFiltro() {
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
            "Todos los estados",
            "Pendiente",
            "En Preparacion",
            "Enviado",
            "Entregado",
            "Cancelado"
        ));
        cmbFiltroEstado.getSelectionModel().selectFirst();
        
        cmbFiltroEstado.valueProperty().addListener((obs, oldV, newV) -> aplicarFiltros());
        if(txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldV, newV) -> aplicarFiltros());
        }
    }

    private void configurarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<com.mycompany.vitalsa.dto.PedidoDTO, Void>() {
            private final Button btnGestionar = new Button();
            private final Button btnEditarItems = new Button();
            private final Button btnCancelar = new Button();
            private final HBox pane = new HBox(5, btnGestionar, btnEditarItems, btnCancelar);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER);
                btnGestionar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btnEditarItems.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btnCancelar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                
                FontIcon gearIcon = new FontIcon("fas-cog");
                gearIcon.setIconSize(16);
                gearIcon.setIconColor(Color.web("#0369a1"));
                btnGestionar.setGraphic(gearIcon);
                
                FontIcon pencilIcon = new FontIcon("fas-pencil-alt");
                pencilIcon.setIconSize(16);
                pencilIcon.setIconColor(Color.web("#F59E0B"));
                btnEditarItems.setGraphic(pencilIcon);
                
                FontIcon cancelIcon = new FontIcon("fas-times-circle");
                cancelIcon.setIconSize(16);
                cancelIcon.setIconColor(Color.web("#DC2626"));
                btnCancelar.setGraphic(cancelIcon);
                
                btnGestionar.setOnAction(e -> {
                    com.mycompany.vitalsa.dto.PedidoDTO pedido = getTableView().getItems().get(getIndex());
                    try {
                        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/pedido/PedidoEditModalView.fxml"));
                        javafx.scene.Parent root = loader.load();
                        
                        PedidoEditModalController modalController = loader.getController();
                        modalController.initData(PedidoController.this, pedido);
                        
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.setTitle("Modificar Pedido");
                        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                        stage.setResizable(false);
                        stage.showAndWait();
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                });
                
                btnEditarItems.setOnAction(e -> {
                    com.mycompany.vitalsa.dto.PedidoDTO pedido = getTableView().getItems().get(getIndex());
                    if ("CANCELADO".equals(pedido.getEstado().toUpperCase())) return;
                    
                    try {
                        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/pedido/PedidoModalView.fxml"));
                        javafx.scene.Parent root = loader.load();
                        
                        PedidoModalController modalController = loader.getController();
                        modalController.initForEdit(PedidoController.this, pedido);
                        
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.setTitle("Editar Ítems del Pedido");
                        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                });
                
                btnCancelar.setOnAction(e -> {
                    com.mycompany.vitalsa.dto.PedidoDTO pedido = getTableView().getItems().get(getIndex());
                    if ("CANCELADO".equals(pedido.getEstado().toUpperCase())) return;
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar Cancelación");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Estás seguro que deseas cancelar el pedido " + pedido.getSku() + "?");
                    
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        dbController.cambiarEstadoPedido(pedido.getIdOriginal(), "CANCELADO");
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
                    com.mycompany.vitalsa.dto.PedidoDTO pedido = getTableView().getItems().get(getIndex());
                    boolean isCancelado = "CANCELADO".equals(pedido.getEstado().toUpperCase());
                    btnCancelar.setDisable(isCancelado);
                    btnEditarItems.setDisable(isCancelado);
                    setGraphic(pane);
                }
            }
        });
    }

    // Trae toda la info y la recarga en la vista
    public void actualizarDatos() {
        // Actualizamos tarjetas de métricas (cuidado que pega a la db 4 veces seguidas)
        lblTotal.setText(String.format("%,d", dbController.contarPedidosTotales()));
        lblPendientes.setText(String.valueOf(dbController.contarPedidosPendientes()));
        lblTransito.setText(String.valueOf(dbController.contarPedidosEnTransito()));
        lblEntregados.setText(String.valueOf(dbController.contarPedidosEntregadosHoy()));
        
        actualizarTabla();
    }
    
    // Refresca solo la grilla de pedidos
    private void actualizarTabla() {
        List<PedidoDTO> pedidos = dbController.obtenerPedidos("Todos los estados");
        masterData.setAll(pedidos);
        aplicarFiltros();
    }
    
    // Filtro en vivo sin pegarle a la DB cada vez que tipean
    private void aplicarFiltros() {
        if (cmbFiltroEstado == null) return;
        
        String textoBuscado = txtBuscar != null && txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estadoFiltro = cmbFiltroEstado.getValue();

        javafx.collections.transformation.FilteredList<PedidoDTO> filtrados = new javafx.collections.transformation.FilteredList<>(masterData, pedido -> {
            boolean coincideTexto = true;
            if (!textoBuscado.isEmpty()) {
                String cliente = pedido.getCliente() != null ? pedido.getCliente().toLowerCase() : "";
                String fecha = pedido.getFecha() != null ? pedido.getFecha().toLowerCase() : "";
                String sku = pedido.getSku() != null ? pedido.getSku().toLowerCase() : "";
                
                coincideTexto = cliente.contains(textoBuscado) || 
                                fecha.contains(textoBuscado) || 
                                sku.contains(textoBuscado) ||
                                String.valueOf(pedido.getIdOriginal()).equals(textoBuscado);
            }

            boolean coincideEstado = true;
            if (estadoFiltro != null && !"Todos los estados".equals(estadoFiltro)) {
                String ef = estadoFiltro.toUpperCase().trim();
                String pe = pedido.getEstado().toUpperCase().trim();
                if (ef.equals("EN PREPARACION") || ef.equals("EN PREPARACIÓN")) {
                    coincideEstado = pe.equals("EN PREPARACION") || pe.equals("EN PREPARACIÓN") || pe.equals("CONFIRMADO");
                } else {
                    coincideEstado = pe.equals(ef);
                }
            }

            return coincideTexto && coincideEstado;
        });

        tablaPedidos.setItems(filtrados);
    }
    
    @FXML
    public void abrirModalRegistro() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/pedido/PedidoModalView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            PedidoModalController modalController = loader.getController();
            modalController.setParentController(this);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Nuevo Pedido");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exportarReporte() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Reporte de Pedidos");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        fileChooser.setInitialFileName("reporte_pedidos.csv");
        
        java.io.File file = fileChooser.showSaveDialog(tablaPedidos.getScene().getWindow());
        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write('\ufeff'); 
                writer.println("ID PEDIDO,CLIENTE,FECHA REGISTRO,ESTADO,TOTAL");
                
                for (com.mycompany.vitalsa.dto.PedidoDTO p : tablaPedidos.getItems()) {
                    String id = p.getSku() != null ? p.getSku() : "";
                    String cliente = p.getCliente() != null ? p.getCliente().replace(",", " ") : "";
                    String fecha = p.getFecha() != null ? p.getFecha() : "";
                    String estado = p.getEstado() != null ? p.getEstado() : "";
                    String total = p.getTotal() != null ? p.getTotal().replace(",", "") : "";
                    
                    writer.printf("%s,%s,%s,%s,%s%n", id, cliente, fecha, estado, total);
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Reporte exportado correctamente a CSV.");
                alert.showAndWait();
            } catch (java.io.IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo guardar el archivo.");
                alert.showAndWait();
            }
        }
    }
}