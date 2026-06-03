/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
 */
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
