package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Locale;

public class CuentasPagarAdminActivity extends AppCompatActivity {

    private Spinner spinnerTipoEgreso;

    private EditText edtFechaPagar;
    private EditText edtValorPagar;
    private EditText edtObservacionPagar;

    private TextView txtTotalCuentasPagar;
    private TextView txtSinCuentasPagar;

    private LinearLayout contenedorCuentasPagar;

    private AppCompatButton btnRegistrarCuentaPagar;
    private AppCompatButton btnVolverCuentasPagar;

    private RequestQueue requestQueue;

    private ArrayList<CuentaPagarApi> listaCuentasPagar;

    private int anioSeleccionado = -1;
    private int mesSeleccionado = -1;
    private int diaSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_pagar_admin);

        spinnerTipoEgreso = findViewById(R.id.spinnerTipoEgreso);

        edtFechaPagar = findViewById(R.id.edtFechaPagar);
        edtValorPagar = findViewById(R.id.edtValorPagar);
        edtObservacionPagar = findViewById(R.id.edtObservacionPagar);

        txtTotalCuentasPagar = findViewById(R.id.txtTotalCuentasPagar);
        txtSinCuentasPagar = findViewById(R.id.txtSinCuentasPagar);

        contenedorCuentasPagar = findViewById(R.id.contenedorCuentasPagar);

        btnRegistrarCuentaPagar = findViewById(R.id.btnRegistrarCuentaPagar);
        btnVolverCuentasPagar = findViewById(R.id.btnVolverCuentasPagar);

        requestQueue = Volley.newRequestQueue(this);
        listaCuentasPagar = new ArrayList<>();

        cargarTiposEgreso();
        cargarCuentasPagarApi();

        edtFechaPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
            }
        });

        btnRegistrarCuentaPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarYRegistrarCuentaPagarApi();
            }
        });

        btnVolverCuentasPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCuentasPagarApi();
    }

    private void cargarTiposEgreso() {
        ArrayList<String> tipos = new ArrayList<>();
        tipos.add("Seleccione tipo de egreso");
        tipos.add("Proveedor");
        tipos.add("Arriendo");
        tipos.add("Servicio básico");
        tipos.add("Insumos");
        tipos.add("Mantenimiento");
        tipos.add("Otro");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoEgreso.setAdapter(adapter);
    }

    private void mostrarCalendario() {
        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog selectorFecha = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    anioSeleccionado = year;
                    mesSeleccionado = month;
                    diaSeleccionado = dayOfMonth;

                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtFechaPagar.setText(fechaSeleccionada);
                },
                anio,
                mes,
                dia
        );

        selectorFecha.show();
    }

    private void validarYRegistrarCuentaPagarApi() {
        int posicionTipo = spinnerTipoEgreso.getSelectedItemPosition();

        String fechaVisual = edtFechaPagar.getText().toString().trim();
        String valorTexto = edtValorPagar.getText().toString().trim();
        String observacion = edtObservacionPagar.getText().toString().trim();

        if (posicionTipo == 0) {
            Toast.makeText(this, "Seleccione el tipo de egreso", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaVisual.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            edtValorPagar.setError("Ingrese el valor a pagar");
            edtValorPagar.requestFocus();
            return;
        }

        double valor;

        try {
            valor = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            edtValorPagar.setError("Ingrese un valor válido");
            edtValorPagar.requestFocus();
            return;
        }

        if (valor <= 0) {
            edtValorPagar.setError("El valor debe ser mayor a cero");
            edtValorPagar.requestFocus();
            return;
        }

        if (observacion.isEmpty()) {
            observacion = "Sin observación";
        }

        String tipoEgreso = spinnerTipoEgreso.getSelectedItem().toString();

        registrarCuentaPagarApi(
                tipoEgreso,
                obtenerFechaFormatoApi(),
                valor,
                observacion
        );
    }

    private void registrarCuentaPagarApi(String tipoEgreso,
                                         String fechaApi,
                                         double valor,
                                         String observacion) {

        JSONObject datosCuenta = new JSONObject();

        try {
            datosCuenta.put("tipo_egreso", tipoEgreso);
            datosCuenta.put("fecha", fechaApi);
            datosCuenta.put("valor", valor);
            datosCuenta.put("observacion", observacion);

        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar datos del egreso", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_CUENTAS_PAGAR,
                datosCuenta,
                response -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "Egreso registrado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarFormulario();
                    cargarCuentasPagarApi();
                },
                error -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "No se pudo registrar el egreso",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void cargarCuentasPagarApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_CUENTAS_PAGAR,
                null,
                response -> {
                    try {
                        JSONArray cuentasJson = response.getJSONArray("cuentas");

                        listaCuentasPagar.clear();

                        double totalEgresos = 0;

                        for (int i = 0; i < cuentasJson.length(); i++) {
                            JSONObject cuentaJson = cuentasJson.getJSONObject(i);

                            CuentaPagarApi cuenta = new CuentaPagarApi(
                                    cuentaJson.getInt("id_cuenta_pagar"),
                                    cuentaJson.optString("tipo_egreso", ""),
                                    formatearFecha(cuentaJson.optString("fecha", "")),
                                    cuentaJson.optDouble("valor", 0),
                                    cuentaJson.optString("estado", ""),
                                    cuentaJson.optString("observacion", "Sin observación")
                            );

                            listaCuentasPagar.add(cuenta);
                            totalEgresos += cuenta.getValor();
                        }

                        txtTotalCuentasPagar.setText(
                                "Total egresos registrados: $" + String.format(Locale.US, "%.2f", totalEgresos)
                        );

                        mostrarCuentasPagar();

                    } catch (JSONException e) {
                        Toast.makeText(
                                CuentasPagarAdminActivity.this,
                                "Error al leer egresos del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "No se pudo consultar egresos desde la API",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarCuentasPagar() {
        contenedorCuentasPagar.removeAllViews();

        if (listaCuentasPagar.isEmpty()) {
            txtSinCuentasPagar.setVisibility(View.VISIBLE);
            contenedorCuentasPagar.setVisibility(View.GONE);
            return;
        }

        txtSinCuentasPagar.setVisibility(View.GONE);
        contenedorCuentasPagar.setVisibility(View.VISIBLE);

        for (CuentaPagarApi cuenta : listaCuentasPagar) {
            TextView tarjeta = crearTarjetaCuentaPagar(cuenta);
            contenedorCuentasPagar.addView(tarjeta);
        }
    }

    private TextView crearTarjetaCuentaPagar(CuentaPagarApi cuenta) {
        TextView tarjeta = new TextView(this);

        String informacion = "Tipo de egreso: " + cuenta.getTipoEgreso() + "\n"
                + "Fecha: " + cuenta.getFecha() + "\n"
                + "Valor: $" + String.format(Locale.US, "%.2f", cuenta.getValor()) + "\n"
                + "Estado: " + cuenta.getEstado() + "\n"
                + "Observación: " + cuenta.getObservacion();

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

    private String obtenerFechaFormatoApi() {
        return String.format(
                "%04d-%02d-%02d",
                anioSeleccionado,
                mesSeleccionado + 1,
                diaSeleccionado
        );
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

    private void limpiarFormulario() {
        spinnerTipoEgreso.setSelection(0);
        edtFechaPagar.setText("");
        edtValorPagar.setText("");
        edtObservacionPagar.setText("");

        anioSeleccionado = -1;
        mesSeleccionado = -1;
        diaSeleccionado = -1;
    }

    private static class CuentaPagarApi {

        private int idCuentaPagar;
        private String tipoEgreso;
        private String fecha;
        private double valor;
        private String estado;
        private String observacion;

        public CuentaPagarApi(int idCuentaPagar,
                              String tipoEgreso,
                              String fecha,
                              double valor,
                              String estado,
                              String observacion) {

            this.idCuentaPagar = idCuentaPagar;
            this.tipoEgreso = tipoEgreso;
            this.fecha = fecha;
            this.valor = valor;
            this.estado = estado;
            this.observacion = observacion;
        }

        public int getIdCuentaPagar() {
            return idCuentaPagar;
        }

        public String getTipoEgreso() {
            return tipoEgreso;
        }

        public String getFecha() {
            return fecha;
        }

        public double getValor() {
            return valor;
        }

        public String getEstado() {
            return estado;
        }

        public String getObservacion() {
            return observacion;
        }
    }
}