package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroActivity extends AppCompatActivity {

    EditText edtNombreRegistro, edtApellidoRegistro, edtTelefonoRegistro, edtCorreoRegistro;
    EditText edtUsuarioRegistro, edtClaveRegistro, edtConfirmarClaveRegistro;
    AppCompatButton btnRegistrarUsuario, btnVolverRegistro;

    RequestQueue requestQueue;

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

        requestQueue = Volley.newRequestQueue(this);

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

        registrarUsuarioApi(nombre, apellido, telefono, correo, usuario, clave);
    }

    private void registrarUsuarioApi(String nombre,
                                     String apellido,
                                     String telefono,
                                     String correo,
                                     String usuario,
                                     String clave) {

        JSONObject datosRegistro = new JSONObject();

        try {
            datosRegistro.put("nombre", nombre);
            datosRegistro.put("apellido", apellido);
            datosRegistro.put("telefono", telefono);
            datosRegistro.put("correo", correo);
            datosRegistro.put("usuario", usuario);
            datosRegistro.put("clave", clave);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos del registro", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_REGISTRO,
                datosRegistro,
                response -> {
                    try {
                        JSONObject usuarioJson = response.getJSONObject("usuario");

                        int idUsuario = usuarioJson.getInt("id_usuario");
                        String nombreRespuesta = usuarioJson.getString("nombre");
                        String apellidoRespuesta = usuarioJson.getString("apellido");
                        String telefonoRespuesta = usuarioJson.getString("telefono");
                        String correoRespuesta = usuarioJson.getString("correo");
                        String usuarioRespuesta = usuarioJson.getString("usuario");
                        String rolRespuesta = usuarioJson.getString("rol");

                        SesionUsuario.iniciarSesion(
                                idUsuario,
                                nombreRespuesta,
                                apellidoRespuesta,
                                telefonoRespuesta,
                                correoRespuesta,
                                usuarioRespuesta,
                                rolRespuesta
                        );

                        PerfilUsuario perfil = new PerfilUsuario(
                                nombreRespuesta + " " + apellidoRespuesta,
                                telefonoRespuesta,
                                correoRespuesta
                        );

                        RepositorioPerfil.guardarPerfil(perfil);

                        Toast.makeText(
                                RegistroActivity.this,
                                "Registro realizado correctamente",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(RegistroActivity.this, MenuClienteActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        Toast.makeText(
                                RegistroActivity.this,
                                "Error al leer la respuesta del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    String mensaje = "No se pudo registrar el usuario";

                    if (error.networkResponse == null) {
                        mensaje = "No se pudo conectar con la API. Verifique que el backend esté encendido.";
                    } else if (error.networkResponse.statusCode == 409) {
                        mensaje = "Ya existe un usuario con ese correo o nombre de usuario";
                    } else if (error.networkResponse.statusCode == 400) {
                        mensaje = "Todos los campos son obligatorios";
                    }

                    Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
    }
}