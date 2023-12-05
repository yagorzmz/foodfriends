package com.example.foodfriends.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.DateTransformation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.Modelo.LineaPedido;
import com.example.foodfriends.Modelo.Pedido;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MasVendidosActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    private ImageView iconoToolbar;
    private DatabaseReference pedidosReference,lineasPedidosReference;
    private List<String> top5Productos;
    private TextView txtPrimerPuesto, txtSegundoPuesto, txtTercerPuesto, txtCuartoPuesto, txtQuintoPuesto;
    private ImageView imgPrimerPuesto, imgSegundoPuesto, imgTercerPuesto, imgCuartoPuesto, imgQuintoPuesto;
    private DatabaseReference productosReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mas_vendidos);

        //Creamos la lista de los 5 productos mas vendidos
        top5Productos=new ArrayList<>();

        // Enlazamos los elementos
        toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        iconoToolbar=findViewById(R.id.iconoToolbar);

        // Eliminar el título del Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txtPrimerPuesto = findViewById(R.id.txtPrimerPuesto);
        txtSegundoPuesto = findViewById(R.id.txtSegundoPuesto);
        txtTercerPuesto = findViewById(R.id.txtTercerPuesto);
        txtCuartoPuesto = findViewById(R.id.txtCuartoPuesto);
        txtQuintoPuesto = findViewById(R.id.txtQuintoPuesto);

        //Metodos OnClick en las imagenes, apra acceder al producto
        imgPrimerPuesto = findViewById(R.id.imgPrimerPuesto);
        imgPrimerPuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirProductoActivity(top5Productos.get(0)); // Pasa el ID del primer producto
            }
        });

        imgSegundoPuesto = findViewById(R.id.imgSegundoPuesto);
        imgSegundoPuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirProductoActivity(top5Productos.get(1)); // Pasa el ID del segundo producto
            }
        });

        imgTercerPuesto = findViewById(R.id.imgTercerPuesto);
        imgTercerPuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirProductoActivity(top5Productos.get(2)); // Pasa el ID del tercer producto
            }
        });

        imgCuartoPuesto = findViewById(R.id.imgCuartoPuesto);
        imgCuartoPuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirProductoActivity(top5Productos.get(3)); // Pasa el ID del cuarto producto
            }
        });

        imgQuintoPuesto = findViewById(R.id.imgQuintoPuesto);
        imgQuintoPuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirProductoActivity(top5Productos.get(4)); // Pasa el ID del quinto producto
            }
        });

        // Inicializar la referencia a la base de datos
        pedidosReference = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Pedidos");
        lineasPedidosReference = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LineasPedidos");
        productosReference = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Productos");

        // Obtenemos los pedidos de los últimos 7 días
        obtenerPedidosUltimos7Dias(new OnPedidosLoadedListener() {
            @Override
            public void onPedidosLoaded(List<String> idPedidos) {
                // Obtenemos los top 5 productos más vendidos
                obtenerTop5Productos(idPedidos, new OnProductosIdLoadedListener() {
                    @Override
                    public void onProductosIdLoaded(List<String> top5Productos) {
                        // Mostramos los detalles de los productos en las vistas
                        mostrarTop(top5Productos);
                    }
                });
            }
        });
    }
    // Método para abrir la ProductoActivity con la información del producto
    private void abrirProductoActivity(String idProducto) {
        // Obtenemos el producto utilizando el id
        obtenerProductoPorId(idProducto, new OnProductoLoadedListener() {
            @Override
            public void onProductoLoaded(Producto producto) {
                //Verificamos que el producto no sea nulo antes de abrir la actividad
                if (producto != null) {
                    //Creamos un Intent para abrir la ProductoActivity
                    Intent intent = new Intent(getApplicationContext(), ProductoActivity.class);
                    // Pasa la información del producto al Intent
                    intent.putExtra("idProducto", producto.getIdProducto());
                    intent.putExtra("nombreProducto", producto.getNombreProducto());
                    intent.putExtra("descripcion", producto.getDescripcion());
                    intent.putExtra("empresaId", producto.getEmpresaId());
                    intent.putExtra("urlProducto", producto.getUrlProducto());
                    intent.putExtra("precio", producto.getPrecio());
                    // Inicia la ProductoActivity
                    startActivity(intent);
                } else {
                    //Manejamos el caso en el que no se encuentre el producto
                    Toast.makeText(getApplicationContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Método para obtener detalles de un producto por su ID
    private void obtenerProductoPorId(String idProducto, OnProductoLoadedListener listener)
    {
        //Referencia de productos
        productosReference.child(idProducto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verificamos si hay algún dato en el snapshot
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // Obtenemos detalles del producto
                    String nombreProducto = dataSnapshot.child("NombreProducto").getValue(String.class);
                    String urlProducto = dataSnapshot.child("urlProducto").getValue(String.class);
                    String descripcion = dataSnapshot.child("Descripcion").getValue(String.class);
                    String empresaId = dataSnapshot.child("EmpresaId").getValue(String.class);
                    Double precio = dataSnapshot.child("Precio").getValue(Double.class);

                    // Verificar si alguno de los valores es nulo
                    if (nombreProducto != null && urlProducto != null && descripcion != null && empresaId != null && precio != null) {
                        // Creamos un objeto Producto con la información
                        Producto producto = new Producto(idProducto, nombreProducto, descripcion, empresaId, urlProducto, precio);

                        // Notificamos al listener que el producto ha sido cargado
                        if (listener != null) {
                            listener.onProductoLoaded(producto);
                        }
                    } else {
                        // Notificamos al listener que algún valor es nulo
                        if (listener != null) {
                            listener.onProductoLoaded(null);
                        }
                    }
                } else {
                    // Notificamos al listener que el producto no fue encontrado
                    if (listener != null) {
                        listener.onProductoLoaded(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"No se encontraron prodcutos",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Interfaz para manejar la carga del producto
    public interface OnProductoLoadedListener {
        void onProductoLoaded(Producto producto);
    }
    //Inflamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        // Deshabilita el ítem del menú correspondiente a esta actividad
        MenuItem item = menu.findItem(R.id.item_masvendidos);
        if (item != null) {
            item.setEnabled(false);
        }

        return true;
    }
    //Recogemos los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar las opciones del menú
        int id = item.getItemId();
        if (id == R.id.item_inicio) {
            // Iniciar la actividad del Carrito
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.item_carrito) {
            // Iniciar la actividad del Carrito
            Intent i = new Intent(getApplicationContext(), CarritoActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_perfil) {
            // Iniciar la actividad de Perfil
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_acercade) {
            // Iniciar la actividad Acerca de
            Intent i = new Intent(getApplicationContext(), AcercaDeActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    //Metodo que recoge y devuelve la lista de pedidos realizados en los ultimos 7 dias
    private void obtenerPedidosUltimos7Dias(final OnPedidosLoadedListener listener) {
        final List<String> idPedidos = new ArrayList<>();

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = sdf.format(calendar.getTime());

        // Calcular la fecha de hace 7 días
        calendar.add(Calendar.DAY_OF_YEAR, -6);  // Cambia a -6 para incluir el día actual
        String fechaHace7Dias = sdf.format(calendar.getTime());

        // Realizar la consulta con un rango de fechas
        Query query = pedidosReference.orderByChild("FechaPedido").startAt(fechaHace7Dias + " 00:00:00").endAt(fechaActual + " 23:59:59");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                    // Obtener el idPedido y agregarlo a la lista
                    String idPedido = pedidoSnapshot.getKey();
                    idPedidos.add(idPedido);
                }

                // Notificar que los datos han sido cargados
                if (listener != null) {
                    listener.onPedidosLoaded(idPedidos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });
    }


    public interface OnPedidosLoadedListener {
        void onPedidosLoaded(List<String> idPedidos);
    }
    //Metodo que recoge y devuelve la lista de id de productos realizados en los ultimos 7 dias
    //y obtiene los 5 mas vendidos
    private void obtenerTop5Productos(List<String>listaId,final OnProductosIdLoadedListener listener) {
        final List<String> idProductos = new ArrayList<>();
        final Map<String, Integer> topProductos = new HashMap<>();
        lineasPedidosReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String idPedido : listaId) {
                    for (DataSnapshot lineaSnapshot : dataSnapshot.getChildren()) {
                        String idProducto = lineaSnapshot.child("ProductoId").getValue(String.class);
                        String PedidoId = lineaSnapshot.child("PedidoId").getValue(String.class);
                        Integer Unidades = lineaSnapshot.child("Unidades").getValue(Integer.class);
                        if(idPedido.equals(PedidoId)) {
                            // Sumar las unidades al producto correspondiente en el mapa
                            topProductos.put(idProducto, topProductos.getOrDefault(idProducto, 0) + Unidades);
                        }
                    }
                }
                // Ordenar el mapa por valor en orden descendente para obtener el top 5
                List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(topProductos.entrySet());
                listaOrdenada.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

                // Limpiar la lista antes de agregar elementos
                top5Productos.clear();

                // Tomar los primeros 5 elementos o menos si no hay suficientes
                int count = 0;
                for (Map.Entry<String, Integer> entry : listaOrdenada) {
                    top5Productos.add(entry.getKey());
                    count++;
                    if (count == 5) {
                        break;
                    }
                }
                // Notificar que los datos han sido cargados
                if (listener != null) {
                    listener.onProductosIdLoaded(top5Productos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface OnProductosIdLoadedListener {
        void onProductosIdLoaded(List<String> idProductos);
    }
    //Metodo que recoge los prodcutos y ecupera su informacion para poder mostrarla
    private void mostrarTop(List<String> top5Productos) {
        for (int i = 0; i < top5Productos.size(); i++) {
            final int index = i;
            String idProducto = top5Productos.get(i);

            // Realizar la consulta para obtener detalles del producto por su ID
            productosReference.child(idProducto).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Obtener detalles del producto
                    String nombreProducto = dataSnapshot.child("NombreProducto").getValue(String.class);
                    String urlProducto = dataSnapshot.child("urlProducto").getValue(String.class);

                    // Mostrar detalles en las vistas correspondientes
                    mostrarDetallesProducto(index, nombreProducto, urlProducto);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores si es necesario
                }
            });
        }
    }

    //Recoge cada productos y lo plasma en los imageview y textviews
    private void mostrarDetallesProducto(int index, String nombreProducto, String urlProducto) {
        StorageReference storageReference;
        // Verificar que el índice sea válido
        if (index >= 0 && index < 5) {
            // Mostrar detalles en las vistas correspondientes
            switch (index) {
                case 0:
                    txtPrimerPuesto.setText(nombreProducto);
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlProducto);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Carga la imagen en el ImageView utilizando Picasso
                            Glide.with(getApplicationContext())
                                    .load(uri.toString())
                                    .placeholder(R.drawable.waiting)
                                    .error(R.drawable.error)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(imgPrimerPuesto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar fallos al obtener la URL de descarga
                            e.printStackTrace();
                        }
                    });
                    break;
                case 1:
                    txtSegundoPuesto.setText(nombreProducto);
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlProducto);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Carga la imagen en el ImageView utilizando Picasso
                            Glide.with(getApplicationContext())
                                    .load(uri.toString())
                                    .placeholder(R.drawable.waiting)
                                    .error(R.drawable.error)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(imgSegundoPuesto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar fallos al obtener la URL de descarga
                            e.printStackTrace();
                        }
                    });
                    break;
                case 2:
                    txtTercerPuesto.setText(nombreProducto);
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlProducto);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Carga la imagen en el ImageView utilizando Picasso
                            Glide.with(getApplicationContext())
                                    .load(uri.toString())
                                    .placeholder(R.drawable.waiting)
                                    .error(R.drawable.error)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(imgTercerPuesto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar fallos al obtener la URL de descarga
                            e.printStackTrace();
                        }
                    });
                    break;
                case 3:
                    txtCuartoPuesto.setText(nombreProducto);
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlProducto);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Carga la imagen en el ImageView utilizando Picasso
                            Glide.with(getApplicationContext())
                                    .load(uri.toString())
                                    .placeholder(R.drawable.waiting)
                                    .error(R.drawable.error)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(imgCuartoPuesto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar fallos al obtener la URL de descarga
                            e.printStackTrace();
                        }
                    });
                    break;
                case 4:
                    txtQuintoPuesto.setText(nombreProducto);
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlProducto);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Carga la imagen en el ImageView utilizando Picasso
                            Glide.with(getApplicationContext())
                                    .load(uri.toString())
                                    .placeholder(R.drawable.waiting)
                                    .error(R.drawable.error)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(imgQuintoPuesto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar fallos al obtener la URL de descarga
                            e.printStackTrace();
                        }
                    });
                    break;
            }
        }
    }
}