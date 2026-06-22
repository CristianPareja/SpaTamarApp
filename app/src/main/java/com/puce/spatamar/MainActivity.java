package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MainActivity extends AppCompatActivity {

    EditText edtUsuario, edtClave;
    AppCompatButton btnIngresar;
    TextView txtRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsuario = findViewById(R.id.edtUsuario);
        edtClave = findViewById(R.id.edtClave);
        btnIngresar = findViewById(R.id.btnIngresar);
        txtRegistrarse = findViewById(R.id.txtRegistrarse);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarLogin();
            }
        });

        txtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validarLogin() {
        String usuarioTexto = edtUsuario.getText().toString().trim();
        String clave = edtClave.getText().toString().trim();

        if (usuarioTexto.isEmpty()) {
            edtUsuario.setError("Ingrese usuario o correo");
            edtUsuario.requestFocus();
            return;
        }

        if (clave.isEmpty()) {
            edtClave.setError("Ingrese contraseña");
            edtClave.requestFocus();
            return;
        }

        Usuario usuarioEncontrado = RepositorioUsuarios.validarLogin(usuarioTexto, clave);

        if (usuarioEncontrado == null) {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioEncontrado.getRol().equalsIgnoreCase("administrador")) {
            Toast.makeText(this, "Bienvenido administrador", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, MenuAdministradorActivity.class);
            startActivity(intent);
            finish();

        } else {
            PerfilUsuario perfil = new PerfilUsuario(
                    usuarioEncontrado.getNombreCompleto(),
                    usuarioEncontrado.getTelefono(),
                    usuarioEncontrado.getCorreo()
            );

            RepositorioPerfil.registrarPerfilInicial(perfil);

            Toast.makeText(this, "Bienvenido " + usuarioEncontrado.getNombre(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, MenuClienteActivity.class);
            startActivity(intent);
            finish();
        }
    }
}