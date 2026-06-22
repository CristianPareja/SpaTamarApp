package com.puce.spatamar;

import java.util.ArrayList;

public class RepositorioCuentasPendientes {

    private static ArrayList<CuentaPendiente> listaCuentasPendientes = new ArrayList<>();

    static {
        listaCuentasPendientes.add(new CuentaPendiente(
                "Cliente Prueba",
                "Manicure + diseño",
                "22/06/2026",
                15.00,
                "Pendiente",
                "Servicio fiado pendiente de cancelación"
        ));

        listaCuentasPendientes.add(new CuentaPendiente(
                "Cliente Prueba",
                "Pedicure spa",
                "23/06/2026",
                20.00,
                "Pendiente",
                "Valor pendiente registrado por administración"
        ));
    }

    public static void agregarCuentaPendiente(CuentaPendiente cuentaPendiente) {
        listaCuentasPendientes.add(cuentaPendiente);
    }

    public static ArrayList<CuentaPendiente> obtenerCuentasPendientes() {
        return listaCuentasPendientes;
    }

    public static ArrayList<CuentaPendiente> obtenerCuentasPendientesPorCliente(String nombreCliente) {
        ArrayList<CuentaPendiente> cuentasCliente = new ArrayList<>();

        for (CuentaPendiente cuenta : listaCuentasPendientes) {
            boolean mismoCliente = cuenta.getNombreCliente().equalsIgnoreCase(nombreCliente);
            boolean estaPendiente = cuenta.getEstado().equalsIgnoreCase("Pendiente");

            if (mismoCliente && estaPendiente) {
                cuentasCliente.add(cuenta);
            }
        }

        return cuentasCliente;
    }

    public static double calcularTotalPendientePorCliente(String nombreCliente) {
        double total = 0;

        for (CuentaPendiente cuenta : listaCuentasPendientes) {
            boolean mismoCliente = cuenta.getNombreCliente().equalsIgnoreCase(nombreCliente);
            boolean estaPendiente = cuenta.getEstado().equalsIgnoreCase("Pendiente");

            if (mismoCliente && estaPendiente) {
                total = total + cuenta.getValorPendiente();
            }
        }

        return total;
    }

    public static double calcularTotalGeneralPendiente() {
        double total = 0;

        for (CuentaPendiente cuenta : listaCuentasPendientes) {
            if (cuenta.getEstado().equalsIgnoreCase("Pendiente")) {
                total = total + cuenta.getValorPendiente();
            }
        }

        return total;
    }
}