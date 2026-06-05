package com.mycompany.vitalsa.model;

import java.util.List;

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

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public TipoDocumento getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(TipoDocumento tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getNroDoc() { return nroDoc; }
    public void setNroDoc(String nroDoc) { this.nroDoc = nroDoc; }
}
