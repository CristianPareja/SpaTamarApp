package com.puce.spatamar;

import android.graphics.Typeface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class CuentasPendientesActivity extends AppCompatActivity {

    private TextView txtClienteCuentaPendiente;
    private TextView txtTotalPendiente;
    private TextView txtSinCuentasPendientes;

    private LinearLayout contenedorCuentasPendientes;

    private AppCompatButton btnVolverCuentasPendientes;

    private RequestQueue requestQueue;
    private ArrayList<CuentaPendienteApi> listaCuentasPendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_pendientes);

        txtClienteCuentaPendiente = findViewById(R.id.txtClienteCuentaPendiente);
        txtTotalPendiente = findViewById(R.id.txtTotalPendiente);
        txtSinCuentasPendientes = findViewById(R.id.txtSinCuentasPendientes);

        contenedorCuentasPendientes = findViewById(R.id.contenedorCuentasPendientes);

        btnVolverCuentasPendientes = findViewById(R.id.btnVolverCuentasPendientes);

        requestQueue = Volley.newRequestQueue(this);
        listaCuentasPendientes = new ArrayList<>();

        btnVolverCuentasPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarCuentasPendientesApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCuentasPendientesApi();
    }

    private void cargarCuentasPendientesApi() {
        if (!SesionUsuario.haySesionActiva()) {
            Toast.makeText(
                    this,
                    "Debe iniciar sesión para consultar sus cuentas pendientes.",
                    Toast.LENGTH_LONG
            ).show();

            txtClienteCuentaPendiente.setText("Cliente: no identificado");
            txtTotalPendiente.setText("Total pendiente: $0.00");
            txtSinCuentasPendientes.setVisibility(View.VISIBLE);
            contenedorCuentasPendientes.setVisibility(View.GONE);
            return;
        }

        txtClienteCuentaPendiente.setText("Cliente: " + SesionUsuario.getNombreCompleto());

        String url = ApiConfig.URL_CUENTAS_COBRAR_USUARIO + SesionUsuario.getIdUsuario();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray cuentasJson = response.getJSONArray("cuentas");

                        listaCuentasPendientes.clear();

                        double totalPendiente = 0;

                        for (int i = 0; i < cuentasJson.length(); i++) {
                            JSONObject cuentaJson = cuentasJson.getJSONObject(i);

                            CuentaPendienteApi cuenta = new CuentaPendienteApi(
                                    cuentaJson.getInt("id_cuenta_cobrar"),
                                    cuentaJson.optInt("id_usuario", 0),
                                    cuentaJson.optString("nombre_cliente", ""),
                                    cuentaJson.optString("correo_cliente", ""),
                                    cuentaJson.optString("concepto", ""),
                                    formatearFecha(cuentaJson.optString("fecha", "")),
                                    cuentaJson.optDouble("valor_pendiente", 0),
                                    cuentaJson.optString("estado", ""),
                                    cuentaJson.optString("observacion", "Sin observación")
                            );

                            if (cuenta.getEstado().equalsIgnoreCase("Pendiente")) {
                                listaCuentasPendientes.add(cuenta);
                                totalPendiente += cuenta.getValorPendiente();
                            }
                        }

                        txtTotalPendiente.setText(
                                "Total pendiente: $" + String.format(Locale.US, "%.2f", totalPendiente)
                        );

                        mostrarCuentasPendientes();

                    } catch (JSONException e) {
                        Toast.makeText(
                                CuentasPendientesActivity.this,
                                "Error al leer cuentas pendientes del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            CuentasPendientesActivity.this,
                            "No se pudo consultar las cuentas pendientes desde la API",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarCuentasPendientes() {
        contenedorCuentasPendientes.removeAllViews();

        if (listaCuentasPendientes.isEmpty()) {
            txtSinCuentasPendientes.setVisibility(View.VISIBLE);
            contenedorCuentasPendientes.setVisibility(View.GONE);
            return;
        }

        txtSinCuentasPendientes.setVisibility(View.GONE);
        contenedorCuentasPendientes.setVisibility(View.VISIBLE);

        for (CuentaPendienteApi cuenta : listaCuentasPendientes) {
            LinearLayout tarjeta = crearTarjetaCuentaPendiente(cuenta);
            contenedorCuentasPendientes.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCuentaPendiente(CuentaPendienteApi cuenta) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(18), dp(18), dp(18), dp(18));
        tarjeta.setBackgroundResource(R.drawable.card_moderno);
        tarjeta.setElevation(dp(4));

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametros.setMargins(0, 0, 0, dp(14));
        tarjeta.setLayoutParams(parametros);

        TextView titulo = new TextView(this);
        titulo.setText(cuenta.getConcepto());
        titulo.setTextSize(17);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(getResources().getColor(R.color.azul_oscuro_moderno));
        titulo.setPadding(0, 0, 0, dp(8));

        TextView valor = new TextView(this);
        valor.setText("$" + String.format(Locale.US, "%.2f", cuenta.getValorPendiente()));
        valor.setTextSize(24);
        valor.setTypeface(null, Typeface.BOLD);
        valor.setTextColor(getResources().getColor(R.color.naranja_alerta));
        valor.setPadding(0, 0, 0, dp(8));

        TextView estado = new TextView(this);
        estado.setText("Estado: " + cuenta.getEstado());
        estado.setTextSize(13);
        estado.setTypeface(null, Typeface.BOLD);
        estado.setTextColor(getResources().getColor(R.color.naranja_alerta));
        estado.setBackgroundResource(R.drawable.card_moderno_naranja);
        estado.setPadding(dp(12), dp(7), dp(12), dp(7));

        LinearLayout.LayoutParams parametrosEstado = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametrosEstado.setMargins(0, 0, 0, dp(10));
        estado.setLayoutParams(parametrosEstado);

        TextView detalle = new TextView(this);
        String informacion = "Fecha: " + cuenta.getFecha() + "\n"
                + "Observación: " + cuenta.getObservacion();

        detalle.setText(informacion);
        detalle.setTextSize(14);
        detalle.setTextColor(getResources().getColor(R.color.texto_oscuro_moderno));
        detalle.setLineSpacing(4, 1);

        tarjeta.addView(titulo);
        tarjeta.addView(valor);
        tarjeta.addView(estado);
        tarjeta.addView(detalle);

        return tarjeta;
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

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private static class CuentaPendienteApi {

        private int idCuentaCobrar;
        private int idUsuario;
        private String nombreCliente;
        private String correoCliente;
        private String concepto;
        private String fecha;
        private double valorPendiente;
        private String estado;
        private String observacion;

        public CuentaPendienteApi(int idCuentaCobrar,
                                  int idUsuario,
                                  String nombreCliente,
                                  String correoCliente,
                                  String concepto,
                                  String fecha,
                                  double valorPendiente,
                                  String estado,
                                  String observacion) {

            this.idCuentaCobrar = idCuentaCobrar;
            this.idUsuario = idUsuario;
            this.nombreCliente = nombreCliente;
            this.correoCliente = correoCliente;
            this.concepto = concepto;
            this.fecha = fecha;
            this.valorPendiente = valorPendiente;
            this.estado = estado;
            this.observacion = observacion;
        }

        public int getIdCuentaCobrar() {
            return idCuentaCobrar;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public String getNombreCliente() {
            return nombreCliente;
        }

        public String getCorreoCliente() {
            return correoCliente;
        }

        public String getConcepto() {
            return concepto;
        }

        public String getFecha() {
            return fecha;
        }

        public double getValorPendiente() {
            return valorPendiente;
        }

        public String getEstado() {
            return estado;
        }

        public String getObservacion() {
            return observacion;
        }
    }
}