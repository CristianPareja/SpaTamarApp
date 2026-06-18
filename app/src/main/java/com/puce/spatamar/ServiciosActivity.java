package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ServiciosActivity extends AppCompatActivity {

    AppCompatButton btnVolverServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        btnVolverServicios = findViewById(R.id.btnVolverServicios);

        btnVolverServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiciosActivity.this, MenuAdministradorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}