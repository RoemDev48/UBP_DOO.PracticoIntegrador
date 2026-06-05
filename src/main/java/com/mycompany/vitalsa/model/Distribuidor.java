package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Distribuidor dentro del sistema
public class Distribuidor {
    private int id;
    private String nombre;
    private int capacidadDiaria;
    private Zona zonaACargo;

    public Distribuidor() {
    }

    public Distribuidor(int id, String nombre, int capacidadDiaria, Zona zonaACargo) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadDiaria = capacidadDiaria;
        this.zonaACargo = zonaACargo;
    }
}