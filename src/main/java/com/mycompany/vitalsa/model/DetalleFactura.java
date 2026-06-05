package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un DetalleFactura dentro del sistema
public class DetalleFactura {
    private int id;
    private Factura factura;
    private String descripcion;
    private int cantidad;
    private double precioVenta;
    private double subtotal;

    public DetalleFactura() {
    }

    public DetalleFactura(int id, Factura factura, String descripcion, int cantidad, double precioVenta, double subtotal) {
        this.id = id;
        this.factura = factura;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.subtotal = subtotal;
    }
}