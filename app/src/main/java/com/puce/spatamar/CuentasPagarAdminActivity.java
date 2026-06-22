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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CuentasPagarAdminActivity extends AppCompatActivity {

    private Spinner spinnerTipoEgreso;

    private EditText edtProveedorDetalle;
    private EditText edtConceptoPagar;
    private EditText edtFechaPagar;
    private EditText edtValorPagar;
    private EditText edtObservacionPagar;

    private TextView txtTotalCuentasPagar;
    private TextView txtSinCuentasPagar;

    private LinearLayout contenedorCuentasPagar;

    private AppCompatButton btnRegistrarCuentaPagar;
    private AppCompatButton btnVolverCuentasPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas_pagar_admin);

        spinnerTipoEgreso = findViewById(R.id.spinnerTipoEgreso);

        edtProveedorDetalle = findViewById(R.id.edtProveedorDetalle);
        edtConceptoPagar = findViewById(R.id.edtConceptoPagar);
        edtFechaPagar = findViewById(R.id.edtFechaPagar);
        edtValorPagar = findViewById(R.id.edtValorPagar);
        edtObservacionPagar = findViewById(R.id.edtObservacionPagar);

        txtTotalCuentasPagar = findViewById(R.id.txtTotalCuentasPagar);
        txtSinCuentasPagar = findViewById(R.id.txtSinCuentasPagar);

        contenedorCuentasPagar = findViewById(R.id.contenedorCuentasPagar);

        btnRegistrarCuentaPagar = findViewById(R.id.btnRegistrarCuentaPagar);
        btnVolverCuentasPagar = findViewById(R.id.btnVolverCuentasPagar);

        cargarTiposEgreso();
        cargarCuentasPagar();

        edtFechaPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
            }
        });

        btnRegistrarCuentaPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarEgreso();
            }
        });

        btnVolverCuentasPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarTiposEgreso() {
        String[] tipos = {
                "Seleccione tipo de egreso",
                "Proveedor",
                "Arriendo",
                "Servicio básico",
                "Otro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoEgreso.setAdapter(adapter);
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
                    edtFechaPagar.setText(fechaSeleccionada);
                },
                anio,
                mes,
                dia
        );

        selectorFecha.show();
    }

    private void registrarEgreso() {
        int posicionTipo = spinnerTipoEgreso.getSelectedItemPosition();

        String tipoEgreso = spinnerTipoEgreso.getSelectedItem().toString();
        String proveedorDetalle = edtProveedorDetalle.getText().toString().trim();
        String concepto = edtConceptoPagar.getText().toString().trim();
        String fecha = edtFechaPagar.getText().toString().trim();
        String valorTexto = edtValorPagar.getText().toString().trim();
        String observacion = edtObservacionPagar.getText().toString().trim();

        if (posicionTipo == 0) {
            Toast.makeText(this, "Seleccione el tipo de egreso", Toast.LENGTH_SHORT).show();
            return;
        }

        if (proveedorDetalle.isEmpty()) {
            edtProveedorDetalle.setError("Ingrese proveedor, arriendo o servicio básico");
            edtProveedorDetalle.requestFocus();
            return;
        }

        if (concepto.isEmpty()) {
            edtConceptoPagar.setError("Ingrese el concepto del pago");
            edtConceptoPagar.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            edtValorPagar.setError("Ingrese el valor a pagar");
            edtValorPagar.requestFocus();
            return;
        }

        double valor;

        try {
            valor = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            edtValorPagar.setError("Ingrese un valor válido");
            edtValorPagar.requestFocus();
            return;
        }

        if (valor <= 0) {
            edtValorPagar.setError("El valor debe ser mayor a cero");
            edtValorPagar.requestFocus();
            return;
        }

        if (observacion.isEmpty()) {
            observacion = "Sin observación";
        }

        CuentaPagar nuevaCuenta = new CuentaPagar(
                tipoEgreso,
                proveedorDetalle,
                concepto,
                fecha,
                valor,
                "Registrado",
                observacion
        );

        RepositorioCuentasPagar.agregarCuentaPagar(nuevaCuenta);

        Toast.makeText(this, "Egreso registrado correctamente", Toast.LENGTH_SHORT).show();

        limpiarFormulario();
        cargarCuentasPagar();
    }

    private void cargarCuentasPagar() {
        ArrayList<CuentaPagar> cuentas = RepositorioCuentasPagar.obtenerCuentasPagar();

        double total = RepositorioCuentasPagar.calcularTotalCuentasPagar();

        txtTotalCuentasPagar.setText("Total egresos registrados: $" + String.format(Locale.US, "%.2f", total));

        contenedorCuentasPagar.removeAllViews();

        if (cuentas.isEmpty()) {
            txtSinCuentasPagar.setVisibility(View.VISIBLE);
            contenedorCuentasPagar.setVisibility(View.GONE);
            return;
        }

        txtSinCuentasPagar.setVisibility(View.GONE);
        contenedorCuentasPagar.setVisibility(View.VISIBLE);

        for (CuentaPagar cuenta : cuentas) {
            TextView tarjeta = crearTarjetaCuentaPagar(cuenta);
            contenedorCuentasPagar.addView(tarjeta);
        }
    }

    private TextView crearTarjetaCuentaPagar(CuentaPagar cuenta) {
        TextView tarjeta = new TextView(this);

        String informacion = "Tipo: " + cuenta.getTipoEgreso() + "\n"
                + "Detalle: " + cuenta.getProveedorDetalle() + "\n"
                + "Concepto: " + cuenta.getConcepto() + "\n"
                + "Fecha: " + cuenta.getFecha() + "\n"
                + "Valor: $" + String.format(Locale.US, "%.2f", cuenta.getValor()) + "\n"
                + "Estado: " + cuenta.getEstado() + "\n"
                + "Observación: " + cuenta.getObservacion();

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

    private void limpiarFormulario() {
        spinnerTipoEgreso.setSelection(0);
        edtProveedorDetalle.setText("");
        edtConceptoPagar.setText("");
        edtFechaPagar.setText("");
        edtValorPagar.setText("");
        edtObservacionPagar.setText("");
    }
}