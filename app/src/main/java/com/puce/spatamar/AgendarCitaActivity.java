package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class AgendarCitaActivity extends AppCompatActivity {

    private TextView txtClientePerfil;
    private TextView txtNombreFamiliar;

    private CheckBox chkAgendarFamiliar;

    private EditText edtNombreFamiliar;
    private EditText edtTelefonoCliente;
    private EditText edtFechaCita;
    private EditText edtHoraCita;
    private EditText edtObservaciones;

    private Spinner spinnerServicio;

    private AppCompatButton btnGuardarCita;
    private AppCompatButton btnVolverAgendar;

    private RequestQueue requestQueue;

    private String nombrePerfil = "";
    private String servicioRecibido = "";
    private int idServicioRecibido = 0;

    private int anioSeleccionado = -1;
    private int mesSeleccionado = -1;
    private int diaSeleccionado = -1;
    private int horaSeleccionada = -1;
    private int minutoSeleccionado = -1;

    private static final String ZONA_HORARIA_ECUADOR = "America/Guayaquil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        txtClientePerfil = findViewById(R.id.txtClientePerfil);
        txtNombreFamiliar = findViewById(R.id.txtNombreFamiliar);

        chkAgendarFamiliar = findViewById(R.id.chkAgendarFamiliar);

        edtNombreFamiliar = findViewById(R.id.edtNombreFamiliar);
        edtTelefonoCliente = findViewById(R.id.edtTelefonoCliente);
        edtFechaCita = findViewById(R.id.edtFechaCita);
        edtHoraCita = findViewById(R.id.edtHoraCita);
        edtObservaciones = findViewById(R.id.edtObservaciones);

        spinnerServicio = findViewById(R.id.spinnerServicio);

        btnGuardarCita = findViewById(R.id.btnGuardarCita);
        btnVolverAgendar = findViewById(R.id.btnVolverAgendar);

        requestQueue = Volley.newRequestQueue(this);

        servicioRecibido = getIntent().getStringExtra("servicioSeleccionado");
        idServicioRecibido = getIntent().getIntExtra("idServicioSeleccionado", 0);

        if (servicioRecibido == null) {
            servicioRecibido = "";
        }

        cargarPerfilEnFormulario();
        cargarServicios();

        chkAgendarFamiliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOcultarCampoFamiliar();
            }
        });

        edtFechaCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
            }
        });

        edtHoraCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarSelectorHora();
            }
        });

        btnGuardarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarYRegistrarCitaApi();
            }
        });

        btnVolverAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarPerfilEnFormulario() {
        if (!SesionUsuario.haySesionActiva() && !RepositorioPerfil.existePerfil()) {
            txtClientePerfil.setText("Cliente: perfil no registrado");

            Toast.makeText(
                    this,
                    "Debe iniciar sesión antes de agendar una cita",
                    Toast.LENGTH_LONG
            ).show();

            btnGuardarCita.setEnabled(false);
            return;
        }

        if (SesionUsuario.haySesionActiva()) {
            nombrePerfil = SesionUsuario.getNombreCompleto();
            txtClientePerfil.setText("Cliente: " + nombrePerfil);
            edtTelefonoCliente.setText(SesionUsuario.getTelefono());
            return;
        }

        PerfilUsuario perfil = RepositorioPerfil.obtenerPerfil();

        nombrePerfil = perfil.getNombre();
        txtClientePerfil.setText("Cliente: " + nombrePerfil);
        edtTelefonoCliente.setText(perfil.getTelefono());
    }

    private void mostrarOcultarCampoFamiliar() {
        if (chkAgendarFamiliar.isChecked()) {
            txtNombreFamiliar.setVisibility(View.VISIBLE);
            edtNombreFamiliar.setVisibility(View.VISIBLE);
            edtNombreFamiliar.requestFocus();
        } else {
            txtNombreFamiliar.setVisibility(View.GONE);
            edtNombreFamiliar.setVisibility(View.GONE);
            edtNombreFamiliar.setText("");
        }
    }

    private void cargarServicios() {
        ArrayList<Servicio> serviciosActivos = RepositorioServicios.obtenerServiciosActivos();

        ArrayList<String> nombresServicios = new ArrayList<>();
        nombresServicios.add("Seleccione un servicio");

        for (Servicio servicio : serviciosActivos) {
            nombresServicios.add(servicio.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresServicios
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServicio.setAdapter(adapter);

        seleccionarServicioRecibido(nombresServicios);
    }

    private void seleccionarServicioRecibido(ArrayList<String> nombresServicios) {
        if (servicioRecibido.isEmpty()) {
            return;
        }

        for (int i = 0; i < nombresServicios.size(); i++) {
            if (nombresServicios.get(i).equalsIgnoreCase(servicioRecibido)) {
                spinnerServicio.setSelection(i);
                return;
            }
        }
    }

    private int obtenerIdServicioSeleccionado(String nombreServicio) {
        if (idServicioRecibido > 0 && nombreServicio.equalsIgnoreCase(servicioRecibido)) {
            return idServicioRecibido;
        }

        ArrayList<Servicio> serviciosActivos = RepositorioServicios.obtenerServiciosActivos();

        for (Servicio servicio : serviciosActivos) {
            if (servicio.getNombre().equalsIgnoreCase(nombreServicio)) {
                return servicio.getIdServicio();
            }
        }

        return 0;
    }

    private void mostrarCalendario() {
        Calendar calendarioEcuador = obtenerCalendarioEcuador();

        int anio = calendarioEcuador.get(Calendar.YEAR);
        int mes = calendarioEcuador.get(Calendar.MONTH);
        int dia = calendarioEcuador.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog selectorFecha = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    anioSeleccionado = year;
                    mesSeleccionado = month;
                    diaSeleccionado = dayOfMonth;

                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtFechaCita.setText(fechaSeleccionada);

                    edtHoraCita.setText("");
                    horaSeleccionada = -1;
                    minutoSeleccionado = -1;
                },
                anio,
                mes,
                dia
        );

        Calendar inicioDiaActualEcuador = obtenerCalendarioEcuador();
        inicioDiaActualEcuador.set(Calendar.HOUR_OF_DAY, 0);
        inicioDiaActualEcuador.set(Calendar.MINUTE, 0);
        inicioDiaActualEcuador.set(Calendar.SECOND, 0);
        inicioDiaActualEcuador.set(Calendar.MILLISECOND, 0);

        selectorFecha.getDatePicker().setMinDate(inicioDiaActualEcuador.getTimeInMillis());
        selectorFecha.show();
    }

    private void mostrarSelectorHora() {
        if (edtFechaCita.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Primero seleccione la fecha de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendarioEcuador = obtenerCalendarioEcuador();

        int hora = calendarioEcuador.get(Calendar.HOUR_OF_DAY);
        int minuto = calendarioEcuador.get(Calendar.MINUTE);

        TimePickerDialog selectorHora = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteSelected) -> {
                    horaSeleccionada = hourOfDay;
                    minutoSeleccionado = minuteSelected;

                    String horaTexto = String.format("%02d:%02d", hourOfDay, minuteSelected);
                    edtHoraCita.setText(horaTexto);
                },
                hora,
                minuto,
                true
        );

        selectorHora.show();
    }

    private void validarYRegistrarCitaApi() {
        String telefono = edtTelefonoCliente.getText().toString().trim();
        String servicio = spinnerServicio.getSelectedItem().toString();
        String fechaVisual = edtFechaCita.getText().toString().trim();
        String hora = edtHoraCita.getText().toString().trim();
        String observaciones = edtObservaciones.getText().toString().trim();

        String nombreParaCita = nombrePerfil;

        if (chkAgendarFamiliar.isChecked()) {
            String nombreFamiliar = edtNombreFamiliar.getText().toString().trim();

            if (nombreFamiliar.isEmpty()) {
                edtNombreFamiliar.setError("Ingrese el nombre del familiar");
                edtNombreFamiliar.requestFocus();
                return;
            }

            nombreParaCita = nombreFamiliar;
        }

        if (nombreParaCita.isEmpty()) {
            Toast.makeText(this, "No se encontró el nombre del cliente", Toast.LENGTH_LONG).show();
            return;
        }

        if (telefono.isEmpty()) {
            edtTelefonoCliente.setError("Ingrese el teléfono");
            edtTelefonoCliente.requestFocus();
            return;
        }

        if (telefono.length() < 10) {
            edtTelefonoCliente.setError("Ingrese un teléfono válido de 10 dígitos");
            edtTelefonoCliente.requestFocus();
            return;
        }

        if (spinnerServicio.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaVisual.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hora.isEmpty()) {
            Toast.makeText(this, "Seleccione la hora de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fechaYHoraSonValidas()) {
            Toast.makeText(this, "No puede agendar una cita en una fecha u hora pasada", Toast.LENGTH_LONG).show();
            return;
        }

        if (observaciones.isEmpty()) {
            observaciones = "Sin observaciones";
        }

        int idServicio = obtenerIdServicioSeleccionado(servicio);
        String fechaApi = obtenerFechaFormatoApi();

        registrarCitaApi(
                nombreParaCita,
                telefono,
                servicio,
                fechaApi,
                hora,
                observaciones,
                idServicio
        );
    }

    private void registrarCitaApi(String nombreParaCita,
                                  String telefono,
                                  String servicio,
                                  String fechaApi,
                                  String hora,
                                  String observaciones,
                                  int idServicio) {

        JSONObject datosCita = new JSONObject();

        try {
            if (SesionUsuario.haySesionActiva()) {
                datosCita.put("id_usuario", SesionUsuario.getIdUsuario());
            } else {
                datosCita.put("id_usuario", JSONObject.NULL);
            }

            if (idServicio > 0) {
                datosCita.put("id_servicio", idServicio);
            } else {
                datosCita.put("id_servicio", JSONObject.NULL);
            }

            datosCita.put("nombre_cliente", nombreParaCita);
            datosCita.put("telefono", telefono);
            datosCita.put("servicio", servicio);
            datosCita.put("fecha", fechaApi);
            datosCita.put("hora", hora);
            datosCita.put("observaciones", observaciones);

        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_CITAS,
                datosCita,
                response -> {
                    String resumen = "Cliente: " + nombreParaCita + "\n"
                            + "Teléfono: " + telefono + "\n"
                            + "Servicio: " + servicio + "\n"
                            + "Fecha: " + edtFechaCita.getText().toString().trim() + "\n"
                            + "Hora: " + hora + "\n"
                            + "Estado: En curso\n"
                            + "Observaciones: " + observaciones;


                    new AlertDialog.Builder(AgendarCitaActivity.this)
                            .setTitle("Cita registrada")
                            .setMessage(resumen)
                            .setPositiveButton("Aceptar", (dialog, which) -> limpiarFormulario())
                            .show();
                },
                error -> {
                    String mensaje = "No se pudo registrar la cita";

                    if (error.networkResponse == null) {
                        mensaje = "No se pudo conectar con la API. Verifique que el backend esté encendido.";
                    } else if (error.networkResponse.statusCode == 409) {
                        mensaje = "Ya existe una cita registrada en esa fecha y hora";
                    } else if (error.networkResponse.statusCode == 400) {
                        mensaje = "Revise los datos obligatorios de la cita";
                    }

                    Toast.makeText(
                            AgendarCitaActivity.this,
                            mensaje,
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

    private boolean fechaYHoraSonValidas() {
        if (anioSeleccionado == -1 || mesSeleccionado == -1 || diaSeleccionado == -1
                || horaSeleccionada == -1 || minutoSeleccionado == -1) {
            return false;
        }

        Calendar fechaHoraCita = Calendar.getInstance(TimeZone.getTimeZone(ZONA_HORARIA_ECUADOR));
        fechaHoraCita.set(Calendar.YEAR, anioSeleccionado);
        fechaHoraCita.set(Calendar.MONTH, mesSeleccionado);
        fechaHoraCita.set(Calendar.DAY_OF_MONTH, diaSeleccionado);
        fechaHoraCita.set(Calendar.HOUR_OF_DAY, horaSeleccionada);
        fechaHoraCita.set(Calendar.MINUTE, minutoSeleccionado);
        fechaHoraCita.set(Calendar.SECOND, 0);
        fechaHoraCita.set(Calendar.MILLISECOND, 0);

        Calendar fechaHoraActualEcuador = obtenerCalendarioEcuador();

        return fechaHoraCita.after(fechaHoraActualEcuador);
    }

    private Calendar obtenerCalendarioEcuador() {
        return Calendar.getInstance(TimeZone.getTimeZone(ZONA_HORARIA_ECUADOR));
    }

    private void limpiarFormulario() {
        chkAgendarFamiliar.setChecked(false);
        txtNombreFamiliar.setVisibility(View.GONE);
        edtNombreFamiliar.setVisibility(View.GONE);
        edtNombreFamiliar.setText("");

        edtFechaCita.setText("");
        edtHoraCita.setText("");
        edtObservaciones.setText("");
        spinnerServicio.setSelection(0);

        anioSeleccionado = -1;
        mesSeleccionado = -1;
        diaSeleccionado = -1;
        horaSeleccionada = -1;
        minutoSeleccionado = -1;
    }
}