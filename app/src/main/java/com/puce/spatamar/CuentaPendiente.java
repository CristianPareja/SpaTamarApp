package com.puce.spatamar;

public class CuentaPendiente {

    private String nombreCliente;
    private String concepto;
    private String fecha;
    private double valorPendiente;
    private String estado;
    private String observacion;

    public CuentaPendiente(String nombreCliente, String concepto, String fecha, double valorPendiente, String estado, String observacion) {
        this.nombreCliente = nombreCliente;
        this.concepto = concepto;
        this.fecha = fecha;
        this.valorPendiente = valorPendiente;
        this.estado = estado;
        this.observacion = observacion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getFecha() {
        return fecha;
    }

    public double getValorPendiente() {
        return valorPendiente;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}