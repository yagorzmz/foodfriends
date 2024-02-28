package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.foodfriends.Activities.CarritoActivity;
import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.R;

import java.util.List;

/**
 * Clase AdaptadorLineasPedidosTemp
 * Esta clase actúa como un adaptador para mostrar las lineas de pedido temporales que se crean
 * cuando el usuario añade un producto al carrito y que conforman el pedido.
 * @autor Yago Rodríguez Martínez
 * @version 1.0
 */
public class AdaptadorLineasPedidosTemp extends ArrayAdapter<LineaPedidoTemp>
{
    private OnLineaPedidoChangeListener mListener;
    public AdaptadorLineasPedidosTemp(Context context, List<LineaPedidoTemp> lineasPedidos, OnLineaPedidoChangeListener listener) {
        super(context, 0, lineasPedidos);
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Obtener el elemento de datos para esta posición
        LineaPedidoTemp lineaPedido = getItem(position);

        // Comprobar si una vista existente está siendo reutilizada, de lo contrario, inflar la vista
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.carrito_lista, parent, false);
        }

        // Obtener referencias a las vistas en el layout
        TextView txtNumeroItem = convertView.findViewById(R.id.txtNumeroLineaPedido);
        TextView txtNombreProducto = convertView.findViewById(R.id.txtIdUsuario);
        TextView txtPrecioUnidad = convertView.findViewById(R.id.txtPrecioLineaPedido);
        TextView txtUnidades = convertView.findViewById(R.id.txtUnidades);
        ImageButton btnEliminarLinea = convertView.findViewById(R.id.imgButtonEliminarLinea);

        // Asignar los datos del objeto a las vistas
        txtNumeroItem.setText(String.valueOf(position + 1));  // El índice de posición comienza en 0, por lo que agregamos 1
        txtNombreProducto.setText("Producto: " + lineaPedido.getNombreProducto());
        txtPrecioUnidad.setText("Precio/unidad: " + String.valueOf(lineaPedido.getPrecioProducto()));
        txtUnidades.setText("Unidades: " + String.valueOf(lineaPedido.getUnidades()));

        // En algún lugar donde haya un cambio en tus datos
        if (mListener != null) {
            mListener.onLineaPedidoChanged();
        }
        btnEliminarLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEliminarLineaPedido(position);
                CarritoActivity.reproducirSonidoDelete();
            }
        });
        return convertView;
    }
    public interface OnLineaPedidoChangeListener {
        void onLineaPedidoChanged();
        void onEliminarLineaPedido(int position);
    }
    // Método para establecer el listener
    public void setOnLineaPedidoChangeListener(OnLineaPedidoChangeListener listener) {
        this.mListener = listener;
    }
    // Método que maneja el clic del botón eliminar
    private void onEliminarButtonClick(int position) {
        if (mListener != null) {
            mListener.onEliminarLineaPedido(position);
        }
    }
}


