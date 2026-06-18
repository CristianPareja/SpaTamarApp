package com.puce.spatamar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MenuAdministradorActivity extends AppCompatActivity {

    AppCompatButton btnCerrarSesionAdmin, btnClientesAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);
        btnClientesAdmin = findViewById(R.id.btnClientesAdmin);

        btnCerrarSesionAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnClientesAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdministradorActivity.this, ClientesActivity.class);
                startActivity(intent);
            }
        });
    }
}