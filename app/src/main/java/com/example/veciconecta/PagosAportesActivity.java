package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PagosAportesActivity extends AppCompatActivity {

    ListView lvAportes;
    ArrayList<String> listaAportes;
    int usuarioId;
    String URL = Config.BASE_URL + "/aportes_multas.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_aportes);

        lvAportes = findViewById(R.id.lvAportes);
        listaAportes = new ArrayList<>();

        // Recuperar usuario_id del Intent
        usuarioId = getIntent().getIntExtra("usuario_id", 0);

        // Cargar datos del servidor
        cargarAportes();
    }

    private void cargarAportes() {
        String urlFinal = URL + "?usuario_id=" + usuarioId;

        StringRequest request = new StringRequest(Request.Method.GET, urlFinal,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listaAportes.clear();

                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String descripcion = obj.getString("descripcion");
                            double monto = obj.getDouble("monto");
                            boolean pagado = obj.getBoolean("pagado");

                            String estado = pagado ? "✅ Pagado" : "❌ Pendiente";
                            listaAportes.add(descripcion + " - Bs. " + monto + " - " + estado);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, listaAportes);
                        lvAportes.setAdapter(adapter);

                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
