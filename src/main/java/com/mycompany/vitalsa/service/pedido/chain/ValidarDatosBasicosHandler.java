package com.mycompany.vitalsa.service.pedido.chain;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Primer paso de validación: que el pedido no venga vacío o sin cliente asignado
public class ValidarDatosBasicosHandler extends AbstractPedidoHandler {
    @Override
    public boolean handle(IPedido pedido) throws ValidationException {
        if (pedido.getClienteId() <= 0) {
            throw new ValidationException("El pedido debe tener un cliente asignado válido.");
        }
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new ValidationException("El carrito de compras está vacío. No se puede generar un pedido sin productos.");
        }
        
        System.out.println("[Chain] Datos básicos validados correctamente.");
        return super.handle(pedido);
    }
}