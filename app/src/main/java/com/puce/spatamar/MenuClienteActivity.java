package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MenuClienteActivity extends AppCompatActivity {

    TextView txtCampanitaRecordatorio;
    TextView txtSaludoCliente;
    TextView txtDescripcionCliente;
    TextView txtFechaCliente;

    AppCompatButton btnServiciosCliente;
    AppCompatButton btnPerfilCliente;
    AppCompatButton btnMisCitasCliente;
    AppCompatButton btnCuentasPendientesCliente;
    AppCompatButton btnCerrarSesionCliente;

    private RequestQueue requestQueue;
    private int citasPendientesApi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);

        txtCampanitaRecordatorio = findViewById(R.id.txtCampanitaRecordatorio);
        txtSaludoCliente = findViewById(R.id.txtSaludoCliente);
        txtDescripcionCliente = findViewById(R.id.txtDescripcionCliente);
        txtFechaCliente = findViewById(R.id.txtFechaCliente);

        btnServiciosCliente = findViewById(R.id.btnServiciosCliente);
        btnPerfilCliente = findViewById(R.id.btnPerfilCliente);
        btnMisCitasCliente = findViewById(R.id.btnMisCitasCliente);
        btnCuentasPendientesCliente = findViewById(R.id.btnCuentasPendientesCliente);
        btnCerrarSesionCliente = findViewById(R.id.btnCerrarSesionCliente);

        requestQueue = Volley.newRequestQueue(this);

        actualizarFechaCliente();
        cargarDatosBienvenida();
        actualizarCampanitaDesdeApi();

        txtCampanitaRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarMensajeCampanita();
            }
        });

        btnServiciosCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, ServiciosClienteActivity.class);
                startActivity(intent);
            }
        });

        btnPerfilCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, PerfilClienteActivity.class);
                startActivity(intent);
            }
        });

        btnMisCitasCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, MisCitasActivity.class);
                startActivity(intent);
            }
        });

        btnCuentasPendientesCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, CuentasPendientesActivity.class);
                startActivity(intent);
            }
        });

        btnCerrarSesionCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SesionUsuario.cerrarSesion();

                Intent intent = new Intent(MenuClienteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        actualizarFechaCliente();
        cargarDatosBienvenida();
        actualizarCampanitaDesdeApi();
    }

    private void actualizarFechaCliente() {
        txtFechaCliente.setText(obtenerFechaActual());
    }

    private String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance(TimeZone.getTimeZone("America/Guayaquil"));

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return String.format(Locale.US, "%02d/%02d/%04d", dia, mes, anio);
    }

    private void cargarDatosBienvenida() {
        String nombreCliente = "cliente";

        if (SesionUsuario.haySesionActiva()) {
            nombreCliente = SesionUsuario.getNombre();

            if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
                nombreCliente = "cliente";
            }
        }

        txtSaludoCliente.setText("Hola, " + nombreCliente);
        txtDescripcionCliente.setText("Agenda tu cita y nosotros nos encargamos de tu cuidado personal.");
    }

    private void actualizarCampanitaDesdeApi() {
        if (!SesionUsuario.haySesionActiva()) {
            citasPendientesApi = 0;
            pintarCampanita();
            return;
        }

        String url = ApiConfig.URL_CITAS_USUARIO + SesionUsuario.getIdUsuario();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray citasJson = response.getJSONArray("citas");

                        int contadorPendientes = 0;

                        for (int i = 0; i < citasJson.length(); i++) {
                            JSONObject citaJson = citasJson.getJSONObject(i);

                            String estado = citaJson.optString("estado", "");

                            if (estado.equalsIgnoreCase("En curso")) {
                                contadorPendientes++;
                            }
                        }

                        citasPendientesApi = contadorPendientes;
                        pintarCampanita();

                    } catch (JSONException e) {
                        citasPendientesApi = 0;
                        pintarCampanita();

                        Toast.makeText(
                                MenuClienteActivity.this,
                                "Error al leer las citas pendientes",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    citasPendientesApi = 0;
                    pintarCampanita();

                    Toast.makeText(
                            MenuClienteActivity.this,
                            "No se pudo actualizar la campanita desde la API",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void pintarCampanita() {
        if (citasPendientesApi > 0) {
            txtCampanitaRecordatorio.setText("🔔 " + citasPendientesApi);
        } else {
            txtCampanitaRecordatorio.setText("🔔");
        }
    }

    private void mostrarMensajeCampanita() {
        if (citasPendientesApi == 0) {
            Toast.makeText(
                    MenuClienteActivity.this,
                    "No tiene citas pendientes.",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    MenuClienteActivity.this,
                    "Tiene " + citasPendientesApi + " cita(s) pendiente(s). Revise la sección Mis citas e historial.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}