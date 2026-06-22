package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Calendar;
import java.util.Locale;

public class MenuAdministradorActivity extends AppCompatActivity {

    private TextView txtFechaDashboard;
    private TextView txtTotalClientesDashboard;
    private TextView txtCitasHoyDashboard;
    private TextView txtCitasActivasDashboard;
    private TextView txtTotalCobroDashboard;
    private TextView txtResumenDashboard;

    private AppCompatButton btnClientesAdmin;
    private AppCompatButton btnServiciosAdmin;
    private AppCompatButton btnCitasAdmin;
    private AppCompatButton btnProveedoresAdmin;
    private AppCompatButton btnCuentasCobrarAdmin;
    private AppCompatButton btnCuentasPagarAdmin;
    private AppCompatButton btnSimulacionProyeccionAdmin;
    private AppCompatButton btnCerrarSesionAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        txtFechaDashboard = findViewById(R.id.txtFechaDashboard);
        txtTotalClientesDashboard = findViewById(R.id.txtTotalClientesDashboard);
        txtCitasHoyDashboard = findViewById(R.id.txtCitasHoyDashboard);
        txtCitasActivasDashboard = findViewById(R.id.txtCitasActivasDashboard);
        txtTotalCobroDashboard = findViewById(R.id.txtTotalCobroDashboard);
        txtResumenDashboard = findViewById(R.id.txtResumenDashboard);

        btnClientesAdmin = findViewById(R.id.btnClientesAdmin);
        btnServiciosAdmin = findViewById(R.id.btnServiciosAdmin);
        btnCitasAdmin = findViewById(R.id.btnCitasAdmin);
        btnProveedoresAdmin = findViewById(R.id.btnProveedoresAdmin);
        btnCuentasCobrarAdmin = findViewById(R.id.btnCuentasCobrarAdmin);
        btnCuentasPagarAdmin = findViewById(R.id.btnCuentasPagarAdmin);
        btnSimulacionProyeccionAdmin = findViewById(R.id.btnSimulacionProyeccionAdmin);
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);

        cargarDashboard();

        btnClientesAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, ClientesActivity.class);
                startActivity(intent);
            }
        });

        btnServiciosAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, ServiciosActivity.class);
                startActivity(intent);
            }
        });

        btnCitasAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        MenuAdministradorActivity.this,
                        "Módulo de citas del administrador en desarrollo.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnProveedoresAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        MenuAdministradorActivity.this,
                        "Módulo de proveedores en desarrollo.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnCuentasCobrarAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        MenuAdministradorActivity.this,
                        "Módulo de cuentas por cobrar en desarrollo.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnCuentasPagarAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        MenuAdministradorActivity.this,
                        "Módulo de cuentas por pagar en desarrollo.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnSimulacionProyeccionAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        MenuAdministradorActivity.this,
                        "Módulo de simulación y proyección en desarrollo.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnCerrarSesionAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDashboard();
    }

    private void cargarDashboard() {
        String fechaActual = obtenerFechaActual();
        int totalClientes = RepositorioUsuarios.contarClientesRegistrados();
        int citasHoy = RepositorioCitas.contarCitasPorFecha(fechaActual);
        int citasActivas = RepositorioCitas.contarCitasActivas();
        double totalCobro = RepositorioCuentasPendientes.calcularTotalGeneralPendiente();

        txtFechaDashboard.setText("Fecha del panel: " + fechaActual);
        txtTotalClientesDashboard.setText(String.valueOf(totalClientes));
        txtCitasHoyDashboard.setText(String.valueOf(citasHoy));
        txtCitasActivasDashboard.setText(String.valueOf(citasActivas));
        txtTotalCobroDashboard.setText("$" + String.format(Locale.US, "%.2f", totalCobro));

        String resumen;

        if (citasHoy == 0) {
            resumen = "Para hoy no existen citas registradas. Actualmente el sistema registra "
                    + totalClientes + " cliente(s), "
                    + citasActivas + " cita(s) activa(s) y un total pendiente de cobro de $"
                    + String.format(Locale.US, "%.2f", totalCobro) + ".";
        } else {
            resumen = "Para hoy existen " + citasHoy + " cita(s) programada(s). "
                    + "Actualmente el sistema registra " + totalClientes + " cliente(s), "
                    + citasActivas + " cita(s) activa(s) y un total pendiente de cobro de $"
                    + String.format(Locale.US, "%.2f", totalCobro) + ".";
        }

        txtResumenDashboard.setText(resumen);
    }

    private String obtenerFechaActual() {
        Calendar calendario = Calendar.getInstance();

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }
}