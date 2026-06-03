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
