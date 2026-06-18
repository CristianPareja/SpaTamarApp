package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ServiciosClienteActivity extends AppCompatActivity {

    AppCompatButton btnVolverServiciosCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios_cliente);

        btnVolverServiciosCliente = findViewById(R.id.btnVolverServiciosCliente);

        btnVolverServiciosCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiciosClienteActivity.this, MenuClienteActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}