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

    @Override
    protected void onResume() {
        super.onResume();
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
            LinearLayout tarjeta = crearTarjetaHistorial(cita);
            contenedorHistorialCitas.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCitaActual(CitaApi cita) {
        LinearLayout tarjeta = crearBaseTarjeta();

        TextView titulo = crearTextoTitulo(cita.getServicio());
        TextView estado = crearChipEstado(cita.getEstado());
        TextView detalle = crearTextoDetalle(
                "Cliente: " + cita.getNombreCliente() + "\n"
                        + "Teléfono: " + cita.getTelefono() + "\n"
                        + "Fecha: " + cita.getFecha() + "\n"
                        + "Hora: " + cita.getHora() + "\n"
                        + "Observaciones: " + cita.getObservaciones()
        );

        AppCompatButton btnCancelar = new AppCompatButton(this);
        btnCancelar.setText("Cancelar cita");
        btnCancelar.setTextSize(14);
        btnCancelar.setTextColor(getResources().getColor(R.color.rojo_negativo));
        btnCancelar.setAllCaps(false);
        btnCancelar.setBackgroundResource(R.drawable.boton_cerrar_sesion_moderno);

        LinearLayout.LayoutParams parametrosBoton = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        parametrosBoton.setMargins(0, dp(14), 0, 0);
        btnCancelar.setLayoutParams(parametrosBoton);

        btnCancelar.setMinHeight(dp(48));
        btnCancelar.setPadding(dp(12), 0, dp(12), 0);
        parametrosBoton.setMargins(0, 14, 0, 0);
        btnCancelar.setLayoutParams(parametrosBoton);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoCancelar(cita);
            }
        });

        tarjeta.addView(titulo);
        tarjeta.addView(estado);
        tarjeta.addView(detalle);
        tarjeta.addView(btnCancelar);

        return tarjeta;
    }

    private LinearLayout crearTarjetaHistorial(CitaApi cita) {
        LinearLayout tarjeta = crearBaseTarjeta();

        TextView titulo = crearTextoTitulo(cita.getServicio());
        TextView estado = crearChipEstado(cita.getEstado());
        TextView detalle = crearTextoDetalle(
                "Cliente: " + cita.getNombreCliente() + "\n"
                        + "Teléfono: " + cita.getTelefono() + "\n"
                        + "Fecha: " + cita.getFecha() + "\n"
                        + "Hora: " + cita.getHora() + "\n"
                        + "Observaciones: " + cita.getObservaciones()
        );

        tarjeta.addView(titulo);
        tarjeta.addView(estado);
        tarjeta.addView(detalle);

        return tarjeta;
    }

    private LinearLayout crearBaseTarjeta() {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(22, 22, 22, 22);
        tarjeta.setBackgroundResource(R.drawable.card_moderno);
        tarjeta.setElevation(4);

        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametrosTarjeta.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametrosTarjeta);

        return tarjeta;
    }

    private TextView crearTextoTitulo(String textoTitulo) {
        TextView texto = new TextView(this);
        texto.setText(textoTitulo);
        texto.setTextSize(18);
        texto.setTextColor(getResources().getColor(R.color.azul_oscuro_moderno));
        texto.setTypeface(null, android.graphics.Typeface.BOLD);
        texto.setPadding(0, 0, 0, 8);

        return texto;
    }

    private TextView crearTextoDetalle(String textoDetalle) {
        TextView texto = new TextView(this);
        texto.setText(textoDetalle);
        texto.setTextSize(14);
        texto.setTextColor(getResources().getColor(R.color.texto_oscuro_moderno));
        texto.setLineSpacing(4, 1);
        texto.setPadding(0, 12, 0, 0);

        return texto;
    }

    private TextView crearChipEstado(String estadoCita) {
        TextView chip = new TextView(this);
        chip.setText("Estado: " + estadoCita);
        chip.setTextSize(13);
        chip.setTypeface(null, android.graphics.Typeface.BOLD);
        chip.setPadding(14, 8, 14, 8);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametros.setMargins(0, 4, 0, 6);
        chip.setLayoutParams(parametros);

        if (estadoCita.equalsIgnoreCase("Finalizado")) {
            chip.setTextColor(getResources().getColor(R.color.verde_positivo));
            chip.setBackgroundResource(R.drawable.card_moderno_verde);
        } else if (estadoCita.equalsIgnoreCase("Cancelado")) {
            chip.setTextColor(getResources().getColor(R.color.rojo_negativo));
            chip.setBackgroundResource(R.drawable.card_moderno_rojo);
        } else {
            chip.setTextColor(getResources().getColor(R.color.azul_moderno));
            chip.setBackgroundResource(R.drawable.fondo_chip_moderno);
        }

        return chip;
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

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
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