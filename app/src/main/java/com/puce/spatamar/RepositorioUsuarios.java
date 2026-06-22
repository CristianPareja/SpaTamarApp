package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioUsuarios {

    private static ArrayList<Usuario> listaUsuarios = new ArrayList<>();

    static {
        listaUsuarios.add(new Usuario(
                "Cliente",
                "Prueba",
                "0999999999",
                "cliente@spatamar.com",
                "cliente",
                "1234",
                "cliente"
        ));

        listaUsuarios.add(new Usuario(
                "Administrador",
                "Spa TAMAR",
                "0999999999",
                "admin@spatamar.com",
                "admin",
                "1234",
                "administrador"
        ));
    }

    public static void agregarUsuario(Usuario usuario) {
        listaUsuarios.add(usuario);
    }

    public static Usuario validarLogin(String usuarioOCorreo, String clave) {
        for (Usuario usuario : listaUsuarios) {
            boolean coincideUsuario = usuario.getUsuario().equalsIgnoreCase(usuarioOCorreo);
            boolean coincideCorreo = usuario.getCorreo().equalsIgnoreCase(usuarioOCorreo);
            boolean coincideClave = usuario.getClave().equals(clave);

            if ((coincideUsuario || coincideCorreo) && coincideClave) {
                return usuario;
            }
        }

        return null;
    }

    public static boolean existeUsuario(String usuarioTexto, String correoTexto) {
        for (Usuario usuario : listaUsuarios) {
            boolean mismoUsuario = usuario.getUsuario().equalsIgnoreCase(usuarioTexto);
            boolean mismoCorreo = usuario.getCorreo().equalsIgnoreCase(correoTexto);

            if (mismoUsuario || mismoCorreo) {
                return true;
            }
        }

        return false;
    }

    public static int contarClientesRegistrados() {
        int total = 0;

        for (Usuario usuario : listaUsuarios) {
            if (usuario.getRol().equalsIgnoreCase("cliente")) {
                total++;
            }
        }

        return total;
    }

    public static ArrayList<Usuario> obtenerClientesRegistrados() {
        ArrayList<Usuario> clientes = new ArrayList<>();

        for (Usuario usuario : listaUsuarios) {
            if (usuario.getRol().equalsIgnoreCase("cliente")) {
                clientes.add(usuario);
            }
        }

        return clientes;
    }
}