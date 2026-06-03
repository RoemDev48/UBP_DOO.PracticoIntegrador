/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
 */
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
