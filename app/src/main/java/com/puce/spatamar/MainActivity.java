package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText edtUsuario, edtClave;
    AppCompatButton btnIngresar;
    TextView txtRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
                Toast.makeText(MainActivity.this, "Aquí se abrirá la pantalla de registro", Toast.LENGTH_SHORT).show();
            }
        });
        }

    private void validarLogin() {
        String usuario = edtUsuario.getText().toString().trim();
        String clave = edtClave.getText().toString().trim();
        if (usuario.isEmpty()) {
            edtUsuario.setError("Ingrese usuario o correo");
            edtUsuario.requestFocus();
            return;
        }
        if (clave.isEmpty()) {
            edtClave.setError("Ingrese contraseña");
            edtClave.requestFocus();
            return;
        }

        if (usuario.equals("cliente") && clave.equals("1234")) {
            Toast.makeText(this, "Bienvenido cliente", Toast.LENGTH_SHORT).show();

        } else if (usuario.equals("admin") && clave.equals("1234")) {
            Toast.makeText(this, "Bienvenido administrador", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    ;
    }