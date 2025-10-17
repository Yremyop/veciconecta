package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarioActivity extends AppCompatActivity {

    private ListView listaNoticias;
    private ArrayList<JSONObject> listaEventos;
    private EventosAdapter adapter;
    private RequestQueue requestQueue;
    private final String URL_CALENDARIO = Config.BASE_URL + "/calendario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        // Inicializar el botón de volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            finish(); // Esto cerrará la actividad y volverá al menú principal
        });

        listaNoticias = findViewById(R.id.listaNoticias);
        listaEventos = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        adapter = new EventosAdapter();
        listaNoticias.setAdapter(adapter);

        // Cargar eventos del servidor
        cargarEventos();
    }

    private void cargarEventos() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_CALENDARIO, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            listaEventos.clear();

                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject evento = jsonArray.getJSONObject(i);
                                listaEventos.add(evento);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Error al obtener los eventos", Toast.LENGTH_SHORT).show();
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

    private class EventosAdapter extends BaseAdapter {
        private SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        private SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM", Locale.getDefault());

        @Override
        public int getCount() {
            return listaEventos.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return listaEventos.get(position);
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
                convertView = LayoutInflater.from(CalendarioActivity.this)
                        .inflate(R.layout.item_evento, parent, false);
            }

            TextView tvTitulo = convertView.findViewById(R.id.tvTitulo);
            TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
            TextView tvFecha = convertView.findViewById(R.id.tvFecha);

            try {
                JSONObject evento = getItem(position);
                String titulo = evento.getString("titulo");
                String descripcion = evento.getString("descripcion");
                String fechaStr = evento.getString("fecha");

                // Convertir y formatear la fecha
                try {
                    Date fecha = formatoEntrada.parse(fechaStr);
                    fechaStr = formatoSalida.format(fecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Agregar emoji según el tipo de evento
                String emoji = "🗓️";
                if (titulo.toLowerCase().contains("agua")) emoji = "🚰";
                else if (titulo.toLowerCase().contains("pago")) emoji = "💰";
                else if (titulo.toLowerCase().contains("limpieza")) emoji = "🌳";

                tvTitulo.setText(emoji + " " + titulo);
                tvDescripcion.setText(descripcion);
                tvFecha.setText(fechaStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
