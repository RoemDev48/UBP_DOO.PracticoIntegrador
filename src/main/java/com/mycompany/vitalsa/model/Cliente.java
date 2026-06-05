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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
    
    public Telefono getTelefono() { return telefono; }
    public void setTelefono(Telefono telefono) { this.telefono = telefono; }
    
    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }
}