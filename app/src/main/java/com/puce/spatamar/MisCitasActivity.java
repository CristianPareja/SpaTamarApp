package com.puce.spatamar;

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

public class MisCitasActivity extends AppCompatActivity {

    private TextView txtSinCitasActuales;
    private TextView txtSinHistorial;

    private LinearLayout contenedorCitasActuales;
    private LinearLayout contenedorHistorialCitas;

    private AppCompatButton btnVolverMisCitas;

    private RequestQueue requestQueue;

    private ArrayList<CitaApi> citasActuales;
    private ArrayList<CitaApi> historialCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_citas);

        txtSinCitasActuales = findViewById(R.id.txtSinCitasActuales);
        txtSinHistorial = findViewById(R.id.txtSinHistorial);

        contenedorCitasActuales = findViewById(R.id.contenedorCitasActuales);
        contenedorHistorialCitas = findViewById(R.id.contenedorHistorialCitas);

        btnVolverMisCitas = findViewById(R.id.btnVolverMisCitas);

        requestQueue = Volley.newRequestQueue(this);

        citasActuales = new ArrayList<>();
        historialCitas = new ArrayList<>();

        btnVolverMisCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarCitasDesdeApi();
    }

    private void cargarCitasDesdeApi() {
        if (!SesionUsuario.haySesionActiva()) {
            Toast.makeText(this, "Debe iniciar sesión para consultar sus citas", Toast.LENGTH_LONG).show();
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

                        citasActuales.clear();
                        historialCitas.clear();

                        for (int i = 0; i < citasJson.length(); i++) {
                            JSONObject citaJson = citasJson.getJSONObject(i);

                            CitaApi cita = new CitaApi(
                                    citaJson.getInt("id_cita"),
                                    citaJson.optString("nombre_cliente", ""),
                                    citaJson.optString("telefono", ""),
                                    citaJson.optString("servicio", ""),
                                    formatearFecha(citaJson.optString("fecha", "")),
                                    formatearHora(citaJson.optString("hora", "")),
                                    citaJson.optString("estado", ""),
                                    citaJson.optString("observaciones", "Sin observaciones")
                            );

                            if (cita.getEstado().equalsIgnoreCase("En curso")) {
                                citasActuales.add(cita);
                            } else {
                                historialCitas.add(cita);
                            }
                        }

                        cargarCitasActuales();
                        cargarHistorialCitas();

                    } catch (JSONException e) {
                        Toast.makeText(
                                MisCitasActivity.this,
                                "Error al leer las citas del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            MisCitasActivity.this,
                            "No se pudo conectar con la API para consultar sus citas",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void cargarCitasActuales() {
        contenedorCitasActuales.removeAllViews();

        if (citasActuales.isEmpty()) {
            txtSinCitasActuales.setVisibility(View.VISIBLE);
            contenedorCitasActuales.setVisibility(View.GONE);
            return;
        }

        txtSinCitasActuales.setVisibility(View.GONE);
        contenedorCitasActuales.setVisibility(View.VISIBLE);

        for (CitaApi cita : citasActuales) {
            LinearLayout tarjeta = crearTarjetaCitaActual(cita);
            contenedorCitasActuales.addView(tarjeta);
        }
    }

    private void cargarHistorialCitas() {
        contenedorHistorialCitas.removeAllViews();

        if (historialCitas.isEmpty()) {
            txtSinHistorial.setVisibility(View.VISIBLE);
            contenedorHistorialCitas.setVisibility(View.GONE);
            return;
        }

        txtSinHistorial.setVisibility(View.GONE);
        contenedorHistorialCitas.setVisibility(View.VISIBLE);

        for (CitaApi cita : historialCitas) {
            TextView tarjeta = crearTarjetaHistorial(cita);
            contenedorHistorialCitas.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCitaActual(CitaApi cita) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(18, 18, 18, 18);
        tarjeta.setBackgroundResource(R.drawable.card_login);

        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametrosTarjeta.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametrosTarjeta);

        TextView informacion = new TextView(this);

        String texto = "Cliente: " + cita.getNombreCliente() + "\n"
                + "Teléfono: " + cita.getTelefono() + "\n"
                + "Servicio: " + cita.getServicio() + "\n"
                + "Fecha: " + cita.getFecha() + "\n"
                + "Hora: " + cita.getHora() + "\n"
                + "Estado: " + cita.getEstado() + "\n"
                + "Observaciones: " + cita.getObservaciones();

        informacion.setText(texto);
        informacion.setTextSize(15);
        informacion.setTextColor(getResources().getColor(android.R.color.black));
        informacion.setPadding(0, 0, 0, 14);

        AppCompatButton btnCancelar = new AppCompatButton(this);
        btnCancelar.setText("Cancelar cita");
        btnCancelar.setTextSize(14);
        btnCancelar.setTextColor(getResources().getColor(android.R.color.white));
        btnCancelar.setAllCaps(false);
        btnCancelar.setBackgroundResource(R.drawable.boton_principal);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoCancelar(cita);
            }
        });

        tarjeta.addView(informacion);
        tarjeta.addView(btnCancelar);

        return tarjeta;
    }

    private TextView crearTarjetaHistorial(CitaApi cita) {
        TextView tarjeta = new TextView(this);

        String informacion = "Cliente: " + cita.getNombreCliente() + "\n"
                + "Teléfono: " + cita.getTelefono() + "\n"
                + "Servicio: " + cita.getServicio() + "\n"
                + "Fecha: " + cita.getFecha() + "\n"
                + "Hora: " + cita.getHora() + "\n"
                + "Estado: " + cita.getEstado() + "\n"
                + "Observaciones: " + cita.getObservaciones();

        tarjeta.setText(informacion);
        tarjeta.setTextSize(15);
        tarjeta.setTextColor(getResources().getColor(android.R.color.black));
        tarjeta.setPadding(18, 18, 18, 18);
        tarjeta.setBackgroundResource(R.drawable.card_login);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametros.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametros);

        return tarjeta;
    }

    private void mostrarDialogoCancelar(CitaApi cita) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar cita")
                .setMessage("Antes de continuar, recuerde que la cancelación de una cita puede realizarse hasta 1 hora antes del horario establecido. ¿Está seguro de que desea cancelar esta cita?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> cancelarCitaApi(cita))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelarCitaApi(CitaApi cita) {
        String url = ApiConfig.URL_CITAS + "/" + cita.getIdCita() + "/cancelar";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                null,
                response -> {
                    Toast.makeText(
                            MisCitasActivity.this,
                            "Cita cancelada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    cargarCitasDesdeApi();
                },
                error -> {
                    Toast.makeText(
                            MisCitasActivity.this,
                            "No se pudo cancelar la cita",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private String formatearFecha(String fechaApi) {
        if (fechaApi == null || fechaApi.isEmpty()) {
            return "";
        }

        if (fechaApi.length() >= 10) {
            String anio = fechaApi.substring(0, 4);
            String mes = fechaApi.substring(5, 7);
            String dia = fechaApi.substring(8, 10);
            return dia + "/" + mes + "/" + anio;
        }

        return fechaApi;
    }

    private String formatearHora(String horaApi) {
        if (horaApi == null || horaApi.isEmpty()) {
            return "";
        }

        if (horaApi.length() >= 5) {
            return horaApi.substring(0, 5);
        }

        return horaApi;
    }

    private static class CitaApi {

        private int idCita;
        private String nombreCliente;
        private String telefono;
        private String servicio;
        private String fecha;
        private String hora;
        private String estado;
        private String observaciones;

        public CitaApi(int idCita,
                       String nombreCliente,
                       String telefono,
                       String servicio,
                       String fecha,
                       String hora,
                       String estado,
                       String observaciones) {

            this.idCita = idCita;
            this.nombreCliente = nombreCliente;
            this.telefono = telefono;
            this.servicio = servicio;
            this.fecha = fecha;
            this.hora = hora;
            this.estado = estado;
            this.observaciones = observaciones;
        }

        public int getIdCita() {
            return idCita;
        }

        public String getNombreCliente() {
            return nombreCliente;
        }

        public String getTelefono() {
            return telefono;
        }

        public String getServicio() {
            return servicio;
        }

        public String getFecha() {
            return fecha;
        }

        public String getHora() {
            return hora;
        }

        public String getEstado() {
            return estado;
        }

        public String getObservaciones() {
            return observaciones;
        }
    }
}