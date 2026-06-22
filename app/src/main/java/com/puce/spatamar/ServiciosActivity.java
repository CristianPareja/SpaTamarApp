package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Locale;

public class ServiciosActivity extends AppCompatActivity {

    private EditText edtNombreServicio;
    private EditText edtDescripcionServicio;
    private EditText edtPrecioServicio;

    private TextView txtTituloFormularioServicio;
    private TextView txtTotalServicios;
    private TextView txtSinServicios;

    private LinearLayout contenedorServicios;

    private AppCompatButton btnGuardarServicio;
    private AppCompatButton btnCancelarEdicionServicio;
    private AppCompatButton btnVolverServicios;

    private Servicio servicioEnEdicion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        edtNombreServicio = findViewById(R.id.edtNombreServicio);
        edtDescripcionServicio = findViewById(R.id.edtDescripcionServicio);
        edtPrecioServicio = findViewById(R.id.edtPrecioServicio);

        txtTituloFormularioServicio = findViewById(R.id.txtTituloFormularioServicio);
        txtTotalServicios = findViewById(R.id.txtTotalServicios);
        txtSinServicios = findViewById(R.id.txtSinServicios);

        contenedorServicios = findViewById(R.id.contenedorServicios);

        btnGuardarServicio = findViewById(R.id.btnGuardarServicio);
        btnCancelarEdicionServicio = findViewById(R.id.btnCancelarEdicionServicio);
        btnVolverServicios = findViewById(R.id.btnVolverServicios);

        cargarServicios();

        btnGuardarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarServicio();
            }
        });

        btnCancelarEdicionServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarEdicion();
            }
        });

        btnVolverServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void guardarServicio() {
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
            edtPrecioServicio.setError("Ingrese el precio del servicio");
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

        if (servicioEnEdicion == null) {
            if (RepositorioServicios.existeServicioPorNombre(nombre)) {
                Toast.makeText(this, "Ya existe un servicio con ese nombre", Toast.LENGTH_LONG).show();
                return;
            }

            Servicio nuevoServicio = new Servicio(nombre, descripcion, precio, true);
            RepositorioServicios.agregarServicio(nuevoServicio);

            Toast.makeText(this, "Servicio registrado correctamente", Toast.LENGTH_SHORT).show();

        } else {
            RepositorioServicios.actualizarServicio(servicioEnEdicion, nombre, descripcion, precio);

            Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show();
        }

        limpiarFormulario();
        cargarServicios();
    }

    private void cargarServicios() {
        ArrayList<Servicio> servicios = RepositorioServicios.obtenerServicios();

        int activos = 0;

        for (Servicio servicio : servicios) {
            if (servicio.isActivo()) {
                activos++;
            }
        }

        txtTotalServicios.setText("Servicios registrados: " + servicios.size() + " | Activos: " + activos);

        contenedorServicios.removeAllViews();

        if (servicios.isEmpty()) {
            txtSinServicios.setVisibility(View.VISIBLE);
            contenedorServicios.setVisibility(View.GONE);
            return;
        }

        txtSinServicios.setVisibility(View.GONE);
        contenedorServicios.setVisibility(View.VISIBLE);

        for (Servicio servicio : servicios) {
            LinearLayout tarjeta = crearTarjetaServicio(servicio);
            contenedorServicios.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaServicio(Servicio servicio) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(18, 18, 18, 18);
        tarjeta.setBackgroundResource(R.drawable.card_login);

        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametrosTarjeta.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametrosTarjeta);

        TextView informacion = new TextView(this);

        String estado = servicio.isActivo() ? "Activo" : "Deshabilitado";

        String texto = "Servicio: " + servicio.getNombre() + "\n"
                + "Descripción: " + servicio.getDescripcion() + "\n"
                + "Precio: $" + String.format(Locale.US, "%.2f", servicio.getPrecio()) + "\n"
                + "Estado: " + estado;

        informacion.setText(texto);
        informacion.setTextSize(15);
        informacion.setTextColor(getResources().getColor(android.R.color.black));
        informacion.setPadding(0, 0, 0, 14);

        AppCompatButton btnActualizar = new AppCompatButton(this);
        btnActualizar.setText("Actualizar servicio");
        btnActualizar.setTextSize(14);
        btnActualizar.setTextColor(getResources().getColor(android.R.color.white));
        btnActualizar.setAllCaps(false);
        btnActualizar.setBackgroundResource(R.drawable.boton_principal);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepararEdicion(servicio);
            }
        });

        AppCompatButton btnCambiarEstado = new AppCompatButton(this);

        if (servicio.isActivo()) {
            btnCambiarEstado.setText("Deshabilitar servicio");
        } else {
            btnCambiarEstado.setText("Habilitar servicio");
        }

        btnCambiarEstado.setTextSize(14);
        btnCambiarEstado.setTextColor(getResources().getColor(android.R.color.white));
        btnCambiarEstado.setAllCaps(false);
        btnCambiarEstado.setBackgroundResource(R.drawable.boton_principal);

        LinearLayout.LayoutParams parametrosBotonEstado = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametrosBotonEstado.setMargins(0, 10, 0, 0);
        btnCambiarEstado.setLayoutParams(parametrosBotonEstado);

        btnCambiarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarCambioEstado(servicio);
            }
        });

        tarjeta.addView(informacion);
        tarjeta.addView(btnActualizar);
        tarjeta.addView(btnCambiarEstado);

        return tarjeta;
    }

    private void prepararEdicion(Servicio servicio) {
        servicioEnEdicion = servicio;

        edtNombreServicio.setText(servicio.getNombre());
        edtDescripcionServicio.setText(servicio.getDescripcion());
        edtPrecioServicio.setText(String.format(Locale.US, "%.2f", servicio.getPrecio()));

        txtTituloFormularioServicio.setText("Actualizar servicio");
        btnGuardarServicio.setText("Guardar actualización");
        btnCancelarEdicionServicio.setVisibility(View.VISIBLE);

        edtNombreServicio.requestFocus();
    }

    private void confirmarCambioEstado(Servicio servicio) {
        String mensaje;

        if (servicio.isActivo()) {
            mensaje = "¿Está seguro de deshabilitar este servicio? Ya no será visible para el cliente ni estará disponible al agendar citas.";
        } else {
            mensaje = "¿Está seguro de habilitar este servicio? Volverá a ser visible para el cliente.";
        }

        new AlertDialog.Builder(this)
                .setTitle("Cambiar estado del servicio")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    if (servicio.isActivo()) {
                        RepositorioServicios.deshabilitarServicio(servicio);
                        Toast.makeText(this, "Servicio deshabilitado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        RepositorioServicios.habilitarServicio(servicio);
                        Toast.makeText(this, "Servicio habilitado correctamente", Toast.LENGTH_SHORT).show();
                    }

                    cargarServicios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cancelarEdicion() {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        edtNombreServicio.setText("");
        edtDescripcionServicio.setText("");
        edtPrecioServicio.setText("");

        servicioEnEdicion = null;

        txtTituloFormularioServicio.setText("Registrar nuevo servicio");
        btnGuardarServicio.setText("Registrar servicio");
        btnCancelarEdicionServicio.setVisibility(View.GONE);
    }
}