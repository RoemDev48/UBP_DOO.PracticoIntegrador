/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
 */
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
