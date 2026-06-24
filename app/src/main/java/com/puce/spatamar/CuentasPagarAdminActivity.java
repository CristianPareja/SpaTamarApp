package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

    private CheckBox chkEgresoRecurrente;

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

    private boolean modoEdicion = false;
    private int idCuentaEditar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_pagar_admin);

        spinnerTipoEgreso = findViewById(R.id.spinnerTipoEgreso);

        edtFechaPagar = findViewById(R.id.edtFechaPagar);
        edtValorPagar = findViewById(R.id.edtValorPagar);
        edtObservacionPagar = findViewById(R.id.edtObservacionPagar);

        chkEgresoRecurrente = findViewById(R.id.chkEgresoRecurrente);

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
                validarFormularioCuentaPagar();
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

        if (anioSeleccionado > 0 && mesSeleccionado >= 0 && diaSeleccionado > 0) {
            calendario.set(Calendar.YEAR, anioSeleccionado);
            calendario.set(Calendar.MONTH, mesSeleccionado);
            calendario.set(Calendar.DAY_OF_MONTH, diaSeleccionado);
        }

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

    private void validarFormularioCuentaPagar() {
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

        if (anioSeleccionado <= 0 || mesSeleccionado < 0 || diaSeleccionado <= 0) {
            Toast.makeText(this, "Seleccione una fecha válida", Toast.LENGTH_SHORT).show();
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
        String fechaApi = obtenerFechaFormatoApi();

        if (modoEdicion) {
            actualizarCuentaPagarApi(
                    idCuentaEditar,
                    tipoEgreso,
                    fechaApi,
                    valor,
                    observacion
            );
        } else {
            if (chkEgresoRecurrente.isChecked()) {
                registrarEgresoRecurrenteApi(
                        tipoEgreso,
                        fechaApi,
                        valor,
                        diaSeleccionado,
                        observacion
                );
            } else {
                registrarCuentaPagarApi(
                        tipoEgreso,
                        fechaApi,
                        valor,
                        observacion
                );
            }
        }
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

    private void registrarEgresoRecurrenteApi(String tipoEgreso,
                                              String fechaInicio,
                                              double valor,
                                              int diaCobro,
                                              String observacion) {

        JSONObject datosCuenta = new JSONObject();

        try {
            datosCuenta.put("tipo_egreso", tipoEgreso);
            datosCuenta.put("fecha_inicio", fechaInicio);
            datosCuenta.put("valor", valor);
            datosCuenta.put("dia_cobro", diaCobro);
            datosCuenta.put("observacion", observacion);

        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar datos del egreso mensual", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConfig.URL_CUENTAS_PAGAR + "/recurrente";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                datosCuenta,
                response -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "Egreso mensual registrado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarFormulario();
                    cargarCuentasPagarApi();
                },
                error -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "No se pudo registrar el egreso mensual",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void actualizarCuentaPagarApi(int idCuenta,
                                          String tipoEgreso,
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

        String url = ApiConfig.URL_CUENTAS_PAGAR + "/" + idCuenta;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                datosCuenta,
                response -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "Egreso actualizado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarFormulario();
                    cargarCuentasPagarApi();
                },
                error -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "No se pudo actualizar el egreso",
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
                                    cuentaJson.optString("fecha", ""),
                                    formatearFecha(cuentaJson.optString("fecha", "")),
                                    cuentaJson.optDouble("valor", 0),
                                    cuentaJson.optString("estado", ""),
                                    cuentaJson.optString("observacion", "Sin observación"),
                                    cuentaJson.optInt("id_recurrente", 0),
                                    cuentaJson.optString("mes_aplicado", "")
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
            LinearLayout tarjeta = crearTarjetaCuentaPagar(cuenta);
            contenedorCuentasPagar.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCuentaPagar(CuentaPagarApi cuenta) {
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

        String texto = "Tipo de egreso: " + cuenta.getTipoEgreso() + "\n"
                + "Fecha: " + cuenta.getFechaVisual() + "\n"
                + "Valor: $" + String.format(Locale.US, "%.2f", cuenta.getValor()) + "\n"
                + "Estado: " + cuenta.getEstado() + "\n"
                + "Observación: " + cuenta.getObservacion();

        if (cuenta.esRecurrente()) {
            texto = texto + "\nTipo de registro: Mensual recurrente"
                    + "\nMes aplicado: " + cuenta.getMesAplicado();
        } else {
            texto = texto + "\nTipo de registro: Único";
        }

        informacion.setText(texto);
        informacion.setTextSize(15);
        informacion.setTextColor(getResources().getColor(android.R.color.black));
        informacion.setPadding(0, 0, 0, 14);

        AppCompatButton btnEditar = new AppCompatButton(this);
        btnEditar.setText("Editar egreso");
        btnEditar.setTextSize(14);
        btnEditar.setTextColor(getResources().getColor(android.R.color.white));
        btnEditar.setAllCaps(false);
        btnEditar.setBackgroundResource(R.drawable.boton_principal);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarEgresoEnFormulario(cuenta);
            }
        });

        tarjeta.addView(informacion);
        tarjeta.addView(btnEditar);

        return tarjeta;
    }

    private void cargarEgresoEnFormulario(CuentaPagarApi cuenta) {
        modoEdicion = true;
        idCuentaEditar = cuenta.getIdCuentaPagar();

        seleccionarTipoEgreso(cuenta.getTipoEgreso());

        edtFechaPagar.setText(cuenta.getFechaVisual());
        edtValorPagar.setText(String.format(Locale.US, "%.2f", cuenta.getValor()));
        edtObservacionPagar.setText(cuenta.getObservacion());

        cargarFechaDesdeApi(cuenta.getFechaApi());

        chkEgresoRecurrente.setChecked(false);
        chkEgresoRecurrente.setEnabled(false);

        btnRegistrarCuentaPagar.setText("Actualizar egreso");

        Toast.makeText(
                this,
                "Modo edición activado. Modifique los datos y presione Actualizar egreso.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void seleccionarTipoEgreso(String tipoEgreso) {
        for (int i = 0; i < spinnerTipoEgreso.getCount(); i++) {
            String item = spinnerTipoEgreso.getItemAtPosition(i).toString();

            if (item.equalsIgnoreCase(tipoEgreso)) {
                spinnerTipoEgreso.setSelection(i);
                return;
            }
        }

        spinnerTipoEgreso.setSelection(0);
    }

    private void cargarFechaDesdeApi(String fechaApi) {
        if (fechaApi == null || fechaApi.length() < 10) {
            return;
        }

        try {
            anioSeleccionado = Integer.parseInt(fechaApi.substring(0, 4));
            mesSeleccionado = Integer.parseInt(fechaApi.substring(5, 7)) - 1;
            diaSeleccionado = Integer.parseInt(fechaApi.substring(8, 10));
        } catch (Exception e) {
            anioSeleccionado = -1;
            mesSeleccionado = -1;
            diaSeleccionado = -1;
        }
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

        chkEgresoRecurrente.setChecked(false);
        chkEgresoRecurrente.setEnabled(true);

        anioSeleccionado = -1;
        mesSeleccionado = -1;
        diaSeleccionado = -1;

        modoEdicion = false;
        idCuentaEditar = 0;

        btnRegistrarCuentaPagar.setText("Registrar egreso");
    }

    private static class CuentaPagarApi {

        private int idCuentaPagar;
        private String tipoEgreso;
        private String fechaApi;
        private String fechaVisual;
        private double valor;
        private String estado;
        private String observacion;
        private int idRecurrente;
        private String mesAplicado;

        public CuentaPagarApi(int idCuentaPagar,
                              String tipoEgreso,
                              String fechaApi,
                              String fechaVisual,
                              double valor,
                              String estado,
                              String observacion,
                              int idRecurrente,
                              String mesAplicado) {

            this.idCuentaPagar = idCuentaPagar;
            this.tipoEgreso = tipoEgreso;
            this.fechaApi = fechaApi;
            this.fechaVisual = fechaVisual;
            this.valor = valor;
            this.estado = estado;
            this.observacion = observacion;
            this.idRecurrente = idRecurrente;
            this.mesAplicado = mesAplicado;
        }

        public int getIdCuentaPagar() {
            return idCuentaPagar;
        }

        public String getTipoEgreso() {
            return tipoEgreso;
        }

        public String getFechaApi() {
            return fechaApi;
        }

        public String getFechaVisual() {
            return fechaVisual;
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

        public int getIdRecurrente() {
            return idRecurrente;
        }

        public String getMesAplicado() {
            return mesAplicado;
        }

        public boolean esRecurrente() {
            return idRecurrente > 0;
        }
    }
}