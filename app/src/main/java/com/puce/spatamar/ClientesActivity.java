package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ClientesActivity extends AppCompatActivity {

    AppCompatButton btnVolverClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        btnVolverClientes = findViewById(R.id.btnVolverClientes);

        btnVolverClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientesActivity.this, MenuAdministradorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}