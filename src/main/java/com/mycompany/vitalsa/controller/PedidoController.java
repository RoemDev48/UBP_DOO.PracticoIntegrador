/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.model.*;
import java.util.List;

/**
 *
 * @author RRDev
 */
public class PedidoController {

    // CU: Registrar Pedido
    //   «include» Consultar Stock
    //   «include» Consultar Cliente
    //   «include» Estimar Fecha de Entrega
    public void registrarPedido() {
    }

    // CU: Cancelar Pedido
    //   «extend» Notificar al Distribuidor
    public void cancelarPedido() {
    }

    // CU: Consultar Stock (incluido por Registrar Pedido)
    public void consultarStock() {
    }

    // CU: Estimar Fecha de Entrega (incluido por Registrar Pedido)
    public void estimarFechaEntrega() {
    }

    // CU: Consultar Histórico de Pedidos
    public void consultarHistoricoPedidos() {
    }

    // CU: Gestionar Pedidos Pendientes
    public void gestionarPedidosPendientes() {
    }

    // Extra: Confirmar Pedido (PENDIENTE → CONFIRMADO)
    public void confirmarPedido() {
    }

    // Extra: Listar Pedidos por Estado
    public void listarPedidosPorEstado() {
    }

    // Extra: Listar Pedidos por Cliente
    public void listarPedidosPorCliente() {
    }
}
