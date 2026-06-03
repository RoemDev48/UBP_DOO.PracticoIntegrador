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
public class Producto {
    private int id;
    private String nombre;
    private List<Presentacion> presentaciones;

    public Producto() {
    }

    public Producto(int id, String nombre, List<Presentacion> presentaciones) {
        this.id = id;
        this.nombre = nombre;
        this.presentaciones = presentaciones;
    }
}
