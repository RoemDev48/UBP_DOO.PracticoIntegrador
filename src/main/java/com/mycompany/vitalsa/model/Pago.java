/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

import java.util.Date;

/**
 *
 * @author RRDev
 */
public class Pago {
    private int id;
    private Factura factura;
    private Date fecha;
    private TipoPago tipo;
    private double monto;

    public Pago() {
    }

    public Pago(int id, Factura factura, Date fecha, TipoPago tipo, double monto) {
        this.id = id;
        this.factura = factura;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
    }
}
