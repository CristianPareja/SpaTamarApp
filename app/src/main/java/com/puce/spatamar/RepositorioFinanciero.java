package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioFinanciero {

    private static ArrayList<MovimientoFinanciero> listaMovimientos = new ArrayList<>();

    public static void registrarIngreso(String categoria, String concepto, String fecha, double valor, String referencia, String observacion) {
        MovimientoFinanciero movimiento = new MovimientoFinanciero(
                "Ingreso",
                categoria,
                concepto,
                fecha,
                valor,
                referencia,
                observacion
        );

        listaMovimientos.add(movimiento);
    }

    public static void registrarEgreso(String categoria, String concepto, String fecha, double valor, String referencia, String observacion) {
        MovimientoFinanciero movimiento = new MovimientoFinanciero(
                "Egreso",
                categoria,
                concepto,
                fecha,
                valor,
                referencia,
                observacion
        );

        listaMovimientos.add(movimiento);
    }

    public static ArrayList<MovimientoFinanciero> obtenerMovimientos() {
        return listaMovimientos;
    }

    public static double calcularTotalIngresos() {
        double total = 0;

        for (MovimientoFinanciero movimiento : listaMovimientos) {
            if (movimiento.getTipo().equalsIgnoreCase("Ingreso")) {
                total = total + movimiento.getValor();
            }
        }

        return total;
    }

    public static double calcularTotalEgresos() {
        double total = 0;

        for (MovimientoFinanciero movimiento : listaMovimientos) {
            if (movimiento.getTipo().equalsIgnoreCase("Egreso")) {
                total = total + movimiento.getValor();
            }
        }

        return total;
    }

    public static double calcularGananciaNeta() {
        return calcularTotalIngresos() - calcularTotalEgresos();
    }
}