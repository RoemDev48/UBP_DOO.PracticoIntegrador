package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Telefono dentro del sistema
public class Telefono {
    private int id;
    private String numero;
    private String tipo; 

    public Telefono() {
    }

    public Telefono(int id, String numero, String tipo) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
    }
}