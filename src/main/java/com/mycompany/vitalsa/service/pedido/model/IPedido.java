package com.mycompany.vitalsa.service.pedido.model;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import com.mycompany.vitalsa.service.pedido.strategy.ICalculoTotalStrategy;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Interfaz base que deben cumplir todos los tipos de pedidos (para que el Factory y el Strategy funcionen joya)
public interface IPedido {
    int getClienteId();
    int getOperadorId();
    List<DetallePedidoDTO> getDetalles();
    double getTotal();
    void calcularTotal(ICalculoTotalStrategy estrategia);
    
    // Métodos para ir cambiando en qué estado está el pedido (Pendiente, Entregado, etc)
    void setEstado(com.mycompany.vitalsa.service.pedido.state.IPedidoState estado);
    com.mycompany.vitalsa.service.pedido.state.IPedidoState getEstado();
    void avanzarEstado();
    void cancelarPedido();
}