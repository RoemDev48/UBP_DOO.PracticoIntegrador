package com.mycompany.vitalsa.dto;

public class FacturaItemDTO {
    private String sku;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public FacturaItemDTO(String sku, String descripcion, int cantidad, double precioUnitario, double subtotal) {
        this.sku = sku;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getSku() { return sku; }
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}
