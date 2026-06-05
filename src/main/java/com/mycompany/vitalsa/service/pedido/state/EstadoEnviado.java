package com.mycompany.vitalsa.service.pedido.state;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Ya está en la calle arriba del camión del distribuidor
public class EstadoEnviado implements IPedidoState {
    @Override
    public void siguienteEstado(IPedido pedido) {
        System.out.println("Cambiando estado de ENVIADO a ENTREGADO.");
        pedido.setEstado(new EstadoEntregado());
    }

    @Override
    public void cancelar(IPedido pedido) {
        throw new IllegalStateException("El pedido ya fue ENVIADO. No se puede cancelar directamente sin autorización superior.");
    }

    @Override
    public String getNombre() {
        return "ENVIADO";
    }
}