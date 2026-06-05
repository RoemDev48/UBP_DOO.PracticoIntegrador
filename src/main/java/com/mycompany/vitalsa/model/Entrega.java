package com.mycompany.vitalsa.model;

import java.util.Date;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Entrega dentro del sistema
public class Entrega {
    private int id;
    private Distribuidor distribuidor;
    private Pedido pedido;
    private Date fechaEntrega;
    private String observaciones;

    public Entrega() {
    }

    public Entrega(int id, Distribuidor distribuidor, Pedido pedido, Date fechaEntrega, String observaciones) {
        this.id = id;
        this.distribuidor = distribuidor;
        this.pedido = pedido;
        this.fechaEntrega = fechaEntrega;
        this.observaciones = observaciones;
    }
}