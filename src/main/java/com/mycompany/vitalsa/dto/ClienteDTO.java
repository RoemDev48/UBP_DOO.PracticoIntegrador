package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// DTO para llevar la data de un Cliente desde la base hasta la grilla de JavaFX
// También lo reusamos cuando abrimos el modal de edición para rellenar los textfields
public class ClienteDTO {
    private int id;
    private String nombre;
    private String documento;
    private String tipo;
    private String direccion;
    private String telefono;
    
    // Campos de recarga en Modal
    private int numeracion;
    private int zonaId;
    private String tipoTelefono;

    // Nuevos campos para Dashboard Rediseñado
    private String zonaNombre;
    private String ultimoPedido;
    private String estado;

    public ClienteDTO(int id, String nombre, String documento, String tipo, String direccion, String telefono, int numeracion, int zonaId, String tipoTelefono, String zonaNombre, String ultimoPedido, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.tipo = tipo;
        this.direccion = direccion;
        this.telefono = telefono;
        this.numeracion = numeracion;
        this.zonaId = zonaId;
        this.tipoTelefono = tipoTelefono;
        this.zonaNombre = zonaNombre;
        this.ultimoPedido = ultimoPedido;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getTipo() { return tipo; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    
    public int getNumeracion() { return numeracion; }
    public int getZonaId() { return zonaId; }
    public String getTipoTelefono() { return tipoTelefono; }
    
    public String getZonaNombre() { return zonaNombre; }
    public String getUltimoPedido() { return ultimoPedido; }
    public String getEstado() { return estado; }
}