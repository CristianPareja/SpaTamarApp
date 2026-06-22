package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;

public class CuentasPendientesActivity extends AppCompatActivity {

    private TextView txtClienteCuentaPendiente;
    private TextView txtTotalPendiente;
    private TextView txtSinCuentasPendientes;

    private LinearLayout contenedorCuentasPendientes;

    private AppCompatButton btnVolverCuentasPendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_pendientes);

        txtClienteCuentaPendiente = findViewById(R.id.txtClienteCuentaPendiente);
        txtTotalPendiente = findViewById(R.id.txtTotalPendiente);
        txtSinCuentasPendientes = findViewById(R.id.txtSinCuentasPendientes);

        contenedorCuentasPendientes = findViewById(R.id.contenedorCuentasPendientes);

        btnVolverCuentasPendientes = findViewById(R.id.btnVolverCuentasPendientes);

        btnVolverCuentasPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarCuentasPendientes();
    }

    private void cargarCuentasPendientes() {
        if (!RepositorioPerfil.existePerfil()) {
            Toast.makeText(
                    this,
                    "No se encontró un perfil registrado.",
                    Toast.LENGTH_LONG
            ).show();

            txtClienteCuentaPendiente.setText("Cliente: no identificado");
            txtTotalPendiente.setText("Total pendiente: $0.00");
            txtSinCuentasPendientes.setVisibility(View.VISIBLE);
            contenedorCuentasPendientes.setVisibility(View.GONE);
            return;
        }

        PerfilUsuario perfil = RepositorioPerfil.obtenerPerfil();
        String nombreCliente = perfil.getNombre();

        txtClienteCuentaPendiente.setText("Cliente: " + nombreCliente);

        ArrayList<CuentaPendiente> cuentasCliente = RepositorioCuentasPendientes.obtenerCuentasPendientesPorCliente(nombreCliente);
        double totalPendiente = RepositorioCuentasPendientes.calcularTotalPendientePorCliente(nombreCliente);

        txtTotalPendiente.setText(String.format("Total pendiente: $%.2f", totalPendiente));

        contenedorCuentasPendientes.removeAllViews();

        if (cuentasCliente.isEmpty()) {
            txtSinCuentasPendientes.setVisibility(View.VISIBLE);
            contenedorCuentasPendientes.setVisibility(View.GONE);
            return;
        }

        txtSinCuentasPendientes.setVisibility(View.GONE);
        contenedorCuentasPendientes.setVisibility(View.VISIBLE);

        for (CuentaPendiente cuenta : cuentasCliente) {
            TextView tarjeta = crearTarjetaCuentaPendiente(cuenta);
            contenedorCuentasPendientes.addView(tarjeta);
        }
    }

    private TextView crearTarjetaCuentaPendiente(CuentaPendiente cuenta) {
        TextView tarjeta = new TextView(this);

        String informacion = "Concepto: " + cuenta.getConcepto() + "\n"
                + "Fecha: " + cuenta.getFecha() + "\n"
                + "Valor pendiente: $" + String.format("%.2f", cuenta.getValorPendiente()) + "\n"
                + "Estado: " + cuenta.getEstado() + "\n"
                + "Observación: " + cuenta.getObservacion();

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