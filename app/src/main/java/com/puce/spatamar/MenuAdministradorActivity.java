package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MenuAdministradorActivity extends AppCompatActivity {

    private TextView txtFechaDashboard;
    private TextView txtCampanitaAdmin;

    private TextView txtTotalClientesDashboard;
    private TextView txtCitasHoyDashboard;
    private TextView txtTotalCobroDashboard;
    private TextView txtGananciaNetaDashboard;
    private TextView txtDetalleFinancieroDashboard;
    private TextView txtEstadoFinanciero;
    private TextView txtMensajeEstadoFinanciero;

    private TextView txtServicioMasUtilizado;
    private TextView txtServicioMenosUtilizado;

    private GraficoPastelServiciosView graficoPastelServicios;

    private LinearLayout cardClientesDashboard;
    private LinearLayout cardCitasHoyDashboard;
    private LinearLayout cardTotalCobroDashboard;

    private AppCompatButton btnServiciosAdmin;
    private AppCompatButton btnCuentasPagarAdmin;
    private AppCompatButton btnSimulacionProyeccionAdmin;
    private AppCompatButton btnCerrarSesionAdmin;

    private RequestQueue requestQueue;

    private String textoPendientesAdmin = "No existen pendientes relevantes hasta ahora.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        txtFechaDashboard = findViewById(R.id.txtFechaDashboard);
        txtCampanitaAdmin = findViewById(R.id.txtCampanitaAdmin);

        txtTotalClientesDashboard = findViewById(R.id.txtTotalClientesDashboard);
        txtCitasHoyDashboard = findViewById(R.id.txtCitasHoyDashboard);
        txtTotalCobroDashboard = findViewById(R.id.txtTotalCobroDashboard);
        txtGananciaNetaDashboard = findViewById(R.id.txtGananciaNetaDashboard);
        txtDetalleFinancieroDashboard = findViewById(R.id.txtDetalleFinancieroDashboard);
        txtEstadoFinanciero = findViewById(R.id.txtEstadoFinanciero);
        txtMensajeEstadoFinanciero = findViewById(R.id.txtMensajeEstadoFinanciero);

        txtServicioMasUtilizado = findViewById(R.id.txtServicioMasUtilizado);
        txtServicioMenosUtilizado = findViewById(R.id.txtServicioMenosUtilizado);

        graficoPastelServicios = findViewById(R.id.graficoPastelServicios);

        cardClientesDashboard = findViewById(R.id.cardClientesDashboard);
        cardCitasHoyDashboard = findViewById(R.id.cardCitasHoyDashboard);
        cardTotalCobroDashboard = findViewById(R.id.cardTotalCobroDashboard);

        btnServiciosAdmin = findViewById(R.id.btnServiciosAdmin);
        btnCuentasPagarAdmin = findViewById(R.id.btnCuentasPagarAdmin);
        btnSimulacionProyeccionAdmin = findViewById(R.id.btnSimulacionProyeccionAdmin);
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);

        requestQueue = Volley.newRequestQueue(this);

        cargarDashboardApi();

        txtCampanitaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPendientesAdmin();
            }
        });

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
        txtFechaDashboard.setText(fechaActual);

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

                        if (gananciaNeta > 0) {
                            txtGananciaNetaDashboard.setTextColor(getResources().getColor(R.color.verde_positivo));
                        } else if (gananciaNeta < 0) {
                            txtGananciaNetaDashboard.setTextColor(getResources().getColor(R.color.rojo_negativo));
                        } else {
                            txtGananciaNetaDashboard.setTextColor(getResources().getColor(R.color.azul_titulo));
                        }

                        txtDetalleFinancieroDashboard.setText(
                                "Ingresos: $" + String.format(Locale.US, "%.2f", totalIngresos)
                                        + " | Egresos: $" + String.format(Locale.US, "%.2f", totalEgresos)
                        );

                        cargarEstadisticasServicios(response);
                        cargarGraficoPastelServicios(response);

                        cargarEstadoFinanciero(gananciaNeta, totalIngresos, totalEgresos, totalPorCobrar);
                        actualizarPendientesAdmin(citasHoy, totalPorCobrar, totalIngresos, totalEgresos, gananciaNeta);

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

    private void cargarEstadisticasServicios(JSONObject response) {
        try {
            JSONObject estadisticas = response.getJSONObject("estadisticas_servicios");

            JSONObject masUtilizado = estadisticas.getJSONObject("servicio_mas_utilizado");
            JSONObject menosUtilizado = estadisticas.getJSONObject("servicio_menos_utilizado");

            String nombreMasUtilizado = masUtilizado.getString("nombre");
            int totalUsosMas = masUtilizado.getInt("total_usos");

            String nombreMenosUtilizado = menosUtilizado.getString("nombre");
            int totalUsosMenos = menosUtilizado.getInt("total_usos");

            txtServicioMasUtilizado.setText(
                    "Más utilizado: " + nombreMasUtilizado + " (" + totalUsosMas + " uso(s))"
            );

            txtServicioMenosUtilizado.setText(
                    "Menos utilizado: " + nombreMenosUtilizado + " (" + totalUsosMenos + " uso(s))"
            );

        } catch (JSONException e) {
            txtServicioMasUtilizado.setText("Más utilizado: Sin datos");
            txtServicioMenosUtilizado.setText("Menos utilizado: Sin datos");
        }
    }

    private void cargarGraficoPastelServicios(JSONObject response) {
        try {
            JSONObject estadisticas = response.getJSONObject("estadisticas_servicios");
            JSONArray ranking = estadisticas.getJSONArray("ranking_servicios");

            ArrayList<GraficoPastelServiciosView.ItemPastel> datosPastel = new ArrayList<>();

            for (int i = 0; i < ranking.length(); i++) {
                JSONObject item = ranking.getJSONObject(i);

                String nombreServicio = item.optString("servicio", "Servicio");
                double totalIngresos = item.optDouble("total_ingresos", 0);

                if (totalIngresos > 0) {
                    datosPastel.add(
                            new GraficoPastelServiciosView.ItemPastel(
                                    nombreServicio,
                                    (float) totalIngresos
                            )
                    );
                }
            }

            graficoPastelServicios.setDatos(datosPastel);

        } catch (Exception e) {
            graficoPastelServicios.setDatos(new ArrayList<>());
        }
    }

    private void cargarDashboardVacio() {
        txtCitasHoyDashboard.setText("0");
        txtTotalCobroDashboard.setText("$0.00");
        txtTotalClientesDashboard.setText("0");
        txtGananciaNetaDashboard.setText("$0.00");
        txtGananciaNetaDashboard.setTextColor(getResources().getColor(R.color.azul_titulo));
        txtDetalleFinancieroDashboard.setText("Ingresos: $0.00 | Egresos: $0.00");

        txtEstadoFinanciero.setText("Sin conexión con API");
        txtMensajeEstadoFinanciero.setText("No fue posible consultar el resumen financiero desde PostgreSQL.");

        txtServicioMasUtilizado.setText("Más utilizado: Sin datos");
        txtServicioMenosUtilizado.setText("Menos utilizado: Sin datos");

        txtCampanitaAdmin.setText("🔔");
        textoPendientesAdmin = "No se pudo consultar la información de pendientes.";
        graficoPastelServicios.setDatos(new ArrayList<>());
    }

    private void cargarEstadoFinanciero(double gananciaNeta,
                                        double totalIngresos,
                                        double totalEgresos,
                                        double totalPorCobrar) {

        if (gananciaNeta < 0) {
            txtEstadoFinanciero.setText("Riesgo financiero alto");
            txtEstadoFinanciero.setTextColor(getResources().getColor(R.color.rojo_negativo));
            txtMensajeEstadoFinanciero.setText("Los egresos superan a los ingresos. Se recomienda revisar pagos, gastos y cobros pendientes.");
        } else if (totalIngresos == 0 && totalEgresos > 0) {
            txtEstadoFinanciero.setText("Riesgo financiero medio");
            txtEstadoFinanciero.setTextColor(getResources().getColor(R.color.naranja_alerta));
            txtMensajeEstadoFinanciero.setText("Existen egresos registrados, pero todavía no hay ingresos suficientes.");
        } else if (totalPorCobrar > totalIngresos && totalPorCobrar > 0) {
            txtEstadoFinanciero.setText("Cobros pendientes elevados");
            txtEstadoFinanciero.setTextColor(getResources().getColor(R.color.naranja_alerta));
            txtMensajeEstadoFinanciero.setText("Hay valores pendientes por cobrar que podrían afectar el flujo de caja.");
        } else {
            txtEstadoFinanciero.setText("Flujo saludable");
            txtEstadoFinanciero.setTextColor(getResources().getColor(R.color.verde_positivo));
            txtMensajeEstadoFinanciero.setText("El negocio mantiene una ganancia positiva y un flujo financiero estable.");
        }
    }

    private void actualizarPendientesAdmin(int citasHoy,
                                           double totalPorCobrar,
                                           double totalIngresos,
                                           double totalEgresos,
                                           double gananciaNeta) {

        String pendientes = "";

        if (citasHoy > 0) {
            pendientes = pendientes + "• " + citasHoy + " cita(s) pendiente(s) por atender hoy.\n";
        }

        if (totalPorCobrar > 0) {
            pendientes = pendientes + "• $" + String.format(Locale.US, "%.2f", totalPorCobrar)
                    + " pendientes por cobrar.\n";
        }

        if (totalEgresos > totalIngresos && totalEgresos > 0) {
            pendientes = pendientes + "• Los egresos superan los ingresos registrados.\n";
        }

        if (gananciaNeta < 0) {
            pendientes = pendientes + "• La ganancia neta actual es negativa.\n";
        }

        if (pendientes.isEmpty()) {
            pendientes = "No existen pendientes relevantes hasta ahora.";
            txtCampanitaAdmin.setText("🔔");
        } else {
            txtCampanitaAdmin.setText("🔔 !");
        }

        textoPendientesAdmin = pendientes.trim();
    }

    private void mostrarPendientesAdmin() {
        new AlertDialog.Builder(this)
                .setTitle("Pendientes del panel")
                .setMessage(textoPendientesAdmin)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance();

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }
}