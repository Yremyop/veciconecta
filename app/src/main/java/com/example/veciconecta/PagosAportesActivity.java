package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
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
    ArrayList<JSONObject> listaAportes;
    int usuarioId;
    String URL_APORTES = Config.BASE_URL + "/aportes_multas.php";
    String URL_PAGAR = Config.BASE_URL + "/pagar_aporte.php";
    RequestQueue requestQueue;
    AportesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_aportes);

        lvAportes = findViewById(R.id.lvAportes);
        listaAportes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        // Recuperar usuario_id del Intent
        usuarioId = getIntent().getIntExtra("usuario_id", 0);

        adapter = new AportesAdapter();
        lvAportes.setAdapter(adapter);

        // Cargar datos del servidor
        cargarAportes();
    }

    private void cargarAportes() {
        String urlFinal = URL_APORTES + "?usuario_id=" + usuarioId;

        StringRequest request = new StringRequest(Request.Method.GET, urlFinal,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listaAportes.clear();

                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            listaAportes.add(obj);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void realizarPago(int aporteId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("aporte_id", aporteId);
            jsonBody.put("usuario_id", usuarioId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_PAGAR, jsonBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        
                        if (success) {
                            cargarAportes(); // Recargar la lista
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String mensaje = "Error al procesar el pago";
                    if (error.networkResponse != null) {
                        mensaje += " (" + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private class AportesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listaAportes.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return listaAportes.get(position);
        }

        @Override
        public long getItemId(int position) {
            try {
                return getItem(position).getInt("id");
            } catch (JSONException e) {
                return 0;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(PagosAportesActivity.this)
                        .inflate(R.layout.item_aporte, parent, false);
            }

            TextView tvConcepto = convertView.findViewById(R.id.tvConcepto);
            TextView tvMonto = convertView.findViewById(R.id.tvMonto);
            TextView tvEstado = convertView.findViewById(R.id.tvEstado);
            Button btnPagar = convertView.findViewById(R.id.btnPagar);

            try {
                JSONObject aporte = getItem(position);
                tvConcepto.setText(aporte.getString("descripcion"));
                tvMonto.setText(String.format("Bs. %.2f", aporte.getDouble("monto")));
                
                boolean pagado = aporte.getBoolean("pagado");
                tvEstado.setText(pagado ? "✅ PAGADO" : "❌ PENDIENTE");
                tvEstado.setTextColor(getResources().getColor(
                    pagado ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
                ));

                // Mostrar botón de pagar solo si no está pagado
                if (!pagado) {
                    btnPagar.setVisibility(View.VISIBLE);
                    btnPagar.setOnClickListener(v -> realizarPago(aporte.optInt("id")));
                } else {
                    btnPagar.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
