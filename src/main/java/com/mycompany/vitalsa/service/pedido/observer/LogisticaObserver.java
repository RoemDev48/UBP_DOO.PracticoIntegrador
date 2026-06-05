package com.mycompany.vitalsa.service.pedido.observer;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// El área de Logística que se entera cuando hay que ir preparando la mercadería
public class LogisticaObserver implements IPedidoObserver {
    @Override
    public void onPedidoGuardado(int pedidoId, IPedido pedido) {
        System.out.println("[LogisticaObserver] Notificado de nuevo pedido ID: " + pedidoId + ". Preparando para despacho...");
        // Acá a futuro podríamos clavarlo en una tabla de 'envios_pendientes' 
        // o mandarle una alerta a la pantalla del depósito
    }
}