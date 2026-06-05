package com.mycompany.vitalsa.dto;

import java.time.LocalDate;
import java.util.List;

public class FacturaCompletaDTO {
    private String skuFactura;
    private LocalDate fechaEmision;
    private String tipoComprobante; // "A" o "B"
    
    private String clienteNombre;
    private String clienteDomicilio;
    private String clienteLocalidad; // O Barrio
    private String condicionIva;
    private String cuitODni;
    
    private List<FacturaItemDTO> items;
    private double total;

    public FacturaCompletaDTO(String skuFactura, LocalDate fechaEmision, String tipoComprobante, 
                              String clienteNombre, String clienteDomicilio, String clienteLocalidad, 
                              String condicionIva, String cuitODni, List<FacturaItemDTO> items, double total) {
        this.skuFactura = skuFactura;
        this.fechaEmision = fechaEmision;
        this.tipoComprobante = tipoComprobante;
        this.clienteNombre = clienteNombre;
        this.clienteDomicilio = clienteDomicilio;
        this.clienteLocalidad = clienteLocalidad;
        this.condicionIva = condicionIva;
        this.cuitODni = cuitODni;
        this.items = items;
        this.total = total;
    }

    public String getSkuFactura() { return skuFactura; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public String getTipoComprobante() { return tipoComprobante; }
    
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteDomicilio() { return clienteDomicilio; }
    public String getClienteLocalidad() { return clienteLocalidad; }
    public String getCondicionIva() { return condicionIva; }
    public String getCuitODni() { return cuitODni; }
    
    public List<FacturaItemDTO> getItems() { return items; }
    public double getTotal() { return total; }
}
