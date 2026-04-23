package com.example.lab2_20222297.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lab2_20222297.R;
import com.example.lab2_20222297.model.Equipo;

import java.util.List;

public class EquipoAdapter extends ArrayAdapter<Equipo> {

    private final Context context;
    private final List<Equipo> equipos;

    public EquipoAdapter(Context context, List<Equipo> equipos) {
        super(context, 0, equipos);
        this.context = context;
        this.equipos = equipos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_equipo, parent, false);
        }

        Equipo equipo = equipos.get(position);

        View viewEstadoColor = convertView.findViewById(R.id.viewEstadoColor);
        TextView tvCodigo = convertView.findViewById(R.id.tvCodigo);
        TextView tvNombre = convertView.findViewById(R.id.tvNombre);
        TextView tvTipo = convertView.findViewById(R.id.tvTipo);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);

        tvCodigo.setText(equipo.getCodigo());
        tvNombre.setText(equipo.getNombre());
        tvTipo.setText(equipo.getTipo());
        tvEstado.setText(equipo.getEstado());

        int color;
        switch (equipo.getEstado()) {
            case "Operativo":
                color = context.getColor(R.color.estado_operativo);
                break;
            case "En mantenimiento":
                color = context.getColor(R.color.estado_mantenimiento);
                break;
            default:
                color = context.getColor(R.color.estado_fuera_servicio);
                break;
        }
        viewEstadoColor.setBackgroundColor(color);

        return convertView;
    }
}
