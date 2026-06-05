package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.dto.ClienteDTO;
import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import com.mycompany.vitalsa.dto.ProductoDTO;
import com.mycompany.vitalsa.service.pedido.PedidoService;
import com.mycompany.vitalsa.service.pedido.factory.PedidoFactory;
import com.mycompany.vitalsa.service.pedido.model.IPedido;
import com.mycompany.vitalsa.service.pedido.observer.FacturacionObserver;
import com.mycompany.vitalsa.service.pedido.observer.LogisticaObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 *
 * @author RRDev
*/

public class PedidoModalController {

    @FXML private ComboBox<ClienteDTO> cmbCliente;
    @FXML private FlowPane panelCatalogo;
    @FXML private VBox panelCarrito;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;
    @FXML private Label lblTiempoEstimado;
    @FXML private TextArea txtObservacion;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private DatabaseController dbController;
    private PedidoService pedidoService;
    private PedidoController parentController;
    private ObservableList<DetallePedidoDTO> carrito;
    private Integer pedidoIdEnEdicion = null;

    public void setParentController(PedidoController parentController) {
        this.parentController = parentController;
        this.dbController = new DatabaseController();
        this.carrito = FXCollections.observableArrayList();
        
        // Instanciamos el servicio (le mandamos 'this' para que nos avise cuando haya cambios)
        this.pedidoService = new PedidoService();
        this.pedidoService.addObserver(new FacturacionObserver());
        this.pedidoService.addObserver(new LogisticaObserver());

        // Cada vez que cambien al cliente, recalculamos los totales por los posibles descuentos
        cmbCliente.valueProperty().addListener((obs, oldVal, newVal) -> renderizarCarrito());
        
        cargarClientes();
        cargarCatalogo();
    }

    public void initForEdit(PedidoController parentController, com.mycompany.vitalsa.dto.PedidoDTO pedido) {
        setParentController(parentController);
        this.pedidoIdEnEdicion = pedido.getIdOriginal();
        
        btnGuardar.setText("Actualizar Pedido");
        
        for (ClienteDTO c : cmbCliente.getItems()) {
            if (pedido.getCliente().contains(c.getNombre()) || c.getNombre().contains(pedido.getCliente())) {
                cmbCliente.setValue(c);
                break;
            }
        }
        cmbCliente.setDisable(true);
        
        List<DetallePedidoDTO> detalles = dbController.obtenerDetallesPedido(pedidoIdEnEdicion);
        carrito.addAll(detalles);
        renderizarCarrito();
    }

    private void cargarClientes() {
        List<ClienteDTO> clientes = dbController.obtenerClientes();
        cmbCliente.setItems(FXCollections.observableArrayList(clientes));
        cmbCliente.setConverter(new StringConverter<ClienteDTO>() {
            @Override
            public String toString(ClienteDTO cliente) {
                if (cliente == null) return "";
                String doc = cliente.getDocumento();
                if (doc != null && !doc.trim().isEmpty()) {
                    return cliente.getNombre() + " (" + doc.trim() + ")";
                }
                return cliente.getNombre();
            }

            @Override
            public ClienteDTO fromString(String string) {
                return null;
            }
        });
    }

    private void cargarCatalogo() {
        List<ProductoDTO> productos = dbController.obtenerProductos();
        
        for (ProductoDTO prod : productos) {
            VBox card = new VBox(5);
            card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-radius: 8;");
            card.setPrefWidth(160);

            Label lblNombre = new Label(prod.getNombre());
            lblNombre.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
            lblNombre.setWrapText(true);

            Label lblPrecio = new Label(prod.getPrecioUnitario());
            lblPrecio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0369a1;");

            Label lblStock = new Label("Stock: " + prod.getStockActual());
            lblStock.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

            Button btnAdd = new Button("Añadir");
            btnAdd.setMaxWidth(Double.MAX_VALUE);
            btnAdd.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #0369a1; -fx-font-weight: bold; -fx-cursor: hand;");
            
            btnAdd.setOnAction(e -> agregarAlCarrito(prod));

            card.getChildren().addAll(lblNombre, lblPrecio, lblStock, btnAdd);
            panelCatalogo.getChildren().add(card);
        }
    }

    private void agregarAlCarrito(ProductoDTO prod) {
        for (DetallePedidoDTO detalle : carrito) {
            if (detalle.getProductoId() == prod.getId()) {
                detalle.setCantidad(detalle.getCantidad() + 1);
                renderizarCarrito();
                return;
            }
        }
        
        double precio = Double.parseDouble(prod.getPrecioUnitario().replace("$", "").replace(",", "."));
        carrito.add(new DetallePedidoDTO(prod.getId(), prod.getNombre(), precio, 1));
        renderizarCarrito();
    }

