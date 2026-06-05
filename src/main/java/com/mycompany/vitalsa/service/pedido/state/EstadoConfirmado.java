package com.mycompany.vitalsa.service.pedido.state;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Ya lo agarraron en administración y está todo OK, ahora le toca a logística armarlo
public class EstadoConfirmado implements IPedidoState {
    @Override
    public void siguienteEstado(IPedido pedido) {
        System.out.println("Cambiando estado de CONFIRMADO a ENVIADO.");
        pedido.setEstado(new EstadoEnviado());
    }

    @Override
    public void cancelar(IPedido pedido) {
        System.out.println("Cancelando pedido desde estado CONFIRMADO.");
        pedido.setEstado(new EstadoCancelado());
    }

    @Override
    public String getNombre() {
        return "CONFIRMADO";
    }
}