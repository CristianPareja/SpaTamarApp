package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Calendar;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText edtNombreCliente;
    private EditText edtTelefonoCliente;
    private EditText edtFechaCita;
    private EditText edtHoraCita;
    private EditText edtObservaciones;

    private Spinner spinnerServicio;

    private AppCompatButton btnGuardarCita;
    private AppCompatButton btnVolverAgendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        edtNombreCliente = findViewById(R.id.edtNombreCliente);
        edtTelefonoCliente = findViewById(R.id.edtTelefonoCliente);
        edtFechaCita = findViewById(R.id.edtFechaCita);
        edtHoraCita = findViewById(R.id.edtHoraCita);
        edtObservaciones = findViewById(R.id.edtObservaciones);

        spinnerServicio = findViewById(R.id.spinnerServicio);

        btnGuardarCita = findViewById(R.id.btnGuardarCita);
        btnVolverAgendar = findViewById(R.id.btnVolverAgendar);

        cargarServicios();

        edtFechaCita.setOnClickListener(v -> mostrarCalendario());
        edtHoraCita.setOnClickListener(v -> mostrarSelectorHora());

        btnGuardarCita.setOnClickListener(v -> guardarCitaTemporal());

        btnVolverAgendar.setOnClickListener(v -> finish());
    }

    private void cargarServicios() {
        String[] servicios = {
                "Seleccione un servicio",
                "Manicure",
                "Pedicure",
                "Uñas acrílicas",
                "Masajes relajantes"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                servicios
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServicio.setAdapter(adapter);
    }

    private void mostrarCalendario() {
        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog selectorFecha = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtFechaCita.setText(fechaSeleccionada);
                },
                anio,
                mes,
                dia
        );

        selectorFecha.show();
    }

    private void mostrarSelectorHora() {
        Calendar calendario = Calendar.getInstance();

        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog selectorHora = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                    edtHoraCita.setText(horaSeleccionada);
                },
                hora,
                minuto,
                true
        );

        selectorHora.show();
    }

    private void guardarCitaTemporal() {
        String nombre = edtNombreCliente.getText().toString().trim();
        String telefono = edtTelefonoCliente.getText().toString().trim();
        String servicio = spinnerServicio.getSelectedItem().toString();
        String fecha = edtFechaCita.getText().toString().trim();
        String hora = edtHoraCita.getText().toString().trim();
        String observaciones = edtObservaciones.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombreCliente.setError("Ingrese el nombre del cliente");
            edtNombreCliente.requestFocus();
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

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hora.isEmpty()) {
            Toast.makeText(this, "Seleccione la hora de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (observaciones.isEmpty()) {
            observaciones = "Sin observaciones";
        }

        if (RepositorioCitas.existeCruceCita(fecha, hora)) {
            Toast.makeText(this, "Ya existe una cita registrada en esa fecha y hora", Toast.LENGTH_LONG).show();
            return;
        }

        String estado = "Pendiente";

        Cita nuevaCita = new Cita(
                nombre,
                telefono,
                servicio,
                fecha,
                hora,
                estado,
                observaciones
        );

        RepositorioCitas.agregarCita(nuevaCita);

        String resumen = "Cliente: " + nombre + "\n"
                + "Teléfono: " + telefono + "\n"
                + "Servicio: " + servicio + "\n"
                + "Fecha: " + fecha + "\n"
                + "Hora: " + hora + "\n"
                + "Estado: " + estado + "\n"
                + "Observaciones: " + observaciones;

        new AlertDialog.Builder(this)
                .setTitle("Cita registrada")
                .setMessage(resumen)
                .setPositiveButton("Aceptar", (dialog, which) -> limpiarFormulario())
                .show();
    }

    private void limpiarFormulario() {
        edtNombreCliente.setText("");
        edtTelefonoCliente.setText("");
        edtFechaCita.setText("");
        edtHoraCita.setText("");
        edtObservaciones.setText("");
        spinnerServicio.setSelection(0);
    }
}