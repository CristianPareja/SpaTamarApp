package com.puce.spatamar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;

public class ClientesActivity extends AppCompatActivity {

    private TextView txtTotalClientes;
    private TextView txtSinClientes;

    private LinearLayout contenedorClientes;

    private AppCompatButton btnVolverClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        txtTotalClientes = findViewById(R.id.txtTotalClientes);
        txtSinClientes = findViewById(R.id.txtSinClientes);

        contenedorClientes = findViewById(R.id.contenedorClientes);

        btnVolverClientes = findViewById(R.id.btnVolverClientes);

        btnVolverClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarClientesRegistrados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarClientesRegistrados();
    }

    private void cargarClientesRegistrados() {
        ArrayList<Usuario> clientes = RepositorioUsuarios.obtenerClientesRegistrados();

        txtTotalClientes.setText("Total de clientes registrados: " + clientes.size());

        contenedorClientes.removeAllViews();

        if (clientes.isEmpty()) {
            txtSinClientes.setVisibility(View.VISIBLE);
            contenedorClientes.setVisibility(View.GONE);
            return;
        }

        txtSinClientes.setVisibility(View.GONE);
        contenedorClientes.setVisibility(View.VISIBLE);

        for (Usuario cliente : clientes) {
            TextView tarjetaCliente = crearTarjetaCliente(cliente);
            contenedorClientes.addView(tarjetaCliente);
        }
    }

    private TextView crearTarjetaCliente(Usuario cliente) {
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
}