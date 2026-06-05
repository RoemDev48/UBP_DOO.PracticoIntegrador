package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Operador dentro del sistema
public class Operador extends Empleado {

    public Operador() {
    }

    public Operador(int id, int legajo, String nombre, String apellido, Telefono telefono) {
        super(id, legajo, nombre, apellido, telefono);
    }
}