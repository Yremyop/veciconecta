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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PagosAportesActivity extends AppCompatActivity {

    ListView lvAportes;
    Button btnPagarTodo;
    ArrayList<JSONObject> listaAportes;
    int usuarioId;
    String URL_APORTES = Config.BASE_URL + "/pagos_aportes.php";
    String URL_PAGAR = Config.BASE_URL + "/pagos_aportes.php";
    RequestQueue requestQueue;
    AportesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_aportes);

        // Configurar botón de volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            finish(); // Esto cerrará la actividad y volverá al menú principal
        });

        lvAportes = findViewById(R.id.lvAportes);
        btnPagarTodo = findViewById(R.id.btnPagarTodo);
        listaAportes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        btnPagarTodo.setOnClickListener(v -> pagarTodasLasDeudas());

        // Recuperar usuario_id del Intent
        usuarioId = getIntent().getIntExtra("usuario_id", 0);

        adapter = new AportesAdapter();
        lvAportes.setAdapter(adapter);

        // Cargar datos del servidor
        cargarAportes();
    }

    private void cargarAportes() {
        String urlFinal = URL_APORTES + "?usuario_id=" + usuarioId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlFinal, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            listaAportes.clear();
                            double deudaTotal = 0;

                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.getInt("pagado") == 0) {
                                    listaAportes.add(obj);
                                    deudaTotal += obj.getDouble("monto");
                                }
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(PagosAportesActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PagosAportesActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(PagosAportesActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
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

    private void realizarPago(int aporteId) {
        JSONObject jsonBody = new JSONObject();
        try {
            // Aseguramos que el body tenga exactamente el formato requerido
            jsonBody.put("id", aporteId);
            jsonBody.put("pagado", true);
            
            // Log para debug
            Toast.makeText(this, "Enviando pago: " + jsonBody.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL_APORTES, jsonBody,
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

    private void pagarTodasLasDeudas() {
        final int[] pagosCompletados = {0};
        final int totalPagos = listaAportes.size();

        if (totalPagos == 0) {
            Toast.makeText(this, "No hay aportes pendientes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deshabilitar el botón mientras se procesan los pagos
        btnPagarTodo.setEnabled(false);
        
        for (JSONObject aporte : listaAportes) {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("id", aporte.getInt("id"));
                jsonBody.put("pagado", true);

                // Log para debug
                Toast.makeText(this, "Enviando pago múltiple: " + jsonBody.toString(), Toast.LENGTH_SHORT).show();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL_APORTES, jsonBody,
                        response -> {
                            try {
                                if (response.getBoolean("success")) {
                                    pagosCompletados[0]++;
                                    // Verificar si todos los pagos están completos
                                    if (pagosCompletados[0] == totalPagos) {
                                        Toast.makeText(this, "Todos los aportes procesados exitosamente", Toast.LENGTH_SHORT).show();
                                        cargarAportes();
                                        btnPagarTodo.setEnabled(true);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                btnPagarTodo.setEnabled(true);
                            }
                        },
                        error -> {
                            Toast.makeText(this, "Error en uno de los pagos", Toast.LENGTH_SHORT).show();
                            btnPagarTodo.setEnabled(true);
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

            } catch (JSONException e) {
                e.printStackTrace();
                btnPagarTodo.setEnabled(true);
            }
        }
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
                
                int pagado = aporte.getInt("pagado");
                tvEstado.setText(pagado == 1 ? "✅ PAGADO" : "❌ PENDIENTE");
                tvEstado.setTextColor(getResources().getColor(
                    pagado == 1 ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
                ));

                // Mostrar botón de pagar solo si no está pagado
                if (pagado == 0) {
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
