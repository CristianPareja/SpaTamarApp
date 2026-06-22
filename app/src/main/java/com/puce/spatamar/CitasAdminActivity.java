package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Calendar;

public class CitasAdminActivity extends AppCompatActivity {

    private TextView txtFechaSeleccionadaAdmin;
    private TextView txtTotalCitasFechaAdmin;
    private TextView txtSinCitasAdmin;

    private EditText edtBuscarClienteCitas;
    private EditText edtBuscarFechaCitas;

    private LinearLayout contenedorCitasAdmin;

    private AppCompatButton btnDiaAnteriorCitas;
    private AppCompatButton btnDiaSiguienteCitas;
    private AppCompatButton btnBuscarClienteCitas;
    private AppCompatButton btnBuscarFechaCitas;
    private AppCompatButton btnMostrarHoyCitas;
    private AppCompatButton btnVolverCitasAdmin;

    private Calendar fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_admin);

        txtFechaSeleccionadaAdmin = findViewById(R.id.txtFechaSeleccionadaAdmin);
        txtTotalCitasFechaAdmin = findViewById(R.id.txtTotalCitasFechaAdmin);
        txtSinCitasAdmin = findViewById(R.id.txtSinCitasAdmin);

        edtBuscarClienteCitas = findViewById(R.id.edtBuscarClienteCitas);
        edtBuscarFechaCitas = findViewById(R.id.edtBuscarFechaCitas);

        contenedorCitasAdmin = findViewById(R.id.contenedorCitasAdmin);

        btnDiaAnteriorCitas = findViewById(R.id.btnDiaAnteriorCitas);
        btnDiaSiguienteCitas = findViewById(R.id.btnDiaSiguienteCitas);
        btnBuscarClienteCitas = findViewById(R.id.btnBuscarClienteCitas);
        btnBuscarFechaCitas = findViewById(R.id.btnBuscarFechaCitas);
        btnMostrarHoyCitas = findViewById(R.id.btnMostrarHoyCitas);
        btnVolverCitasAdmin = findViewById(R.id.btnVolverCitasAdmin);

        fechaSeleccionada = Calendar.getInstance();

        cargarCitasPorFecha();

        btnDiaAnteriorCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaSeleccionada.add(Calendar.DAY_OF_MONTH, -1);
                cargarCitasPorFecha();
            }
        });

        btnDiaSiguienteCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaSeleccionada.add(Calendar.DAY_OF_MONTH, 1);
                cargarCitasPorFecha();
            }
        });

        edtBuscarFechaCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendarioBusqueda();
            }
        });

        btnBuscarFechaCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPorFecha();
            }
        });

        btnBuscarClienteCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPorCliente();
            }
        });

        btnMostrarHoyCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaSeleccionada = Calendar.getInstance();
                edtBuscarClienteCitas.setText("");
                edtBuscarFechaCitas.setText("");
                cargarCitasPorFecha();
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
        cargarCitasPorFecha();
    }

    private void cargarCitasPorFecha() {
        String fechaTexto = convertirFechaCalendario(fechaSeleccionada);

        txtFechaSeleccionadaAdmin.setText("Fecha: " + fechaTexto);

        ArrayList<Cita> citas = RepositorioCitas.obtenerCitasPorFecha(fechaTexto);

        txtTotalCitasFechaAdmin.setText("Total de citas: " + citas.size());

        mostrarListadoCitas(citas);
    }

    private void buscarPorFecha() {
        String fechaBusqueda = edtBuscarFechaCitas.getText().toString().trim();

        if (fechaBusqueda.isEmpty()) {
            Toast.makeText(this, "Seleccione una fecha para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        txtFechaSeleccionadaAdmin.setText("Fecha: " + fechaBusqueda);

        ArrayList<Cita> citas = RepositorioCitas.obtenerCitasPorFecha(fechaBusqueda);

        txtTotalCitasFechaAdmin.setText("Total de citas: " + citas.size());

        mostrarListadoCitas(citas);
    }

    private void buscarPorCliente() {
        String clienteBusqueda = edtBuscarClienteCitas.getText().toString().trim();

        if (clienteBusqueda.isEmpty()) {
            edtBuscarClienteCitas.setError("Ingrese el nombre del cliente");
            edtBuscarClienteCitas.requestFocus();
            return;
        }

        txtFechaSeleccionadaAdmin.setText("Búsqueda por cliente");
        txtTotalCitasFechaAdmin.setText("Cliente: " + clienteBusqueda);

        ArrayList<Cita> citas = RepositorioCitas.obtenerCitasPorCliente(clienteBusqueda);

        mostrarListadoCitas(citas);
    }

    private void mostrarCalendarioBusqueda() {
        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog selectorFecha = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionadaTexto = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtBuscarFechaCitas.setText(fechaSeleccionadaTexto);

                    fechaSeleccionada.set(Calendar.YEAR, year);
                    fechaSeleccionada.set(Calendar.MONTH, month);
                    fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                },
                anio,
                mes,
                dia
        );

        selectorFecha.show();
    }

    private void mostrarListadoCitas(ArrayList<Cita> citas) {
        contenedorCitasAdmin.removeAllViews();

        if (citas.isEmpty()) {
            txtSinCitasAdmin.setVisibility(View.VISIBLE);
            contenedorCitasAdmin.setVisibility(View.GONE);
            return;
        }

        txtSinCitasAdmin.setVisibility(View.GONE);
        contenedorCitasAdmin.setVisibility(View.VISIBLE);

        for (Cita cita : citas) {
            LinearLayout tarjeta = crearTarjetaCita(cita);
            contenedorCitasAdmin.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCita(Cita cita) {
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
        }

        return tarjeta;
    }

    private void mostrarDialogoFinalizar(Cita cita) {
        new AlertDialog.Builder(this)
                .setTitle("Finalizar cita")
                .setMessage("¿Está seguro de marcar esta cita como finalizada? Esta acción la moverá al historial del cliente.")
                .setPositiveButton("Sí, finalizar", (dialog, which) -> {
                    RepositorioCitas.finalizarCita(cita);
                    Toast.makeText(this, "Cita finalizada correctamente", Toast.LENGTH_SHORT).show();
                    cargarCitasPorFecha();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private String convertirFechaCalendario(Calendar calendario) {
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }
}