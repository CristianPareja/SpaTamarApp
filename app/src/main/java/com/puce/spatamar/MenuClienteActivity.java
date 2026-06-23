package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MenuClienteActivity extends AppCompatActivity {

    TextView txtCampanitaRecordatorio;

    AppCompatButton btnServiciosCliente;
    AppCompatButton btnPerfilCliente;
    AppCompatButton btnMisCitasCliente;
    AppCompatButton btnCuentasPendientesCliente;
    AppCompatButton btnCerrarSesionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);

        txtCampanitaRecordatorio = findViewById(R.id.txtCampanitaRecordatorio);

        btnServiciosCliente = findViewById(R.id.btnServiciosCliente);
        btnPerfilCliente = findViewById(R.id.btnPerfilCliente);
        btnMisCitasCliente = findViewById(R.id.btnMisCitasCliente);
        btnCuentasPendientesCliente = findViewById(R.id.btnCuentasPendientesCliente);
        btnCerrarSesionCliente = findViewById(R.id.btnCerrarSesionCliente);

        actualizarCampanita();

        txtCampanitaRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pendientes = RepositorioCitas.obtenerCitasActuales().size();

                if (pendientes == 0) {
                    Toast.makeText(
                            MenuClienteActivity.this,
                            "No tiene citas pendientes.",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            MenuClienteActivity.this,
                            "Tiene " + pendientes + " cita(s) pendiente(s). Revise la sección Mis citas e historial.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        btnServiciosCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, ServiciosClienteActivity.class);
                startActivity(intent);
            }
        });

        btnPerfilCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, PerfilClienteActivity.class);
                startActivity(intent);
            }
        });

        btnMisCitasCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, MisCitasActivity.class);
                startActivity(intent);
            }
        });

        btnCuentasPendientesCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, CuentasPendientesActivity.class);
                startActivity(intent);
            }
        });

        btnCerrarSesionCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarCampanita();
    }

    private void actualizarCampanita() {
        int pendientes = RepositorioCitas.obtenerCitasActuales().size();

        if (pendientes > 0) {
            txtCampanitaRecordatorio.setText("🔔 " + pendientes);
        } else {
            txtCampanitaRecordatorio.setText("🔔");
        }
    }
}