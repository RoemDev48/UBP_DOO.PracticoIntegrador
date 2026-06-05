package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Cliente dentro del sistema
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