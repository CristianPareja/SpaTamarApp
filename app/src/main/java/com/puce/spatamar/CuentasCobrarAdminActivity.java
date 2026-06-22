package com.puce.spatamar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CuentasCobrarAdminActivity extends AppCompatActivity {

    private Spinner spinnerClientesCobrar;

    private EditText edtConceptoCobrar;
    private EditText edtFechaCobrar;
    private EditText edtValorCobrar;
    private EditText edtObservacionCobrar;

    private TextView txtTotalGeneralCobrar;
    private TextView txtSinCuentasCobrar;

    private LinearLayout contenedorCuentasCobrar;

    private AppCompatButton btnRegistrarCuentaCobrar;
    private AppCompatButton btnVolverCuentasCobrar;

    private ArrayList<Usuario> listaClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_cobrar_admin);

        spinnerClientesCobrar = findViewById(R.id.spinnerClientesCobrar);

        edtConceptoCobrar = findViewById(R.id.edtConceptoCobrar);
        edtFechaCobrar = findViewById(R.id.edtFechaCobrar);
        edtValorCobrar = findViewById(R.id.edtValorCobrar);
        edtObservacionCobrar = findViewById(R.id.edtObservacionCobrar);

        txtTotalGeneralCobrar = findViewById(R.id.txtTotalGeneralCobrar);
        txtSinCuentasCobrar = findViewById(R.id.txtSinCuentasCobrar);

        contenedorCuentasCobrar = findViewById(R.id.contenedorCuentasCobrar);

        btnRegistrarCuentaCobrar = findViewById(R.id.btnRegistrarCuentaCobrar);
        btnVolverCuentasCobrar = findViewById(R.id.btnVolverCuentasCobrar);

        cargarClientesEnSpinner();
        cargarCuentasRegistradas();

        edtFechaCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
            }
        });

        btnRegistrarCuentaCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarCuentaPorCobrar();
            }
        });

        btnVolverCuentasCobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarClientesEnSpinner() {
        listaClientes = RepositorioUsuarios.obtenerClientesRegistrados();

        ArrayList<String> nombresClientes = new ArrayList<>();
        nombresClientes.add("Seleccione un cliente");

        for (Usuario cliente : listaClientes) {
            nombresClientes.add(cliente.getNombreCompleto() + " - " + cliente.getCorreo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresClientes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClientesCobrar.setAdapter(adapter);
    }

    private void mostrarCalendario() {
        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog selectorFecha = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtFechaCobrar.setText(fechaSeleccionada);
                },
                anio,
                mes,
                dia
        );

        selectorFecha.show();
    }

    private void registrarCuentaPorCobrar() {
        int posicionCliente = spinnerClientesCobrar.getSelectedItemPosition();

        String concepto = edtConceptoCobrar.getText().toString().trim();
        String fecha = edtFechaCobrar.getText().toString().trim();
        String valorTexto = edtValorCobrar.getText().toString().trim();
        String observacion = edtObservacionCobrar.getText().toString().trim();

        if (posicionCliente == 0) {
            Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (concepto.isEmpty()) {
            edtConceptoCobrar.setError("Ingrese el concepto del servicio");
            edtConceptoCobrar.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            edtValorCobrar.setError("Ingrese el valor pendiente");
            edtValorCobrar.requestFocus();
            return;
        }

        double valorPendiente;

        try {
            valorPendiente = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            edtValorCobrar.setError("Ingrese un valor válido");
            edtValorCobrar.requestFocus();
            return;
        }

        if (valorPendiente <= 0) {
            edtValorCobrar.setError("El valor debe ser mayor a cero");
            edtValorCobrar.requestFocus();
            return;
        }

        if (observacion.isEmpty()) {
            observacion = "Sin observación";
        }

        Usuario clienteSeleccionado = listaClientes.get(posicionCliente - 1);

        CuentaPendiente nuevaCuenta = new CuentaPendiente(
                clienteSeleccionado.getNombreCompleto(),
                clienteSeleccionado.getUsuario(),
                clienteSeleccionado.getCorreo(),
                concepto,
                fecha,
                valorPendiente,
                "Pendiente",
                observacion
        );

        RepositorioCuentasPendientes.agregarCuentaPendiente(nuevaCuenta);

        Toast.makeText(this, "Cuenta por cobrar registrada correctamente", Toast.LENGTH_SHORT).show();

        limpiarFormulario();
        cargarCuentasRegistradas();
    }

    private void cargarCuentasRegistradas() {
        ArrayList<CuentaPendiente> cuentas = RepositorioCuentasPendientes.obtenerCuentasPendientes();

        double totalGeneral = RepositorioCuentasPendientes.calcularTotalGeneralPendiente();
        txtTotalGeneralCobrar.setText("Total general por cobrar: $" + String.format(Locale.US, "%.2f", totalGeneral));

        contenedorCuentasCobrar.removeAllViews();

        if (cuentas.isEmpty()) {
            txtSinCuentasCobrar.setVisibility(View.VISIBLE);
            contenedorCuentasCobrar.setVisibility(View.GONE);
            return;
        }

        txtSinCuentasCobrar.setVisibility(View.GONE);
        contenedorCuentasCobrar.setVisibility(View.VISIBLE);

        for (CuentaPendiente cuenta : cuentas) {
            LinearLayout tarjeta = crearTarjetaCuenta(cuenta);
            contenedorCuentasCobrar.addView(tarjeta);
        }
    }

    private LinearLayout crearTarjetaCuenta(CuentaPendiente cuenta) {
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

        String texto = "Cliente: " + cuenta.getNombreCliente() + "\n"
                + "Correo: " + cuenta.getCorreoCliente() + "\n"
                + "Concepto: " + cuenta.getConcepto() + "\n"
                + "Fecha: " + cuenta.getFecha() + "\n"
                + "Valor pendiente: $" + String.format(Locale.US, "%.2f", cuenta.getValorPendiente()) + "\n"
                + "Estado: " + cuenta.getEstado() + "\n"
                + "Observación: " + cuenta.getObservacion();

        informacion.setText(texto);
        informacion.setTextSize(15);
        informacion.setTextColor(getResources().getColor(android.R.color.black));
        informacion.setPadding(0, 0, 0, 14);

        tarjeta.addView(informacion);

        if (cuenta.getEstado().equalsIgnoreCase("Pendiente")) {
            AppCompatButton btnMarcarPagado = new AppCompatButton(this);
            btnMarcarPagado.setText("Marcar como pagado");
            btnMarcarPagado.setTextSize(14);
            btnMarcarPagado.setTextColor(getResources().getColor(android.R.color.white));
            btnMarcarPagado.setAllCaps(false);
            btnMarcarPagado.setBackgroundResource(R.drawable.boton_principal);

            btnMarcarPagado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmarPago(cuenta);
                }
            });

            tarjeta.addView(btnMarcarPagado);
        }

        return tarjeta;
    }

    private void confirmarPago(CuentaPendiente cuenta) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar pago")
                .setMessage("¿Está seguro de marcar esta cuenta como pagada? El cliente dejará de verla como cuenta pendiente.")
                .setPositiveButton("Sí, marcar pagado", (dialog, which) -> {
                    RepositorioCuentasPendientes.marcarComoPagado(cuenta);
                    Toast.makeText(this, "Cuenta marcada como pagada", Toast.LENGTH_SHORT).show();
                    cargarCuentasRegistradas();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void limpiarFormulario() {
        spinnerClientesCobrar.setSelection(0);
        edtConceptoCobrar.setText("");
        edtFechaCobrar.setText("");
        edtValorCobrar.setText("");
        edtObservacionCobrar.setText("");
    }
}