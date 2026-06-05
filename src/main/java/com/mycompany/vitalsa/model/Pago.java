package com.mycompany.vitalsa.model;

import java.util.Date;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Pago dentro del sistema
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