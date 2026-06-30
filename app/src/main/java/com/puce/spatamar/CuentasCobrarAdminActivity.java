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
import java.util.Locale;

public class CuentasCobrarAdminActivity extends AppCompatActivity {

    private TextView txtTotalGeneralCobrar;
    private TextView txtSinCuentasCobrar;

    private LinearLayout contenedorCuentasCobrar;

    private AppCompatButton btnVolverCuentasCobrar;

    private RequestQueue requestQueue;

    private ArrayList<CuentaCobrarApi> listaCuentas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_cobrar_admin);

        txtTotalGeneralCobrar = findViewById(R.id.txtTotalGeneralCobrar);
        txtSinCuentasCobrar = findViewById(R.id.txtSinCuentasCobrar);

        contenedorCuentasCobrar = findViewById(R.id.contenedorCuentasCobrar);

        btnVolverCuentasCobrar = findViewById(R.id.btnVolverCuentasCobrar);

        requestQueue = Volley.newRequestQueue(this);

        listaCuentas = new ArrayList<>();

        cargarCuentasRegistradasApi();

        btnVolverCuentasCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCuentasRegistradasApi();
    }

    private void cargarCuentasRegistradasApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_CUENTAS_COBRAR,
                null,
                response -> {
                    try {
                        JSONArray cuentasJson = response.getJSONArray("cuentas");

                        listaCuentas.clear();

                        double totalGeneralPendiente = 0;

                        for (int i = 0; i < cuentasJson.length(); i++) {
                            JSONObject cuentaJson = cuentasJson.getJSONObject(i);

                            CuentaCobrarApi cuenta = new CuentaCobrarApi(
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

                            listaCuentas.add(cuenta);

                            if (cuenta.getEstado().equalsIgnoreCase("Pendiente")) {
                                totalGeneralPendiente += cuenta.getValorPendiente();
                            }
                        }

                        txtTotalGeneralCobrar.setText(
                                "Total pendiente por cobrar: $" + String.format(Locale.US, "%.2f", totalGeneralPendiente)
                        );

                        mostrarCuentasRegistradas();

                    } catch (JSONException e) {
                        Toast.makeText(
                                CuentasCobrarAdminActivity.this,
                                "Error al leer cuentas por cobrar",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            CuentasCobrarAdminActivity.this,
                            "No se pudo consultar cuentas por cobrar",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarCuentasRegistradas() {
        contenedorCuentasCobrar.removeAllViews();

        if (listaCuentas.isEmpty()) {
            txtSinCuentasCobrar.setVisibility(View.VISIBLE);
            contenedorCuentasCobrar.setVisibility(View.GONE);
            return;
        }

        txtSinCuentasCobrar.setVisibility(View.GONE);
        contenedorCuentasCobrar.setVisibility(View.VISIBLE);

        for (CuentaCobrarApi cuenta : listaCuentas) {
            LinearLayout tarjeta = crearTarjetaCuenta(cuenta);
            contenedorCuentasCobrar.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCuenta(CuentaCobrarApi cuenta) {
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

        String texto = "Cliente: " + cuenta.getNombreCliente() + "\n"
                + "Correo: " + cuenta.getCorreoCliente() + "\n"
                + "Concepto: " + cuenta.getConcepto() + "\n"
                + "Fecha: " + cuenta.getFecha() + "\n"
                + "Valor pendiente: $" + String.format(Locale.US, "%.2f", cuenta.getValorPendiente()) + "\n"
                + "Estado: " + cuenta.getEstado() + "\n"
                + "Observación: " + cuenta.getObservacion();

        informacion.setText(texto);
        informacion.setTextSize(15);
        informacion.setTextColor(getResources().getColor(android.R.color.black));
        informacion.setPadding(0, 0, 0, 14);

        tarjeta.addView(informacion);

        if (cuenta.getEstado().equalsIgnoreCase("Pendiente")) {
            AppCompatButton btnMarcarPagado = new AppCompatButton(this);
            btnMarcarPagado.setText("Marcar como pagado");
            btnMarcarPagado.setTextSize(14);
            btnMarcarPagado.setTextColor(getResources().getColor(android.R.color.white));
            btnMarcarPagado.setAllCaps(false);
            btnMarcarPagado.setBackgroundResource(R.drawable.boton_principal);

            btnMarcarPagado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmarPago(cuenta);
                }
            });

            tarjeta.addView(btnMarcarPagado);
        }

        return tarjeta;
    }

    private void confirmarPago(CuentaCobrarApi cuenta) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar pago")
                .setMessage("¿Está seguro de marcar esta cuenta como pagada? El sistema registrará automáticamente este saldo como ingreso.")
                .setPositiveButton("Sí, marcar pagado", (dialog, which) -> marcarCuentaComoPagadaApi(cuenta))
                .setNegativeButton("No", null)
                .show();
    }

    private void marcarCuentaComoPagadaApi(CuentaCobrarApi cuenta) {
        String url = ApiConfig.URL_CUENTAS_COBRAR + "/" + cuenta.getIdCuentaCobrar() + "/pagar";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                null,
                response -> {
                    Toast.makeText(
                            CuentasCobrarAdminActivity.this,
                            "Cuenta marcada como pagada e ingreso registrado",
                            Toast.LENGTH_SHORT
                    ).show();

                    cargarCuentasRegistradasApi();
                },
                error -> {
                    Toast.makeText(
                            CuentasCobrarAdminActivity.this,
                            "No se pudo marcar la cuenta como pagada",
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

    private static class CuentaCobrarApi {

        private int idCuentaCobrar;
        private int idUsuario;
        private String nombreCliente;
        private String correoCliente;
        private String concepto;
        private String fecha;
        private double valorPendiente;
        private String estado;
        private String observacion;

        public CuentaCobrarApi(int idCuentaCobrar,
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