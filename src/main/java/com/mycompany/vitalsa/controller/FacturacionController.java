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
            "Transferencia Bancaria", "Efectivo", "Tarjeta de CrÃ©dito", "Mercado Pago"
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

    // Cuando tocan el botÃ³n de "Registrar Pago" (acÃ¡ deberÃ­amos validar con AFIP jaja)
    @FXML
    public void procesarPago(ActionEvent event) {
        String input = txtBuscadorFactura.getText().trim();
        if (input.isEmpty()) {
            mostrarAlerta("AtenciÃ³n", "Debe ingresar o seleccionar un nÃºmero de factura.", Alert.AlertType.WARNING);
            return;
        }

        String metodoPago = cmbMetodoPago.getValue();
        boolean ok = dbController.registrarPagoFactura(input, metodoPago);

        if (ok) {
            mostrarAlerta("Pago Exitoso", "El pago de la factura " + input + " ha sido registrado.", Alert.AlertType.INFORMATION);
            txtBuscadorFactura.clear();
            cargarDatos(); // Refrescar UI
        } else {
            mostrarAlerta("Error", "No se encontrÃ³ la factura o ya estÃ¡ pagada.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void generarResumenDiario(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Reporte de FacturaciÃ³n");
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
                
                mostrarAlerta("Ã‰xito", "Reporte exportado correctamente a PDF.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "No se pudo generar el PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // Magia negra con iText para escupir el PDF de la factura
    // Magia negra con iText para escupir el PDF de la factura (Estilo AFIP)
    private void generarFacturaPdf(FacturaRecienteDTO data) {
        FacturaCompletaDTO facturaCompleta = dbController.obtenerDetalleCompletoFactura(data.getId());
        if (facturaCompleta == null) {
            mostrarAlerta("Error", "No se pudo recuperar el detalle de la factura.", Alert.AlertType.ERROR);
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Factura " + facturaCompleta.getSkuFactura());
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        fileChooser.setInitialFileName("Factura_" + facturaCompleta.getSkuFactura() + ".pdf");
        
        java.io.File file = fileChooser.showSaveDialog(tablaFacturas.getScene().getWindow());
        if (file != null) {
            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 54, 36);
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                document.open();
                
                com.itextpdf.text.Font fontTitle = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
                com.itextpdf.text.Font fontBold = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10);
                com.itextpdf.text.Font fontNormal = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 10);
                com.itextpdf.text.Font fontSmall = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 8);
                com.itextpdf.text.Font fontLetra = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 24);

                // --- HEADER ---
                com.itextpdf.text.pdf.PdfPTable headerTable = new com.itextpdf.text.pdf.PdfPTable(3);
                headerTable.setWidthPercentage(100);
                headerTable.setWidths(new float[]{45f, 10f, 45f});
                
                // Empresa
                com.itextpdf.text.pdf.PdfPCell cellEmpresa = new com.itextpdf.text.pdf.PdfPCell();
                cellEmpresa.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                cellEmpresa.addElement(new com.itextpdf.text.Paragraph("VITALSA S.A.", fontTitle));
                cellEmpresa.addElement(new com.itextpdf.text.Paragraph("Distribuidora de Insumos", fontNormal));
                cellEmpresa.addElement(new com.itextpdf.text.Paragraph("Dir: Av. ColÃ³n 1234, CÃ³rdoba", fontSmall));
                cellEmpresa.addElement(new com.itextpdf.text.Paragraph("IVA: Responsable Inscripto", fontSmall));
                headerTable.addCell(cellEmpresa);
                
                // Letra
                com.itextpdf.text.pdf.PdfPCell cellLetra = new com.itextpdf.text.pdf.PdfPCell();
                cellLetra.setBorder(com.itextpdf.text.Rectangle.BOX);
                cellLetra.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cellLetra.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                cellLetra.setPadding(5);
                com.itextpdf.text.Paragraph pLetra = new com.itextpdf.text.Paragraph(facturaCompleta.getTipoComprobante(), fontLetra);
                pLetra.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cellLetra.addElement(pLetra);
                headerTable.addCell(cellLetra);
                
                // Datos Comprobante
                com.itextpdf.text.pdf.PdfPCell cellComprobante = new com.itextpdf.text.pdf.PdfPCell();
                cellComprobante.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                cellComprobante.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                com.itextpdf.text.Paragraph pComp = new com.itextpdf.text.Paragraph("FACTURA", fontTitle);
                pComp.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cellComprobante.addElement(pComp);
                com.itextpdf.text.Paragraph pNum = new com.itextpdf.text.Paragraph("NÂº " + facturaCompleta.getSkuFactura(), fontBold);
                pNum.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cellComprobante.addElement(pNum);
                com.itextpdf.text.Paragraph pFec = new com.itextpdf.text.Paragraph("FECHA: " + facturaCompleta.getFechaEmision().toString(), fontBold);
                pFec.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cellComprobante.addElement(pFec);
                headerTable.addCell(cellComprobante);
                
                document.add(headerTable);
                document.add(new com.itextpdf.text.Paragraph(" ")); // Spacer
                
                // --- DATOS CLIENTE ---
                com.itextpdf.text.pdf.PdfPTable clienteTable = new com.itextpdf.text.pdf.PdfPTable(2);
                clienteTable.setWidthPercentage(100);
                clienteTable.setSpacingBefore(10f);
                
                com.itextpdf.text.pdf.PdfPCell cellCliLeft = new com.itextpdf.text.pdf.PdfPCell();
                cellCliLeft.setBorder(com.itextpdf.text.Rectangle.TOP | com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.BOTTOM);
                cellCliLeft.setPadding(8);
                cellCliLeft.addElement(new com.itextpdf.text.Paragraph("SEÃ‘OR/ES: " + facturaCompleta.getClienteNombre(), fontBold));
                cellCliLeft.addElement(new com.itextpdf.text.Paragraph("DOMICILIO: " + facturaCompleta.getClienteDomicilio(), fontNormal));
                clienteTable.addCell(cellCliLeft);
                
                com.itextpdf.text.pdf.PdfPCell cellCliRight = new com.itextpdf.text.pdf.PdfPCell();
                cellCliRight.setBorder(com.itextpdf.text.Rectangle.TOP | com.itextpdf.text.Rectangle.RIGHT | com.itextpdf.text.Rectangle.BOTTOM);
                cellCliRight.setPadding(8);
                cellCliRight.addElement(new com.itextpdf.text.Paragraph("IVA: " + facturaCompleta.getCondicionIva(), fontBold));
                cellCliRight.addElement(new com.itextpdf.text.Paragraph("CUIT/DNI: " + facturaCompleta.getCuitODni(), fontNormal));
                cellCliRight.addElement(new com.itextpdf.text.Paragraph("LOCALIDAD: " + facturaCompleta.getClienteLocalidad(), fontNormal));
                clienteTable.addCell(cellCliRight);
                
                document.add(clienteTable);
                document.add(new com.itextpdf.text.Paragraph(" ")); // Spacer
                
                // --- TABLA ITEMS ---
                com.itextpdf.text.pdf.PdfPTable itemsTable = new com.itextpdf.text.pdf.PdfPTable(4);
                itemsTable.setWidthPercentage(100);
                itemsTable.setWidths(new float[]{55f, 10f, 15f, 20f});
                itemsTable.setSpacingBefore(10f);
                
                String[] headers = {"DescripciÃ³n", "Cant.", "Precio Uni.", "Sub Total"};
                for (String h : headers) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontBold));
                    headerCell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(5);
                    itemsTable.addCell(headerCell);
                }
                
                for (FacturaItemDTO item : facturaCompleta.getItems()) {
                    com.itextpdf.text.pdf.PdfPCell cDesc = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(item.getDescripcion(), fontNormal));
                    com.itextpdf.text.pdf.PdfPCell cCant = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(item.getCantidad()), fontNormal));
                    cCant.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    com.itextpdf.text.pdf.PdfPCell cPrecio = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%,.2f", item.getPrecioUnitario()), fontNormal));
                    cPrecio.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                    com.itextpdf.text.pdf.PdfPCell cSubt = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%,.2f", item.getSubtotal()), fontNormal));
                    cSubt.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                    
                    cDesc.setPadding(5); cCant.setPadding(5); cPrecio.setPadding(5); cSubt.setPadding(5);
                    itemsTable.addCell(cDesc); itemsTable.addCell(cCant); itemsTable.addCell(cPrecio); itemsTable.addCell(cSubt);
                }
                
                document.add(itemsTable);
                document.add(new com.itextpdf.text.Paragraph(" ")); // Spacer
                
                // --- TOTAL ---
                com.itextpdf.text.pdf.PdfPTable totalTable = new com.itextpdf.text.pdf.PdfPTable(2);
                totalTable.setWidthPercentage(100);
                totalTable.setWidths(new float[]{80f, 20f});
                
                com.itextpdf.text.pdf.PdfPCell cTotalLabel = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("SUBTOTAL:", fontBold));
                cTotalLabel.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                cTotalLabel.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cTotalLabel.setPadding(8);
                totalTable.addCell(cTotalLabel);
                
                com.itextpdf.text.pdf.PdfPCell cTotalValue = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%,.2f", facturaCompleta.getTotal()), fontBold));
                cTotalValue.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                cTotalValue.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cTotalValue.setPadding(8);
                totalTable.addCell(cTotalValue);
                
                document.add(totalTable);
                
                document.close();
                
                mostrarAlerta("Ã‰xito", "Factura " + facturaCompleta.getSkuFactura() + " exportada correctamente con el nuevo diseÃ±o.", Alert.AlertType.INFORMATION);
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
