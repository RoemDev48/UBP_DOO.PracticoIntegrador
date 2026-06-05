package com.mycompany.vitalsa.dto;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author RRDev
*/

// Usamos las Properties de JavaFX para que la tabla escuche los cambios en vivo (ej: cuando tocamos la cantidad)
public class DetallePedidoDTO {
    private final int productoId;
    private final StringProperty nombre;
    private final DoubleProperty precioUnitario;
    private final IntegerProperty cantidad;
    private final DoubleProperty subtotal;

    public DetallePedidoDTO(int productoId, String nombre, double precioUnitario, int cantidad) {
        this.productoId = productoId;
        this.nombre = new SimpleStringProperty(nombre);
        this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.subtotal = new SimpleDoubleProperty(precioUnitario * cantidad);

        // Auto-actualizar subtotal cuando cambia la cantidad
        this.cantidad.addListener((obs, oldVal, newVal) -> {
            this.subtotal.set(this.precioUnitario.get() * newVal.intValue());
        });
    }

    public int getProductoId() { return productoId; }

    public String getNombre() { return nombre.get(); }
    public StringProperty nombreProperty() { return nombre; }

    public double getPrecioUnitario() { return precioUnitario.get(); }
    public DoubleProperty precioUnitarioProperty() { return precioUnitario; }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }
    public IntegerProperty cantidadProperty() { return cantidad; }

    public double getSubtotal() { return subtotal.get(); }
    public DoubleProperty subtotalProperty() { return subtotal; }
}