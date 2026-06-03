/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
 */
public class Cliente {
    private int id;
    private Direccion direccion;
    private Telefono telefono;
    private List<Pedido> pedidos;

    public Cliente() {
    }

    public Cliente(int id, Direccion direccion, Telefono telefono, List<Pedido> pedidos) {
        this.id = id;
        this.direccion = direccion;
        this.telefono = telefono;
        this.pedidos = pedidos;
    }
}
