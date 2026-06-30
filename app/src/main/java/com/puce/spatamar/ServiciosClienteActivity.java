package com.puce.spatamar;

import android.content.Intent;
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
import java.util.Locale;

public class ServiciosClienteActivity extends AppCompatActivity {

    LinearLayout contenedorServiciosCliente;
    AppCompatButton btnVolverServiciosCliente;

    RequestQueue requestQueue;
    ArrayList<Servicio> listaServiciosActivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios_cliente);

        contenedorServiciosCliente = findViewById(R.id.contenedorServiciosCliente);
        btnVolverServiciosCliente = findViewById(R.id.btnVolverServiciosCliente);

        requestQueue = Volley.newRequestQueue(this);
        listaServiciosActivos = new ArrayList<>();

        cargarServiciosActivosApi();

        btnVolverServiciosCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarServiciosActivosApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_SERVICIOS_ACTIVOS,
                null,
                response -> {
                    try {
                        JSONArray serviciosJson = response.getJSONArray("servicios");

                        listaServiciosActivos.clear();

                        for (int i = 0; i < serviciosJson.length(); i++) {
                            JSONObject servicioJson = serviciosJson.getJSONObject(i);

                            int idServicio = servicioJson.getInt("id_servicio");
                            String nombre = servicioJson.getString("nombre");
                            String descripcion = servicioJson.getString("descripcion");
                            double precio = servicioJson.getDouble("precio");
                            boolean activo = servicioJson.getBoolean("activo");

                            Servicio servicio = new Servicio(
                                    idServicio,
                                    nombre,
                                    descripcion,
                                    precio,
                                    activo
                            );

                            listaServiciosActivos.add(servicio);
                        }

                        RepositorioServicios.guardarServicios(listaServiciosActivos);
                        mostrarServicios();

                    } catch (JSONException e) {
                        Toast.makeText(
                                ServiciosClienteActivity.this,
                                "Error al leer servicios del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            ServiciosClienteActivity.this,
                            "No se pudo conectar con la API. Se mostrarán servicios locales.",
                            Toast.LENGTH_LONG
                    ).show();

                    listaServiciosActivos = RepositorioServicios.obtenerServiciosActivos();
                    mostrarServicios();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarServicios() {
        contenedorServiciosCliente.removeAllViews();

        if (listaServiciosActivos.isEmpty()) {
            TextView txtSinServicios = new TextView(this);
            txtSinServicios.setText("No existen servicios disponibles por el momento.");
            txtSinServicios.setTextSize(16);
            txtSinServicios.setPadding(20, 20, 20, 20);
            contenedorServiciosCliente.addView(txtSinServicios);
            return;
        }

        for (Servicio servicio : listaServiciosActivos) {
            View tarjetaServicio = getLayoutInflater().inflate(R.layout.item_servicio_cliente, null);

            TextView txtNombreServicio = tarjetaServicio.findViewById(R.id.txtNombreServicioCliente);
            TextView txtDescripcionServicio = tarjetaServicio.findViewById(R.id.txtDescripcionServicioCliente);
            TextView txtPrecioServicio = tarjetaServicio.findViewById(R.id.txtPrecioServicioCliente);

            txtNombreServicio.setText(servicio.getNombre());
            txtDescripcionServicio.setText(servicio.getDescripcion());
            txtPrecioServicio.setText("$" + String.format(Locale.US, "%.2f", servicio.getPrecio()));

            tarjetaServicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ServiciosClienteActivity.this, AgendarCitaActivity.class);
                    intent.putExtra("idServicioSeleccionado", servicio.getIdServicio());
                    intent.putExtra("servicioSeleccionado", servicio.getNombre());
                    startActivity(intent);
                }
            });

            contenedorServiciosCliente.addView(tarjetaServicio);
        }
    }
}