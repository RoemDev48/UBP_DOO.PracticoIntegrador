package com.mycompany.vitalsa.model;

import java.util.List;

/**
 *
 * @author RRDev
*/

// Objeto del dominio que representa a un ClienteParticular dentro del sistema
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