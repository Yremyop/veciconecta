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

public class PagosAguaActivity extends AppCompatActivity {

    private ListView lvPagosAgua;
    private Button btnPagarTodo;
    private ArrayList<JSONObject> listaPagosAgua;
    private int usuarioId;
    private RequestQueue requestQueue;
    private PagosAguaAdapter adapter;
    //private final String URL_BASE = "http://localhost/veciconecta/pagos_agua.php";
    String URL_BASE = Config.BASE_URL + "/pagos_agua.php"; // apunta a tu PHP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_agua);

        // Configurar botón de volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            finish(); // Esto cerrará la actividad y volverá al menú principal
        });

        lvPagosAgua = findViewById(R.id.lvPagosAgua);
        btnPagarTodo = findViewById(R.id.btnPagarTodo);
        listaPagosAgua = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        // Recuperar usuario_id del Intent
        usuarioId = getIntent().getIntExtra("usuario_id", 0);

        adapter = new PagosAguaAdapter();
        lvPagosAgua.setAdapter(adapter);

        btnPagarTodo.setOnClickListener(v -> pagarTodasLasDeudas());

        // Cargar deudas del usuario
        cargarPagosAgua();
    }

    private void cargarPagosAgua() {
        String url = URL_BASE + "?usuario_id=" + usuarioId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            listaPagosAgua.clear();
                            double deudaTotal = 0;

                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.getInt("pagado") == 0) {  // Cambio aquí para manejar 0/1 en lugar de boolean
                                    listaPagosAgua.add(obj);
                                    deudaTotal += obj.getDouble("monto");
                                }
                            }

                            // Actualizar el texto del botón pagar todo con el total
                            btnPagarTodo.setText(String.format("Pagar Todo (Bs. %.2f)", deudaTotal));
                            btnPagarTodo.setEnabled(!listaPagosAgua.isEmpty());
                            
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
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

    private void realizarPago(int pagoId) {
        JSONObject jsonBody = new JSONObject();
        try {
            // Aseguramos que el body tenga exactamente el formato requerido
            jsonBody.put("id", pagoId);
            jsonBody.put("pagado", true);  // enviamos true como booleano
            
            // Log para debug
            Toast.makeText(this, "Enviando pago: " + jsonBody.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL_BASE, jsonBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        
                        if (success) {
                            cargarPagosAgua(); // Recargar la lista
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
        final int totalPagos = listaPagosAgua.size();

        if (totalPagos == 0) {
            Toast.makeText(this, "No hay pagos pendientes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deshabilitar el botón mientras se procesan los pagos
        btnPagarTodo.setEnabled(false);
        
        for (JSONObject pago : listaPagosAgua) {
            try {
                JSONObject jsonBody = new JSONObject();
                // Aseguramos que el body tenga exactamente el formato requerido
                jsonBody.put("id", pago.getInt("id"));
                jsonBody.put("pagado", true);  // enviamos true como booleano
                
                // Log para debug
                Toast.makeText(this, "Enviando pago múltiple: " + jsonBody.toString(), Toast.LENGTH_SHORT).show();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL_BASE, jsonBody,
                        response -> {
                            try {
                                if (response.getBoolean("success")) {
                                    pagosCompletados[0]++;
                                    // Verificar si todos los pagos están completos
                                    if (pagosCompletados[0] == totalPagos) {
                                        Toast.makeText(this, "Todos los pagos procesados exitosamente", Toast.LENGTH_SHORT).show();
                                        cargarPagosAgua();
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

    private class PagosAguaAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listaPagosAgua.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return listaPagosAgua.get(position);
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
                convertView = LayoutInflater.from(PagosAguaActivity.this)
                        .inflate(R.layout.item_pago_agua, parent, false);
            }

            TextView tvMes = convertView.findViewById(R.id.tvMes);
            TextView tvMonto = convertView.findViewById(R.id.tvMonto);
            Button btnPagar = convertView.findViewById(R.id.btnPagar);

            try {
                JSONObject pago = getItem(position);
                String mes = pago.getString("mes");
                // Convertir primera letra a mayúscula
                mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);
                tvMes.setText(mes);
                tvMonto.setText(String.format("Bs. %s", pago.getString("monto")));
                
                btnPagar.setOnClickListener(v -> realizarPago(pago.optInt("id")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
