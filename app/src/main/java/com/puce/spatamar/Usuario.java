package com.puce.spatamar;

public class Usuario {

    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String usuario;
    private String clave;
    private String rol;

    public Usuario(String nombre, String apellido, String telefono, String correo, String usuario, String clave, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getClave() {
        return clave;
    }

    public String getRol() {
        return rol;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}