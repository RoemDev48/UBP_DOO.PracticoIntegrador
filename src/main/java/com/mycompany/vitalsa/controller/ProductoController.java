package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.ProductoDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class ProductoController implements Initializable {

    @FXML private Label lblTotal;
    @FXML private Label lblUltimaActualizacion;

    @FXML private TableView<ProductoDTO> tablaProductos;
    @FXML private TableColumn<ProductoDTO, String> colSku;
    @FXML private TableColumn<ProductoDTO, String> colNombre;
    @FXML private TableColumn<ProductoDTO, String> colCategoria;
    @FXML private TableColumn<ProductoDTO, String> colPrecio;
    @FXML private TableColumn<ProductoDTO, Integer> colStock;
    @FXML private TableColumn<ProductoDTO, String> colEstado;
    @FXML private TableColumn<ProductoDTO, Void> colAcciones;

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TextField txtBuscar;
    
    private ObservableList<ProductoDTO> masterData = FXCollections.observableArrayList();

    private DatabaseController dbController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbController = new DatabaseController();

        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        
        // Le clavamos un estilo tipo "badge" o "pastilla" a la columna Estado para que quede más lindo
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(param -> new TableCell<ProductoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label lbl = new Label(item);
                    if ("EN STOCK".equals(item)) {
                        lbl.getStyleClass().add("badge-stock-alto");
                    } else if ("STOCK BAJO".equals(item)) {
                        lbl.getStyleClass().add("badge-stock-bajo");
                    } else {
                        lbl.getStyleClass().add("badge-stock-cero");
                    }
                    setGraphic(lbl);
                    setText(null);
                }
            }
        });

        configurarBotonesAccion();
        
        if (cmbFiltroEstado != null) {
            cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "En Stock", "Stock Bajo", "Sin Stock"));
            cmbFiltroEstado.getSelectionModel().selectFirst();
            cmbFiltroEstado.valueProperty().addListener((obs, oldV, newV) -> aplicarFiltros());
        }
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldV, newV) -> aplicarFiltros());
        }

        actualizarDatos();
    }

    private void configurarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<ProductoDTO, Void>() {
            private final Button btnPrecio = new Button("Precio");
            private final HBox pane = new HBox(btnPrecio);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                btnPrecio.getStyleClass().add("btn-gestion-precio");
                
                FontIcon tagIcon = new FontIcon("fas-tag");
                tagIcon.setIconSize(12);
                tagIcon.setIconColor(Color.WHITE);
                btnPrecio.setGraphic(tagIcon);
                
                btnPrecio.setOnAction(e -> {
                    ProductoDTO producto = getTableView().getItems().get(getIndex());
                    abrirModalPrecio(producto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    // Busca los productos y refresca las tarjetas de arriba
    public void actualizarDatos() {
        lblTotal.setText(String.valueOf(dbController.contarProductosActivos()));
        lblUltimaActualizacion.setText(dbController.obtenerTiempoUltimaActualizacion());
        List<ProductoDTO> productos = dbController.obtenerProductos();
        masterData.setAll(productos);
        aplicarFiltros();
    }

    // Filtra la lista de productos que ya trajimos (sin ir a la BD de nuevo)
    private void aplicarFiltros() {
        if (cmbFiltroEstado == null) return;
        
        String textoBuscado = txtBuscar != null && txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estadoFiltro = cmbFiltroEstado.getValue();

        javafx.collections.transformation.FilteredList<ProductoDTO> filtrados = new javafx.collections.transformation.FilteredList<>(masterData, producto -> {
            boolean coincideTexto = true;
            if (!textoBuscado.isEmpty()) {
                String nombre = producto.getNombre() != null ? producto.getNombre().toLowerCase() : "";
                String sku = producto.getSku() != null ? producto.getSku().toLowerCase() : "";
                String cat = producto.getCategoria() != null ? producto.getCategoria().toLowerCase() : "";
                
                coincideTexto = nombre.contains(textoBuscado) || 
                                sku.contains(textoBuscado) || 
                                cat.contains(textoBuscado);
            }

            boolean coincideEstado = true;
            if (estadoFiltro != null && !"Todos".equals(estadoFiltro)) {
                String pe = producto.getEstado().toUpperCase().trim();
                String ef = estadoFiltro.toUpperCase().trim();
                coincideEstado = pe.equals(ef);
            }

            return coincideTexto && coincideEstado;
        });

        tablaProductos.setItems(filtrados);
    }

    @FXML
    // Abre la pantallita para dar de alta un producto nuevo
    public void abrirModalRegistro() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/producto/ProductoModalView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            ProductoModalController modalController = loader.getController();
            modalController.setParentController(this);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Alta de Producto");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    // Abre el modal cortito que solo cambia el precio
    public void abrirModalPrecio(ProductoDTO producto) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/producto/PrecioModalView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            PrecioModalController modalController = loader.getController();
            modalController.setParentController(this);
            modalController.cargarDatos(producto);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Modificar Precio");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}