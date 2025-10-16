package com.example.veciconecta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PagosAguaActivity extends AppCompatActivity {

    TextView tvDeudaAgua;
    Button btnPagarAgua;

    double deuda = 120.00;
    boolean pagado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_agua);

        tvDeudaAgua = findViewById(R.id.tvDeudaAgua);
        btnPagarAgua = findViewById(R.id.btnPagarAgua);

        actualizarTextoDeuda();

        btnPagarAgua.setOnClickListener(v -> realizarPago());
    }

    private void realizarPago() {
        if (!pagado) {
            pagado = true;
            deuda = 0.0;
            actualizarTextoDeuda();
            Toast.makeText(this, "Pago de agua realizado con éxito 💧", Toast.LENGTH_SHORT).show();
            btnPagarAgua.setEnabled(false); // desactiva el botón después del pago
        } else {
            Toast.makeText(this, "Ya has pagado tu deuda ✅", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarTextoDeuda() {
        tvDeudaAgua.setText("Deuda actual: Bs. " + String.format("%.2f", deuda));
    }
}