    // Pinta el carrito en pantalla (borra todo y vuelve a generar las filas)
    private void renderizarCarrito() {
        panelCarrito.getChildren().clear();

        // Patrones Factory + Strategy
        // Armamos un pedido temporal en memoria para poder calcular los descuentos al vuelo
        // y que el usuario vea el precio real antes de guardar
        ClienteDTO clienteActual = cmbCliente.getValue();
        IPedido pedidoTemp = PedidoFactory.crearPedido(clienteActual, 1, carrito);
        double totalCalculado = pedidoTemp.getTotal();

        for (DetallePedidoDTO detalle : carrito) {
            VBox itemBox = new VBox(5);
            itemBox.setStyle("-fx-padding: 10 0; -fx-border-color: transparent transparent #E2E8F0 transparent; -fx-border-width: 0 0 1 0;");

            HBox headerBox = new HBox();
            Label lblNombre = new Label(detalle.getNombre());
            lblNombre.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label lblSubtotal = new Label(String.format("$%.2f", detalle.getSubtotal())); // Subtotal que trae aplicado la estrategia si aplica
            lblSubtotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #0369a1;");
            headerBox.getChildren().addAll(lblNombre, spacer, lblSubtotal);

            HBox controlBox = new HBox(10);
            controlBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label lblUnitario = new Label(String.format("Unidad: $%.2f", detalle.getPrecioUnitario()));
            lblUnitario.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
            
            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);

            Button btnMinus = new Button("-");
            btnMinus.setStyle("-fx-background-color: #F1F5F9; -fx-cursor: hand;");
            btnMinus.setOnAction(e -> {
                if (detalle.getCantidad() > 1) {
                    detalle.setCantidad(detalle.getCantidad() - 1);
                    renderizarCarrito();
                }
            });

            Label lblCant = new Label(String.valueOf(detalle.getCantidad()));
            lblCant.setStyle("-fx-font-weight: bold;");

            Button btnPlus = new Button("+");
            btnPlus.setStyle("-fx-background-color: #F1F5F9; -fx-cursor: hand;");
            btnPlus.setOnAction(e -> {
                detalle.setCantidad(detalle.getCantidad() + 1);
                renderizarCarrito();
            });

            Button btnDelete = new Button("X");
            btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #DC2626; -fx-cursor: hand; -fx-font-weight: bold;");
            btnDelete.setOnAction(e -> {
                carrito.remove(detalle);
                renderizarCarrito();
            });

            controlBox.getChildren().addAll(lblUnitario, spacer2, btnMinus, lblCant, btnPlus, btnDelete);
            itemBox.getChildren().addAll(headerBox, controlBox);
            
            panelCarrito.getChildren().add(itemBox);
        }

        lblSubtotal.setText(String.format("$%.2f", totalCalculado));
        
        lblTotal.setText(String.format("$%.2f", totalCalculado));
        
        // Tiempo estimado de entrega basado en la zona
        if (clienteActual != null) {
            String zona = clienteActual.getZonaNombre();
            String tiempoEstimado = "45 minutos"; 
            if (zona != null) {
                String zLower = zona.toLowerCase();
                if (zLower.contains("norte")) tiempoEstimado = "30 minutos";
                else if (zLower.contains("sur")) tiempoEstimado = "60 minutos";
                else if (zLower.contains("centro")) tiempoEstimado = "20 minutos";
            } else {
                zona = "Sin Zona";
            }
            
            lblTiempoEstimado.setText("Entrega estimada: " + tiempoEstimado + " (" + zona + ")");
        } else {
            lblTiempoEstimado.setText("");
        }
    }

    // Cuando le dan a "Guardar Pedido" (acá entra toda la validación con la Chain of Responsibility)
    @FXML
    private void guardarPedido() {
        ClienteDTO clienteSeleccionado = cmbCliente.getValue();
        
        // Si no eligió cliente va a fallar más adelante en la validación, pero lo atajamos acá
        int clienteId = (clienteSeleccionado != null) ? clienteSeleccionado.getId() : -1;

        // Armamos el objeto Pedido completo para pasarlo a la BD
        // Ojo: si no seleccionó cliente le mandamos un dummy para que salte el error de validación
        IPedido pedidoFinal;
        if (clienteSeleccionado != null) {
            pedidoFinal = PedidoFactory.crearPedido(clienteSeleccionado, 1, carrito);
        } else {
            // Forzamos un cliente trucho para que falle la validación porque no eligió nada
            pedidoFinal = new com.mycompany.vitalsa.service.pedido.model.PedidoEstandar(-1, 1, carrito);
        }

        try {
            boolean exito;
            if (pedidoIdEnEdicion != null) {
                exito = pedidoService.actualizarPedidoExistente(pedidoIdEnEdicion, pedidoFinal);
            } else {
                // Le pasamos el pedido al servicio para que valide todo (stock, cliente, etc) antes de guardar
                exito = pedidoService.procesarNuevoPedido(pedidoFinal);
            }

            if (exito) {
                if (parentController != null) {
                    parentController.actualizarDatos();
                }
                cerrar();
            } else {
                mostrarAlerta("Error", "Ocurrió un error inesperado al guardar en la base de datos.");
            }

        } catch (com.mycompany.vitalsa.service.pedido.chain.ValidationException ex) {
            // Alguna validación falló (falta stock, cliente no existe, etc)
            mostrarAlerta("Validación Rechazada", ex.getMessage());
        }
    }

    // Abre la ventanita para dar de alta a un cliente si no está en la lista
    @FXML
    private void abrirModalNuevoCliente() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/mycompany/vitalsa/view/cliente/ClienteModalView.fxml"));
            javafx.scene.Parent root = loader.load();

            ClienteModalController controller = loader.getController();
            controller.initData(dbController, null);

            Stage stage = new Stage();
            stage.setTitle("Nuevo Cliente");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            if (controller.isGuardadoExitoso()) {
                cargarClientes();
                List<ClienteDTO> clientes = dbController.obtenerClientes();
                if (!clientes.isEmpty()) {
                    cmbCliente.setValue(clientes.get(clientes.size() - 1));
                }
            }
        } catch (java.io.IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de nuevo cliente.");
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
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