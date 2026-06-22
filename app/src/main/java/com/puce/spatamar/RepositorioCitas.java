package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioCitas {

    private static ArrayList<Cita> listaCitas = new ArrayList<>();

    public static void agregarCita(Cita cita) {
        listaCitas.add(cita);
    }

    public static ArrayList<Cita> obtenerCitas() {
        return listaCitas;
    }

    public static ArrayList<Cita> obtenerCitasActuales() {
        ArrayList<Cita> citasActuales = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getEstado().equalsIgnoreCase("En curso")) {
                citasActuales.add(cita);
            }
        }

        return citasActuales;
    }

    public static ArrayList<Cita> obtenerHistorialCitas() {
        ArrayList<Cita> historial = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getEstado().equalsIgnoreCase("Finalizado")
                    || cita.getEstado().equalsIgnoreCase("Cancelado")) {
                historial.add(cita);
            }
        }

        return historial;
    }

    public static ArrayList<Cita> obtenerCitasPorFecha(String fecha) {
        ArrayList<Cita> citasPorFecha = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getFecha().equals(fecha)) {
                citasPorFecha.add(cita);
            }
        }

        return citasPorFecha;
    }

    public static ArrayList<Cita> obtenerCitasPorCliente(String nombreCliente) {
        ArrayList<Cita> citasPorCliente = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getNombreCliente().toLowerCase().contains(nombreCliente.toLowerCase())) {
                citasPorCliente.add(cita);
            }
        }

        return citasPorCliente;
    }

    public static int contarCitasPorFecha(String fecha) {
        int total = 0;

        for (Cita cita : listaCitas) {
            if (cita.getFecha().equals(fecha)) {
                total++;
            }
        }

        return total;
    }

    public static int contarCitasActivas() {
        int total = 0;

        for (Cita cita : listaCitas) {
            if (cita.getEstado().equalsIgnoreCase("En curso")) {
                total++;
            }
        }

        return total;
    }

    public static boolean existeCruceCita(String fecha, String hora) {
        for (Cita cita : listaCitas) {
            if (cita.getFecha().equals(fecha)
                    && cita.getHora().equals(hora)
                    && cita.getEstado().equalsIgnoreCase("En curso")) {
                return true;
            }
        }
        return false;
    }

    public static void cancelarCita(Cita citaSeleccionada) {
        for (Cita cita : listaCitas) {
            if (cita == citaSeleccionada) {
                cita.setEstado("Cancelado");
                return;
            }
        }
    }

    public static void finalizarCita(Cita citaSeleccionada) {
        for (Cita cita : listaCitas) {
            if (cita == citaSeleccionada) {
                cita.setEstado("Finalizado");
                return;
            }
        }
    }
}