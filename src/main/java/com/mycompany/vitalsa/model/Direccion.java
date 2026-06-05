package com.mycompany.vitalsa.model;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un Direccion dentro del sistema
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    
    public int getNumeracion() { return numeracion; }
    public void setNumeracion(int numeracion) { this.numeracion = numeracion; }
    
    public Barrio getBarrio() { return barrio; }
    public void setBarrio(Barrio barrio) { this.barrio = barrio; }
}