package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// Para la tabla inferior de logística (pedidos que ya están en la calle)
public class EnvioTransitoDTO {
    private int idPedido;
    private String sku;
    private String distribuidorNombre;
    private String zonaRuta;
    private String estado;

    public EnvioTransitoDTO(int idPedido, String sku, String distribuidorNombre, String zonaRuta, String estado) {
        this.idPedido = idPedido;
        this.sku = sku;
        this.distribuidorNombre = distribuidorNombre;
        this.zonaRuta = zonaRuta;
        this.estado = estado;
    }

    public int getIdPedido() { return idPedido; }
    public String getSku() { return sku; }
    public String getDistribuidorNombre() { return distribuidorNombre; }
    public String getZonaRuta() { return zonaRuta; }
    public String getEstado() { return estado; }
}