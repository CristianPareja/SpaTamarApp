package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class CuentasPagarAdminActivity extends AppCompatActivity {

    private Spinner spinnerTipoEgreso;

    private EditText edtFechaPagar;
    private EditText edtValorPagar;
    private EditText edtObservacionPagar;

    private CheckBox chkEgresoRecurrente;

    private TextView txtTotalCuentasPagar;
    private TextView txtSinCuentasPagar;

    private LinearLayout cardFormularioCuentaPagar;
    private LinearLayout contenedorCuentasPagar;

    private AppCompatButton btnMostrarFormularioEgreso;
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

        cardFormularioCuentaPagar = findViewById(R.id.cardFormularioCuentaPagar);

        spinnerTipoEgreso = findViewById(R.id.spinnerTipoEgreso);

        edtFechaPagar = findViewById(R.id.edtFechaPagar);
        edtValorPagar = findViewById(R.id.edtValorPagar);
        edtObservacionPagar = findViewById(R.id.edtObservacionPagar);

        chkEgresoRecurrente = findViewById(R.id.chkEgresoRecurrente);

        txtTotalCuentasPagar = findViewById(R.id.txtTotalCuentasPagar);
        txtSinCuentasPagar = findViewById(R.id.txtSinCuentasPagar);

        contenedorCuentasPagar = findViewById(R.id.contenedorCuentasPagar);

        btnMostrarFormularioEgreso = findViewById(R.id.btnMostrarFormularioEgreso);
        btnRegistrarCuentaPagar = findViewById(R.id.btnRegistrarCuentaPagar);
        btnVolverCuentasPagar = findViewById(R.id.btnVolverCuentasPagar);

        requestQueue = Volley.newRequestQueue(this);
        listaCuentasPagar = new ArrayList<>();

        cardFormularioCuentaPagar.setVisibility(View.GONE);
        btnMostrarFormularioEgreso.setText("Nuevo egreso");

        cargarTiposEgreso();
        cargarCuentasPagarApi();

        btnMostrarFormularioEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alternarFormularioEgreso();
            }
        });

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

    private void alternarFormularioEgreso() {
        if (cardFormularioCuentaPagar.getVisibility() == View.VISIBLE) {
            cardFormularioCuentaPagar.setVisibility(View.GONE);
            btnMostrarFormularioEgreso.setText("Nuevo egreso");

            if (!modoEdicion) {
                limpiarFormulario();
            }
        } else {
            cardFormularioCuentaPagar.setVisibility(View.VISIBLE);

            if (modoEdicion) {
                btnMostrarFormularioEgreso.setText("Ocultar edición");
            } else {
                btnMostrarFormularioEgreso.setText("Ocultar formulario");
            }
        }
    }

    private void mostrarFormularioNuevoEgreso() {
        limpiarFormulario();
        cardFormularioCuentaPagar.setVisibility(View.VISIBLE);
        btnMostrarFormularioEgreso.setText("Ocultar formulario");
    }

    private void mostrarFormularioEdicion() {
        cardFormularioCuentaPagar.setVisibility(View.VISIBLE);
        btnMostrarFormularioEgreso.setText("Ocultar edición");
    }

    private void ocultarFormularioEgreso() {
        cardFormularioCuentaPagar.setVisibility(View.GONE);
        btnMostrarFormularioEgreso.setText("Nuevo egreso");
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
                    ocultarFormularioEgreso();
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
                    ocultarFormularioEgreso();
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
                    ocultarFormularioEgreso();
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
        tarjeta.setPadding(dp(18), dp(18), dp(18), dp(18));
        tarjeta.setBackgroundResource(R.drawable.card_moderno);
        tarjeta.setElevation(dp(4));

        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametrosTarjeta.setMargins(0, 0, 0, dp(20));
        tarjeta.setLayoutParams(parametrosTarjeta);

        TextView titulo = new TextView(this);
        titulo.setText(cuenta.getTipoEgreso());
        titulo.setTextSize(18);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(getResources().getColor(R.color.azul_oscuro_moderno));
        titulo.setPadding(0, 0, 0, dp(6));

        TextView valor = new TextView(this);
        valor.setText("$" + String.format(Locale.US, "%.2f", cuenta.getValor()));
        valor.setTextSize(25);
        valor.setTypeface(null, Typeface.BOLD);
        valor.setTextColor(getResources().getColor(R.color.rojo_negativo));
        valor.setPadding(0, 0, 0, dp(8));

        TextView estado = crearChipEstado(cuenta.getEstado());

        TextView detalle = new TextView(this);

        String texto = "Fecha: " + cuenta.getFechaVisual() + "\n"
                + "Observación: " + cuenta.getObservacion();

        if (cuenta.esRecurrente()) {
            texto = texto + "\nTipo de registro: Mensual recurrente"
                    + "\nMes aplicado: " + cuenta.getMesAplicado();
        } else {
            texto = texto + "\nTipo de registro: Único";
        }

        detalle.setText(texto);
        detalle.setTextSize(14);
        detalle.setTextColor(getResources().getColor(R.color.texto_oscuro_moderno));
        detalle.setLineSpacing(4, 1);
        detalle.setPadding(0, dp(10), 0, 0);

        AppCompatButton btnEditar = new AppCompatButton(this);
        btnEditar.setText("Editar egreso");
        btnEditar.setTextSize(14);
        btnEditar.setTextColor(getResources().getColor(R.color.azul_moderno));
        btnEditar.setAllCaps(false);
        btnEditar.setBackgroundResource(R.drawable.boton_secundario_moderno);

        AppCompatButton btnEliminar = new AppCompatButton(this);
        btnEliminar.setText("Eliminar egreso");
        btnEliminar.setTextSize(14);
        btnEliminar.setTextColor(getResources().getColor(R.color.rojo_negativo));
        btnEliminar.setAllCaps(false);
        btnEliminar.setBackgroundResource(R.drawable.boton_cerrar_sesion_moderno);

        LinearLayout.LayoutParams parametrosBoton = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
        );

        parametrosBoton.setMargins(0, dp(14), 0, 0);
        btnEditar.setLayoutParams(parametrosBoton);
        btnEditar.setMinHeight(dp(48));
        btnEditar.setPadding(dp(12), 0, dp(12), 0);

        LinearLayout.LayoutParams parametrosBotonEliminar = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
        );

        parametrosBotonEliminar.setMargins(0, dp(10), 0, 0);
        btnEliminar.setLayoutParams(parametrosBotonEliminar);
        btnEliminar.setMinHeight(dp(48));
        btnEliminar.setPadding(dp(12), 0, dp(12), 0);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarEgresoEnFormulario(cuenta);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEliminarEgreso(cuenta);
            }
        });

        tarjeta.addView(titulo);
        tarjeta.addView(valor);
        tarjeta.addView(estado);
        tarjeta.addView(detalle);
        tarjeta.addView(btnEditar);
        tarjeta.addView(btnEliminar);

        return tarjeta;
    }

    private TextView crearChipEstado(String estadoCuenta) {
        TextView chip = new TextView(this);
        chip.setText("Estado: " + estadoCuenta);
        chip.setTextSize(13);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setPadding(dp(12), dp(7), dp(12), dp(7));

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametros.setMargins(0, 0, 0, dp(6));
        chip.setLayoutParams(parametros);

        chip.setTextColor(getResources().getColor(R.color.rojo_negativo));
        chip.setBackgroundResource(R.drawable.card_moderno_rojo);

        return chip;
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

        mostrarFormularioEdicion();

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
                Locale.US,
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

    private void confirmarEliminarEgreso(CuentaPagarApi cuenta) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar egreso")
                .setMessage("¿Está seguro de que desea eliminar este egreso? Ya no afectará la utilidad del mes.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarCuentaPagarApi(cuenta))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCuentaPagarApi(CuentaPagarApi cuenta) {
        String url = ApiConfig.URL_CUENTAS_PAGAR + "/" + cuenta.getIdCuentaPagar();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "Egreso eliminado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (modoEdicion && idCuentaEditar == cuenta.getIdCuentaPagar()) {
                        limpiarFormulario();
                        ocultarFormularioEgreso();
                    }

                    cargarCuentasPagarApi();
                },
                error -> {
                    Toast.makeText(
                            CuentasPagarAdminActivity.this,
                            "No se pudo eliminar el egreso",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
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

        btnMostrarFormularioEgreso.setText(
                cardFormularioCuentaPagar.getVisibility() == View.VISIBLE
                        ? "Ocultar formulario"
                        : "Nuevo egreso"
        );
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
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