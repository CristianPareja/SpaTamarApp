package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioCuentasPagar {

    private static ArrayList<CuentaPagar> listaCuentasPagar = new ArrayList<>();

    public static void agregarCuentaPagar(CuentaPagar cuentaPagar) {
        listaCuentasPagar.add(cuentaPagar);

        RepositorioFinanciero.registrarEgreso(
                cuentaPagar.getTipoEgreso(),
                cuentaPagar.getConcepto(),
                cuentaPagar.getFecha(),
                cuentaPagar.getValor(),
                cuentaPagar.getProveedorDetalle(),
                cuentaPagar.getObservacion()
        );
    }

    public static ArrayList<CuentaPagar> obtenerCuentasPagar() {
        return listaCuentasPagar;
    }

    public static double calcularTotalCuentasPagar() {
        double total = 0;

        for (CuentaPagar cuenta : listaCuentasPagar) {
            total = total + cuenta.getValor();
        }

        return total;
    }
}