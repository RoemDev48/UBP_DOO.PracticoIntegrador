package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un EncargadoAdministrativo dentro del sistema
public class EncargadoAdministrativo extends Empleado {

    public EncargadoAdministrativo() {
    }

    public EncargadoAdministrativo(int id, int legajo, String nombre, String apellido, Telefono telefono) {
        super(id, legajo, nombre, apellido, telefono);
    }
}