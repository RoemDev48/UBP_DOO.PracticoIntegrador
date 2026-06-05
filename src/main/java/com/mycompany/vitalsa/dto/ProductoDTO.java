package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// DTO mezclado de Producto y Presentacion (porque en la BD están separados pero en la UI mostramos todo junto)
public class ProductoDTO {
    private int id;
    private String sku;
    private String nombre;
    private String categoria;
    private String precioUnitario;
    private int stockActual;
    private String estado;

    public ProductoDTO(int id, String sku, String nombre, String categoria, String precioUnitario, int stockActual) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioUnitario = precioUnitario;
        this.stockActual = stockActual;
        
        if (stockActual > 20) {
            this.estado = "EN STOCK";
        } else if (stockActual > 0) {
            this.estado = "STOCK BAJO";
        } else {
            this.estado = "SIN STOCK";
        }
    }

    public int getId() { return id; }
    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getPrecioUnitario() { return precioUnitario; }
    public int getStockActual() { return stockActual; }
    public String getEstado() { return estado; }
}