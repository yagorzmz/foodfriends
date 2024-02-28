package com.example.foodfriends.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.AdaptadorProductos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TodosProductosActivity extends AppCompatActivity {

    private static final long TIEMPO_ESPERA = 500; // Tiempo de espera en milisegundos

    private Handler handler = new Handler();
    private Runnable runnable;
    private androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recycler;
    private SearchView searchview;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference productosReference;
    private AdaptadorProductos adaptadorProductos;
    List<Producto> listaProductos;
    ImageView iconoToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_productos);

        //Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar10);
        setSupportActionBar(toolbar);
        iconoToolbar = findViewById(R.id.iconoToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        productosReference = firebaseDatabase.getReference("Productos");

        recycler = findViewById(R.id.recyclerView);
        searchview = findViewById(R.id.buscadorProductos);
        searchview.clearFocus();

        //Establecemos el listener del filtro
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Aplicamos el filtro de busqueda
                filterList(s);
                return true;
            }
        });

        //Cargamos los productos filtrados
        cargarProductos(new CatalogoActivity.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Producto> lista) {
                // Cargamos el RecyclerView
                init(listaProductos);
            }
        });
    }

    //Método que filtra las empresas mediante el searcher
    private void filterList(String texto) {
        // Cancelamos el runnable si está en curso
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        // Definimos el nuevo runnable para mostrar el mensaje si no se encuentra el producto
        runnable = new Runnable() {
            @Override
            public void run() {
                // Creamos una nueva lista para almacenar los elementos filtrados
                List<Producto> listaFiltrada = new ArrayList<>();

                // Iteramos a través de la lista original de productos
                for (Producto producto : listaProductos) {
                    // Comprobamos si el nombre del producto contiene el texto de búsqueda (ignorando mayúsculas/minúsculas)
                    if (producto.getNombreProducto().toLowerCase().contains(texto.toLowerCase())) {
                        // Si se encuentra una coincidencia, agregar el producto a la lista filtrada
                        listaFiltrada.add(producto);
                    }
                }

                // Verificamos si la lista filtrada está vacía
                if (listaFiltrada.isEmpty()) {
                    // Mostramos un mensaje Toast indicando que no se encontraron resultados
                    Toast.makeText(getApplicationContext(), "No se ha encontrado el producto", Toast.LENGTH_SHORT).show();
                } else {
                    // Si hay resultados, actualizamos el adaptador con la nueva lista filtrada
                    adaptadorProductos.setFilteredList(listaFiltrada);

                    // Notificamos al adaptador que los datos han cambiado para que actualice la vista
                    adaptadorProductos.notifyDataSetChanged();
                }
            }
        };

        // Programamos la ejecución del runnable después de un tiempo de espera
        handler.postDelayed(runnable, TIEMPO_ESPERA);
    }
    //Método que inicia el RecyclerView
    public void init(List<Producto> listaProductos) {
        adaptadorProductos = new AdaptadorProductos(listaProductos, this,new AdaptadorProductos.OnItemClickListener(){

            //Metodo onClick de las empresas
            @Override
            public void onItemClick(Producto producto)
            {
                Intent intent = new Intent(getApplicationContext(), ProductoActivity.class);
                intent.putExtra("idProducto", producto.getIdProducto());
                intent.putExtra("nombreProducto", producto.getNombreProducto());
                intent.putExtra("descripcion", producto.getDescripcion());
                intent.putExtra("empresaId", producto.getEmpresaId());
                intent.putExtra("urlProducto", producto.getUrlProducto());
                intent.putExtra("precio", producto.getPrecio());
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Establecemos el adaptador correcto aquí
        recyclerView.setAdapter(adaptadorProductos);
    }
    //Método que añade en una lista las empresas de la base de datos
    private void cargarProductos(final CatalogoActivity.OnDataLoadedListener listener) {
        try {
            productosReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaProductos = new ArrayList<>();

                    //Iteramos sobre los productos en la base de datos
                    for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                        // Obtener los datos del producto desde la base de datos
                        String id = productoSnapshot.getKey();
                        String nombreProducto = (String) productoSnapshot.child("NombreProducto").getValue();
                        String descripcionProducto = (String) productoSnapshot.child("Descripcion").getValue();
                        String empresaId = (String) productoSnapshot.child("EmpresaId").getValue();
                        Double precio = (Double) productoSnapshot.child("Precio").getValue();
                        String urlProducto = (String) productoSnapshot.child("urlProducto").getValue();

                        // Crear un objeto Producto y añadirlo a la lista
                        Producto producto = new Producto(id, nombreProducto, descripcionProducto, empresaId, urlProducto, precio);
                        listaProductos.add(producto);
                    }

                    //Notificamos que los datos han sido cargados
                    if (listener != null) {
                        listener.onDataLoaded(listaProductos);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores si es necesario
                    mostrarToast(databaseError.getMessage());
                }
            });
        } catch (Exception e) {
            //Manejamos cualquier excepción que pueda ocurrir al cargar los productos
            e.printStackTrace();
        }
    }


    //Método que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    //Inflamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return true;
    }

    //Recogemos los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Manejamos las opciones del menú
        int id = item.getItemId();
        if (id == R.id.item_inicio) {
            // Iniciar la actividad de Inicio
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.item_carrito) {
            // Iniciar la actividad del Carrito
            Intent i = new Intent(getApplicationContext(), CarritoActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_masvendidos) {
            // Iniciar la actividad de Más Vendidos
            Intent i = new Intent(getApplicationContext(), MasVendidosActivity.class);
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

}