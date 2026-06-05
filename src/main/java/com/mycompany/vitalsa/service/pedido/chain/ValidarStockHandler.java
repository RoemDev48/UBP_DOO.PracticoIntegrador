package com.mycompany.vitalsa.service.pedido.chain;

import com.mycompany.vitalsa.controller.DatabaseController;
import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import com.mycompany.vitalsa.dto.ProductoDTO;
import com.mycompany.vitalsa.service.pedido.model.IPedido;

import java.util.List;

/**
 *
 * @author RRDev
*/

// Tercer paso: verificar que alcance la mercadería en el depósito
public class ValidarStockHandler extends AbstractPedidoHandler {

    private DatabaseController dbController;

    public ValidarStockHandler() {
        this.dbController = new DatabaseController();
    }

    @Override
    public boolean handle(IPedido pedido) throws ValidationException {
        List<ProductoDTO> productosBD = dbController.obtenerProductos();

        for (DetallePedidoDTO detalle : pedido.getDetalles()) {
            // Pegamos a la BD para ver si queda stock de cada producto
            ProductoDTO prodDB = productosBD.stream()
                .filter(p -> p.getId() == detalle.getProductoId())
                .findFirst()
                .orElse(null);

            if (prodDB == null) {
                throw new ValidationException("El producto '" + detalle.getNombre() + "' no existe en el sistema.");
            }

            if (detalle.getCantidad() > prodDB.getStockActual()) {
                throw new ValidationException("Stock insuficiente para '" + detalle.getNombre() + 
                    "'. Solicitado: " + detalle.getCantidad() + ", Disponible: " + prodDB.getStockActual());
            }
        }

        System.out.println("[Chain] Validación de stock exitosa para todos los ítems.");
        return super.handle(pedido);
    }
}