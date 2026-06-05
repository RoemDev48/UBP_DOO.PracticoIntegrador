package com.mycompany.vitalsa.service.pedido.factory;

import com.mycompany.vitalsa.dto.ClienteDTO;
import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import com.mycompany.vitalsa.service.pedido.model.IPedido;
import com.mycompany.vitalsa.service.pedido.model.PedidoEstandar;
import com.mycompany.vitalsa.service.pedido.model.PedidoMayorista;
import com.mycompany.vitalsa.service.pedido.strategy.CalculoEstandarStrategy;
import com.mycompany.vitalsa.service.pedido.strategy.CalculoMayoristaStrategy;
import com.mycompany.vitalsa.service.pedido.strategy.ICalculoTotalStrategy;

import java.util.List;

/**
 *
 * @author RRDev
*/

public class PedidoFactory {

    // Armamos el pedido final (acá metemos la lógica para saber si le hacemos precio mayorista o no)
    public static IPedido crearPedido(ClienteDTO cliente, int operadorId, List<DetallePedidoDTO> detalles) {
        
        // Chequeo hardcodeado: si el nombre dice mayorista o lleva más de 50 cosas, le cobramos menos
        boolean esMayorista = false;
        
        if (cliente != null && (cliente.getNombre().toLowerCase().contains("mayorista") || cliente.getNombre().toLowerCase().contains("distribuidora"))) {
            esMayorista = true;
        } else {
            int totalItems = detalles.stream().mapToInt(DetallePedidoDTO::getCantidad).sum();
            if (totalItems >= 50) {
                esMayorista = true;
            }
        }

        IPedido pedido;
        ICalculoTotalStrategy estrategia;

        if (esMayorista) {
            pedido = new PedidoMayorista(cliente != null ? cliente.getId() : 0, operadorId, detalles);
            estrategia = new CalculoMayoristaStrategy();
        } else {
            pedido = new PedidoEstandar(cliente != null ? cliente.getId() : 0, operadorId, detalles);
            estrategia = new CalculoEstandarStrategy();
        }

        // Le pasamos la estrategia al pedido para que calcule cuánto duele
        pedido.calcularTotal(estrategia);

        return pedido;
    }
}