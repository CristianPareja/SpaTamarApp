package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText edtUsuario, edtClave;
    AppCompatButton btnIngresar;
    TextView txtRegistrarse, txtOlvideClave;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsuario = findViewById(R.id.edtUsuario);
        edtClave = findViewById(R.id.edtClave);
        btnIngresar = findViewById(R.id.btnIngresar);
        txtRegistrarse = findViewById(R.id.txtRegistrarse);
        txtOlvideClave = findViewById(R.id.txtOlvideClave);

        requestQueue = Volley.newRequestQueue(this);

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

        txtOlvideClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecuperarClaveActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validarLogin() {
        String usuarioOCorreo = edtUsuario.getText().toString().trim();
        String clave = edtClave.getText().toString().trim();

        if (usuarioOCorreo.isEmpty()) {
            edtUsuario.setError("Ingrese usuario o correo");
            edtUsuario.requestFocus();
            return;
        }

        if (clave.isEmpty()) {
            edtClave.setError("Ingrese contraseña");
            edtClave.requestFocus();
            return;
        }

        iniciarSesionApi(usuarioOCorreo, clave);
    }

    private void iniciarSesionApi(String usuarioOCorreo, String clave) {
        JSONObject datosLogin = new JSONObject();

        try {
            datosLogin.put("usuarioOCorreo", usuarioOCorreo);
            datosLogin.put("clave", clave);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos de login", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_LOGIN,
                datosLogin,
                response -> {
                    try {
                        JSONObject usuarioJson = response.getJSONObject("usuario");

                        int idUsuario = usuarioJson.getInt("id_usuario");
                        String nombre = usuarioJson.getString("nombre");
                        String apellido = usuarioJson.getString("apellido");
                        String telefono = usuarioJson.getString("telefono");
                        String correo = usuarioJson.getString("correo");
                        String usuario = usuarioJson.getString("usuario");
                        String rol = usuarioJson.getString("rol");

                        SesionUsuario.iniciarSesion(
                                idUsuario,
                                nombre,
                                apellido,
                                telefono,
                                correo,
                                usuario,
                                rol
                        );

                        PerfilUsuario perfil = new PerfilUsuario(
                                nombre + " " + apellido,
                                telefono,
                                correo
                        );

                        RepositorioPerfil.guardarPerfil(perfil);

                        Toast.makeText(
                                MainActivity.this,
                                "Bienvenido " + nombre,
                                Toast.LENGTH_SHORT
                        ).show();

                        if (rol.equalsIgnoreCase("administrador")) {
                            Intent intent = new Intent(MainActivity.this, MenuAdministradorActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(MainActivity.this, MenuClienteActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(
                                MainActivity.this,
                                "Error al leer la respuesta del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    String mensaje = "Usuario o contraseña incorrectos";

                    if (error.networkResponse == null) {
                        mensaje = "No se pudo conectar con la API. Verifique que el backend esté encendido.";
                    } else if (error.networkResponse.statusCode == 401) {
                        mensaje = "Usuario o contraseña incorrectos";
                    } else if (error.networkResponse.statusCode == 400) {
                        mensaje = "Ingrese usuario/correo y contraseña";
                    }

                    Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
    }
}