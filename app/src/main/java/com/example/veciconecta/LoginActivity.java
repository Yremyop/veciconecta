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
import com.android.volley.toolbox.StringRequest;
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

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");
                        String message = json.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if(success){
                            Intent intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                            // opcional: enviar datos de usuario
                            intent.putExtra("usuario_id", json.getJSONObject("usuario").getInt("id"));
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(this, "Error en respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
