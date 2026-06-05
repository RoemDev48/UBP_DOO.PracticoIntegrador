package com.mycompany.vitalsa.service.pedido.observer;

import com.mycompany.vitalsa.controller.DatabaseController;
import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// El encargado de Facturación que está atento a los pedidos nuevos para cobrar
public class FacturacionObserver implements IPedidoObserver {

    private DatabaseController db;

    public FacturacionObserver() {
        this.db = new DatabaseController();
    }

    @Override
    public void onPedidoGuardado(int pedidoId, IPedido pedido) {
        System.out.println("[FacturacionObserver] Notificado de nuevo pedido ID: " + pedidoId + ". Generando factura pendiente...");
        
        // Magia de BD: Le decimos a la base que arme la factura en estado PENDIENTE para este pedido
        db.generarFacturaPendiente(pedidoId, pedido.getTotal());
    }
}