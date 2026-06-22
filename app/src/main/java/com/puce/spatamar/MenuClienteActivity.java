package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MenuClienteActivity extends AppCompatActivity {

    AppCompatButton btnServiciosCliente;
    AppCompatButton btnPerfilCliente;
    AppCompatButton btnAgendarCitaCliente;
    AppCompatButton btnMisCitasCliente;
    AppCompatButton btnCerrarSesionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);

        btnServiciosCliente = findViewById(R.id.btnServiciosCliente);
        btnPerfilCliente = findViewById(R.id.btnPerfilCliente);
        btnAgendarCitaCliente = findViewById(R.id.btnAgendarCitaCliente);
        btnMisCitasCliente = findViewById(R.id.btnMisCitasCliente);
        btnCerrarSesionCliente = findViewById(R.id.btnCerrarSesionCliente);

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

        btnAgendarCitaCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, AgendarCitaActivity.class);
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

        btnCerrarSesionCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}