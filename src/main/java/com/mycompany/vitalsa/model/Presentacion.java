/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
 */
public class Presentacion {
    private int id;
    private Producto producto;
    private String descripcion;
    private double precio;
    private int stock;

    public Presentacion() {
    }

    public Presentacion(int id, Producto producto, String descripcion, double precio, int stock) {
        this.id = id;
        this.producto = producto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }
}
