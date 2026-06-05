package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// DTO simple para llenar la tabla principal de facturas
public class FacturaRecienteDTO {
    private int id;
    private String sku;
    private String clienteNombre;
    private String estado;
    
    public FacturaRecienteDTO(int id, String sku, String clienteNombre, String estado) {
        this.id = id;
        this.sku = sku;
        this.clienteNombre = clienteNombre;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getSku() { return sku; }
    public String getClienteNombre() { return clienteNombre; }
    public String getEstado() { return estado; }
}