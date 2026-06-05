package com.mycompany.vitalsa.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Pedido dentro del sistema
public class Pedido {
    private int id;
    private Operador operador;
    private Cliente cliente;
    private Date fechaRealizacion;
    private EstadoPedido estado; 
    private double montoTotal;
    private Date fechaEntregaEstimada;
    private List<DetallePedido> items;

    public Pedido() {
    }

    public Pedido(int id, Operador operador, Cliente cliente, Date fechaRealizacion, EstadoPedido estado, double montoTotal, Date fechaEntregaEstimada, List<DetallePedido> items) {
        this.id = id;
        this.operador = operador;
        this.cliente = cliente;
        this.fechaRealizacion = fechaRealizacion;
        this.estado = estado;
        this.montoTotal = montoTotal;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.items = items;
    }
}