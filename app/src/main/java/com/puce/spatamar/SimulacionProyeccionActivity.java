package com.puce.spatamar;

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

import java.util.Locale;

public class SimulacionProyeccionActivity extends AppCompatActivity {

    private TextView txtIngresosActuales;
    private TextView txtEgresosActuales;
    private TextView txtCuentasPendientesActuales;
    private TextView txtGananciaActual;
    private TextView txtResultadoSimulacion;
    private TextView txtRiesgoSimulacion;
    private TextView txtAlertasSimulacion;

    private EditText edtVariacionIngresos;
    private EditText edtIncrementoEgresos;
    private EditText edtPagosAtrasados;

    private AppCompatButton btnCalcularSimulacion;
    private AppCompatButton btnLimpiarSimulacion;
    private AppCompatButton btnVolverSimulacion;

    private RequestQueue requestQueue;

    private double ingresosActuales = 0;
    private double egresosActuales = 0;
    private double cuentasPendientes = 0;
    private double gananciaActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulacion_proyeccion);

        txtIngresosActuales = findViewById(R.id.txtIngresosActuales);
        txtEgresosActuales = findViewById(R.id.txtEgresosActuales);
        txtCuentasPendientesActuales = findViewById(R.id.txtCuentasPendientesActuales);
        txtGananciaActual = findViewById(R.id.txtGananciaActual);
        txtResultadoSimulacion = findViewById(R.id.txtResultadoSimulacion);
        txtRiesgoSimulacion = findViewById(R.id.txtRiesgoSimulacion);
        txtAlertasSimulacion = findViewById(R.id.txtAlertasSimulacion);

        edtVariacionIngresos = findViewById(R.id.edtVariacionIngresos);
        edtIncrementoEgresos = findViewById(R.id.edtIncrementoEgresos);
        edtPagosAtrasados = findViewById(R.id.edtPagosAtrasados);

        btnCalcularSimulacion = findViewById(R.id.btnCalcularSimulacion);
        btnLimpiarSimulacion = findViewById(R.id.btnLimpiarSimulacion);
        btnVolverSimulacion = findViewById(R.id.btnVolverSimulacion);

        requestQueue = Volley.newRequestQueue(this);

        cargarDatosActualesApi();

        btnCalcularSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularSimulacion();
            }
        });

        btnLimpiarSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarSimulacion();
            }
        });

        btnVolverSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosActualesApi();
    }

    private void cargarDatosActualesApi() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_FINANZAS_RESUMEN,
                null,
                response -> {
                    try {
                        ingresosActuales = response.getDouble("ingresos");
                        egresosActuales = response.getDouble("egresos");
                        cuentasPendientes = response.getDouble("total_por_cobrar");
                        gananciaActual = response.getDouble("ganancia_neta");

                        mostrarDatosActuales();

                    } catch (JSONException e) {
                        Toast.makeText(
                                SimulacionProyeccionActivity.this,
                                "Error al leer datos financieros",
                                Toast.LENGTH_SHORT
                        ).show();

                        cargarDatosLocalesComoRespaldo();
                    }
                },
                error -> {
                    Toast.makeText(
                            SimulacionProyeccionActivity.this,
                            "No se pudo consultar el resumen financiero desde la API",
                            Toast.LENGTH_LONG
                    ).show();

                    cargarDatosLocalesComoRespaldo();
                }
        );

        requestQueue.add(request);
    }

    private void cargarDatosLocalesComoRespaldo() {
        ingresosActuales = RepositorioFinanciero.calcularTotalIngresos();
        egresosActuales = RepositorioFinanciero.calcularTotalEgresos();
        cuentasPendientes = RepositorioCuentasPendientes.calcularTotalGeneralPendiente();
        gananciaActual = RepositorioFinanciero.calcularGananciaNeta();

        mostrarDatosActuales();
    }

    private void mostrarDatosActuales() {
        txtIngresosActuales.setText("Ingresos actuales: $" + String.format(Locale.US, "%.2f", ingresosActuales));
        txtEgresosActuales.setText("Egresos actuales: $" + String.format(Locale.US, "%.2f", egresosActuales));
        txtCuentasPendientesActuales.setText("Cuentas por cobrar pendientes: $" + String.format(Locale.US, "%.2f", cuentasPendientes));
        txtGananciaActual.setText("Ganancia actual: $" + String.format(Locale.US, "%.2f", gananciaActual));

        if (gananciaActual > 0) {
            txtGananciaActual.setTextColor(getResources().getColor(R.color.verde_positivo));
        } else if (gananciaActual < 0) {
            txtGananciaActual.setTextColor(getResources().getColor(R.color.rojo_negativo));
        } else {
            txtGananciaActual.setTextColor(getResources().getColor(R.color.azul_moderno));
        }
    }

    private void calcularSimulacion() {
        String variacionIngresosTexto = edtVariacionIngresos.getText().toString().trim();
        String incrementoEgresosTexto = edtIncrementoEgresos.getText().toString().trim();
        String pagosAtrasadosTexto = edtPagosAtrasados.getText().toString().trim();

        if (variacionIngresosTexto.isEmpty()) {
            edtVariacionIngresos.setError("Ingrese la variación de ingresos");
            edtVariacionIngresos.requestFocus();
            return;
        }

        if (incrementoEgresosTexto.isEmpty()) {
            edtIncrementoEgresos.setError("Ingrese el incremento de egresos");
            edtIncrementoEgresos.requestFocus();
            return;
        }

        if (pagosAtrasadosTexto.isEmpty()) {
            edtPagosAtrasados.setError("Ingrese el porcentaje de pagos atrasados");
            edtPagosAtrasados.requestFocus();
            return;
        }

        double variacionIngresos;
        double incrementoEgresos;
        double pagosAtrasados;

        try {
            variacionIngresos = Double.parseDouble(variacionIngresosTexto);
            incrementoEgresos = Double.parseDouble(incrementoEgresosTexto);
            pagosAtrasados = Double.parseDouble(pagosAtrasadosTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (incrementoEgresos < 0) {
            edtIncrementoEgresos.setError("El incremento de egresos no puede ser negativo");
            edtIncrementoEgresos.requestFocus();
            return;
        }

        if (pagosAtrasados < 0 || pagosAtrasados > 100) {
            edtPagosAtrasados.setError("Ingrese un porcentaje entre 0 y 100");
            edtPagosAtrasados.requestFocus();
            return;
        }

        double ingresosProyectados = ingresosActuales + (ingresosActuales * variacionIngresos / 100);
        double egresosProyectados = egresosActuales + (egresosActuales * incrementoEgresos / 100);

        if (ingresosProyectados < 0) {
            ingresosProyectados = 0;
        }

        double valorAtrasado = cuentasPendientes * pagosAtrasados / 100;
        double ingresoDisponible = ingresosProyectados - valorAtrasado;

        if (ingresoDisponible < 0) {
            ingresoDisponible = 0;
        }

        double gananciaProyectada = ingresoDisponible - egresosProyectados;

        String riesgo = calcularRiesgo(
                gananciaProyectada,
                ingresosProyectados,
                egresosProyectados,
                valorAtrasado
        );

        String alertas = generarAlertas(
                riesgo,
                gananciaProyectada,
                ingresosProyectados,
                egresosProyectados,
                valorAtrasado,
                cuentasPendientes
        );

        String resultado = "Ingresos proyectados: $" + String.format(Locale.US, "%.2f", ingresosProyectados) + "\n"
                + "Egresos proyectados: $" + String.format(Locale.US, "%.2f", egresosProyectados) + "\n"
                + "Valor afectado por pagos atrasados: $" + String.format(Locale.US, "%.2f", valorAtrasado) + "\n"
                + "Ingreso disponible estimado: $" + String.format(Locale.US, "%.2f", ingresoDisponible) + "\n"
                + "Ganancia proyectada: $" + String.format(Locale.US, "%.2f", gananciaProyectada);

        txtResultadoSimulacion.setText(resultado);
        txtRiesgoSimulacion.setText("Riesgo: " + riesgo);
        txtAlertasSimulacion.setText(alertas);

        aplicarColorRiesgo(riesgo);
    }

    private String calcularRiesgo(double gananciaProyectada,
                                  double ingresosProyectados,
                                  double egresosProyectados,
                                  double valorAtrasado) {

        if (gananciaProyectada < 0) {
            return "ALTO";
        }

        if (ingresosProyectados == 0 && egresosProyectados > 0) {
            return "ALTO";
        }

        if (ingresosProyectados > 0 && valorAtrasado > ingresosProyectados * 0.4) {
            return "ALTO";
        }

        if (ingresosProyectados > 0 && gananciaProyectada <= ingresosProyectados * 0.2) {
            return "MEDIO";
        }

        if (ingresosProyectados == 0 && egresosProyectados == 0) {
            return "MEDIO";
        }

        return "BAJO";
    }

    private String generarAlertas(String riesgo,
                                  double gananciaProyectada,
                                  double ingresosProyectados,
                                  double egresosProyectados,
                                  double valorAtrasado,
                                  double cuentasPendientes) {

        String alertas = "";

        if (riesgo.equals("BAJO")) {
            alertas = alertas + "• El flujo proyectado se mantiene saludable.\n";
        }

        if (riesgo.equals("MEDIO")) {
            alertas = alertas + "• Riesgo medio: revise gastos y cobros pendientes.\n";
        }

        if (riesgo.equals("ALTO")) {
            alertas = alertas + "• Riesgo alto: la ganancia proyectada puede ser insuficiente.\n";
        }

        if (valorAtrasado > 0) {
            alertas = alertas + "• $" + String.format(Locale.US, "%.2f", valorAtrasado)
                    + " podrían retrasarse por pagos pendientes.\n";
        }

        if (egresosProyectados > ingresosProyectados) {
            alertas = alertas + "• Los egresos proyectados superan los ingresos proyectados.\n";
        }

        if (cuentasPendientes > 0) {
            alertas = alertas + "• Existen cuentas por cobrar que deben ser gestionadas.\n";
        }

        if (gananciaProyectada > 0 && riesgo.equals("BAJO")) {
            alertas = alertas + "• Se proyecta ganancia positiva para el escenario ingresado.";
        }

        return alertas.trim();
    }

    private void aplicarColorRiesgo(String riesgo) {
        if (riesgo.equals("ALTO")) {
            txtRiesgoSimulacion.setTextColor(getResources().getColor(R.color.rojo_negativo));
            txtRiesgoSimulacion.setBackgroundResource(R.drawable.card_moderno_rojo);
        } else if (riesgo.equals("MEDIO")) {
            txtRiesgoSimulacion.setTextColor(getResources().getColor(R.color.naranja_alerta));
            txtRiesgoSimulacion.setBackgroundResource(R.drawable.card_moderno_naranja);
        } else {
            txtRiesgoSimulacion.setTextColor(getResources().getColor(R.color.verde_positivo));
            txtRiesgoSimulacion.setBackgroundResource(R.drawable.card_moderno_verde);
        }
    }

    private void limpiarSimulacion() {
        edtVariacionIngresos.setText("");
        edtIncrementoEgresos.setText("");
        edtPagosAtrasados.setText("");

        txtResultadoSimulacion.setText("Ingrese los valores del escenario y presione calcular.");
        txtRiesgoSimulacion.setText("Riesgo: sin calcular");
        txtRiesgoSimulacion.setTextColor(getResources().getColor(R.color.azul_moderno));
        txtRiesgoSimulacion.setBackgroundResource(R.drawable.fondo_chip_moderno);
        txtAlertasSimulacion.setText("Las alertas aparecerán después de calcular la simulación.");
    }
}