/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
 */
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
