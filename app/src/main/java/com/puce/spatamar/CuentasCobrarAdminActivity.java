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

public class CuentasCobrarAdminActivity extends AppCompatActivity {

    private Spinner spinnerClientesCobrar;

    private EditText edtConceptoCobrar;
    private EditText edtFechaCobrar;
    private EditText edtValorCobrar;
    private EditText edtObservacionCobrar;

    private TextView txtTotalGeneralCobrar;
    private TextView txtSinCuentasCobrar;

    private LinearLayout contenedorCuentasCobrar;

    private AppCompatButton btnRegistrarCuentaCobrar;
    private AppCompatButton btnVolverCuentasCobrar;

    private RequestQueue requestQueue;

    private ArrayList<ClienteApi> listaClientes;
    private ArrayList<CuentaCobrarApi> listaCuentas;

    private int anioSeleccionado = -1;
    private int mesSeleccionado = -1;
    private int diaSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_cobrar_admin);

        spinnerClientesCobrar = findViewById(R.id.spinnerClientesCobrar);

        edtConceptoCobrar = findViewById(R.id.edtConceptoCobrar);
        edtFechaCobrar = findViewById(R.id.edtFechaCobrar);
        edtValorCobrar = findViewById(R.id.edtValorCobrar);
        edtObservacionCobrar = findViewById(R.id.edtObservacionCobrar);

        txtTotalGeneralCobrar = findViewById(R.id.txtTotalGeneralCobrar);
        txtSinCuentasCobrar = findViewById(R.id.txtSinCuentasCobrar);

        contenedorCuentasCobrar = findViewById(R.id.contenedorCuentasCobrar);

        btnRegistrarCuentaCobrar = findViewById(R.id.btnRegistrarCuentaCobrar);
        btnVolverCuentasCobrar = findViewById(R.id.btnVolverCuentasCobrar);

        requestQueue = Volley.newRequestQueue(this);

        listaClientes = new ArrayList<>();
        listaCuentas = new ArrayList<>();

        cargarClientesApi();
        cargarCuentasRegistradasApi();

        edtFechaCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
            }
        });

        btnRegistrarCuentaCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarYRegistrarCuentaApi();
            }
        });

        btnVolverCuentasCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarClientesApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_USUARIOS,
                null,
                response -> {
                    try {
                        JSONArray usuariosJson = response.getJSONArray("usuarios");

                        listaClientes.clear();

                        ArrayList<String> nombresClientes = new ArrayList<>();
                        nombresClientes.add("Seleccione un cliente");

                        for (int i = 0; i < usuariosJson.length(); i++) {
                            JSONObject usuarioJson = usuariosJson.getJSONObject(i);

                            String rol = usuarioJson.optString("rol", "");
                            boolean estado = usuarioJson.optBoolean("estado", true);

                            if (rol.equalsIgnoreCase("cliente") && estado) {
                                int idUsuario = usuarioJson.getInt("id_usuario");
                                String nombre = usuarioJson.optString("nombre", "");
                                String apellido = usuarioJson.optString("apellido", "");
                                String telefono = usuarioJson.optString("telefono", "");
                                String correo = usuarioJson.optString("correo", "");
                                String usuario = usuarioJson.optString("usuario", "");

                                ClienteApi cliente = new ClienteApi(
                                        idUsuario,
                                        nombre,
                                        apellido,
                                        telefono,
                                        correo,
                                        usuario
                                );

                                listaClientes.add(cliente);
                                nombresClientes.add(cliente.getNombreCompleto() + " - " + cliente.getCorreo());
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                CuentasCobrarAdminActivity.this,
                                android.R.layout.simple_spinner_item,
                                nombresClientes
                        );

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerClientesCobrar.setAdapter(adapter);

                    } catch (JSONException e) {
                        Toast.makeText(
                                CuentasCobrarAdminActivity.this,
                                "Error al leer clientes del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            CuentasCobrarAdminActivity.this,
                            "No se pudo cargar clientes desde la API",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
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
                    edtFechaCobrar.setText(fechaSeleccionada);
                },
                anio,
                mes,
                dia
        );

        selectorFecha.show();
    }

    private void validarYRegistrarCuentaApi() {
        int posicionCliente = spinnerClientesCobrar.getSelectedItemPosition();

        String concepto = edtConceptoCobrar.getText().toString().trim();
        String fechaVisual = edtFechaCobrar.getText().toString().trim();
        String valorTexto = edtValorCobrar.getText().toString().trim();
        String observacion = edtObservacionCobrar.getText().toString().trim();

        if (posicionCliente == 0) {
            Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (concepto.isEmpty()) {
            edtConceptoCobrar.setError("Ingrese el concepto del servicio");
            edtConceptoCobrar.requestFocus();
            return;
        }

        if (fechaVisual.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            edtValorCobrar.setError("Ingrese el valor pendiente");
            edtValorCobrar.requestFocus();
            return;
        }

        double valorPendiente;

        try {
            valorPendiente = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            edtValorCobrar.setError("Ingrese un valor válido");
            edtValorCobrar.requestFocus();
            return;
        }

        if (valorPendiente <= 0) {
            edtValorCobrar.setError("El valor debe ser mayor a cero");
            edtValorCobrar.requestFocus();
            return;
        }

        if (observacion.isEmpty()) {
            observacion = "Sin observación";
        }

        ClienteApi clienteSeleccionado = listaClientes.get(posicionCliente - 1);

        registrarCuentaCobrarApi(
                clienteSeleccionado,
                concepto,
                obtenerFechaFormatoApi(),
                valorPendiente,
                observacion
        );
    }

    private void registrarCuentaCobrarApi(ClienteApi cliente,
                                          String concepto,
                                          String fechaApi,
                                          double valorPendiente,
                                          String observacion) {

        JSONObject datosCuenta = new JSONObject();

        try {
            datosCuenta.put("id_usuario", cliente.getIdUsuario());
            datosCuenta.put("nombre_cliente", cliente.getNombreCompleto());
            datosCuenta.put("correo_cliente", cliente.getCorreo());
            datosCuenta.put("concepto", concepto);
            datosCuenta.put("fecha", fechaApi);
            datosCuenta.put("valor_pendiente", valorPendiente);
            datosCuenta.put("observacion", observacion);

        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar datos de la cuenta", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_CUENTAS_COBRAR,
                datosCuenta,
                response -> {
                    Toast.makeText(
                            CuentasCobrarAdminActivity.this,
                            "Cuenta por cobrar registrada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarFormulario();
                    cargarCuentasRegistradasApi();
                },
                error -> {
                    Toast.makeText(
                            CuentasCobrarAdminActivity.this,
                            "No se pudo registrar la cuenta por cobrar",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
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
                                "Total general por cobrar: $" + String.format(Locale.US, "%.2f", totalGeneralPendiente)
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
                .setMessage("¿Está seguro de marcar esta cuenta como pagada? El sistema registrará automáticamente un ingreso financiero.")
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
        spinnerClientesCobrar.setSelection(0);
        edtConceptoCobrar.setText("");
        edtFechaCobrar.setText("");
        edtValorCobrar.setText("");
        edtObservacionCobrar.setText("");

        anioSeleccionado = -1;
        mesSeleccionado = -1;
        diaSeleccionado = -1;
    }

    private static class ClienteApi {

        private int idUsuario;
        private String nombre;
        private String apellido;
        private String telefono;
        private String correo;
        private String usuario;

        public ClienteApi(int idUsuario,
                          String nombre,
                          String apellido,
                          String telefono,
                          String correo,
                          String usuario) {

            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.apellido = apellido;
            this.telefono = telefono;
            this.correo = correo;
            this.usuario = usuario;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public String getNombreCompleto() {
            return nombre + " " + apellido;
        }

        public String getTelefono() {
            return telefono;
        }

        public String getCorreo() {
            return correo;
        }

        public String getUsuario() {
            return usuario;
        }
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