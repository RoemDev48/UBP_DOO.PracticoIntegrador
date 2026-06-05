package com.mycompany.vitalsa.service.pedido.chain;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Interfaz para eslabones de validación del pedido
public interface IPedidoHandler {
    IPedidoHandler setNext(IPedidoHandler handler);
    boolean handle(IPedido pedido) throws ValidationException;
}