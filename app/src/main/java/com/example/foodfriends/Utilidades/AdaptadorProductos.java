package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.R;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.List;

public class AdaptadorProductos extends ArrayAdapter<Producto> {


    public AdaptadorProductos(Context context, List<Producto> productos) {
        super(context, 0, productos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Obtén el objeto Producto para esta posición
        Producto producto = getItem(position);

        // Reutiliza o infla la vista
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.productos_lista, parent, false);
        }

        // Obtén las referencias a las vistas en el layout item_producto
        ImageView imgProducto = convertView.findViewById(R.id.imgProducto);
        TextView txtNombreProducto = convertView.findViewById(R.id.txtNombreProducto);
        TextView txtDescripcionProducto = convertView.findViewById(R.id.txtDescripcionProducto);

        // Configura las vistas con los datos del producto actual
        if (producto != null) {
            // Configura la imagen, el nombre y la descripción del producto en las vistas correspondientes
            imgProducto.setImageResource(R.drawable.ic_launcher_foreground);  // Puedes cambiar esto con la lógica para cargar imágenes
            txtNombreProducto.setText(producto.getNombreProducto());
            txtDescripcionProducto.setText(producto.getDescripcion());
        }

        return convertView;
    }
}