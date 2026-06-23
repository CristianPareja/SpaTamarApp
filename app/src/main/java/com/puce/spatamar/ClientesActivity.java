package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClientesActivity extends AppCompatActivity {

    private TextView txtTotalClientes;
    private TextView txtSinClientes;

    private LinearLayout contenedorClientes;

    private AppCompatButton btnVolverClientes;

    private RequestQueue requestQueue;
    private ArrayList<ClienteApi> listaClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        txtTotalClientes = findViewById(R.id.txtTotalClientes);
        txtSinClientes = findViewById(R.id.txtSinClientes);

        contenedorClientes = findViewById(R.id.contenedorClientes);

        btnVolverClientes = findViewById(R.id.btnVolverClientes);

        requestQueue = Volley.newRequestQueue(this);
        listaClientes = new ArrayList<>();

        btnVolverClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarClientesRegistradosApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarClientesRegistradosApi();
    }

    private void cargarClientesRegistradosApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_USUARIOS,
                null,
                response -> {
                    try {
                        JSONArray usuariosJson = response.getJSONArray("usuarios");

                        listaClientes.clear();

                        for (int i = 0; i < usuariosJson.length(); i++) {
                            JSONObject usuarioJson = usuariosJson.getJSONObject(i);

                            String rol = usuarioJson.optString("rol", "");
                            boolean estado = usuarioJson.optBoolean("estado", true);

                            if (rol.equalsIgnoreCase("cliente") && estado) {
                                ClienteApi cliente = new ClienteApi(
                                        usuarioJson.getInt("id_usuario"),
                                        usuarioJson.optString("nombre", ""),
                                        usuarioJson.optString("apellido", ""),
                                        usuarioJson.optString("telefono", ""),
                                        usuarioJson.optString("correo", ""),
                                        usuarioJson.optString("usuario", ""),
                                        rol,
                                        estado
                                );

                                listaClientes.add(cliente);
                            }
                        }

                        mostrarClientes();

                    } catch (JSONException e) {
                        Toast.makeText(
                                ClientesActivity.this,
                                "Error al leer clientes del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            ClientesActivity.this,
                            "No se pudo consultar clientes desde la API",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarClientes() {
        txtTotalClientes.setText("Total de clientes registrados: " + listaClientes.size());

        contenedorClientes.removeAllViews();

        if (listaClientes.isEmpty()) {
            txtSinClientes.setVisibility(View.VISIBLE);
            contenedorClientes.setVisibility(View.GONE);
            return;
        }

        txtSinClientes.setVisibility(View.GONE);
        contenedorClientes.setVisibility(View.VISIBLE);

        for (ClienteApi cliente : listaClientes) {
            TextView tarjetaCliente = crearTarjetaCliente(cliente);
            contenedorClientes.addView(tarjetaCliente);
        }
    }

    private TextView crearTarjetaCliente(ClienteApi cliente) {
        TextView tarjeta = new TextView(this);

        String informacion = "Nombre: " + cliente.getNombreCompleto() + "\n"
                + "Teléfono: " + cliente.getTelefono() + "\n"
                + "Correo: " + cliente.getCorreo() + "\n"
                + "Usuario: " + cliente.getUsuario() + "\n"
                + "Rol: " + cliente.getRol();

        tarjeta.setText(informacion);
        tarjeta.setTextSize(15);
        tarjeta.setTextColor(getResources().getColor(android.R.color.black));
        tarjeta.setPadding(18, 18, 18, 18);
        tarjeta.setBackgroundResource(R.drawable.card_login);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametros.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametros);

        return tarjeta;
    }

    private static class ClienteApi {

        private int idUsuario;
        private String nombre;
        private String apellido;
        private String telefono;
        private String correo;
        private String usuario;
        private String rol;
        private boolean estado;

        public ClienteApi(int idUsuario,
                          String nombre,
                          String apellido,
                          String telefono,
                          String correo,
                          String usuario,
                          String rol,
                          boolean estado) {

            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.apellido = apellido;
            this.telefono = telefono;
            this.correo = correo;
            this.usuario = usuario;
            this.rol = rol;
            this.estado = estado;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public String getNombreCompleto() {
            return nombre + " " + apellido;
        }

        public String getTelefono() {
            return telefono;
        }

        public String getCorreo() {
            return correo;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getRol() {
            return rol;
        }

        public boolean isEstado() {
            return estado;
        }
    }
}