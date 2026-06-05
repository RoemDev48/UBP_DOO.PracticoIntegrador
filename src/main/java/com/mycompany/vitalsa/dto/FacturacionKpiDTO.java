package com.mycompany.vitalsa.dto;

/**
 *
 * @author RRDev
*/

// Objeto que junta toda la info para las tarjetitas de arriba en Facturación
public class FacturacionKpiDTO {
    private double totalMes;
    private double crecimientoMensualPct;
    private double cuentasPorCobrar;
    private int cantidadPendientes;
    private int pagosVerificados;
    private int pagosTotal;
    private double cobradoHoy;
    
    public FacturacionKpiDTO(double totalMes, double crecimientoMensualPct, double cuentasPorCobrar, int cantidadPendientes, int pagosVerificados, int pagosTotal, double cobradoHoy) {
        this.totalMes = totalMes;
        this.crecimientoMensualPct = crecimientoMensualPct;
        this.cuentasPorCobrar = cuentasPorCobrar;
        this.cantidadPendientes = cantidadPendientes;
        this.pagosVerificados = pagosVerificados;
        this.pagosTotal = pagosTotal;
        this.cobradoHoy = cobradoHoy;
    }

    public double getTotalMes() { return totalMes; }
    public double getCrecimientoMensualPct() { return crecimientoMensualPct; }
    public double getCuentasPorCobrar() { return cuentasPorCobrar; }
    public int getCantidadPendientes() { return cantidadPendientes; }
    public int getPagosVerificados() { return pagosVerificados; }
    public int getPagosTotal() { return pagosTotal; }
    public double getCobradoHoy() { return cobradoHoy; }
}