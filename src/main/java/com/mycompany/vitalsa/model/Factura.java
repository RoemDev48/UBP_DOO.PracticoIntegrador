/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vitalsa.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author RRDev
 */
public class Factura {
    private int id;
    private Date fecha;
    private double total;
    private Pedido pedido;
    private List<DetalleFactura> detalles;
}
