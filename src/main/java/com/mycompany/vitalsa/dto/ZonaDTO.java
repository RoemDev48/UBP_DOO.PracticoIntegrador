package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// DTO chiquito solo para llenar los combobox de zonas sin traernos todo el objeto complejo
public class ZonaDTO {
    private int id;
    private String nombre;

    public ZonaDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre; // Esto es lo que mostrará el ComboBox visualmente
    }
}