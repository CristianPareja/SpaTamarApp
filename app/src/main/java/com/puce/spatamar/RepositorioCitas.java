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

    public static boolean existeCruceCita(String fecha, String hora) {
        for (Cita cita : listaCitas) {
            if (cita.getFecha().equals(fecha) && cita.getHora().equals(hora)) {
                return true;
            }
        }
        return false;
    }
}