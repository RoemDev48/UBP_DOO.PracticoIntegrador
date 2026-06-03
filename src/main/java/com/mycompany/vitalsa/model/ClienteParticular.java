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
public class ClienteParticular extends Cliente {
    private String nombre;
    private String apellido;
    private TipoDocumento tipoDoc;
    private String nroDoc;

    public ClienteParticular() {
    }

    public ClienteParticular(int id, Direccion direccion, Telefono telefono, List<Pedido> pedidos,
                             String nombre, String apellido, TipoDocumento tipoDoc, String nroDoc) {
        super(id, direccion, telefono, pedidos);
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDoc = tipoDoc;
        this.nroDoc = nroDoc;
    }
}
