package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Calendar;
import java.util.Locale;

public class MenuAdministradorActivity extends AppCompatActivity {

    private TextView txtFechaDashboard;
    private TextView txtTotalClientesDashboard;
    private TextView txtCitasHoyDashboard;
    private TextView txtTotalCobroDashboard;
    private TextView txtGananciaNetaDashboard;
    private TextView txtDetalleFinancieroDashboard;
    private TextView txtEstadoFinanciero;
    private TextView txtMensajeEstadoFinanciero;
    private TextView txtAlertasDashboard;
    private TextView txtResumenDashboard;

    private LinearLayout cardClientesDashboard;
    private LinearLayout cardCitasHoyDashboard;
    private LinearLayout cardTotalCobroDashboard;

    private AppCompatButton btnServiciosAdmin;
    private AppCompatButton btnCuentasPagarAdmin;
    private AppCompatButton btnSimulacionProyeccionAdmin;
    private AppCompatButton btnCerrarSesionAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        txtFechaDashboard = findViewById(R.id.txtFechaDashboard);
        txtTotalClientesDashboard = findViewById(R.id.txtTotalClientesDashboard);
        txtCitasHoyDashboard = findViewById(R.id.txtCitasHoyDashboard);
        txtTotalCobroDashboard = findViewById(R.id.txtTotalCobroDashboard);
        txtGananciaNetaDashboard = findViewById(R.id.txtGananciaNetaDashboard);
        txtDetalleFinancieroDashboard = findViewById(R.id.txtDetalleFinancieroDashboard);
        txtEstadoFinanciero = findViewById(R.id.txtEstadoFinanciero);
        txtMensajeEstadoFinanciero = findViewById(R.id.txtMensajeEstadoFinanciero);
        txtAlertasDashboard = findViewById(R.id.txtAlertasDashboard);
        txtResumenDashboard = findViewById(R.id.txtResumenDashboard);

        cardClientesDashboard = findViewById(R.id.cardClientesDashboard);
        cardCitasHoyDashboard = findViewById(R.id.cardCitasHoyDashboard);
        cardTotalCobroDashboard = findViewById(R.id.cardTotalCobroDashboard);

