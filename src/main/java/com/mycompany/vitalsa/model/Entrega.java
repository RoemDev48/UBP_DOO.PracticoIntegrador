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
