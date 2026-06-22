package com.puce.spatamar;

public class RepositorioPerfil {

    private static PerfilUsuario perfilActual;

    public static void registrarPerfilInicial(PerfilUsuario perfil) {
        perfilActual = perfil;
    }

    public static PerfilUsuario obtenerPerfil() {
        return perfilActual;
    }

    public static boolean existePerfil() {
        return perfilActual != null;
    }

    public static boolean actualizarPerfil(String nombre, String telefono, String correo) {
        if (perfilActual == null) {
            return false;
        }

        perfilActual.setNombre(nombre);
        perfilActual.setTelefono(telefono);
        perfilActual.setCorreo(correo);

        return true;
    }
}