        btnServiciosAdmin = findViewById(R.id.btnServiciosAdmin);
        btnCuentasPagarAdmin = findViewById(R.id.btnCuentasPagarAdmin);
        btnSimulacionProyeccionAdmin = findViewById(R.id.btnSimulacionProyeccionAdmin);
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);

        cargarDashboard();

        cardCitasHoyDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, CitasAdminActivity.class);
                startActivity(intent);
            }
        });

        cardTotalCobroDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, CuentasCobrarAdminActivity.class);
                startActivity(intent);
            }
        });

        cardClientesDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, ClientesActivity.class);
                startActivity(intent);
            }
        });

        btnServiciosAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, ServiciosActivity.class);
                startActivity(intent);
            }
        });

        btnCuentasPagarAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, CuentasPagarAdminActivity.class);
                startActivity(intent);
            }
        });

        btnSimulacionProyeccionAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, SimulacionProyeccionActivity.class);
                startActivity(intent);
            }
        });

        btnCerrarSesionAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SesionUsuario.cerrarSesion();

                Intent intent = new Intent(MenuAdministradorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDashboard();
    }

    private void cargarDashboard() {
        String fechaActual = obtenerFechaActual();

        int totalClientes = RepositorioUsuarios.contarClientesRegistrados();
        int citasHoyEnCurso = RepositorioCitas.contarCitasEnCursoPorFecha(fechaActual);
        double totalCobro = RepositorioCuentasPendientes.calcularTotalGeneralPendiente();

        double totalIngresos = RepositorioFinanciero.calcularTotalIngresos();
        double totalEgresos = RepositorioFinanciero.calcularTotalEgresos();
        double gananciaNeta = RepositorioFinanciero.calcularGananciaNeta();

        txtFechaDashboard.setText("Fecha del panel: " + fechaActual);
        txtCitasHoyDashboard.setText(String.valueOf(citasHoyEnCurso));
        txtTotalCobroDashboard.setText("$" + String.format(Locale.US, "%.2f", totalCobro));
        txtTotalClientesDashboard.setText(String.valueOf(totalClientes));

        txtGananciaNetaDashboard.setText("$" + String.format(Locale.US, "%.2f", gananciaNeta));
        txtDetalleFinancieroDashboard.setText(
                "Ingresos: $" + String.format(Locale.US, "%.2f", totalIngresos)
                        + " | Egresos: $" + String.format(Locale.US, "%.2f", totalEgresos)
        );

        cargarEstadoFinanciero(gananciaNeta, totalIngresos, totalEgresos, totalCobro);
        cargarAlertas(citasHoyEnCurso, totalCobro, totalIngresos, totalEgresos, gananciaNeta);
        cargarResumen(citasHoyEnCurso, gananciaNeta, totalIngresos, totalEgresos);
    }

    private void cargarEstadoFinanciero(double gananciaNeta, double totalIngresos, double totalEgresos, double totalCobro) {
        if (gananciaNeta < 0) {
            txtEstadoFinanciero.setText("🚨 Riesgo financiero alto");
            txtMensajeEstadoFinanciero.setText("Los egresos superan a los ingresos. Se recomienda revisar pagos, gastos y cobros pendientes.");
        } else if (totalIngresos == 0 && totalEgresos > 0) {
            txtEstadoFinanciero.setText("⚠️ Riesgo financiero medio");
            txtMensajeEstadoFinanciero.setText("Existen egresos registrados, pero todavía no hay ingresos suficientes.");
        } else if (totalCobro > totalIngresos && totalCobro > 0) {
            txtEstadoFinanciero.setText("⚠️ Cobros pendientes elevados");
            txtMensajeEstadoFinanciero.setText("Hay valores pendientes por cobrar que podrían afectar el flujo de caja.");
        } else {
            txtEstadoFinanciero.setText("✅ Flujo saludable");
            txtMensajeEstadoFinanciero.setText("El negocio mantiene una ganancia positiva y un flujo financiero estable.");
        }
    }

    private void cargarAlertas(int citasHoyEnCurso, double totalCobro, double totalIngresos, double totalEgresos, double gananciaNeta) {
        String alertas = "";

        if (citasHoyEnCurso > 0) {
            alertas = alertas + "🔔 Tiene " + citasHoyEnCurso + " cita(s) pendiente(s) por atender hoy.\n";
        }

        if (totalCobro > 0) {
            alertas = alertas + "⚠️ Existen $" + String.format(Locale.US, "%.2f", totalCobro) + " pendientes de cobro.\n";
        }

        if (totalEgresos > totalIngresos && totalEgresos > 0) {
            alertas = alertas + "🚨 Los egresos superan los ingresos registrados.\n";
        }

        if (gananciaNeta < 0) {
            alertas = alertas + "🚨 La ganancia neta actual es negativa.\n";
        }

        if (alertas.isEmpty()) {
            alertas = "✅ No existen alertas relevantes. El flujo se mantiene estable.";
        }

        txtAlertasDashboard.setText(alertas.trim());
    }

    private void cargarResumen(int citasHoyEnCurso, double gananciaNeta, double totalIngresos, double totalEgresos) {
        String resumen;

        if (citasHoyEnCurso == 0) {
            resumen = "Para hoy no existen citas pendientes por atender. La ganancia neta actual es de $"
                    + String.format(Locale.US, "%.2f", gananciaNeta)
                    + ", con ingresos por $"
                    + String.format(Locale.US, "%.2f", totalIngresos)
                    + " y egresos por $"
                    + String.format(Locale.US, "%.2f", totalEgresos)
                    + ".";
        } else {
            resumen = "Para hoy existen " + citasHoyEnCurso + " cita(s) pendientes por atender. "
                    + "La ganancia neta actual es de $"
                    + String.format(Locale.US, "%.2f", gananciaNeta)
                    + ", con ingresos por $"
                    + String.format(Locale.US, "%.2f", totalIngresos)
                    + " y egresos por $"
                    + String.format(Locale.US, "%.2f", totalEgresos)
                    + ".";
        }

        txtResumenDashboard.setText(resumen);
    }

    private String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance();

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }
}