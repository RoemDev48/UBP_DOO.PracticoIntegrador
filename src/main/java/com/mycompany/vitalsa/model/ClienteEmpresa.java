/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
 */
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
