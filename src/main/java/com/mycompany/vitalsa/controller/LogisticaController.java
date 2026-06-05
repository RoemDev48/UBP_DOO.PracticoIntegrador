package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author RRDev
*/

public class LogisticaController {

    @FXML private Label lblUrgentCount;
    @FXML private ListView<LogisticaPedidoPendienteDTO> listPedidosPendientes;
    @FXML private Button btnAsignarSeleccionados;
    @FXML private ListView<DistribuidorCargaDTO> listDistribuidores;
    
    @FXML private TableView<EnvioTransitoDTO> tablaTransito;
    @FXML private TableColumn<EnvioTransitoDTO, String> colId;
    @FXML private TableColumn<EnvioTransitoDTO, String> colDistribuidor;
    @FXML private TableColumn<EnvioTransitoDTO, String> colZona;
    @FXML private TableColumn<EnvioTransitoDTO, String> colEstado;
    @FXML private TableColumn<EnvioTransitoDTO, Void> colAcciones;

    private DatabaseController dbController;
    private ObservableList<LogisticaPedidoPendienteDTO> pedidosList;
    private ObservableList<DistribuidorCargaDTO> distribuidoresList;
    private ObservableList<EnvioTransitoDTO> transitoList;

    @FXML
    public void initialize() {
        dbController = new DatabaseController();
        
        setupListViews();
        setupTable();
        cargarDatos();
    }

