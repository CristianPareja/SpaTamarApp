package com.puce.spatamar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
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
import java.util.TimeZone;

public class CitasAdminActivity extends AppCompatActivity {

    private TextView txtFechaSeleccionadaAdmin;
    private TextView txtTotalCitasFechaAdmin;
    private TextView txtSinCitasAdmin;

    private EditText edtBuscarClienteCitas;

    private LinearLayout contenedorCitasAdmin;

    private AppCompatButton btnDiaAnteriorCitas;
    private AppCompatButton btnDiaSiguienteCitas;
    private AppCompatButton btnBuscarClienteCitas;
    private AppCompatButton btnMostrarHoyCitas;
    private AppCompatButton btnVolverCitasAdmin;

    private Calendar fechaSeleccionada;
    private RequestQueue requestQueue;

    private ArrayList<CitaAdminApi> listaCitasAdmin;
    private boolean busquedaClienteActiva = false;
    private String textoBusquedaCliente = "";

    private static final String ZONA_HORARIA_ECUADOR = "America/Guayaquil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_admin);

        txtFechaSeleccionadaAdmin = findViewById(R.id.txtFechaSeleccionadaAdmin);
        txtTotalCitasFechaAdmin = findViewById(R.id.txtTotalCitasFechaAdmin);
        txtSinCitasAdmin = findViewById(R.id.txtSinCitasAdmin);

        edtBuscarClienteCitas = findViewById(R.id.edtBuscarClienteCitas);

        contenedorCitasAdmin = findViewById(R.id.contenedorCitasAdmin);

        btnDiaAnteriorCitas = findViewById(R.id.btnDiaAnteriorCitas);
        btnDiaSiguienteCitas = findViewById(R.id.btnDiaSiguienteCitas);
        btnBuscarClienteCitas = findViewById(R.id.btnBuscarClienteCitas);
        btnMostrarHoyCitas = findViewById(R.id.btnMostrarHoyCitas);
        btnVolverCitasAdmin = findViewById(R.id.btnVolverCitasAdmin);

        fechaSeleccionada = obtenerCalendarioEcuador();
        requestQueue = Volley.newRequestQueue(this);
        listaCitasAdmin = new ArrayList<>();

        cargarCitasPorFechaApi();

        btnDiaAnteriorCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busquedaClienteActiva = false;
                textoBusquedaCliente = "";
                edtBuscarClienteCitas.setText("");

