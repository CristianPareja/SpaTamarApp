package com.puce.spatamar;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MenuClienteActivity extends AppCompatActivity {
    AppCompatButton btnCerrarSesionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);
        btnCerrarSesionCliente = findViewById(R.id.btnCerrarSesionCliente);
        btnCerrarSesionCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuClienteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}