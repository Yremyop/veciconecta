package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
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

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");
                        String message = json.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                        if(success){
                            finish(); // volver al login
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
                params.put("nombre", nombre);
                params.put("apellidoPat", apellidoPat);
                params.put("apellidoMat", apellidoMat);
                params.put("ci", ci);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
