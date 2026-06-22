package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioServicios {

    private static ArrayList<Servicio> listaServicios = new ArrayList<>();

    static {
        listaServicios.add(new Servicio(
                "Manicure",
                "Limpieza, limado y cuidado básico de uñas de manos.",
                8.00,
                true
        ));

        listaServicios.add(new Servicio(
                "Pedicure",
                "Limpieza, cuidado y arreglo de uñas de pies.",
                10.00,
                true
        ));

        listaServicios.add(new Servicio(
                "Uñas acrílicas",
                "Aplicación de uñas acrílicas con acabado personalizado.",
                18.00,
                true
        ));

        listaServicios.add(new Servicio(
                "Masajes relajantes",
                "Masaje relajante para reducir tensión y estrés.",
                20.00,
                true
        ));
    }

    public static void agregarServicio(Servicio servicio) {
        listaServicios.add(servicio);
    }

    public static ArrayList<Servicio> obtenerServicios() {
        return listaServicios;
    }

    public static ArrayList<Servicio> obtenerServiciosActivos() {
        ArrayList<Servicio> serviciosActivos = new ArrayList<>();

        for (Servicio servicio : listaServicios) {
            if (servicio.isActivo()) {
                serviciosActivos.add(servicio);
            }
        }

        return serviciosActivos;
    }

    public static void actualizarServicio(Servicio servicioSeleccionado, String nombre, String descripcion, double precio) {
        for (Servicio servicio : listaServicios) {
            if (servicio == servicioSeleccionado) {
                servicio.setNombre(nombre);
                servicio.setDescripcion(descripcion);
                servicio.setPrecio(precio);
                return;
            }
        }
    }

    public static void deshabilitarServicio(Servicio servicioSeleccionado) {
        for (Servicio servicio : listaServicios) {
            if (servicio == servicioSeleccionado) {
                servicio.setActivo(false);
                return;
            }
        }
    }

    public static void habilitarServicio(Servicio servicioSeleccionado) {
        for (Servicio servicio : listaServicios) {
            if (servicio == servicioSeleccionado) {
                servicio.setActivo(true);
                return;
            }
        }
    }

    public static boolean existeServicioPorNombre(String nombreServicio) {
        for (Servicio servicio : listaServicios) {
            if (servicio.getNombre().equalsIgnoreCase(nombreServicio)) {
                return true;
            }
        }

        return false;
    }
}