package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class RegistroActivity extends AppCompatActivity {

    EditText edtNombreRegistro, edtApellidoRegistro, edtTelefonoRegistro, edtCorreoRegistro;
    EditText edtUsuarioRegistro, edtClaveRegistro, edtConfirmarClaveRegistro;
    AppCompatButton btnRegistrarUsuario, btnVolverRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNombreRegistro = findViewById(R.id.edtNombreRegistro);
        edtApellidoRegistro = findViewById(R.id.edtApellidoRegistro);
        edtTelefonoRegistro = findViewById(R.id.edtTelefonoRegistro);
        edtCorreoRegistro = findViewById(R.id.edtCorreoRegistro);
        edtUsuarioRegistro = findViewById(R.id.edtUsuarioRegistro);
        edtClaveRegistro = findViewById(R.id.edtClaveRegistro);
        edtConfirmarClaveRegistro = findViewById(R.id.edtConfirmarClaveRegistro);

        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);
        btnVolverRegistro = findViewById(R.id.btnVolverRegistro);

        btnRegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarRegistro();
            }
        });

        btnVolverRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void validarRegistro() {
        String nombre = edtNombreRegistro.getText().toString().trim();
        String apellido = edtApellidoRegistro.getText().toString().trim();
        String telefono = edtTelefonoRegistro.getText().toString().trim();
        String correo = edtCorreoRegistro.getText().toString().trim();
        String usuario = edtUsuarioRegistro.getText().toString().trim();
        String clave = edtClaveRegistro.getText().toString().trim();
        String confirmarClave = edtConfirmarClaveRegistro.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombreRegistro.setError("Ingrese el nombre");
            edtNombreRegistro.requestFocus();
            return;
        }

        if (apellido.isEmpty()) {
            edtApellidoRegistro.setError("Ingrese el apellido");
            edtApellidoRegistro.requestFocus();
            return;
        }

        if (telefono.isEmpty()) {
            edtTelefonoRegistro.setError("Ingrese el teléfono");
            edtTelefonoRegistro.requestFocus();
            return;
        }

        if (telefono.length() < 10) {
            edtTelefonoRegistro.setError("Ingrese un teléfono válido de 10 dígitos");
            edtTelefonoRegistro.requestFocus();
            return;
        }

        if (correo.isEmpty()) {
            edtCorreoRegistro.setError("Ingrese el correo");
            edtCorreoRegistro.requestFocus();
            return;
        }

        if (!correo.contains("@")) {
            edtCorreoRegistro.setError("Ingrese un correo válido");
            edtCorreoRegistro.requestFocus();
            return;
        }

        if (usuario.isEmpty()) {
            edtUsuarioRegistro.setError("Ingrese un usuario");
            edtUsuarioRegistro.requestFocus();
            return;
        }

        if (clave.isEmpty()) {
            edtClaveRegistro.setError("Ingrese una contraseña");
            edtClaveRegistro.requestFocus();
            return;
        }

        if (confirmarClave.isEmpty()) {
            edtConfirmarClaveRegistro.setError("Confirme la contraseña");
            edtConfirmarClaveRegistro.requestFocus();
            return;
        }

        if (!clave.equals(confirmarClave)) {
            edtConfirmarClaveRegistro.setError("Las contraseñas no coinciden");
            edtConfirmarClaveRegistro.requestFocus();
            return;
        }

        if (RepositorioUsuarios.existeUsuario(usuario, correo)) {
            Toast.makeText(this, "El usuario o correo ya se encuentra registrado", Toast.LENGTH_LONG).show();
            return;
        }

        Usuario nuevoUsuario = new Usuario(
                nombre,
                apellido,
                telefono,
                correo,
                usuario,
                clave,
                "cliente"
        );

        RepositorioUsuarios.agregarUsuario(nuevoUsuario);

        String nombreCompleto = nombre + " " + apellido;

        PerfilUsuario perfil = new PerfilUsuario(
                nombreCompleto,
                telefono,
                correo
        );

        RepositorioPerfil.registrarPerfilInicial(perfil);

        Toast.makeText(this, "Registro realizado correctamente", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}