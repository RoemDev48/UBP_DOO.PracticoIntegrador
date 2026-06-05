package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un ClienteEmpresa dentro del sistema
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
}