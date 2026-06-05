package com.mycompany.vitalsa.service.pedido.state;

import com.mycompany.vitalsa.service.pedido.model.IPedido;

/**
 *
 * @author RRDev
*/

// Interfaz para meterle el patrón State a los pedidos (así evitamos un switch gigante para saber qué hacer en cada estado)
public interface IPedidoState {
    void siguienteEstado(IPedido pedido);
    void cancelar(IPedido pedido);
    String getNombre();
}