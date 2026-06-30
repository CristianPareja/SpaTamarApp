package com.puce.spatamar;

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

import java.nio.charset.StandardCharsets;

public class RecuperarClaveActivity extends AppCompatActivity {

    private EditText edtCorreoRecuperacion;
    private EditText edtCodigoRecuperacion;
    private EditText edtNuevaClave;
    private EditText edtConfirmarNuevaClave;

    private AppCompatButton btnSolicitarCodigo;
    private AppCompatButton btnRestablecerClave;
    private AppCompatButton btnVolverRecuperarClave;

    private RequestQueue requestQueue;

    private final String REGEX_CORREO = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private final String REGEX_CODIGO = "^[0-9]{6}$";
    private final String REGEX_CLAVE = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=.,;:¿?¡])[A-Za-z\\d!@#$%^&*()_\\-+=.,;:¿?¡]{6,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);

        edtCorreoRecuperacion = findViewById(R.id.edtCorreoRecuperacion);
        edtCodigoRecuperacion = findViewById(R.id.edtCodigoRecuperacion);
        edtNuevaClave = findViewById(R.id.edtNuevaClave);
        edtConfirmarNuevaClave = findViewById(R.id.edtConfirmarNuevaClave);

        btnSolicitarCodigo = findViewById(R.id.btnSolicitarCodigo);
        btnRestablecerClave = findViewById(R.id.btnRestablecerClave);
        btnVolverRecuperarClave = findViewById(R.id.btnVolverRecuperarClave);

        requestQueue = Volley.newRequestQueue(this);

        btnSolicitarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarCodigo();
            }
        });

        btnRestablecerClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restablecerClave();
            }
        });

        btnVolverRecuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void solicitarCodigo() {
        String correo = edtCorreoRecuperacion.getText().toString().trim();

        if (correo.isEmpty()) {
            edtCorreoRecuperacion.setError("Ingrese el correo registrado");
            edtCorreoRecuperacion.requestFocus();
            return;
        }

        if (!correo.matches(REGEX_CORREO)) {
            edtCorreoRecuperacion.setError("Ingrese un correo válido. Ejemplo: usuario@correo.com");
            edtCorreoRecuperacion.requestFocus();
            return;
        }

        JSONObject datos = new JSONObject();

        try {
            datos.put("correo", correo);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_SOLICITAR_RECUPERACION,
                datos,
                response -> {
                    Toast.makeText(
                            RecuperarClaveActivity.this,
                            "Si el correo está registrado, recibirá un código de recuperación.",
                            Toast.LENGTH_LONG
                    ).show();
                },
                error -> {
                    String mensaje = "No se pudo solicitar el código. Verifique la conexión con la API.";

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mensaje = obtenerMensajeErrorBackend(error.networkResponse.data);
                    }

                    Toast.makeText(
                            RecuperarClaveActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void restablecerClave() {
        String correo = edtCorreoRecuperacion.getText().toString().trim();
        String codigo = edtCodigoRecuperacion.getText().toString().trim();
        String nuevaClave = edtNuevaClave.getText().toString().trim();
        String confirmarClave = edtConfirmarNuevaClave.getText().toString().trim();

        if (correo.isEmpty()) {
            edtCorreoRecuperacion.setError("Ingrese el correo registrado");
            edtCorreoRecuperacion.requestFocus();
            return;
        }

        if (!correo.matches(REGEX_CORREO)) {
            edtCorreoRecuperacion.setError("Ingrese un correo válido. Ejemplo: usuario@correo.com");
            edtCorreoRecuperacion.requestFocus();
            return;
        }

        if (codigo.isEmpty()) {
            edtCodigoRecuperacion.setError("Ingrese el código recibido");
            edtCodigoRecuperacion.requestFocus();
            return;
        }

        if (!codigo.matches(REGEX_CODIGO)) {
            edtCodigoRecuperacion.setError("El código debe tener exactamente 6 dígitos");
            edtCodigoRecuperacion.requestFocus();
            return;
        }

        if (nuevaClave.isEmpty()) {
            edtNuevaClave.setError("Ingrese la nueva contraseña");
            edtNuevaClave.requestFocus();
            return;
        }

        if (!nuevaClave.matches(REGEX_CLAVE)) {
            edtNuevaClave.setError("Mínimo 6 caracteres, una mayúscula, un número y un carácter especial");
            edtNuevaClave.requestFocus();
            return;
        }

        if (confirmarClave.isEmpty()) {
            edtConfirmarNuevaClave.setError("Confirme la nueva contraseña");
            edtConfirmarNuevaClave.requestFocus();
            return;
        }

        if (!nuevaClave.equals(confirmarClave)) {
            edtConfirmarNuevaClave.setError("Las contraseñas no coinciden");
            edtConfirmarNuevaClave.requestFocus();
            return;
        }

        JSONObject datos = new JSONObject();

        try {
            datos.put("correo", correo);
            datos.put("codigo", codigo);
            datos.put("nueva_clave", nuevaClave);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_RESTABLECER_CLAVE,
                datos,
                response -> {
                    Toast.makeText(
                            RecuperarClaveActivity.this,
                            "Contraseña restablecida correctamente. Inicie sesión con su nueva clave.",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                },
                error -> {
                    String mensaje = "No se pudo restablecer la contraseña";

                    if (error.networkResponse == null) {
                        mensaje = "No se pudo conectar con la API.";
                    } else if (error.networkResponse.data != null) {
                        mensaje = obtenerMensajeErrorBackend(error.networkResponse.data);
                    } else if (error.networkResponse.statusCode == 400) {
                        mensaje = "Código inválido o expirado.";
                    }

                    Toast.makeText(
                            RecuperarClaveActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private String obtenerMensajeErrorBackend(byte[] data) {
        try {
            String respuesta = new String(data, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(respuesta);

            if (json.has("mensaje")) {
                return json.getString("mensaje");
            }

        } catch (Exception e) {
            return "Los datos ingresados no cumplen las validaciones requeridas";
        }

        return "Los datos ingresados no cumplen las validaciones requeridas";
    }
}