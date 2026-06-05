package com.mycompany.vitalsa.service.pedido.strategy;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import java.util.List;

/**
 *
 * @author RRDev
*/

// El cálculo aburrido de siempre: precio unitario x cantidad
public class CalculoEstandarStrategy implements ICalculoTotalStrategy {
    @Override
    public double calcularTotal(List<DetallePedidoDTO> carrito) {
        double total = 0;
        for (DetallePedidoDTO d : carrito) {
            total += (d.getPrecioUnitario() * d.getCantidad());
        }
        return total;
    }
}