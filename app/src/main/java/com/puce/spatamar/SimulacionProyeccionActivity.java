package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

        cargarDatosActuales();

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
        cargarDatosActuales();
    }

    private void cargarDatosActuales() {
        double ingresosActuales = RepositorioFinanciero.calcularTotalIngresos();
        double egresosActuales = RepositorioFinanciero.calcularTotalEgresos();
        double cuentasPendientes = RepositorioCuentasPendientes.calcularTotalGeneralPendiente();
        double gananciaActual = RepositorioFinanciero.calcularGananciaNeta();

        txtIngresosActuales.setText("Ingresos actuales: $" + String.format(Locale.US, "%.2f", ingresosActuales));
        txtEgresosActuales.setText("Egresos actuales: $" + String.format(Locale.US, "%.2f", egresosActuales));
        txtCuentasPendientesActuales.setText("Cuentas por cobrar pendientes: $" + String.format(Locale.US, "%.2f", cuentasPendientes));
        txtGananciaActual.setText("Ganancia actual: $" + String.format(Locale.US, "%.2f", gananciaActual));
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

        double ingresosActuales = RepositorioFinanciero.calcularTotalIngresos();
        double egresosActuales = RepositorioFinanciero.calcularTotalEgresos();
        double cuentasPendientes = RepositorioCuentasPendientes.calcularTotalGeneralPendiente();

        double ingresosProyectados = ingresosActuales + (ingresosActuales * variacionIngresos / 100);
        double egresosProyectados = egresosActuales + (egresosActuales * incrementoEgresos / 100);
        double valorAtrasado = cuentasPendientes * pagosAtrasados / 100;

        double ingresoDisponible = ingresosProyectados - valorAtrasado;
        double gananciaProyectada = ingresoDisponible - egresosProyectados;

        String riesgo = calcularRiesgo(gananciaProyectada, ingresosProyectados, egresosProyectados, valorAtrasado);
        String alertas = generarAlertas(riesgo, gananciaProyectada, ingresosProyectados, egresosProyectados, valorAtrasado, cuentasPendientes);

        String resultado = "Ingresos proyectados: $" + String.format(Locale.US, "%.2f", ingresosProyectados) + "\n"
                + "Egresos proyectados: $" + String.format(Locale.US, "%.2f", egresosProyectados) + "\n"
                + "Valor afectado por pagos atrasados: $" + String.format(Locale.US, "%.2f", valorAtrasado) + "\n"
                + "Ingreso disponible estimado: $" + String.format(Locale.US, "%.2f", ingresoDisponible) + "\n"
                + "Ganancia proyectada: $" + String.format(Locale.US, "%.2f", gananciaProyectada);

        txtResultadoSimulacion.setText(resultado);
        txtRiesgoSimulacion.setText("Riesgo: " + riesgo);
        txtAlertasSimulacion.setText(alertas);
    }

    private String calcularRiesgo(double gananciaProyectada, double ingresosProyectados, double egresosProyectados, double valorAtrasado) {
        if (gananciaProyectada < 0) {
            return "ALTO";
        }

        if (ingresosProyectados == 0 && egresosProyectados > 0) {
            return "ALTO";
        }

        if (valorAtrasado > ingresosProyectados * 0.4) {
            return "ALTO";
        }

        if (gananciaProyectada <= ingresosProyectados * 0.2) {
            return "MEDIO";
        }

        return "BAJO";
    }

    private String generarAlertas(String riesgo, double gananciaProyectada, double ingresosProyectados, double egresosProyectados, double valorAtrasado, double cuentasPendientes) {
        String alertas = "";

        if (riesgo.equals("BAJO")) {
            alertas = alertas + "✅ El flujo proyectado se mantiene saludable.\n";
        }

        if (riesgo.equals("MEDIO")) {
            alertas = alertas + "⚠️ Riesgo medio: revise gastos y cobros pendientes.\n";
        }

        if (riesgo.equals("ALTO")) {
            alertas = alertas + "🚨 Riesgo alto: la ganancia proyectada puede ser insuficiente.\n";
        }

        if (valorAtrasado > 0) {
            alertas = alertas + "⚠️ $" + String.format(Locale.US, "%.2f", valorAtrasado)
                    + " podrían retrasarse por pagos pendientes.\n";
        }

        if (egresosProyectados > ingresosProyectados) {
            alertas = alertas + "🚨 Los egresos proyectados superan los ingresos proyectados.\n";
        }

        if (cuentasPendientes > 0) {
            alertas = alertas + "ℹ️ Existen cuentas por cobrar que deben ser gestionadas.\n";
        }

        if (gananciaProyectada > 0 && riesgo.equals("BAJO")) {
            alertas = alertas + "ℹ️ Se proyecta ganancia positiva para el escenario ingresado.";
        }

        return alertas.trim();
    }

    private void limpiarSimulacion() {
        edtVariacionIngresos.setText("");
        edtIncrementoEgresos.setText("");
        edtPagosAtrasados.setText("");

        txtResultadoSimulacion.setText("Ingrese los valores del escenario y presione calcular.");
        txtRiesgoSimulacion.setText("Riesgo: sin calcular");
        txtAlertasSimulacion.setText("Las alertas aparecerán después de calcular la simulación.");
    }
}