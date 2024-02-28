package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodfriends.Activities.LineasPedidosActivity;
import com.example.foodfriends.Modelo.Pedido;
import com.example.foodfriends.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Clase AdaptadorHistorial
 * Esta clase actúa como un adaptador para mostrar el historial de los pedidos realizados
 * por el usuario.
 * @autor Yago Rodríguez Martínez
 * @version 1.0
 */
public class AdaptadorHistorial extends ArrayAdapter<Pedido> {
    ImageView imgMostrarLineas;

    // Constructor del adaptador
    public AdaptadorHistorial(Context context, List<Pedido> pedidos) {
        super(context, 0, pedidos);
    }

    // Método que se llama para obtener la vista que se muestra en una posición específica
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener el objeto Pedido en la posición actual
        Pedido pedido = getItem(position);

        // Si convertView es null, inflar la vista desde el archivo de diseño historial_lista.xml
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.historial_lista, parent, false);
        }

        // Obtener referencias a los elementos de la interfaz de usuario (TextViews)
        TextView txtNumeroPedido = convertView.findViewById(R.id.txtNumeroLineaPedido);
        TextView txtIdPedido = convertView.findViewById(R.id.txtIdUsuario);
        TextView txtPrecioTotal = convertView.findViewById(R.id.txtUnidades);
        TextView txtFechaPedido = convertView.findViewById(R.id.txtTotalLinea);
        imgMostrarLineas = convertView.findViewById(R.id.imgMostrarLineas);
        imgMostrarLineas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el ID del pedido
                String idPedido = getItem(position).getIdPedido();
                String fechaPedido=getItem(position).getFechaPedido();
                Double precioPedido=getItem(position).getPrecioTotal();

                // Crea un Intent y agrega el ID del pedido como extra
                Intent intent = new Intent(getContext(), LineasPedidosActivity.class);
                intent.putExtra("ID_PEDIDO", idPedido);
                intent.putExtra("FECHA_PEDIDO",fechaPedido);
                intent.putExtra("PRECIO_PEDIDO",precioPedido);

                // Inicia la actividad DetallesPedidoActivity
                getContext().startActivity(intent);
            }
        });

        // Establecer los valores correspondientes en los TextViews
        txtNumeroPedido.setText(String.valueOf(position + 1)); // Se suma 1 para mostrar números de pedido comenzando desde 1
        txtIdPedido.setText("Id Pedido: " + pedido.getIdPedido());
        txtPrecioTotal.setText("Precio: " + String.valueOf(formatearDecimal(pedido.getPrecioTotal()))+"€");
        txtFechaPedido.setText("Fecha: " + pedido.getFechaPedido());

        // Devolver la vista para mostrar en la posición actual
        return convertView;
    }


    // Método privado para formatear un número decimal con dos decimales
    private static String formatearDecimal(double numero) {
        // Utilizar DecimalFormat para formatear el número
        DecimalFormat formato = new DecimalFormat("#.##");
        return formato.format(numero);
    }
}



