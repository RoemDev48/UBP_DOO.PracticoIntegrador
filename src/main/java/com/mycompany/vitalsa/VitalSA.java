/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.vitalsa;
import com.mycompany.vitalsa.controller.DatabaseController;

/**
 *
 * @author RRDev
 */
public class VitalSA {
    public static void main(String[] args) {
        DatabaseController db = new DatabaseController();
        db.conectar(); 
        db.desconectar();
    }
}
