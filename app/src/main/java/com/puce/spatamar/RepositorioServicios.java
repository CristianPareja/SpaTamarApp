package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioServicios {

    private static ArrayList<Servicio> listaServicios = new ArrayList<>();

    static {
        if (listaServicios.isEmpty()) {
            listaServicios.add(new Servicio(1, "Manicure", "Limpieza, limado y cuidado básico de uñas de manos.", 8.00, true));
            listaServicios.add(new Servicio(2, "Pedicure", "Limpieza, cuidado y arreglo de uñas de pies.", 10.00, true));
            listaServicios.add(new Servicio(3, "Uñas acrílicas", "Aplicación de uñas acrílicas con acabado personalizado.", 18.00, true));
            listaServicios.add(new Servicio(4, "Masajes relajantes", "Masaje relajante para reducir tensión y estrés.", 20.00, true));
        }
    }

    public static void guardarServicios(ArrayList<Servicio> servicios) {
        listaServicios.clear();
        listaServicios.addAll(servicios);
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

    public static void agregarServicio(Servicio servicio) {
        listaServicios.add(servicio);
    }

    public static void actualizarServicio(int posicion, Servicio servicioActualizado) {
        if (posicion >= 0 && posicion < listaServicios.size()) {
            listaServicios.set(posicion, servicioActualizado);
        }
    }

    public static Servicio obtenerServicioPorNombre(String nombre) {
        for (Servicio servicio : listaServicios) {
            if (servicio.getNombre().equalsIgnoreCase(nombre)) {
                return servicio;
            }
        }

        return null;
    }
}