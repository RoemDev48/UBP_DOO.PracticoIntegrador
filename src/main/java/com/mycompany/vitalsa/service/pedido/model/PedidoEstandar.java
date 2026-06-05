package com.mycompany.vitalsa.service.pedido.model;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import java.util.List;

/**
 *
 * @author RRDev
*/

// El pedido de toda la vida, para clientes comunes y corrientes
public class PedidoEstandar extends PedidoBase {
    public PedidoEstandar(int clienteId, int operadorId, List<DetallePedidoDTO> detalles) {
        super(clienteId, operadorId, detalles);
    }
}