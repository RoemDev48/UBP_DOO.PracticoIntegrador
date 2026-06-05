package com.mycompany.vitalsa.service.pedido.state;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Recién salido del horno. Todavía no lo procesó nadie.
public class EstadoPendiente implements IPedidoState {
    @Override
    public void siguienteEstado(IPedido pedido) {
        System.out.println("Cambiando estado de PENDIENTE a CONFIRMADO.");
        pedido.setEstado(new EstadoConfirmado());
    }

    @Override
    public void cancelar(IPedido pedido) {
        System.out.println("Cancelando pedido desde estado PENDIENTE.");
        pedido.setEstado(new EstadoCancelado());
    }

    @Override
    public String getNombre() {
        return "PENDIENTE";
    }
}