package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Empleado dentro del sistema
public class Empleado {
    private int id;
    private int legajo;
    private String nombre;
    private String apellido;
    private Telefono telefono;

    public Empleado() {
    }

    public Empleado(int id, int legajo, String nombre, String apellido, Telefono telefono) {
        this.id = id;
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }
}