package com.mycompany.vitalsa.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author RRDev
*/

// DTO que usa Properties para la tabla de pedidos (así se actualiza automático en JavaFX)
public class PedidoDTO {
    private final StringProperty sku;
    private final StringProperty cliente;
    private final StringProperty fecha;
    private final StringProperty estado;
    private final StringProperty total;
    private final int idOriginal;

    public PedidoDTO(int idOriginal, String sku, String cliente, String fecha, String estado, String total) {
        this.idOriginal = idOriginal;
        this.sku = new SimpleStringProperty(sku);
        this.cliente = new SimpleStringProperty(cliente);
        this.fecha = new SimpleStringProperty(fecha);
        this.estado = new SimpleStringProperty(estado);
        this.total = new SimpleStringProperty(total);
    }

    public int getIdOriginal() { return idOriginal; }
    
    public String getSku() { return sku.get(); }
    public StringProperty skuProperty() { return sku; }

    public String getCliente() { return cliente.get(); }
    public StringProperty clienteProperty() { return cliente; }

    public String getFecha() { return fecha.get(); }
    public StringProperty fechaProperty() { return fecha; }

    public String getEstado() { return estado.get(); }
    public StringProperty estadoProperty() { return estado; }

    public String getTotal() { return total.get(); }
    public StringProperty totalProperty() { return total; }
}