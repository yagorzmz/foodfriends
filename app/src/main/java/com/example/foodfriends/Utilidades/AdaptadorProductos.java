package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
/**
 * Clase AdaptadorProductos
 * Esta clase actúa como un adaptador para mostrar todos los productos de un restaurante seleccionado
 * en un recyclerview.
 * @autor Yago Rodríguez Martínez
 * @version 1.0
 */
public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolder>
{
    private List<Producto> listaProductos;
    private List<Producto> listaProductosCompleta;
    // Constructor
    public AdaptadorProductos(List<Producto> itemList, Context context, AdaptadorProductos.OnItemClickListener listener) {
        this.nInflater = LayoutInflater.from(context);
        this.context = context;
        this.listaProductosCompleta = itemList;
        this.listener=listener;

    }
    public void filtrar(String texto) {
        listaProductos.clear();
        if(texto.isEmpty()){
            listaProductos.addAll(listaProductosCompleta);
        } else{
            texto = texto.toLowerCase();
            for(Producto producto: listaProductosCompleta){
                if(producto.getNombreProducto().toLowerCase().contains(texto)){
                    listaProductos.add(producto);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Elementos del adaptador
    final AdaptadorProductos.OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(Producto item);
    }
    private LayoutInflater nInflater;
    private Context context;
    private Uri url_imagen;
    StorageReference storagereference;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño de un elemento de la lista a partir de un archivo XML
        View view = nInflater.inflate(R.layout.productos_lista, parent, false);
        return new AdaptadorProductos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // Llama al método bindData del ViewHolder para establecer los datos en la posición actual
        holder.bindData(listaProductosCompleta.get(position));
    }


    // Clase interna ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView nombreProducto, descripcion;

        ViewHolder(View itemView) {
            super(itemView);

            // Obtiene referencias a las vistas dentro de cada elemento
            nombreProducto = itemView.findViewById(R.id.txtNombreProductoCarrito);
            descripcion=itemView.findViewById(R.id.txtUnidadesPedidasCarrito);
            imgProducto = itemView.findViewById(R.id.imgProductoLineaPedido);
        }

        // Método para establecer los datos en las vistas
        void bindData(final Producto item)
        {
            nombreProducto.setText(item.getNombreProducto());
            descripcion.setText(item.getDescripcion());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });

            // Obtén una referencia al archivo en Firebase Storage
            String imageUrl = item.getUrlProducto();
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

            // Convierte la referencia a URL y plasma la imagen en el imageview
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Carga la imagen en el ImageView utilizando Picasso
                    Glide.with(itemView.getContext())
                            .load(uri.toString())
                            .placeholder(R.drawable.waiting)
                            .error(R.drawable.error)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // Utiliza caché solo para recursos decodificados
                            .into(imgProducto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar fallos al obtener la URL de descarga
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //Devuelve la cantidad de elementos en la lista de datos
        return listaProductosCompleta.size();
    }

    //Método para actualizar la lista de datos
    public void setItem(List<Producto> items) {
        listaProductosCompleta = items;
    }
    public void setFilteredList(List<Producto> listaFiltrada){
        this.listaProductosCompleta=listaFiltrada;
    }


}