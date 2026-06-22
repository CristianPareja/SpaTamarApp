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
}