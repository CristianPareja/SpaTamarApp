package com.puce.spatamar;

public class SesionUsuario {

    private static int idUsuario = 0;
    private static String nombre = "";
    private static String apellido = "";
    private static String telefono = "";
    private static String correo = "";
    private static String usuario = "";
    private static String rol = "";

    public static void iniciarSesion(int idUsuarioRecibido,
                                     String nombreRecibido,
                                     String apellidoRecibido,
                                     String telefonoRecibido,
                                     String correoRecibido,
                                     String usuarioRecibido,
                                     String rolRecibido) {

        idUsuario = idUsuarioRecibido;
        nombre = nombreRecibido;
        apellido = apellidoRecibido;
        telefono = telefonoRecibido;
        correo = correoRecibido;
        usuario = usuarioRecibido;
        rol = rolRecibido;
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static String getNombre() {
        return nombre;
    }

    public static String getApellido() {
        return apellido;
    }

    public static String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public static String getTelefono() {
        return telefono;
    }

    public static String getCorreo() {
        return correo;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getRol() {
        return rol;
    }

    public static boolean haySesionActiva() {
        return idUsuario > 0;
    }

    public static void cerrarSesion() {
        idUsuario = 0;
        nombre = "";
        apellido = "";
        telefono = "";
        correo = "";
        usuario = "";
        rol = "";
        RepositorioPerfil.limpiarPerfil();
    }
}