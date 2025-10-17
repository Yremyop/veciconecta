package com.example.veciconecta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.veciconecta.R;
import com.example.veciconecta.models.Aporte;

import java.util.List;

public class AporteAdapter extends ArrayAdapter<Aporte> {
    private Context context;
    private List<Aporte> aportes;
    private OnPagarClickListener pagarClickListener;

    public interface OnPagarClickListener {
        void onPagarClick(Aporte aporte);
    }

    public AporteAdapter(Context context, List<Aporte> aportes, OnPagarClickListener listener) {
        super(context, 0, aportes);
        this.context = context;
        this.aportes = aportes;
        this.pagarClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_aporte, parent, false);
        }

        Aporte currentAporte = aportes.get(position);

        TextView tvConcepto = listItem.findViewById(R.id.tvConcepto);
        TextView tvMonto = listItem.findViewById(R.id.tvMonto);
        TextView tvFechaVencimiento = listItem.findViewById(R.id.tvFechaVencimiento);
        TextView tvEstado = listItem.findViewById(R.id.tvEstado);
        Button btnPagar = listItem.findViewById(R.id.btnPagar);

        tvConcepto.setText(currentAporte.getConcepto());
        tvMonto.setText("Bs. " + currentAporte.getMonto());
        tvFechaVencimiento.setText("Vence: " + currentAporte.getFechaVencimiento());
        tvEstado.setText(currentAporte.getEstado());

        // Solo mostrar el botón de pagar si está pendiente
        if (currentAporte.getEstado().equals("PENDIENTE")) {
            btnPagar.setVisibility(View.VISIBLE);
            btnPagar.setOnClickListener(v -> {
                if (pagarClickListener != null) {
                    pagarClickListener.onPagarClick(currentAporte);
                }
            });
        } else {
            btnPagar.setVisibility(View.GONE);
        }

        return listItem;
    }
}