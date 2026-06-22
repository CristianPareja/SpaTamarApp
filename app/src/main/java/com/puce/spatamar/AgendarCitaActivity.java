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

import java.util.Calendar;

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

    private String nombrePerfil = "";

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
                guardarCitaTemporal();
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
        if (!RepositorioPerfil.existePerfil()) {
            txtClientePerfil.setText("Cliente: perfil no registrado");

            Toast.makeText(
                    this,
                    "Debe registrar o actualizar su perfil antes de agendar una cita",
                    Toast.LENGTH_LONG
            ).show();

            btnGuardarCita.setEnabled(false);
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
        String telefono = edtTelefonoCliente.getText().toString().trim();
        String servicio = spinnerServicio.getSelectedItem().toString();
        String fecha = edtFechaCita.getText().toString().trim();
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

        String estado = "En curso";

        Cita nuevaCita = new Cita(
                nombreParaCita,
                telefono,
                servicio,
                fecha,
                hora,
                estado,
                observaciones
        );

        RepositorioCitas.agregarCita(nuevaCita);

        String resumen = "Cliente: " + nombreParaCita + "\n"
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
        chkAgendarFamiliar.setChecked(false);
        txtNombreFamiliar.setVisibility(View.GONE);
        edtNombreFamiliar.setVisibility(View.GONE);
        edtNombreFamiliar.setText("");

        edtFechaCita.setText("");
        edtHoraCita.setText("");
        edtObservaciones.setText("");
        spinnerServicio.setSelection(0);
    }
}