                fechaSeleccionada.add(Calendar.DAY_OF_MONTH, -1);
                cargarCitasPorFechaApi();
            }
        });

        btnDiaSiguienteCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busquedaClienteActiva = false;
                textoBusquedaCliente = "";
                edtBuscarClienteCitas.setText("");

                fechaSeleccionada.add(Calendar.DAY_OF_MONTH, 1);
                cargarCitasPorFechaApi();
            }
        });

        btnBuscarClienteCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPorClienteApi();
            }
        });

        btnMostrarHoyCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busquedaClienteActiva = false;
                textoBusquedaCliente = "";
                edtBuscarClienteCitas.setText("");

                fechaSeleccionada = obtenerCalendarioEcuador();
                cargarCitasPorFechaApi();
            }
        });

        btnVolverCitasAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (busquedaClienteActiva) {
            buscarPorClienteApi();
        } else {
            cargarCitasPorFechaApi();
        }
    }

    private void cargarCitasPorFechaApi() {
        String fechaVisual = convertirFechaCalendarioVisual(fechaSeleccionada);
        String fechaApi = convertirFechaCalendarioApi(fechaSeleccionada);

        txtFechaSeleccionadaAdmin.setText("Fecha: " + fechaVisual);

        String url = ApiConfig.URL_CITAS_FECHA + fechaApi;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    procesarRespuestaCitas(response);
                    txtTotalCitasFechaAdmin.setText("Total de citas: " + listaCitasAdmin.size());
                    mostrarListadoCitas(listaCitasAdmin);
                },
                error -> {
                    Toast.makeText(
                            CitasAdminActivity.this,
                            "No se pudo consultar las citas por fecha",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void buscarPorClienteApi() {
        String clienteBusqueda = edtBuscarClienteCitas.getText().toString().trim();

        if (clienteBusqueda.isEmpty()) {
            edtBuscarClienteCitas.setError("Ingrese el nombre del cliente");
            edtBuscarClienteCitas.requestFocus();
            return;
        }

        busquedaClienteActiva = true;
        textoBusquedaCliente = clienteBusqueda;

        txtFechaSeleccionadaAdmin.setText("Búsqueda por cliente");
        txtTotalCitasFechaAdmin.setText("Cliente: " + clienteBusqueda);

        String url = ApiConfig.URL_CITAS_CLIENTE + clienteBusqueda;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    procesarRespuestaCitas(response);
                    txtTotalCitasFechaAdmin.setText("Cliente: " + textoBusquedaCliente + " | Total: " + listaCitasAdmin.size());
                    mostrarListadoCitas(listaCitasAdmin);
                },
                error -> {
                    Toast.makeText(
                            CitasAdminActivity.this,
                            "No se pudo consultar las citas del cliente",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void procesarRespuestaCitas(JSONObject response) {
        try {
            JSONArray citasJson = response.getJSONArray("citas");

            listaCitasAdmin.clear();

            for (int i = 0; i < citasJson.length(); i++) {
                JSONObject citaJson = citasJson.getJSONObject(i);

                CitaAdminApi cita = new CitaAdminApi(
                        citaJson.getInt("id_cita"),
                        citaJson.optString("nombre_cliente", ""),
                        citaJson.optString("telefono", ""),
                        citaJson.optString("servicio", ""),
                        formatearFecha(citaJson.optString("fecha", "")),
                        formatearHora(citaJson.optString("hora", "")),
                        citaJson.optString("estado", ""),
                        citaJson.optString("observaciones", "Sin observaciones"),
                        citaJson.optDouble("precio_servicio", 0.0)
                );

                listaCitasAdmin.add(cita);
            }

        } catch (JSONException e) {
            Toast.makeText(
                    CitasAdminActivity.this,
                    "Error al leer citas del servidor",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void mostrarListadoCitas(ArrayList<CitaAdminApi> citas) {
        contenedorCitasAdmin.removeAllViews();

        if (citas.isEmpty()) {
            txtSinCitasAdmin.setVisibility(View.VISIBLE);
            contenedorCitasAdmin.setVisibility(View.GONE);
            return;
        }

        txtSinCitasAdmin.setVisibility(View.GONE);
        contenedorCitasAdmin.setVisibility(View.VISIBLE);

        for (CitaAdminApi cita : citas) {
            LinearLayout tarjeta = crearTarjetaCita(cita);
            contenedorCitasAdmin.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCita(CitaAdminApi cita) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(18), dp(18), dp(18), dp(18));
        tarjeta.setBackgroundResource(R.drawable.card_moderno);
        tarjeta.setElevation(dp(4));

        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametrosTarjeta.setMargins(0, 0, 0, dp(14));
        tarjeta.setLayoutParams(parametrosTarjeta);

        TextView titulo = new TextView(this);
        titulo.setText(cita.getServicio());
        titulo.setTextSize(18);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(getResources().getColor(R.color.azul_oscuro_moderno));
        titulo.setPadding(0, 0, 0, dp(8));

        TextView estado = crearChipEstado(cita.getEstado());

        TextView informacion = new TextView(this);
        String texto = "Cliente: " + cita.getNombreCliente() + "\n"
                + "Teléfono: " + cita.getTelefono() + "\n"
                + "Fecha: " + cita.getFecha() + "\n"
                + "Hora: " + cita.getHora() + "\n"
                + "Valor referencial: $" + String.format("%.2f", cita.getPrecioServicio()) + "\n"
                + "Observaciones: " + cita.getObservaciones();

        informacion.setText(texto);
        informacion.setTextSize(14);
        informacion.setTextColor(getResources().getColor(R.color.texto_oscuro_moderno));
        informacion.setLineSpacing(4, 1);
        informacion.setPadding(0, dp(10), 0, 0);

        tarjeta.addView(titulo);
        tarjeta.addView(estado);
        tarjeta.addView(informacion);

        if (cita.getEstado().equalsIgnoreCase("En curso")) {
            AppCompatButton btnFinalizar = new AppCompatButton(this);
            btnFinalizar.setText("Finalizar cita");
            btnFinalizar.setTextSize(14);
            btnFinalizar.setTextColor(getResources().getColor(R.color.blanco));
            btnFinalizar.setAllCaps(false);
            btnFinalizar.setBackgroundResource(R.drawable.boton_principal);

            LinearLayout.LayoutParams parametrosBotonFinalizar = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48)
            );
            parametrosBotonFinalizar.setMargins(0, dp(14), 0, 0);
            btnFinalizar.setLayoutParams(parametrosBotonFinalizar);
            btnFinalizar.setMinHeight(dp(48));
            btnFinalizar.setPadding(dp(12), 0, dp(12), 0);

            btnFinalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!citaPuedeFinalizarse(cita.getFecha())) {
                        Toast.makeText(
                                CitasAdminActivity.this,
                                "No puede finalizar una cita futura. El servicio aún no se ha realizado.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    mostrarDialogoFinalizar(cita);
                }
            });

            tarjeta.addView(btnFinalizar);

            AppCompatButton btnCancelar = new AppCompatButton(this);
            btnCancelar.setText("Cancelar cita");
            btnCancelar.setTextSize(14);
            btnCancelar.setTextColor(getResources().getColor(R.color.rojo_negativo));
            btnCancelar.setAllCaps(false);
            btnCancelar.setBackgroundResource(R.drawable.boton_cerrar_sesion_moderno);

            LinearLayout.LayoutParams parametrosBotonCancelar = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48)
            );
            parametrosBotonCancelar.setMargins(0, dp(10), 0, 0);
            btnCancelar.setLayoutParams(parametrosBotonCancelar);
            btnCancelar.setMinHeight(dp(48));
            btnCancelar.setPadding(dp(12), 0, dp(12), 0);

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialogoCancelar(cita);
                }
            });

            tarjeta.addView(btnCancelar);
        }

        return tarjeta;
    }

    private TextView crearChipEstado(String estadoCita) {
        TextView chip = new TextView(this);
        chip.setText("Estado: " + estadoCita);
        chip.setTextSize(13);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setPadding(dp(12), dp(7), dp(12), dp(7));

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametros.setMargins(0, 0, 0, dp(6));
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

    private void mostrarDialogoFinalizar(CitaAdminApi cita) {
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(6), dp(8), dp(6), dp(4));

        TextView txtValorReferencial = new TextView(this);
        txtValorReferencial.setText("Valor referencial del servicio: $" + String.format("%.2f", cita.getPrecioServicio()));
        txtValorReferencial.setTextSize(15);
        txtValorReferencial.setTypeface(null, Typeface.BOLD);
        txtValorReferencial.setTextColor(getResources().getColor(R.color.azul_oscuro_moderno));
        txtValorReferencial.setPadding(0, 0, 0, dp(10));

        TextView txtAyuda = new TextView(this);
        txtAyuda.setText("Ingrese el valor pagado por el cliente. Si el pago es parcial, se generará automáticamente una cuenta por cobrar.");
        txtAyuda.setTextSize(13);
        txtAyuda.setTextColor(getResources().getColor(R.color.texto_oscuro_moderno));
        txtAyuda.setPadding(0, 0, 0, dp(10));

        EditText inputValor = new EditText(this);
        inputValor.setHint("Valor pagado por el cliente");
        inputValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputValor.setPadding(dp(16), dp(12), dp(16), dp(12));
        inputValor.setBackgroundResource(R.drawable.edittext_login);

        if (cita.getPrecioServicio() > 0) {
            inputValor.setText(String.format("%.2f", cita.getPrecioServicio()));
            inputValor.setSelection(inputValor.getText().length());
        }

        contenedor.addView(txtValorReferencial);
        contenedor.addView(txtAyuda);
        contenedor.addView(inputValor);

        new AlertDialog.Builder(this)
                .setTitle("Finalizar cita")
                .setView(contenedor)
                .setPositiveButton("Finalizar", (dialog, which) -> {
                    String valorTexto = inputValor.getText().toString().trim();

                    if (valorTexto.isEmpty()) {
                        Toast.makeText(this, "Debe ingresar el valor pagado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double valorCobrado;

                    try {
                        valorCobrado = Double.parseDouble(valorTexto);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Ingrese un valor válido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (valorCobrado < 0) {
                        Toast.makeText(this, "El valor no puede ser negativo", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (cita.getPrecioServicio() > 0 && valorCobrado > cita.getPrecioServicio()) {
                        Toast.makeText(
                                this,
                                "El valor pagado no puede ser mayor al valor referencial del servicio",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    finalizarCitaApi(cita, valorCobrado);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void finalizarCitaApi(CitaAdminApi cita, double valorCobrado) {
        String url = ApiConfig.URL_CITAS + "/" + cita.getIdCita() + "/finalizar";

        JSONObject datosFinalizar = new JSONObject();

        try {
            datosFinalizar.put("valor_cobrado", valorCobrado);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                datosFinalizar,
                response -> {
                    Toast.makeText(
                            CitasAdminActivity.this,
                            "Cita finalizada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    recargarListadoActual();
                },
                error -> {
                    String mensaje = "No se pudo finalizar la cita";

                    if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                        mensaje = "Revise el valor pagado. No puede ser mayor al precio del servicio.";
                    }

                    Toast.makeText(
                            CitasAdminActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarDialogoCancelar(CitaAdminApi cita) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar cita")
                .setMessage("¿Está seguro de que desea cancelar esta cita?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> cancelarCitaApi(cita))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelarCitaApi(CitaAdminApi cita) {
        String url = ApiConfig.URL_CITAS + "/" + cita.getIdCita() + "/cancelar";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                null,
                response -> {
                    Toast.makeText(
                            CitasAdminActivity.this,
                            "Cita cancelada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    recargarListadoActual();
                },
                error -> {
                    Toast.makeText(
                            CitasAdminActivity.this,
                            "No se pudo cancelar la cita",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void recargarListadoActual() {
        if (busquedaClienteActiva) {
            buscarPorClienteApi();
        } else {
            cargarCitasPorFechaApi();
        }
    }

    private boolean citaPuedeFinalizarse(String fechaVisualCita) {
        try {
            String[] partesFecha = fechaVisualCita.split("/");

            if (partesFecha.length != 3) {
                return false;
            }

            int diaCita = Integer.parseInt(partesFecha[0]);
            int mesCita = Integer.parseInt(partesFecha[1]) - 1;
            int anioCita = Integer.parseInt(partesFecha[2]);

            Calendar fechaCita = obtenerCalendarioEcuador();
            fechaCita.set(Calendar.YEAR, anioCita);
            fechaCita.set(Calendar.MONTH, mesCita);
            fechaCita.set(Calendar.DAY_OF_MONTH, diaCita);
            fechaCita.set(Calendar.HOUR_OF_DAY, 0);
            fechaCita.set(Calendar.MINUTE, 0);
            fechaCita.set(Calendar.SECOND, 0);
            fechaCita.set(Calendar.MILLISECOND, 0);

            Calendar hoyEcuador = obtenerCalendarioEcuador();
            hoyEcuador.set(Calendar.HOUR_OF_DAY, 0);
            hoyEcuador.set(Calendar.MINUTE, 0);
            hoyEcuador.set(Calendar.SECOND, 0);
            hoyEcuador.set(Calendar.MILLISECOND, 0);

            return !fechaCita.after(hoyEcuador);

        } catch (Exception e) {
            return false;
        }
    }

    private Calendar obtenerCalendarioEcuador() {
        return Calendar.getInstance(TimeZone.getTimeZone(ZONA_HORARIA_ECUADOR));
    }

    private String convertirFechaCalendarioVisual(Calendar calendario) {
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return String.format("%02d/%02d/%04d", dia, mes, anio);
    }

    private String convertirFechaCalendarioApi(Calendar calendario) {
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return String.format("%04d-%02d-%02d", anio, mes, dia);
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

    private static class CitaAdminApi {

        private int idCita;
        private String nombreCliente;
        private String telefono;
        private String servicio;
        private String fecha;
        private String hora;
        private String estado;
        private String observaciones;
        private double precioServicio;

        public CitaAdminApi(int idCita,
                            String nombreCliente,
                            String telefono,
                            String servicio,
                            String fecha,
                            String hora,
                            String estado,
                            String observaciones,
                            double precioServicio) {

            this.idCita = idCita;
            this.nombreCliente = nombreCliente;
            this.telefono = telefono;
            this.servicio = servicio;
            this.fecha = fecha;
            this.hora = hora;
            this.estado = estado;
            this.observaciones = observaciones;
            this.precioServicio = precioServicio;
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

        public double getPrecioServicio() {
            return precioServicio;
        }
    }
}