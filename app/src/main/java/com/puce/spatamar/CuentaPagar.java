package com.puce.spatamar;

public class CuentaPagar {

    private String tipoEgreso;
    private String proveedorDetalle;
    private String concepto;
    private String fecha;
    private double valor;
    private String estado;
    private String observacion;

    public CuentaPagar(String tipoEgreso, String proveedorDetalle, String concepto, String fecha, double valor, String estado, String observacion) {
        this.tipoEgreso = tipoEgreso;
        this.proveedorDetalle = proveedorDetalle;
        this.concepto = concepto;
        this.fecha = fecha;
        this.valor = valor;
        this.estado = estado;
        this.observacion = observacion;
    }

    public String getTipoEgreso() {
        return tipoEgreso;
    }

    public String getProveedorDetalle() {
        return proveedorDetalle;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getFecha() {
        return fecha;
    }

    public double getValor() {
        return valor;
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