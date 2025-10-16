package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class CalendarioActivity extends AppCompatActivity {

    private ListView listaNoticias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        // Vinculamos el ListView con el XML
        listaNoticias = findViewById(R.id.listaNoticias);

        // Creamos una lista de eventos/noticias
        ArrayList<String> eventos = new ArrayList<>();
        eventos.add("🗓️ Reunión de vecinos - 20 de Octubre");
        eventos.add("🚰 Corte temporal de agua - 25 de Octubre");
        eventos.add("💰 Pago de aportes - Hasta el 30 de Octubre");
        eventos.add("🌳 Limpieza comunitaria - 5 de Noviembre");

        // Adaptador simple para mostrar los eventos en la lista
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                eventos
        );

        // Asignamos el adaptador a la lista
        listaNoticias.setAdapter(adapter);
    }
}
