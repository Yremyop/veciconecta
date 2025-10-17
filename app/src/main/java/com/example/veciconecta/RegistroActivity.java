package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText etUsuarioRegistro, etPasswordRegistro, etNombre, etApellidoPat, etApellidoMat, etCI;
    Button btnRegistrar;

    String URL = Config.BASE_URL + "/registro.php"; // apunta a tu PHP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Configurar botón de volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            finish(); // Esto cerrará la actividad y volverá al login
        });

        etUsuarioRegistro = findViewById(R.id.etUsuarioRegistro);
        etPasswordRegistro = findViewById(R.id.etPasswordRegistro);
        etNombre = findViewById(R.id.etNombre);
        etApellidoPat = findViewById(R.id.etApellidoPat);
        etApellidoMat = findViewById(R.id.etApellidoMat);
        etCI = findViewById(R.id.etCI);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(view -> registrarUsuario());
    }

    private void registrarUsuario() {
        String usuario = etUsuarioRegistro.getText().toString().trim();
        String password = etPasswordRegistro.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellidoPat = etApellidoPat.getText().toString().trim();
        String apellidoMat = etApellidoMat.getText().toString().trim();
        String ci = etCI.getText().toString().trim();

        if(usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellidoPat.isEmpty() || ci.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto JSON con los datos
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario", usuario);
            jsonBody.put("password", password);
            jsonBody.put("nombre", nombre);
            jsonBody.put("apellidoPat", apellidoPat);
            jsonBody.put("apellidoMat", apellidoMat);
            jsonBody.put("ci", ci);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                jsonBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                        if(success){
                            finish(); // volver al login
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(this, "Error en respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: ";
                    if (error.networkResponse != null) {
                        errorMessage += " Código: " + error.networkResponse.statusCode;
                    }
                    errorMessage += " " + error.getMessage();
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    error.printStackTrace(); // Para ver el error en el logcat
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
