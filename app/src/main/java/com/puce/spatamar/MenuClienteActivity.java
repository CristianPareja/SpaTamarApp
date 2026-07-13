package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MenuClienteActivity extends AppCompatActivity {

    TextView txtSaludoCliente;
    TextView txtDescripcionCliente;
    TextView txtFechaCliente;

    AppCompatButton btnServiciosCliente;
    AppCompatButton btnPerfilCliente;
    AppCompatButton btnMisCitasCliente;
    AppCompatButton btnCuentasPendientesCliente;
    AppCompatButton btnCerrarSesionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);

        txtSaludoCliente = findViewById(R.id.txtSaludoCliente);
        txtDescripcionCliente = findViewById(R.id.txtDescripcionCliente);
        txtFechaCliente = findViewById(R.id.txtFechaCliente);

        btnServiciosCliente = findViewById(R.id.btnServiciosCliente);
        btnPerfilCliente = findViewById(R.id.btnPerfilCliente);
        btnMisCitasCliente = findViewById(R.id.btnMisCitasCliente);
        btnCuentasPendientesCliente = findViewById(R.id.btnCuentasPendientesCliente);
        btnCerrarSesionCliente = findViewById(R.id.btnCerrarSesionCliente);

        actualizarFechaCliente();
        cargarDatosBienvenida();

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
                SesionUsuario.cerrarSesion();

                Intent intent = new Intent(MenuClienteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        actualizarFechaCliente();
        cargarDatosBienvenida();
    }

    private void actualizarFechaCliente() {
        txtFechaCliente.setText(obtenerFechaActual());
    }

    private String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance(TimeZone.getTimeZone("America/Guayaquil"));

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return String.format(Locale.US, "%02d/%02d/%04d", dia, mes, anio);
    }

    private void cargarDatosBienvenida() {
        String nombreCliente = "cliente";

        if (SesionUsuario.haySesionActiva()) {
            nombreCliente = SesionUsuario.getNombre();

            if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
                nombreCliente = "cliente";
            }
        }

        txtSaludoCliente.setText("Hola, " + nombreCliente);
        txtDescripcionCliente.setText("Agenda tu cita y nosotros nos encargamos de tu cuidado personal.");
    }
}