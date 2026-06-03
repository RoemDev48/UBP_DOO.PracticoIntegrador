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
public class Pedido {
    private int id;
    private Empleado operador;
    private Cliente cliente;
    private Date fechaRealizacion;
    private EstadoPedido estado; 
    private double montoTotal;
    private Date fechaEntregaEstimada;
    private List<DetallePedido> items;
}
