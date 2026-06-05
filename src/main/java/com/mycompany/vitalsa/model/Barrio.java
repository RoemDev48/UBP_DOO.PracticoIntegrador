package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Barrio dentro del sistema
public class Barrio {
    private int id;
    private String codPostal;
    private String nombre;
    private Zona zona;

    public Barrio() {
    }

    public Barrio(int id, String codPostal, String nombre, Zona zona) {
        this.id = id;
        this.codPostal = codPostal;
        this.nombre = nombre;
        this.zona = zona;
    }
}