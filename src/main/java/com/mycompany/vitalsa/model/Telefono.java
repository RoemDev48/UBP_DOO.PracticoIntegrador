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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}