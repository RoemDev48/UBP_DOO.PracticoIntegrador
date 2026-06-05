package com.mycompany.vitalsa.service.pedido.strategy;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Cálculo con un 10% de descuento directo al total porque compran en cantidad
public class CalculoMayoristaStrategy implements ICalculoTotalStrategy {
    private static final double DESCUENTO = 0.10; // 10% de descuento

    @Override
    public double calcularTotal(List<DetallePedidoDTO> carrito) {
        double subtotal = calcularSubtotal(carrito);
        return subtotal - (subtotal * DESCUENTO);
    }
}