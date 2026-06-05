package com.mycompany.vitalsa.service.pedido.strategy;

import com.mycompany.vitalsa.dto.DetallePedidoDTO;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Interfaz para enchufarle distintas formas de calcular el precio final (así no llenamos de ifs el código principal)
public interface ICalculoTotalStrategy {
    double calcularTotal(List<DetallePedidoDTO> carrito);
}