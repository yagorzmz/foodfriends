package com.example.foodfriends.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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
/**
 * La clase CatalogoActivity muestra todo el catalogo de la empresa
 * pudiendo así escoger el usuaripo que producto quiere añadir al carrito
 */
public class CatalogoActivity extends AppCompatActivity implements AdaptadorProductos.OnItemClickListener{

    //Elementos de la activity
    private androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recycler;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference productosReference;
    private AdaptadorProductos adaptadorProductos;
    List<Producto> listaProductos;
    ImageView iconoToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        //Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        iconoToolbar=findViewById(R.id.iconoToolbar);
        recycler=findViewById(R.id.recyclerView);

        //Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        productosReference = firebaseDatabase.getReference("Productos");

        //Recuperamos la información de la empresa enviada desde la actividad anterior
        Intent intent = getIntent();
        if (intent != null)
        {
            String idEmpresaSeleccionada = intent.getStringExtra("idEmpresaSeleccionada");
            //Si la empresa no es nula, podemos cargar los productos
            if (idEmpresaSeleccionada != null) {
                // Iniciar la carga de empresas desde Firebase
                cargarProductos(idEmpresaSeleccionada,new CatalogoActivity.OnDataLoadedListener() {
                    @Override
                    public void onDataLoaded(List<Producto> listaProductos) {
                        // Cargamos el RecyclerView
                        init(listaProductos);
                    }
                });
            }
        }
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
    private void cargarProductos(String idEmpresa, final CatalogoActivity.OnDataLoadedListener listener) {
        try {
            productosReference.orderByChild("EmpresaId").equalTo(idEmpresa).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onItemClick(Producto item) {
        mostrarToast(item.getIdProducto());
    }

    //Interfaz para manejar la carga de datos
    public interface OnDataLoadedListener {
        void onDataLoaded(List<Producto> lista);
    }

    //Método que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflamos el menú principal en la barra de acción
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuprincipal, menu);
        return true;
    }

    //Recogemos los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar las opciones del menú
        int id = item.getItemId();
        if (id == R.id.item_inicio) {
            // Iniciar la actividad de Inicio
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_carrito) {
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
    @Override
    public void onBackPressed() {
        //Abre la actividad del catálogo al presionar el botón de atrás
        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
        finish();
    }
}

