package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class AdaptadorEmpresas extends RecyclerView.Adapter<AdaptadorEmpresas.ViewHolder >
{
    //Constructor
    public AdaptadorEmpresas(List<Empresa> itemList, Context context, AdaptadorEmpresas.OnItemClickListener listener) {
        this.nInflater = LayoutInflater.from(context);
        this.context = context;
        this.listaCompleta = itemList;
        this.listener=listener;

    }
    //Elementos del adaptador

    private List<Empresa> listaCompleta; // Lista de empresas sin filtrar
    final AdaptadorEmpresas.OnItemClickListener listener;
    public void setFilteredList(List<Empresa> listaFiltrada){
        this.listaCompleta=listaFiltrada;
    }
    public interface OnItemClickListener{
        void onItemClick(Empresa item);
    }
    private LayoutInflater nInflater;
    private Context context;

    StorageReference storagereference;

    @NonNull
    @Override
    public AdaptadorEmpresas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Infla el diseño de un elemento de la lista a partir de un archivo XML
        View view = nInflater.inflate(R.layout.restaurante_lista, parent, false);
        return new AdaptadorEmpresas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptadorEmpresas.ViewHolder holder, final int position) {
        //Llama al método bindData del ViewHolder para establecer los datos en la posición actual
        holder.bindData(listaCompleta.get(position));
    }

    @Override
    public int getItemCount() {
        //Devuelve la cantidad de elementos en la lista de datos
        return listaCompleta.size();
    }

    //Método para actualizar la lista de datos
    public void setItem(List<Empresa> items) {
        listaCompleta = items;
    }

    //Clase interna ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRestaurante;
        TextView nombre,telefono, tipo;
        RatingBar valoracion;

        ViewHolder(View itemView) {
            super(itemView);

            //Obtiene referencias a las vistas dentro de cada elemento
            imgRestaurante = itemView.findViewById(R.id.imgProductoLineaPedido);
            nombre = itemView.findViewById(R.id.txtIdUsuario);
            tipo = itemView.findViewById(R.id.txtUnidades);
            telefono= itemView.findViewById(R.id.txtNumeroTelefono);
            valoracion = itemView.findViewById(R.id.ratingBarRecycler);
        }

        //Método para establecer los datos en las vistas
        void bindData(final Empresa item) {
            nombre.setText(item.getNombreEmpresa());
            tipo.setText(item.getTipoComida());
            telefono.setText("+34 "+item.getTelefono().toString());
            valoracion.setRating(item.getValoracion());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });

            //Obténemos una referencia al archivo en Firebase Storage
            String imageUrl = item.getImagenRestaurante();
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

            //Convierte la referencia a URL y plasma la imagen en el imageview
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Carga la imagen en el ImageView utilizando Picasso
                    Glide.with(itemView.getContext())
                            .load(uri.toString())
                            .placeholder(R.drawable.waiting)
                            .error(R.drawable.error)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // Utiliza caché solo para recursos decodificados
                            .into(imgRestaurante);
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
    public void setItems(List<Empresa> itemList) {
        listaCompleta = itemList;
        notifyDataSetChanged(); // Asegúrate de llamar a este método
    }
}


