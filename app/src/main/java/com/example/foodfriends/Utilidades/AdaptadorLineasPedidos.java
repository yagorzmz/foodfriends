package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.R;

import java.text.DecimalFormat;
import java.util.List;

public class AdaptadorLineasPedidos extends ArrayAdapter<LineaPedidoTemp> {

    public AdaptadorLineasPedidos(Context context, List<LineaPedidoTemp> lineasPedidos) {
        super(context, 0, lineasPedidos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtén el objeto LineaPedido en la posición actual
        LineaPedidoTemp lineaPedido = getItem(position);

        // Si convertView es null, infla la vista desde el archivo de diseño
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_linea_pedido, parent, false);
        }

        // Obtén referencias a los elementos de la interfaz de usuario (TextViews)
        TextView txtNumeroLineaPedido = convertView.findViewById(R.id.txtNumeroLineaPedido);
        TextView txtNombreProducto = convertView.findViewById(R.id.txtIdUsuario);
        TextView txtPrecioProducto = convertView.findViewById(R.id.txtPrecioProducto);
        TextView txtUnidades = convertView.findViewById(R.id.txtUnidades);
        TextView txtTotalLinea = convertView.findViewById(R.id.txtTotalLinea);

        // Calcula el total de la línea de pedido
        double totalLinea = lineaPedido.getPrecioProducto() * lineaPedido.getUnidades();

        // Establece los valores correspondientes en los TextViews
        txtNumeroLineaPedido.setText(String.valueOf(position + 1));
        txtNombreProducto.setText("Producto: " + lineaPedido.getNombreProducto());
        txtPrecioProducto.setText("Precio/uni: " + formatearDecimal(lineaPedido.getPrecioProducto())+"€");
        txtUnidades.setText("Unidades: " + String.valueOf(lineaPedido.getUnidades()));
        txtTotalLinea.setText("Precio linea: " + formatearDecimal(totalLinea)+"€");

        return convertView;
    }

    private static String formatearDecimal(double numero) {
        DecimalFormat formato = new DecimalFormat("#.##");
        return formato.format(numero);
    }
}
