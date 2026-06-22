package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Locale;

public class ServiciosClienteActivity extends AppCompatActivity {

    private TextView txtSinServiciosCliente;
    private LinearLayout contenedorServiciosCliente;
    private AppCompatButton btnVolverServiciosCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios_cliente);

        txtSinServiciosCliente = findViewById(R.id.txtSinServiciosCliente);
        contenedorServiciosCliente = findViewById(R.id.contenedorServiciosCliente);
        btnVolverServiciosCliente = findViewById(R.id.btnVolverServiciosCliente);

        btnVolverServiciosCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarServiciosDisponibles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarServiciosDisponibles();
    }

    private void cargarServiciosDisponibles() {
        ArrayList<Servicio> serviciosActivos = RepositorioServicios.obtenerServiciosActivos();

        contenedorServiciosCliente.removeAllViews();

        if (serviciosActivos.isEmpty()) {
            txtSinServiciosCliente.setVisibility(View.VISIBLE);
            contenedorServiciosCliente.setVisibility(View.GONE);
            return;
        }

        txtSinServiciosCliente.setVisibility(View.GONE);
        contenedorServiciosCliente.setVisibility(View.VISIBLE);

        for (Servicio servicio : serviciosActivos) {
            TextView tarjeta = crearTarjetaServicioCliente(servicio);
            contenedorServiciosCliente.addView(tarjeta);
        }
    }

    private TextView crearTarjetaServicioCliente(Servicio servicio) {
        TextView tarjeta = new TextView(this);

        String informacion = "Servicio: " + servicio.getNombre() + "\n"
                + "Descripción: " + servicio.getDescripcion() + "\n"
                + "Precio: $" + String.format(Locale.US, "%.2f", servicio.getPrecio());

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