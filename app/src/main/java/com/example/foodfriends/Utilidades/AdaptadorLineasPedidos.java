package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.LineaPedido;
import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class AdaptadorLineasPedidos extends ArrayAdapter<LineaPedidoTemp>
{
    private OnLineaPedidoChangeListener mListener;


    public AdaptadorLineasPedidos(Context context, List<LineaPedidoTemp> lineasPedidos,OnLineaPedidoChangeListener listener) {
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
        TextView txtNumeroItem = convertView.findViewById(R.id.txtNumeroLinea);
        TextView txtNombreProducto = convertView.findViewById(R.id.txtNombreProductoLineaPedido);
        TextView txtPrecioUnidad = convertView.findViewById(R.id.txtPrecioLineaPedido);
        TextView txtUnidades = convertView.findViewById(R.id.txtUnidadesPedidas);
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


