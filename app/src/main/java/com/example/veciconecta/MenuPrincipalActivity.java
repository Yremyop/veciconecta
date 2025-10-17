package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MenuPrincipalActivity extends AppCompatActivity {

    Button btnPagosAportes, btnPagosAgua, btnCalendario;
    int usuarioId; // recibimos desde Login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Configurar botón de volver al login (cerrar sesión)
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            // Crear un intent para volver a LoginActivity y limpiar la pila de actividades
            Intent intent = new Intent(MenuPrincipalActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnPagosAportes = findViewById(R.id.btnPagosAportes);
        btnPagosAgua = findViewById(R.id.btnPagosAgua);
        btnCalendario = findViewById(R.id.btnCalendario);

        // Recuperar usuario_id del intent
        usuarioId = getIntent().getIntExtra("usuario_id", 0);

        btnPagosAportes.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipalActivity.this, PagosAportesActivity.class);
            intent.putExtra("usuario_id", usuarioId);
            startActivity(intent);
        });

        btnPagosAgua.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipalActivity.this, PagosAguaActivity.class);
            intent.putExtra("usuario_id", usuarioId);
            startActivity(intent);
        });

        btnCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipalActivity.this, CalendarioActivity.class);
            startActivity(intent);
        });
    }
}
