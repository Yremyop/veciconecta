package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etUsuarioLogin, etPasswordLogin;
    Button btnLogin;
    TextView tvIrRegistro;
    String URL = Config.BASE_URL + "/login.php"; // apunta a tu PHP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuarioLogin = findViewById(R.id.etUsuarioLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnLogin.setOnClickListener(v -> loginUsuario());

        tvIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }

    private void loginUsuario(){
        String usuario = etUsuarioLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if(usuario.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto JSON con los datos
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario", usuario);
            jsonBody.put("password", password);
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

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if(success){
                            Intent intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                            // opcional: enviar datos de usuario
                            intent.putExtra("usuario_id", response.getJSONObject("usuario").getInt("id"));
                            startActivity(intent);
                            finish();
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
                    error.printStackTrace();
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
