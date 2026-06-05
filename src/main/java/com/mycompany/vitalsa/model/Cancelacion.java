package com.mycompany.vitalsa.model;

import java.util.Date;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Cancelacion dentro del sistema
public class Cancelacion {
    private int id;    
    private String motivo;
    private Date fecha;
    private Pedido pedido;

    public Cancelacion() {
    }

    public Cancelacion(int id, String motivo, Date fecha, Pedido pedido) {
        this.id = id;
        this.motivo = motivo;
        this.fecha = fecha;
        this.pedido = pedido;
    }
}