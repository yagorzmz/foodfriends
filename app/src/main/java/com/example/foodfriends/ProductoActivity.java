package com.example.foodfriends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.Utilidades.AdaptadorEmpresas;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductoActivity extends AppCompatActivity
{

    private androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference productosReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        // Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Food Friends");

        // Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        productosReference = firebaseDatabase.getReference("Productos");

        // Recuperamos la información de la empresa enviada desde la actividad anterior
        Intent intent = getIntent();
        if (intent != null)
        {
            String idProductoSeleccionado = intent.getStringExtra("idProductoSeleccionado");
            //Si la empresa no es nula, podemos cargar los productos
            if (idProductoSeleccionado != null) {

            }
        }
    }
    //Inflamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú principal en la barra de acción
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
    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}