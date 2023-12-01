package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.foodfriends.Modelo.Pedido;
import com.example.foodfriends.R;

import java.util.List;


public class AdaptadorHistorial extends ArrayAdapter<Pedido>
{
    public AdaptadorHistorial(Context context, List<Pedido> pedidos) {
        super(context, 0, pedidos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pedido pedido = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.historial_lista, parent, false);
        }

        TextView txtNumeroPedido= convertView.findViewById(R.id.txtNumeroPedido);
        TextView txtIdPedido = convertView.findViewById(R.id.txtIdPedido);
        TextView txtPrecioTotal = convertView.findViewById(R.id.txtPrecioTotal);
        TextView txtFechaPedido = convertView.findViewById(R.id.txtFechaPedido);

        txtNumeroPedido.setText(String.valueOf(position + 1));
        txtIdPedido.setText("ID Pedido: " + pedido.getIdPedido());
        txtPrecioTotal.setText("Precio: " + String.valueOf(pedido.getPrecioTotal()));
        txtFechaPedido.setText("Fecha: " + pedido.getFechaPedido());

        return convertView;
    }
}


