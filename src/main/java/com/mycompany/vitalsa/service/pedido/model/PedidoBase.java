package com.mycompany.vitalsa.service.pedido.model;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import com.mycompany.vitalsa.service.pedido.strategy.ICalculoTotalStrategy;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Clase padre con las cosas comunes de cualquier pedido (ID, detalles, total y en qué estado anda)
public abstract class PedidoBase implements IPedido {
    protected int clienteId;
    protected int operadorId;
    protected List<DetallePedidoDTO> detalles;
    protected double total;
    protected com.mycompany.vitalsa.service.pedido.state.IPedidoState estadoActual;

    public PedidoBase(int clienteId, int operadorId, List<DetallePedidoDTO> detalles) {
        this.clienteId = clienteId;
        this.operadorId = operadorId;
        this.detalles = detalles;
        this.total = 0.0;
        this.estadoActual = new com.mycompany.vitalsa.service.pedido.state.EstadoPendiente(); // Todo pedido arranca en Pendiente por defecto
    }

    @Override
    public int getClienteId() {
        return clienteId;
    }

    @Override
    public int getOperadorId() {
        return operadorId;
    }

    @Override
    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public void calcularTotal(ICalculoTotalStrategy estrategia) {
        this.total = estrategia.calcularTotal(this.detalles);
    }
    
    @Override
    public void setEstado(com.mycompany.vitalsa.service.pedido.state.IPedidoState estado) {
        this.estadoActual = estado;
    }

    @Override
    public com.mycompany.vitalsa.service.pedido.state.IPedidoState getEstado() {
        return this.estadoActual;
    }

    @Override
    public void avanzarEstado() {
        this.estadoActual.siguienteEstado(this);
    }

    @Override
    public void cancelarPedido() {
        this.estadoActual.cancelar(this);
    }
}