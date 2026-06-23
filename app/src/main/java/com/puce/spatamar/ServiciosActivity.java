package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class ServiciosActivity extends AppCompatActivity {

    EditText edtNombreServicio, edtDescripcionServicio, edtPrecioServicio;
    AppCompatButton btnGuardarServicio, btnLimpiarServicio, btnVolverServicios;
    LinearLayout contenedorServiciosAdmin;

    RequestQueue requestQueue;
    ArrayList<Servicio> listaServicios;

    int idServicioSeleccionado = 0;
    boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        edtNombreServicio = findViewById(R.id.edtNombreServicio);
        edtDescripcionServicio = findViewById(R.id.edtDescripcionServicio);
        edtPrecioServicio = findViewById(R.id.edtPrecioServicio);

        btnGuardarServicio = findViewById(R.id.btnGuardarServicio);
        btnLimpiarServicio = findViewById(R.id.btnLimpiarServicio);
        btnVolverServicios = findViewById(R.id.btnVolverServicios);

        contenedorServiciosAdmin = findViewById(R.id.contenedorServiciosAdmin);

        requestQueue = Volley.newRequestQueue(this);
        listaServicios = new ArrayList<>();

        cargarServiciosApi();

        btnGuardarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarFormularioServicio();
            }
        });

        btnLimpiarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarFormulario();
            }
        });

        btnVolverServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarServiciosApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_SERVICIOS,
                null,
                response -> {
                    try {
                        JSONArray serviciosJson = response.getJSONArray("servicios");

                        listaServicios.clear();

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

                            listaServicios.add(servicio);
                        }

                        RepositorioServicios.guardarServicios(listaServicios);
                        mostrarServicios();

                    } catch (JSONException e) {
                        Toast.makeText(
                                ServiciosActivity.this,
                                "Error al leer servicios del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    Toast.makeText(
                            ServiciosActivity.this,
                            "No se pudo conectar con la API",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void mostrarServicios() {
        contenedorServiciosAdmin.removeAllViews();

        if (listaServicios.isEmpty()) {
            TextView txtSinServicios = new TextView(this);
            txtSinServicios.setText("No existen servicios registrados.");
            txtSinServicios.setTextSize(16);
            txtSinServicios.setPadding(20, 20, 20, 20);
            contenedorServiciosAdmin.addView(txtSinServicios);
            return;
        }

        for (Servicio servicio : listaServicios) {
            View tarjetaServicio = getLayoutInflater().inflate(R.layout.item_servicio_admin, null);

            TextView txtNombre = tarjetaServicio.findViewById(R.id.txtNombreServicioAdmin);
            TextView txtDescripcion = tarjetaServicio.findViewById(R.id.txtDescripcionServicioAdmin);
            TextView txtPrecio = tarjetaServicio.findViewById(R.id.txtPrecioServicioAdmin);
            TextView txtEstado = tarjetaServicio.findViewById(R.id.txtEstadoServicioAdmin);

            AppCompatButton btnEditar = tarjetaServicio.findViewById(R.id.btnEditarServicioAdmin);
            AppCompatButton btnCambiarEstado = tarjetaServicio.findViewById(R.id.btnCambiarEstadoServicioAdmin);

            txtNombre.setText(servicio.getNombre());
            txtDescripcion.setText(servicio.getDescripcion());
            txtPrecio.setText("$ " + servicio.getPrecio());

            if (servicio.isActivo()) {
                txtEstado.setText("Estado: Activo");
                btnCambiarEstado.setText("Deshabilitar");
            } else {
                txtEstado.setText("Estado: Inactivo");
                btnCambiarEstado.setText("Habilitar");
            }

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cargarServicioEnFormulario(servicio);
                }
            });

            btnCambiarEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cambiarEstadoServicioApi(servicio);
                }
            });

            contenedorServiciosAdmin.addView(tarjetaServicio);
        }
    }

    private void validarFormularioServicio() {
        String nombre = edtNombreServicio.getText().toString().trim();
        String descripcion = edtDescripcionServicio.getText().toString().trim();
        String precioTexto = edtPrecioServicio.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombreServicio.setError("Ingrese el nombre del servicio");
            edtNombreServicio.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            edtDescripcionServicio.setError("Ingrese la descripción del servicio");
            edtDescripcionServicio.requestFocus();
            return;
        }

        if (precioTexto.isEmpty()) {
            edtPrecioServicio.setError("Ingrese el precio");
            edtPrecioServicio.requestFocus();
            return;
        }

        double precio;

        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            edtPrecioServicio.setError("Ingrese un precio válido");
            edtPrecioServicio.requestFocus();
            return;
        }

        if (precio <= 0) {
            edtPrecioServicio.setError("El precio debe ser mayor a cero");
            edtPrecioServicio.requestFocus();
            return;
        }

        if (modoEdicion) {
            actualizarServicioApi(nombre, descripcion, precio);
        } else {
            registrarServicioApi(nombre, descripcion, precio);
        }
    }

    private void registrarServicioApi(String nombre, String descripcion, double precio) {
        JSONObject datosServicio = new JSONObject();

        try {
            datosServicio.put("nombre", nombre);
            datosServicio.put("descripcion", descripcion);
            datosServicio.put("precio", precio);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar datos del servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.URL_SERVICIOS,
                datosServicio,
                response -> {
                    Toast.makeText(
                            ServiciosActivity.this,
                            "Servicio registrado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarFormulario();
                    cargarServiciosApi();
                },
                error -> {
                    String mensaje = "No se pudo registrar el servicio";

                    if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                        mensaje = "Ya existe un servicio con ese nombre";
                    }

                    Toast.makeText(ServiciosActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
    }

    private void actualizarServicioApi(String nombre, String descripcion, double precio) {
        JSONObject datosServicio = new JSONObject();

        try {
            datosServicio.put("nombre", nombre);
            datosServicio.put("descripcion", descripcion);
            datosServicio.put("precio", precio);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar datos del servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlActualizar = ApiConfig.URL_SERVICIOS + "/" + idServicioSeleccionado;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                urlActualizar,
                datosServicio,
                response -> {
                    Toast.makeText(
                            ServiciosActivity.this,
                            "Servicio actualizado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarFormulario();
                    cargarServiciosApi();
                },
                error -> {
                    String mensaje = "No se pudo actualizar el servicio";

                    if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                        mensaje = "Ya existe otro servicio con ese nombre";
                    }

                    Toast.makeText(ServiciosActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
    }

    private void cambiarEstadoServicioApi(Servicio servicio) {
        JSONObject datosEstado = new JSONObject();

        try {
            datosEstado.put("activo", !servicio.isActivo());
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar el estado", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlEstado = ApiConfig.URL_SERVICIOS + "/" + servicio.getIdServicio() + "/estado";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                urlEstado,
                datosEstado,
                response -> {
                    Toast.makeText(
                            ServiciosActivity.this,
                            servicio.isActivo() ? "Servicio deshabilitado" : "Servicio habilitado",
                            Toast.LENGTH_SHORT
                    ).show();

                    cargarServiciosApi();
                },
                error -> {
                    Toast.makeText(
                            ServiciosActivity.this,
                            "No se pudo cambiar el estado del servicio",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        requestQueue.add(request);
    }

    private void cargarServicioEnFormulario(Servicio servicio) {
        modoEdicion = true;
        idServicioSeleccionado = servicio.getIdServicio();

        edtNombreServicio.setText(servicio.getNombre());
        edtDescripcionServicio.setText(servicio.getDescripcion());
        edtPrecioServicio.setText(String.valueOf(servicio.getPrecio()));

        btnGuardarServicio.setText("Actualizar servicio");
    }

    private void limpiarFormulario() {
        modoEdicion = false;
        idServicioSeleccionado = 0;

        edtNombreServicio.setText("");
        edtDescripcionServicio.setText("");
        edtPrecioServicio.setText("");

        btnGuardarServicio.setText("Guardar servicio");
    }
}