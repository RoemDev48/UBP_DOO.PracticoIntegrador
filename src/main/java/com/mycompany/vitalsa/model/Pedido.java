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
    private Date fechaRealizacion;
    private Date fechaEntregaEstimada;
    private EstadoPedido estado; 
    private Cliente cliente;
    private Empleado operador;
    private List<DetallePedido> items;
}
