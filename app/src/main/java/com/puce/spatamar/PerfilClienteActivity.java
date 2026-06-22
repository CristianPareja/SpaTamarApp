package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class PerfilClienteActivity extends AppCompatActivity {

    private EditText edtNombrePerfil;
    private EditText edtTelefonoPerfil;
    private EditText edtCorreoPerfil;

    private AppCompatButton btnGuardarPerfil;
    private AppCompatButton btnVolverPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_cliente);

        edtNombrePerfil = findViewById(R.id.edtNombrePerfil);
        edtTelefonoPerfil = findViewById(R.id.edtTelefonoPerfil);
        edtCorreoPerfil = findViewById(R.id.edtCorreoPerfil);

        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnVolverPerfil = findViewById(R.id.btnVolverPerfil);

        cargarPerfilRegistrado();

        btnGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarPerfil();
            }
        });

        btnVolverPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarPerfilRegistrado() {
        if (!RepositorioPerfil.existePerfil()) {
            Toast.makeText(
                    this,
                    "No se encontró un perfil registrado. Primero debe registrarse o iniciar sesión.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        PerfilUsuario perfil = RepositorioPerfil.obtenerPerfil();

        edtNombrePerfil.setText(perfil.getNombre());
        edtTelefonoPerfil.setText(perfil.getTelefono());
        edtCorreoPerfil.setText(perfil.getCorreo());
    }

    private void actualizarPerfil() {
        String nombre = edtNombrePerfil.getText().toString().trim();
        String telefono = edtTelefonoPerfil.getText().toString().trim();
        String correo = edtCorreoPerfil.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombrePerfil.setError("Ingrese su nombre completo");
            edtNombrePerfil.requestFocus();
            return;
        }

        if (telefono.isEmpty()) {
            edtTelefonoPerfil.setError("Ingrese su teléfono");
            edtTelefonoPerfil.requestFocus();
            return;
        }

        if (telefono.length() < 10) {
            edtTelefonoPerfil.setError("Ingrese un teléfono válido de 10 dígitos");
            edtTelefonoPerfil.requestFocus();
            return;
        }

        if (correo.isEmpty()) {
            edtCorreoPerfil.setError("Ingrese su correo electrónico");
            edtCorreoPerfil.requestFocus();
            return;
        }

        RepositorioPerfil.actualizarPerfil(nombre, telefono, correo);

        Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}