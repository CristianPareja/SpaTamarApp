package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;

public class MisCitasActivity extends AppCompatActivity {

    private TextView txtSinCitasActuales;
    private TextView txtSinHistorial;

    private LinearLayout contenedorCitasActuales;
    private LinearLayout contenedorHistorialCitas;

    private AppCompatButton btnVolverMisCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_citas);

        txtSinCitasActuales = findViewById(R.id.txtSinCitasActuales);
        txtSinHistorial = findViewById(R.id.txtSinHistorial);

        contenedorCitasActuales = findViewById(R.id.contenedorCitasActuales);
        contenedorHistorialCitas = findViewById(R.id.contenedorHistorialCitas);

        btnVolverMisCitas = findViewById(R.id.btnVolverMisCitas);

        btnVolverMisCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarCitasActuales();
        cargarHistorialCitas();
    }

    private void cargarCitasActuales() {
        ArrayList<Cita> citasActuales = RepositorioCitas.obtenerCitasActuales();

        contenedorCitasActuales.removeAllViews();

        if (citasActuales.isEmpty()) {
            txtSinCitasActuales.setVisibility(View.VISIBLE);
            contenedorCitasActuales.setVisibility(View.GONE);
            return;
        }

        txtSinCitasActuales.setVisibility(View.GONE);
        contenedorCitasActuales.setVisibility(View.VISIBLE);

        for (Cita cita : citasActuales) {
            LinearLayout tarjeta = crearTarjetaCitaActual(cita);
            contenedorCitasActuales.addView(tarjeta);
        }
    }

    private void cargarHistorialCitas() {
        ArrayList<Cita> historial = RepositorioCitas.obtenerHistorialCitas();

        contenedorHistorialCitas.removeAllViews();

        if (historial.isEmpty()) {
            txtSinHistorial.setVisibility(View.VISIBLE);
            contenedorHistorialCitas.setVisibility(View.GONE);
            return;
        }

        txtSinHistorial.setVisibility(View.GONE);
        contenedorHistorialCitas.setVisibility(View.VISIBLE);

        for (Cita cita : historial) {
            TextView tarjeta = crearTarjetaHistorial(cita);
            contenedorHistorialCitas.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCitaActual(Cita cita) {
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
                + "Servicio: " + cita.getServicio() + "\n"
                + "Fecha: " + cita.getFecha() + "\n"
                + "Hora: " + cita.getHora() + "\n"
                + "Estado: " + cita.getEstado() + "\n"
                + "Observaciones: " + cita.getObservaciones();

        informacion.setText(texto);
        informacion.setTextSize(15);
        informacion.setTextColor(getResources().getColor(android.R.color.black));
        informacion.setPadding(0, 0, 0, 14);

        AppCompatButton btnCancelar = new AppCompatButton(this);
        btnCancelar.setText("Cancelar cita");
        btnCancelar.setTextSize(14);
        btnCancelar.setTextColor(getResources().getColor(android.R.color.white));
        btnCancelar.setAllCaps(false);
        btnCancelar.setBackgroundResource(R.drawable.boton_principal);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoCancelar(cita);
            }
        });

        tarjeta.addView(informacion);
        tarjeta.addView(btnCancelar);

        return tarjeta;
    }

    private TextView crearTarjetaHistorial(Cita cita) {
        TextView tarjeta = new TextView(this);

        String informacion = "Cliente: " + cita.getNombreCliente() + "\n"
                + "Servicio: " + cita.getServicio() + "\n"
                + "Fecha: " + cita.getFecha() + "\n"
                + "Hora: " + cita.getHora() + "\n"
                + "Estado: " + cita.getEstado() + "\n"
                + "Observaciones: " + cita.getObservaciones();

        tarjeta.setText(informacion);
        tarjeta.setTextSize(15);
        tarjeta.setTextColor(getResources().getColor(android.R.color.black));
        tarjeta.setPadding(18, 18, 18, 18);
        tarjeta.setBackgroundResource(R.drawable.card_login);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametros.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametros);

        return tarjeta;
    }

    private void mostrarDialogoCancelar(Cita cita) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar cita")
                .setMessage("Antes de continuar, recuerde que la cancelación de una cita puede realizarse hasta 1 hora antes del horario establecido. ¿Está seguro de que desea cancelar esta cita?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    RepositorioCitas.cancelarCita(cita);
                    cargarCitasActuales();
                    cargarHistorialCitas();
                })
                .setNegativeButton("No", null)
                .show();
    }
}