package com.mycompany.vitalsa.service.pedido.chain;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Segundo paso: frenar a los clientes deudores
public class ValidarDeudaClienteHandler extends AbstractPedidoHandler {
    @Override
    public boolean handle(IPedido pedido) throws ValidationException {
        // Chequeamos si el cliente es moroso (hardcodeado por ahora porque no tenemos tabla de historial crediticio real)
        if (pedido.getClienteId() == 9999) {
            throw new ValidationException("El cliente posee deudas críticas y tiene bloqueada la creación de nuevos pedidos.");
        }
        
        System.out.println("[Chain] Cliente apto crediticiamente.");
        return super.handle(pedido);
    }
}