package com.mycompany.vitalsa.service.pedido;

import com.mycompany.vitalsa.controller.DatabaseController;
import com.mycompany.vitalsa.service.pedido.model.IPedido;
import com.mycompany.vitalsa.service.pedido.observer.IPedidoObserver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Servicio central para los pedidos: se encarga de guardar, validar y pegarle un grito a los observers
public class PedidoService {
    
    private List<IPedidoObserver> observers = new ArrayList<>();
    private DatabaseController dbController;

    public PedidoService() {
        this.dbController = new DatabaseController();
    }

    public void addObserver(IPedidoObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IPedidoObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(int pedidoId, IPedido pedido) {
        for (IPedidoObserver obs : observers) {
            obs.onPedidoGuardado(pedidoId, pedido);
        }
    }

    // Método principal: acá entra el pedido, pasa por los filtros (chain) y si sale vivo se guarda en la BD
    public boolean procesarNuevoPedido(IPedido pedido) throws com.mycompany.vitalsa.service.pedido.chain.ValidationException {
        
        // 1. Armar el trencito de validaciones
        com.mycompany.vitalsa.service.pedido.chain.IPedidoHandler cadenaDeValidacion = new com.mycompany.vitalsa.service.pedido.chain.ValidarDatosBasicosHandler();
        cadenaDeValidacion
            .setNext(new com.mycompany.vitalsa.service.pedido.chain.ValidarStockHandler())
            .setNext(new com.mycompany.vitalsa.service.pedido.chain.ValidarDeudaClienteHandler());

        // 2. Ejecutar validaciones (si algo pincha, explota con un ValidationException)
        cadenaDeValidacion.handle(pedido);

        // 3. Todo en orden, clavamos el pedido en la BD y nos traemos el ID nuevo
        int nuevoId = dbController.guardarPedidoConRetorno(
            pedido.getClienteId(), 
            pedido.getOperadorId(), 
            pedido.getTotal(), 
            pedido.getDetalles()
        );

        if (nuevoId != -1) {
            notifyObservers(nuevoId, pedido);
            return true;
        }
        return false;
    }
    
    public boolean actualizarPedidoExistente(int idExistente, IPedido pedidoActualizado) {
        // Ojo que acá solo actualizamos los datos, no le avisamos a facturación ni a nadie de vuelta para no duplicar cosas
        return dbController.actualizarPedidoCompleto(idExistente, pedidoActualizado.getTotal(), pedidoActualizado.getDetalles());
    }
}
