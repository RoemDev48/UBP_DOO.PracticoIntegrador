package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;
import java.util.List;

/**
 *
 * @author RRDev
*/

public class FacturacionController {

    @FXML private Label lblTotalMes;
    @FXML private Label lblCrecimientoPct;
    @FXML private Label lblCuentasPorCobrar;
    @FXML private Label lblFacturasPendientes;
    @FXML private Label lblPagosVerificados;
    @FXML private ProgressBar pbPagos;
    @FXML private Label lblCobradoHoy;

    @FXML private TableView<FacturaRecienteDTO> tablaFacturas;
    @FXML private TableColumn<FacturaRecienteDTO, String> colId;
    @FXML private TableColumn<FacturaRecienteDTO, String> colCliente;
    @FXML private TableColumn<FacturaRecienteDTO, String> colEstado;
    @FXML private TableColumn<FacturaRecienteDTO, Void> colAcciones;

    @FXML private TextField txtBuscadorFactura;
    @FXML private ComboBox<String> cmbMetodoPago;
    
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TextField txtBuscar;
    
    private ObservableList<FacturaRecienteDTO> masterData = FXCollections.observableArrayList();
    
    private DatabaseController dbController;

    @FXML
    public void initialize() {
        dbController = new DatabaseController();
        
        // Llenamos el ComboBox con las opciones por defecto
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
            "Transferencia Bancaria", "Efectivo", "Tarjeta de Crédito", "Mercado Pago"
        ));
        cmbMetodoPago.getSelectionModel().selectFirst();
        
        if (cmbFiltroEstado != null) {
            cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Pagada", "Pendiente", "Anulada"));
            cmbFiltroEstado.getSelectionModel().selectFirst();
            cmbFiltroEstado.valueProperty().addListener((obs, oldV, newV) -> aplicarFiltros());
        }
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldV, newV) -> aplicarFiltros());
        }
        
        setupTable();
        cargarDatos();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSku()));
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClienteNombre()));
        
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));
        colEstado.setCellFactory(column -> new TableCell<FacturaRecienteDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label lbl = new Label(item);
                    lbl.setStyle("-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
                    if ("PAGADA".equals(item)) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #17A589; -fx-text-fill: white;");
                    } else if ("PENDIENTE".equals(item)) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #D6EAF8; -fx-text-fill: #2980B9;");
                    } else {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #EAECEE; -fx-text-fill: #566573;");
                    }
                    setGraphic(lbl);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colAcciones.setCellFactory(new Callback<TableColumn<FacturaRecienteDTO, Void>, TableCell<FacturaRecienteDTO, Void>>() {
            @Override
            public TableCell<FacturaRecienteDTO, Void> call(final TableColumn<FacturaRecienteDTO, Void> param) {
                return new TableCell<FacturaRecienteDTO, Void>() {
                    private final Button btnEmitir = new Button();
                    private final HBox pane = new HBox(btnEmitir);

                    {
                        org.kordamp.ikonli.javafx.FontIcon iconEmitir = new org.kordamp.ikonli.javafx.FontIcon("fas-file-invoice");
                        iconEmitir.setIconColor(javafx.scene.paint.Color.WHITE);
                        iconEmitir.setIconSize(12);
                        btnEmitir.setGraphic(iconEmitir);
                        
                        btnEmitir.setStyle("-fx-background-color: #27AE60; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 5 10;");
                        btnEmitir.setOnAction((ActionEvent event) -> {
                            FacturaRecienteDTO data = getTableView().getItems().get(getIndex());
                            generarFacturaPdf(data);
                        });

                        pane.setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            FacturaRecienteDTO data = getTableView().getItems().get(getIndex());
                            if (data != null && "PAGADA".equalsIgnoreCase(data.getEstado())) {
                                btnEmitir.setVisible(true);
                                btnEmitir.setManaged(true);
                            } else {
                                btnEmitir.setVisible(false);
                                btnEmitir.setManaged(false);
                            }
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
        
        // Cuando tocan una fila de la tabla de facturas, pasamos los datos al costadito para cobrar
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtBuscadorFactura.setText(newSelection.getSku());
            }
        });
    }

    // Trae datos de la BD y recarga toda la tablita
    private void cargarDatos() {
        // Cargar KPIs (Ojo que la DB se la banca pero hace varias queries)
        FacturacionKpiDTO kpis = dbController.obtenerKpisFacturacion();
        lblTotalMes.setText(String.format("$%,.2f", kpis.getTotalMes()));
        lblCrecimientoPct.setText("+" + kpis.getCrecimientoMensualPct() + "% vs mes anterior");
        lblCuentasPorCobrar.setText(String.format("$%,.2f", kpis.getCuentasPorCobrar()));
        lblFacturasPendientes.setText(kpis.getCantidadPendientes() + " facturas pendientes");
        lblPagosVerificados.setText(kpis.getPagosVerificados() + " / " + kpis.getPagosTotal());
        
        if (kpis.getPagosTotal() > 0) {
            pbPagos.setProgress((double)kpis.getPagosVerificados() / kpis.getPagosTotal());
        } else {
            pbPagos.setProgress(0);
        }
        
        lblCobradoHoy.setText(String.format("$%,.2f", kpis.getCobradoHoy()));

        // Llenamos la grilla principal
        List<FacturaRecienteDTO> facturas = dbController.obtenerFacturasEmitidas();
        masterData.setAll(facturas);
        aplicarFiltros();
    }

    // Filtra las facturas mostradas en la pantalla
    private void aplicarFiltros() {
        if (cmbFiltroEstado == null) return;
        
        String textoBuscado = txtBuscar != null && txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estadoFiltro = cmbFiltroEstado.getValue();

        javafx.collections.transformation.FilteredList<FacturaRecienteDTO> filtrados = new javafx.collections.transformation.FilteredList<>(masterData, factura -> {
            boolean coincideTexto = true;
            if (!textoBuscado.isEmpty()) {
                String cliente = factura.getClienteNombre() != null ? factura.getClienteNombre().toLowerCase() : "";
                String sku = factura.getSku() != null ? factura.getSku().toLowerCase() : "";
                
                coincideTexto = cliente.contains(textoBuscado) || sku.contains(textoBuscado);
            }

            boolean coincideEstado = true;
            if (estadoFiltro != null && !"Todos".equals(estadoFiltro)) {
                String fe = factura.getEstado().toUpperCase().trim();
                String ef = estadoFiltro.toUpperCase().trim();
                coincideEstado = fe.equals(ef);
            }

            return coincideTexto && coincideEstado;
        });

        tablaFacturas.setItems(filtrados);
    }

    // Cuando tocan el botón de "Registrar Pago" (acá deberíamos validar con AFIP jaja)
    @FXML
    public void procesarPago(ActionEvent event) {
        String input = txtBuscadorFactura.getText().trim();
        if (input.isEmpty()) {
            mostrarAlerta("Atención", "Debe ingresar o seleccionar un número de factura.", Alert.AlertType.WARNING);
            return;
        }

        String metodoPago = cmbMetodoPago.getValue();
        boolean ok = dbController.registrarPagoFactura(input, metodoPago);

        if (ok) {
            mostrarAlerta("Pago Exitoso", "El pago de la factura " + input + " ha sido registrado.", Alert.AlertType.INFORMATION);
            txtBuscadorFactura.clear();
            cargarDatos(); // Refrescar UI
        } else {
            mostrarAlerta("Error", "No se encontró la factura o ya está pagada.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void generarResumenDiario(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Reporte de Facturación");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        fileChooser.setInitialFileName("reporte_facturacion.pdf");
        
        java.io.File file = fileChooser.showSaveDialog(tablaFacturas.getScene().getWindow());
        if (file != null) {
            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                document.open();
                
                com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
                document.add(new com.itextpdf.text.Paragraph("Reporte de Facturacion", titleFont));
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(3);
                table.setWidthPercentage(100);
                
                table.addCell("ID FACTURA");
                table.addCell("CLIENTE");
                table.addCell("ESTADO");
                
                for (FacturaRecienteDTO f : tablaFacturas.getItems()) {
                    table.addCell(f.getSku() != null ? f.getSku() : "");
                    table.addCell(f.getClienteNombre() != null ? f.getClienteNombre() : "");
                    table.addCell(f.getEstado() != null ? f.getEstado() : "");
                }
                
                document.add(table);
                document.close();
                
                mostrarAlerta("Éxito", "Reporte exportado correctamente a PDF.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "No se pudo generar el PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // Magia negra con iText para escupir el PDF de la factura
    private void generarFacturaPdf(FacturaRecienteDTO data) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Factura " + data.getSku());
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        fileChooser.setInitialFileName("Factura_" + data.getSku() + ".pdf");
        
        java.io.File file = fileChooser.showSaveDialog(tablaFacturas.getScene().getWindow());
        if (file != null) {
            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                document.open();
                
                com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 22);
                com.itextpdf.text.Font subtitleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14);
                com.itextpdf.text.Font normalFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 12);
                
                document.add(new com.itextpdf.text.Paragraph("FACTURA COMERCIAL", titleFont));
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                document.add(new com.itextpdf.text.Paragraph("Nro. Factura: " + data.getSku(), subtitleFont));
                document.add(new com.itextpdf.text.Paragraph("Cliente: " + data.getClienteNombre(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("Estado: " + data.getEstado(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("Fecha de Emisión: " + java.time.LocalDate.now().toString(), normalFont));
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(new com.itextpdf.text.Paragraph("---------------------------------------------------------"));
                
                document.close();
                
                mostrarAlerta("Éxito", "Factura " + data.getSku() + " exportada correctamente a PDF.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "No se pudo generar la Factura PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
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