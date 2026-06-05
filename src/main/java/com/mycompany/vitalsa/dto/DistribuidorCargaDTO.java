package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// Para armar la tablita de camiones en la vista de Logística (con el porcentaje de carga)
public class DistribuidorCargaDTO {
    private int id;
    private String nombre;
    private String zonaCargo;
    private double porcentajeCarga;
    private int pedidosAsignados;
    private int capacidadTotal;
    
    public DistribuidorCargaDTO(int id, String nombre, String zonaCargo, double porcentajeCarga, int pedidosAsignados, int capacidadTotal) {
        this.id = id;
        this.nombre = nombre;
        this.zonaCargo = zonaCargo;
        this.porcentajeCarga = porcentajeCarga;
        this.pedidosAsignados = pedidosAsignados;
        this.capacidadTotal = capacidadTotal;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getZonaCargo() { return zonaCargo; }
    public double getPorcentajeCarga() { return porcentajeCarga; }
    public int getPedidosAsignados() { return pedidosAsignados; }
    public int getCapacidadTotal() { return capacidadTotal; }
}