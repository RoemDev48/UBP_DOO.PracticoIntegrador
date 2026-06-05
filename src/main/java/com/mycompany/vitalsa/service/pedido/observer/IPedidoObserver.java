package com.mycompany.vitalsa.service.pedido.observer;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Interfaz para los "chismosos" que quieren enterarse cuando se guarda un pedido
public interface IPedidoObserver {
    void onPedidoGuardado(int pedidoId, IPedido pedido);
}