package com.puce.spatamar;

import java.util.ArrayList;
import java.util.Calendar;

public class RepositorioCuentasPendientes {

    private static ArrayList<CuentaPendiente> listaCuentasPendientes = new ArrayList<>();

    static {
        listaCuentasPendientes.add(new CuentaPendiente(
                "Cliente Prueba",
                "cliente",
                "cliente@spatamar.com",
                "Manicure + diseño",
                "22/06/2026",
                15.00,
                "Pendiente",
                "Servicio fiado pendiente de cancelación"
        ));

        listaCuentasPendientes.add(new CuentaPendiente(
                "Cliente Prueba",
                "cliente",
                "cliente@spatamar.com",
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

    public static ArrayList<CuentaPendiente> obtenerCuentasPendientesPorCorreo(String correoCliente) {
        ArrayList<CuentaPendiente> cuentasCliente = new ArrayList<>();

        for (CuentaPendiente cuenta : listaCuentasPendientes) {
            boolean mismoCorreo = cuenta.getCorreoCliente().equalsIgnoreCase(correoCliente);
            boolean estaPendiente = cuenta.getEstado().equalsIgnoreCase("Pendiente");

            if (mismoCorreo && estaPendiente) {
                cuentasCliente.add(cuenta);
            }
        }

        return cuentasCliente;
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

    public static double calcularTotalPendientePorCorreo(String correoCliente) {
        double total = 0;

        for (CuentaPendiente cuenta : listaCuentasPendientes) {
            boolean mismoCorreo = cuenta.getCorreoCliente().equalsIgnoreCase(correoCliente);
            boolean estaPendiente = cuenta.getEstado().equalsIgnoreCase("Pendiente");

            if (mismoCorreo && estaPendiente) {
                total = total + cuenta.getValorPendiente();
            }
        }

        return total;
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

    public static void marcarComoPagado(CuentaPendiente cuentaSeleccionada) {
        for (CuentaPendiente cuenta : listaCuentasPendientes) {
            if (cuenta == cuentaSeleccionada && cuenta.getEstado().equalsIgnoreCase("Pendiente")) {
                cuenta.setEstado("Pagado");

                RepositorioFinanciero.registrarIngreso(
                        "Cuenta por cobrar",
                        cuenta.getConcepto(),
                        obtenerFechaActual(),
                        cuenta.getValorPendiente(),
                        cuenta.getNombreCliente(),
                        "Pago registrado desde cuentas por cobrar"
                );

                return;
            }
        }
    }

    private static String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance();

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }
}