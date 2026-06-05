package com.mycompany.vitalsa.service.pedido.state;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Fin del recorrido, paquete en manos del cliente. Acá ya no se puede cancelar.
public class EstadoEntregado implements IPedidoState {
    @Override
    public void siguienteEstado(IPedido pedido) {
        throw new IllegalStateException("El pedido ya está ENTREGADO. Este es el estado final.");
    }

    @Override
    public void cancelar(IPedido pedido) {
        throw new IllegalStateException("Un pedido ENTREGADO no puede ser cancelado.");
    }

    @Override
    public String getNombre() {
        return "ENTREGADO";
    }
}