/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
 */
public class Direccion {
    private int id;
    private String calle;
    private int numeracion;
    private Barrio barrio;

    public Direccion() {
    }

    public Direccion(int id, String calle, int numeracion, Barrio barrio) {
        this.id = id;
        this.calle = calle;
        this.numeracion = numeracion;
        this.barrio = barrio;
    }
}
