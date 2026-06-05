package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Producto dentro del sistema
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