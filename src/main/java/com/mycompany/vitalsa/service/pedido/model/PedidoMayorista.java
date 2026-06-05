package com.mycompany.vitalsa.service.pedido.model;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Pedido grandote, acá después le aplicamos descuentos o precios especiales a través del Strategy
public class PedidoMayorista extends PedidoBase {
    public PedidoMayorista(int clienteId, int operadorId, List<DetallePedidoDTO> detalles) {
        super(clienteId, operadorId, detalles);
    }
}