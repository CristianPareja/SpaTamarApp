package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MisCitasActivity extends AppCompatActivity {

    private TextView txtSinCitas;
    private LinearLayout contenedorCitas;
    private AppCompatButton btnVolverMisCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_citas);

        txtSinCitas = findViewById(R.id.txtSinCitas);
        contenedorCitas = findViewById(R.id.contenedorCitas);
        btnVolverMisCitas = findViewById(R.id.btnVolverMisCitas);

        btnVolverMisCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarCitas();
    }

    private void cargarCitas() {
        if (RepositorioCitas.obtenerCitas().isEmpty()) {
            txtSinCitas.setVisibility(View.VISIBLE);
            contenedorCitas.setVisibility(View.GONE);
        } else {
            txtSinCitas.setVisibility(View.GONE);
            contenedorCitas.setVisibility(View.VISIBLE);

            contenedorCitas.removeAllViews();

            for (Cita cita : RepositorioCitas.obtenerCitas()) {
                TextView tarjetaCita = crearTarjetaCita(cita);
                contenedorCitas.addView(tarjetaCita);
            }
        }
    }

    private TextView crearTarjetaCita(Cita cita) {
        TextView tarjeta = new TextView(this);

        String informacion = "Servicio: " + cita.getServicio() + "\n"
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
}