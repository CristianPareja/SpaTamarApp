package com.puce.spatamar;

import android.app.DatePickerDialog;
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

        cargarCitasPorFecha();

        btnDiaAnteriorCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaSeleccionada.add(Calendar.DAY_OF_MONTH, -1);
                edtBuscarClienteCitas.setText("");
                cargarCitasPorFecha();
            }
        });

        btnDiaSiguienteCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaSeleccionada.add(Calendar.DAY_OF_MONTH, 1);
                edtBuscarClienteCitas.setText("");
                cargarCitasPorFecha();
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

                    RepositorioCitas.finalizarCita(cita);

                    RepositorioFinanciero.registrarIngreso(
                            "Cita finalizada",
                            cita.getServicio(),
                            obtenerFechaActual(),
                            valorCobrado,
                            cita.getNombreCliente(),
                            "Ingreso registrado al finalizar cita"
                    );

                    Toast.makeText(this, "Cita finalizada e ingreso registrado", Toast.LENGTH_SHORT).show();

                    if (edtBuscarClienteCitas.getText().toString().trim().isEmpty()) {
                        cargarCitasPorFecha();
                    } else {
                        buscarPorCliente();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String convertirFechaCalendario(Calendar calendario) {
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }

    private String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance();

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }
}