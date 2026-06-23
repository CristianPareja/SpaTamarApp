package com.puce.spatamar;

public class RepositorioPerfil {

    private static PerfilUsuario perfilUsuario;

    public static void guardarPerfil(PerfilUsuario perfil) {
        perfilUsuario = perfil;
    }

    public static PerfilUsuario obtenerPerfil() {
        return perfilUsuario;
    }

    public static boolean existePerfil() {
        return perfilUsuario != null;
    }

    public static void actualizarPerfil(String nombre, String telefono, String correo) {
        if (perfilUsuario == null) {
            perfilUsuario = new PerfilUsuario(nombre, telefono, correo);
        } else {
            perfilUsuario.setNombre(nombre);
            perfilUsuario.setTelefono(telefono);
            perfilUsuario.setCorreo(correo);
        }
    }

    public static void limpiarPerfil() {
        perfilUsuario = null;
    }
}