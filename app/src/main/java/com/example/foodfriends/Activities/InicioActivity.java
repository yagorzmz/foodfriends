package com.example.foodfriends.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.AdaptadorEmpresas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity implements AdaptadorEmpresas.OnItemClickListener{

    // Elementos
    private static final int REQUEST_FILTRO = 1;
    private androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recycler;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference empresasReference;
    private AdaptadorEmpresas adaptadorEmpresas;
    List<Empresa> listaEmpresas;
    private SearchView searchview;
    ImageView iconoToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        iconoToolbar=findViewById(R.id.iconoToolbar);
        // Eliminar el título del Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recycler=findViewById(R.id.recyclerView);

        searchview=findViewById(R.id.buscador);
        searchview.clearFocus();

        //Establecemos el listener del filtro
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });

        // Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        empresasReference = firebaseDatabase.getReference("Empresas");

        // Iniciar la carga de empresas desde Firebase
        cargarEmpresas(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Empresa> listaEmpresas) {
                // Cargamos el RecyclerView
                init(listaEmpresas);
            }
        });

    }

    //Inflamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        // Deshabilita el ítem del menú correspondiente a esta actividad
        MenuItem item = menu.findItem(R.id.item_inicio);
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
        if (id == R.id.item_carrito) {
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

    // Método que inicia el RecyclerView
    public void init(List<Empresa> listaEmpresas) {
        adaptadorEmpresas = new AdaptadorEmpresas(listaEmpresas, this,new AdaptadorEmpresas.OnItemClickListener(){

            //Metodo onClick de las empresas
            @Override
            public void onItemClick(Empresa empresa)
            {
                String idEmpresaSeleccionada = empresa.getId();  // Reemplaza con el ID real
                Intent intent = new Intent(getApplicationContext(), CatalogoActivity.class);
                intent.putExtra("idEmpresaSeleccionada", idEmpresaSeleccionada);
                startActivity(intent);
            }
        });
        //Enlazamos el recyclerview y seleccionamos su adaptador

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Establecer el adaptador correcto aquí
        recyclerView.setAdapter(adaptadorEmpresas);
    }

    // Método que añade en una lista las empresas de la base de datos
    private void cargarEmpresas(final OnDataLoadedListener listener) {
        empresasReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 listaEmpresas = new ArrayList<>();

                // Iterar sobre las empresas en la base de datos
                for (DataSnapshot empresaSnapshot : dataSnapshot.getChildren()) {
                    // Obtener los datos de la empresa desde la base de datos
                    String id=empresaSnapshot.getKey();
                    String urlLogo = empresaSnapshot.child("LogoEmpresa").getValue(String.class);
                    String direccion = empresaSnapshot.child("DireccionEmpresa").getValue(String.class);
                    String nombre = empresaSnapshot.child("NombreEmpresa").getValue(String.class);
                    Float estrellas = empresaSnapshot.child("Estrellas").getValue(Float.class);
                    Long telefono = empresaSnapshot.child("Telefono").getValue(Long.class);
                    String tipo = empresaSnapshot.child("TipoComida").getValue(String.class);

                    // Crear un objeto Empresa y añadirlo a la lista
                    Empresa empresa = new Empresa(id,urlLogo, nombre, direccion, estrellas, telefono, tipo);
                    listaEmpresas.add(empresa);

                }

                // Notificar que los datos han sido cargados
                if (listener != null) {
                    listener.onDataLoaded(listaEmpresas);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });
    }
    //Metodo que filtra las empresas mediante el searcher
    private void filterList(String texto) {
        // Creamos una nueva lista para almacenar los elementos filtrados
        List<Empresa> listaFiltrada = new ArrayList<>();

        // Iteramos a través de la lista original de empresas
        for (Empresa empresa : listaEmpresas) {
            // Comprobamos si el nombre de la empresa contiene el texto de búsqueda (ignorando mayúsculas/minúsculas)
            if (empresa.getNombreEmpresa().toLowerCase().contains(texto.toLowerCase())) {
                // Si se encuentra una coincidencia, agregar la empresa a la lista filtrada
                listaFiltrada.add(empresa);
            }
        }

        // Verificamos si la lista filtrada está vacía
        if (listaFiltrada.isEmpty()) {
            // Mostrar un mensaje Toast indicando que no se encontraron resultados
            Toast.makeText(this, "No se ha encontrado el restaurante", Toast.LENGTH_SHORT).show();
        } else {
            // Si hay resultados, actualizamos el adaptador con la nueva lista filtrada
            adaptadorEmpresas.setFilteredList(listaFiltrada);

            // Notificamos al adaptador que los datos han cambiado para que actualice la vista
            adaptadorEmpresas.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(Empresa item) {
        mostrarToast(item.getNombreEmpresa());
    }

    // Interfaz para manejar la carga de datos
    public interface OnDataLoadedListener {
        void onDataLoaded(List<Empresa> lista);
    }

    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}