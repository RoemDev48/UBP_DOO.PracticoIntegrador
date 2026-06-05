package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un DetallePedido dentro del sistema
public class DetallePedido {
    private int id;
    private Pedido pedido;
    private Presentacion presentacion;
    private int cantidad;
    private double precioVenta;
    private double subtotal;

    public DetallePedido() {
    }

    public DetallePedido(int id, Pedido pedido, Presentacion presentacion, int cantidad, double precioVenta, double subtotal) {
        this.id = id;
        this.pedido = pedido;
        this.presentacion = presentacion;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.subtotal = subtotal;
    }
}