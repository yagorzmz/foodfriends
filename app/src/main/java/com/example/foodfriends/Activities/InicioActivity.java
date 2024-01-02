package com.example.foodfriends.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
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

/**
 * La clase InicioActivity muestra la pantalla principal de la aplicacion,
 * donde se muestran las diferentes em,presas de comida. En ella se puede
 * por nombre, acceder mediante el menu a las demas activities y ver los
 * productos de cada empresa
 */
public class InicioActivity extends AppCompatActivity implements AdaptadorEmpresas.OnItemClickListener {

    //Elementos de la activity
    private static final int REQUEST_CODE = 1;
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

        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_CODE
            );
        }
        //Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        iconoToolbar = findViewById(R.id.iconoToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recycler = findViewById(R.id.recyclerView);
        searchview = findViewById(R.id.buscador);
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

        //Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        empresasReference = firebaseDatabase.getReference("Empresas");

        //Iniciamos la carga de empresas desde Firebase
        cargarEmpresas(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Empresa> listaEmpresas) {
                //Cargamos el RecyclerView
                init(listaEmpresas);
            }
        });

    }

    //Inflamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        //Deshabilita el ítem del menú correspondiente a esta actividad
        MenuItem item = menu.findItem(R.id.item_inicio);
        if (item != null) {
            item.setEnabled(false);
        }
        return true;
    }

    //Recogemos los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Manejamos las opciones del menú
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

    //Método que inicia el RecyclerView
    public void init(List<Empresa> listaEmpresas) {
        adaptadorEmpresas = new AdaptadorEmpresas(listaEmpresas, this, new AdaptadorEmpresas.OnItemClickListener() {

            //Método onClick de las empresas
            @Override
            public void onItemClick(Empresa empresa) {
                String idEmpresaSeleccionada = empresa.getId();
                Intent intent = new Intent(getApplicationContext(), CatalogoActivity.class);
                intent.putExtra("idEmpresaSeleccionada", idEmpresaSeleccionada);
                startActivity(intent);
            }
        });
        //Enlazamos el recyclerview y seleccionamos su adaptador
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Establecemos el adaptador
        recyclerView.setAdapter(adaptadorEmpresas);
    }

    //Método que añade en una lista las empresas de la base de datos
    private void cargarEmpresas(final OnDataLoadedListener listener) {
        try {
            empresasReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaEmpresas = new ArrayList<>();

                    //Iteramos sobre las empresas en la base de datos
                    for (DataSnapshot empresaSnapshot : dataSnapshot.getChildren()) {
                        //Obtenemos los datos de la empresa desde la base de datos
                        String id = empresaSnapshot.getKey();
                        String urlLogo = empresaSnapshot.child("LogoEmpresa").getValue(String.class);
                        String direccion = empresaSnapshot.child("DireccionEmpresa").getValue(String.class);
                        String nombre = empresaSnapshot.child("NombreEmpresa").getValue(String.class);
                        Float estrellas = empresaSnapshot.child("Estrellas").getValue(Float.class);
                        Long telefono = empresaSnapshot.child("Telefono").getValue(Long.class);
                        String tipo = empresaSnapshot.child("TipoComida").getValue(String.class);

                        // Crear un objeto Empresa y añadirlo a la lista
                        Empresa empresa = new Empresa(id, urlLogo, nombre, direccion, estrellas, telefono, tipo);
                        listaEmpresas.add(empresa);

                    }

                    // Notificar que los datos han sido cargados
                    if (listener != null) {
                        listener.onDataLoaded(listaEmpresas);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mostrarToast("No se han podido cargar las empresas. Porfavor intente entrar mas tarde.");
                    finish();
                }
            });
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir al cargar las empresas
            e.printStackTrace();
        }
    }

    //Método que filtra las empresas mediante el searcher
    private void filterList(String texto) {
        //Creamos una nueva lista para almacenar los elementos filtrados
        List<Empresa> listaFiltrada = new ArrayList<>();

        //Iteramos a través de la lista original de empresas
        for (Empresa empresa : listaEmpresas) {
            //Comprobamos si el nombre de la empresa contiene el texto de búsqueda (ignorando mayúsculas/minúsculas)
            if (empresa.getNombreEmpresa().toLowerCase().contains(texto.toLowerCase())) {
                //Si se encuentra una coincidencia, agregar la empresa a la lista filtrada
                listaFiltrada.add(empresa);
            }
        }

        //Verificamos si la lista filtrada está vacía
        if (listaFiltrada.isEmpty()) {
            //Mostramos un mensaje Toast indicando que no se encontraron resultados
            Toast.makeText(this, "No se ha encontrado el restaurante", Toast.LENGTH_SHORT).show();
        } else {
            //Si hay resultados, actualizamos el adaptador con la nueva lista filtrada
            adaptadorEmpresas.setFilteredList(listaFiltrada);

            //Notificamos al adaptador que los datos han cambiado para que actualice la vista
            adaptadorEmpresas.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(Empresa item) {
        mostrarToast(item.getNombreEmpresa());
    }

    //Interfaz para manejar la carga de datos
    public interface OnDataLoadedListener {
        void onDataLoaded(List<Empresa> lista);
    }

    //Método que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void requestNotificationPermission() {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            // User has already granted permission
            // Send push notifications
        } else {
            // Request permission
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permisos de notificación");
            builder.setMessage("Para recibir notificaciones de confirmación de pedido, necesitamos que nos otorgues permisos de notificación.");
            builder.setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(
                            InicioActivity.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_CODE
                    );
                }
            });
            builder.setNegativeButton("No permitir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(InicioActivity.this, "No podrás recibir notificaciones de confirmación de pedido.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "No podrás recibir notificaciones de confirmación de pedido.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}