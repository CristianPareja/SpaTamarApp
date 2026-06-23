package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

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

    private RequestQueue requestQueue;

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

        requestQueue = Volley.newRequestQueue(this);

        cargarDashboardApi();

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
        cargarDashboardApi();
    }

    private void cargarDashboardApi() {
        String fechaActual = obtenerFechaActual();
        txtFechaDashboard.setText("Fecha del panel: " + fechaActual);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_FINANZAS_RESUMEN,
                null,
                response -> {
                    try {
                        double totalIngresos = response.getDouble("ingresos");
                        double totalEgresos = response.getDouble("egresos");
                        double gananciaNeta = response.getDouble("ganancia_neta");
                        double totalPorCobrar = response.getDouble("total_por_cobrar");

                        int citasHoy = response.getInt("citas_hoy");
                        int clientesRegistrados = response.getInt("clientes_registrados");

                        txtCitasHoyDashboard.setText(String.valueOf(citasHoy));
                        txtTotalCobroDashboard.setText("$" + String.format(Locale.US, "%.2f", totalPorCobrar));
                        txtTotalClientesDashboard.setText(String.valueOf(clientesRegistrados));

                        txtGananciaNetaDashboard.setText("$" + String.format(Locale.US, "%.2f", gananciaNeta));
                        txtDetalleFinancieroDashboard.setText(
                                "Ingresos: $" + String.format(Locale.US, "%.2f", totalIngresos)
                                        + " | Egresos: $" + String.format(Locale.US, "%.2f", totalEgresos)
                        );

                        cargarEstadoFinanciero(gananciaNeta, totalIngresos, totalEgresos, totalPorCobrar);
                        cargarAlertas(citasHoy, totalPorCobrar, totalIngresos, totalEgresos, gananciaNeta);
                        cargarResumen(citasHoy, gananciaNeta, totalIngresos, totalEgresos);

                    } catch (JSONException e) {
                        Toast.makeText(
                                MenuAdministradorActivity.this,
                                "Error al leer el resumen financiero",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            MenuAdministradorActivity.this,
                            "No se pudo cargar el dashboard desde la API",
                            Toast.LENGTH_LONG
                    ).show();

                    cargarDashboardVacio();
                }
        );

        requestQueue.add(request);
    }

    private void cargarDashboardVacio() {
        txtCitasHoyDashboard.setText("0");
        txtTotalCobroDashboard.setText("$0.00");
        txtTotalClientesDashboard.setText("0");
        txtGananciaNetaDashboard.setText("$0.00");
        txtDetalleFinancieroDashboard.setText("Ingresos: $0.00 | Egresos: $0.00");

        txtEstadoFinanciero.setText("⚠️ Sin conexión con API");
        txtMensajeEstadoFinanciero.setText("No fue posible consultar el resumen financiero desde PostgreSQL.");

        txtAlertasDashboard.setText("Revise que el backend esté encendido con npm run dev.");
        txtResumenDashboard.setText("No se pudo cargar el resumen del día desde la API.");
    }

    private void cargarEstadoFinanciero(double gananciaNeta,
                                        double totalIngresos,
                                        double totalEgresos,
                                        double totalPorCobrar) {

        if (gananciaNeta < 0) {
            txtEstadoFinanciero.setText("🚨 Riesgo financiero alto");
            txtMensajeEstadoFinanciero.setText("Los egresos superan a los ingresos. Se recomienda revisar pagos, gastos y cobros pendientes.");
        } else if (totalIngresos == 0 && totalEgresos > 0) {
            txtEstadoFinanciero.setText("⚠️ Riesgo financiero medio");
            txtMensajeEstadoFinanciero.setText("Existen egresos registrados, pero todavía no hay ingresos suficientes.");
        } else if (totalPorCobrar > totalIngresos && totalPorCobrar > 0) {
            txtEstadoFinanciero.setText("⚠️ Cobros pendientes elevados");
            txtMensajeEstadoFinanciero.setText("Hay valores pendientes por cobrar que podrían afectar el flujo de caja.");
        } else {
            txtEstadoFinanciero.setText("✅ Flujo saludable");
            txtMensajeEstadoFinanciero.setText("El negocio mantiene una ganancia positiva y un flujo financiero estable.");
        }
    }

    private void cargarAlertas(int citasHoy,
                               double totalPorCobrar,
                               double totalIngresos,
                               double totalEgresos,
                               double gananciaNeta) {

        String alertas = "";

        if (citasHoy > 0) {
            alertas = alertas + "🔔 Tiene " + citasHoy + " cita(s) pendiente(s) por atender hoy.\n";
        }

        if (totalPorCobrar > 0) {
            alertas = alertas + "⚠️ Existen $" + String.format(Locale.US, "%.2f", totalPorCobrar) + " pendientes de cobro.\n";
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

    private void cargarResumen(int citasHoy,
                               double gananciaNeta,
                               double totalIngresos,
                               double totalEgresos) {

        String resumen;

        if (citasHoy == 0) {
            resumen = "Para hoy no existen citas pendientes por atender. La ganancia neta actual es de $"
                    + String.format(Locale.US, "%.2f", gananciaNeta)
                    + ", con ingresos por $"
                    + String.format(Locale.US, "%.2f", totalIngresos)
                    + " y egresos por $"
                    + String.format(Locale.US, "%.2f", totalEgresos)
                    + ".";
        } else {
            resumen = "Para hoy existen " + citasHoy + " cita(s) pendientes por atender. "
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