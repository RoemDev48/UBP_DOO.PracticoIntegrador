package com.mycompany.vitalsa.service.pedido.state;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Estado de vía muerta. Alguien se arrepintió o no había stock.
public class EstadoCancelado implements IPedidoState {
    @Override
    public void siguienteEstado(IPedido pedido) {
        throw new IllegalStateException("Un pedido CANCELADO no puede seguir avanzando.");
    }

    @Override
    public void cancelar(IPedido pedido) {
        throw new IllegalStateException("El pedido ya está CANCELADO.");
    }

    @Override
    public String getNombre() {
        return "CANCELADO";
    }
}