package com.puce.spatamar;

public class MovimientoFinanciero {

    private String tipo;
    private String categoria;
    private String concepto;
    private String fecha;
    private double valor;
    private String referencia;
    private String observacion;

    public MovimientoFinanciero(String tipo, String categoria, String concepto, String fecha, double valor, String referencia, String observacion) {
        this.tipo = tipo;
        this.categoria = categoria;
        this.concepto = concepto;
        this.fecha = fecha;
        this.valor = valor;
        this.referencia = referencia;
        this.observacion = observacion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
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

    public String getReferencia() {
        return referencia;
    }

    public String getObservacion() {
        return observacion;
    }
}