package com.mycompany.vitalsa.model;

import java.util.List;

public class ClienteEmpresa extends Cliente {
    private String nombreFantasia;
    private String razonSocial;
    private String cuit;

    public ClienteEmpresa() {
    }

    public ClienteEmpresa(int id, Direccion direccion, Telefono telefono, List<Pedido> pedidos,
                          String nombreFantasia, String razonSocial, String cuit) {
        super(id, direccion, telefono, pedidos);
        this.nombreFantasia = nombreFantasia;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
    }

    public String getNombreFantasia() { return nombreFantasia; }
    public void setNombreFantasia(String nombreFantasia) { this.nombreFantasia = nombreFantasia; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }
}
