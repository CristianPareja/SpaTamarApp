package com.puce.spatamar;

public class Cita {

    private String nombreCliente;
    private String telefono;
    private String servicio;
    private String fecha;
    private String hora;
    private String estado;
    private String observaciones;

    public Cita(String nombreCliente, String telefono, String servicio, String fecha, String hora, String estado, String observaciones) {
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getServicio() {
        return servicio;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}