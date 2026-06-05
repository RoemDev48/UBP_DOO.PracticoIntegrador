package com.mycompany.vitalsa.service.pedido.chain;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Clase base para armar la cadena de validaciones (el famoso patrón Chain of Responsibility)
public abstract class AbstractPedidoHandler implements IPedidoHandler {
    private IPedidoHandler nextHandler;

    @Override
    public IPedidoHandler setNext(IPedidoHandler handler) {
        this.nextHandler = handler;
        return handler;
    }

    @Override
    public boolean handle(IPedido pedido) throws ValidationException {
        if (nextHandler != null) {
            return nextHandler.handle(pedido);
        }
        return true;
    }
}