    private void setupListViews() {
        listPedidosPendientes.setCellFactory(param -> new ListCell<LogisticaPedidoPendienteDTO>() {
            @Override
            protected void updateItem(LogisticaPedidoPendienteDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox root = new HBox(10);
                    root.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 4; -fx-border-color: #E5E8E8; -fx-border-radius: 4;");
                    
                    CheckBox cb = new CheckBox();
                    cb.selectedProperty().bindBidirectional(item.seleccionadoProperty());
                    
                    VBox info = new VBox(5);
                    HBox topInfo = new HBox(10);
                    Label lblSku = new Label(item.getSku());
                    lblSku.setStyle("-fx-font-weight: bold;");
                    Label lblTime = new Label(item.getTiempoEspera());
                    lblTime.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
                    topInfo.getChildren().addAll(lblSku, lblTime);
                    
                    Label lblDir = new Label(item.getZonaDestino() + " - " + item.getDireccion());
                    lblDir.setStyle("-fx-font-size: 11px;");
                    lblDir.setWrapText(true);
                    
                    HBox tagsBox = new HBox(5);
                    for (String tag : item.getTags()) {
                        Label lblTag = new Label(tag);
                        String bgColor = tag.equals("FRAGILE") ? "#D4E6F1" : "#E8DAEF";
                        String txtColor = tag.equals("FRAGILE") ? "#2471A3" : "#7D3C98";
                        lblTag.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + txtColor + "; -fx-padding: 2 6; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 9px; -fx-font-weight: bold;");
                        tagsBox.getChildren().add(lblTag);
                    }
                    
                    info.getChildren().addAll(topInfo, lblDir, tagsBox);
                    root.getChildren().addAll(cb, info);
                    
                    setGraphic(root);
                    setStyle("-fx-padding: 0 0 10 0;");
                }
            }
        });
        
        listDistribuidores.setCellFactory(param -> new ListCell<DistribuidorCargaDTO>() {
            @Override
            protected void updateItem(DistribuidorCargaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    HBox root = new HBox(10);
                    root.setAlignment(Pos.CENTER_LEFT);
                    
                    StackPane avatar = new StackPane();
                    Circle circle = new Circle(15, Color.web(item.getId() % 2 == 0 ? "#85C1E9" : "#F1948A"));
                    String initials = item.getNombre().length() >= 2 ? item.getNombre().substring(0, 2).toUpperCase() : item.getNombre().toUpperCase();
                    Label lblInit = new Label(initials);
                    lblInit.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    avatar.getChildren().addAll(circle, lblInit);
                    
                    VBox info = new VBox(2);
                    Label lblNombre = new Label(item.getNombre());
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    Label lblZona = new Label("Zona: " + item.getZonaCargo());
                    lblZona.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
                    info.getChildren().addAll(lblNombre, lblZona);
                    
                    VBox progressBox = new VBox(2);
                    progressBox.setAlignment(Pos.CENTER_RIGHT);
                    int pct = (int)(item.getPorcentajeCarga() * 100);
                    Label lblCarga = new Label("Carga: " + pct + "%");
                    lblCarga.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
                    ProgressBar pb = new ProgressBar(item.getPorcentajeCarga());
                    pb.setPrefWidth(60);
                    pb.setPrefHeight(8);
                    
                    if (pct >= 80) pb.setStyle("-fx-accent: #E74C3C;");
                    else if (pct >= 50) pb.setStyle("-fx-accent: #F1C40F;");
                    else pb.setStyle("-fx-accent: #2ECC71;");
                    
                    progressBox.getChildren().addAll(lblCarga, pb);
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    root.getChildren().addAll(avatar, info, spacer, progressBox);
                    setGraphic(root);
                    setStyle("-fx-padding: 5 0;");
                }
            }
        });
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSku()));
        colDistribuidor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDistribuidorNombre()));
        colZona.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZonaRuta()));
        
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));
        colEstado.setCellFactory(column -> new TableCell<EnvioTransitoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label lbl = new Label(item);
                    lbl.setStyle("-fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
                    if (item.contains("RUTA")) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #D4E6F1; -fx-text-fill: #2980B9;");
                    } else if (item.contains("CANCELADO")) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #FADBD8; -fx-text-fill: #C0392B;");
                    } else if (item.contains("ENTREGADO")) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #D5F5E3; -fx-text-fill: #27AE60;");
                    } else {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #EAECEE; -fx-text-fill: #566573;");
                    }
                    setGraphic(lbl);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colAcciones.setCellFactory(new Callback<TableColumn<EnvioTransitoDTO, Void>, TableCell<EnvioTransitoDTO, Void>>() {
            @Override
            public TableCell<EnvioTransitoDTO, Void> call(final TableColumn<EnvioTransitoDTO, Void> param) {
                return new TableCell<EnvioTransitoDTO, Void>() {
                    private final Button btnEntregar = new Button("REGISTRAR ENTREGA");
                    private final Button btnNotificar = new Button("NOTIFICAR");
                    private final Button btnAlerta = new Button("NOTIFICAR DISTRIBUIDOR");
                    private final HBox pane = new HBox(5);

                    {
                        btnEntregar.setStyle("-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
                        btnNotificar.setStyle("-fx-background-color: #F4F6F7; -fx-text-fill: #34495E; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
                        btnAlerta.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");

                        btnEntregar.setOnAction((ActionEvent event) -> {
                            EnvioTransitoDTO data = getTableView().getItems().get(getIndex());
                            registrarEntrega(data);
                        });
                        btnAlerta.setOnAction((ActionEvent event) -> {
                            EnvioTransitoDTO data = getTableView().getItems().get(getIndex());
                            mostrarAlerta("Alerta enviada", "Se ha notificado al distribuidor " + data.getDistribuidorNombre() + " sobre la cancelación.", Alert.AlertType.INFORMATION);
                        });
                        btnNotificar.setOnAction((ActionEvent event) -> {
                            mostrarAlerta("Notificación enviada", "Notificación generada.", Alert.AlertType.INFORMATION);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            EnvioTransitoDTO data = getTableView().getItems().get(getIndex());
                            pane.getChildren().clear();
                            if (data.getEstado().contains("CANCELADO")) {
                                pane.getChildren().add(btnAlerta);
                            } else if (data.getEstado().contains("ENTREGADO")) {
                                Label lblDoc = new Label("📄");
                                pane.getChildren().add(lblDoc);
                            } else {
                                pane.getChildren().addAll(btnEntregar, btnNotificar);
                            }
                            pane.setAlignment(Pos.CENTER);
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }

    // Trae todo lo de DB (pedidos, camiones, etc) y recarga
    private void cargarDatos() {
        List<LogisticaPedidoPendienteDTO> pendientes = dbController.obtenerPedidosPendientesLogistica();
        pedidosList = FXCollections.observableArrayList(pendientes);
        listPedidosPendientes.setItems(pedidosList);
        lblUrgentCount.setText(pendientes.size() + " Pendientes");

        List<DistribuidorCargaDTO> distribuidores = dbController.obtenerDistribuidoresLogistica();
        distribuidoresList = FXCollections.observableArrayList(distribuidores);
        listDistribuidores.setItems(distribuidoresList);

        List<EnvioTransitoDTO> transito = dbController.obtenerEnviosEnTransito();
        transitoList = FXCollections.observableArrayList(transito);
        tablaTransito.setItems(transitoList);
    }

    // Asigna los pedidos tildados al distribuidor seleccionado en la derecha
    @FXML
    public void asignarSeleccionados(ActionEvent event) {
        DistribuidorCargaDTO dist = listDistribuidores.getSelectionModel().getSelectedItem();
        if (dist == null) {
            mostrarAlerta("Asignación de Distribuidor", "Debe seleccionar un distribuidor de la lista inferior ('Gestión Distribuidores') haciendo clic sobre él.", Alert.AlertType.WARNING);
            return;
        }

        List<LogisticaPedidoPendienteDTO> seleccionados = pedidosList.stream()
                .filter(LogisticaPedidoPendienteDTO::isSeleccionado)
                .collect(Collectors.toList());

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Asignación de Pedidos", "Debe marcar la casilla de al menos un pedido pendiente en la lista superior.", Alert.AlertType.WARNING);
            return;
        }

        List<Integer> ids = seleccionados.stream().map(LogisticaPedidoPendienteDTO::getId).collect(Collectors.toList());
        boolean ok = dbController.asignarPedidosADistribuidor(ids, dist.getId());
        
        if (ok) {
            mostrarAlerta("Asignación Exitosa", "Se han asignado " + ids.size() + " pedidos a " + dist.getNombre() + ".", Alert.AlertType.INFORMATION);
            cargarDatos(); // Refrescar todo
        } else {
            mostrarAlerta("Error", "Ocurrió un error al asignar los pedidos.", Alert.AlertType.ERROR);
        }
    }

    // Pasa el estado a ENTREGADO cuando el camión avisa que ya lo dejó
    private void registrarEntrega(EnvioTransitoDTO envio) {
        boolean ok = dbController.cambiarEstadoPedido(envio.getIdPedido(), "ENTREGADO");
        if (ok) {
            cargarDatos();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la entrega.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}