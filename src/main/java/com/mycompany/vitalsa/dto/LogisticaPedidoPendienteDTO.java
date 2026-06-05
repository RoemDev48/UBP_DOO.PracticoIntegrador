package com.mycompany.vitalsa.dto;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author RRDev
*/

// Para listar los pedidos en la pantalla de Logística (con el checkbox de "seleccionado")
public class LogisticaPedidoPendienteDTO {
    private int id;
    private String sku;
    private String zonaDestino;
    private String direccion;
    private String tiempoEspera;
    private List<String> tags;
    private BooleanProperty seleccionado;

    public LogisticaPedidoPendienteDTO(int id, String sku, String zonaDestino, String direccion, String tiempoEspera, List<String> tags) {
        this.id = id;
        this.sku = sku;
        this.zonaDestino = zonaDestino;
        this.direccion = direccion;
        this.tiempoEspera = tiempoEspera;
        this.tags = tags;
        this.seleccionado = new SimpleBooleanProperty(false);
    }

    public int getId() { return id; }
    public String getSku() { return sku; }
    public String getZonaDestino() { return zonaDestino; }
    public String getDireccion() { return direccion; }
    public String getTiempoEspera() { return tiempoEspera; }
    public List<String> getTags() { return tags; }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }
    public boolean isSeleccionado() { return seleccionado.get(); }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }
}