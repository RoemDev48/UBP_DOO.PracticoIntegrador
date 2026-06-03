/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author RRDev
 */
public class Factura {
    private int id;
    private Date fecha;
    private Pedido pedido;
    private EstadoFactura estado;
    private List<DetalleFactura> detalles;
    private double total;
    private Pago pago;

    public Factura() {
    }

    public Factura(int id, Date fecha, Pedido pedido, EstadoFactura estado, List<DetalleFactura> detalles, double total, Pago pago) {
        this.id = id;
        this.fecha = fecha;
        this.pedido = pedido;
        this.estado = estado;
        this.detalles = detalles;
        this.total = total;
        this.pago = pago;
    }
}
