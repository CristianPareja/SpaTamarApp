package com.puce.spatamar;

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

        fechaSeleccionada = Calendar.getInstance();
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

                fechaSeleccionada = Calendar.getInstance();
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
                        citaJson.optString("observaciones", "Sin observaciones")
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

        tarjeta.addView(informacion);

        if (cita.getEstado().equalsIgnoreCase("En curso")) {
            AppCompatButton btnFinalizar = new AppCompatButton(this);
            btnFinalizar.setText("Marcar como finalizada");
            btnFinalizar.setTextSize(14);
            btnFinalizar.setTextColor(getResources().getColor(android.R.color.white));
            btnFinalizar.setAllCaps(false);
            btnFinalizar.setBackgroundResource(R.drawable.boton_principal);

            btnFinalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialogoFinalizar(cita);
                }
            });

            tarjeta.addView(btnFinalizar);

            AppCompatButton btnCancelar = new AppCompatButton(this);
            btnCancelar.setText("Cancelar cita");
            btnCancelar.setTextSize(14);
            btnCancelar.setTextColor(getResources().getColor(android.R.color.white));
            btnCancelar.setAllCaps(false);
            btnCancelar.setBackgroundResource(R.drawable.boton_principal);

            LinearLayout.LayoutParams parametrosBotonCancelar = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            parametrosBotonCancelar.setMargins(0, 10, 0, 0);
            btnCancelar.setLayoutParams(parametrosBotonCancelar);

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

    private void mostrarDialogoFinalizar(CitaAdminApi cita) {
        EditText inputValor = new EditText(this);
        inputValor.setHint("Valor cobrado");
        inputValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputValor.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(this)
                .setTitle("Finalizar cita")
                .setMessage("Ingrese el valor cobrado por esta cita para registrarlo como ingreso.")
                .setView(inputValor)
                .setPositiveButton("Finalizar", (dialog, which) -> {
                    String valorTexto = inputValor.getText().toString().trim();

                    if (valorTexto.isEmpty()) {
                        Toast.makeText(this, "Debe ingresar el valor cobrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double valorCobrado;

                    try {
                        valorCobrado = Double.parseDouble(valorTexto);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Ingrese un valor válido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (valorCobrado <= 0) {
                        Toast.makeText(this, "El valor debe ser mayor a cero", Toast.LENGTH_SHORT).show();
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
                            "Cita finalizada e ingreso registrado",
                            Toast.LENGTH_SHORT
                    ).show();

                    recargarListadoActual();
                },
                error -> {
                    Toast.makeText(
                            CitasAdminActivity.this,
                            "No se pudo finalizar la cita",
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

    private String convertirFechaCalendarioVisual(Calendar calendario) {
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
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

    private static class CitaAdminApi {

        private int idCita;
        private String nombreCliente;
        private String telefono;
        private String servicio;
        private String fecha;
        private String hora;
        private String estado;
        private String observaciones;

        public CitaAdminApi(int idCita,
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