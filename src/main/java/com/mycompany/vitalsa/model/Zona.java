/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
 */
public class Zona {
    private int id;
    private String nombre;
    private List<Barrio> barrios;
    private List<Distribuidor> distribuidores;

    public Zona() {
    }

    public Zona(int id, String nombre, List<Barrio> barrios, List<Distribuidor> distribuidores) {
        this.id = id;
        this.nombre = nombre;
        this.barrios = barrios;
        this.distribuidores = distribuidores;
    }
}
