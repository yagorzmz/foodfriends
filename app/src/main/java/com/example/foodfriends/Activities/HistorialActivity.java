package com.example.foodfriends.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodfriends.Modelo.Pedido;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.AdaptadorHistorial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase HistorialActivity permite visualizar el histroial de los pedidos de cada
 * cada usuario, permitiendo ver el precio total de cada pedido, su id y su fecha de
 * realización
 */
public class HistorialActivity extends AppCompatActivity {

    //Elementos de la activity
    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView txtHistorialVacio;
    private ListView listViewHistorial;
    private List<Pedido> listaPedidos;
    private AdaptadorHistorial adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        //Inicializa la lista de pedidos
        listaPedidos = new ArrayList<>();

        txtHistorialVacio=findViewById(R.id.txtHistorialVacio);

        //Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);

        //Elimina el título del Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        listViewHistorial = findViewById(R.id.listviewHistorial);

        //Configurar el adaptador con la lista de pedidos
        adapter = new AdaptadorHistorial(this, listaPedidos);
        listViewHistorial.setAdapter(adapter);

        //Cargamos el historial de pedidos
        cargarPedidos();

    }

    //Método que recorre la tabla pedidos recuperando cada pedido del usuario actual
    private void cargarPedidos() {
        // Obtenemos el ID del usuario actual de Firebase
        String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference pedidosReference = firebaseDatabase.getReference("Pedidos");

        // Realiza la consulta para obtener los pedidos del usuario actual
        Query query = pedidosReference.orderByChild("ClienteId").equalTo(idUsuarioActual);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Limpia la lista actual de pedidos
                listaPedidos.clear();

                // Itera sobre los nodos hijos para obtener la información de cada pedido
                for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                    String id = pedidoSnapshot.getKey();
                    String idCliente = pedidoSnapshot.child("ClienteId").getValue(String.class);
                    Double precio = pedidoSnapshot.child("PrecioTotal").getValue(Double.class);
                    String fechaPedido = pedidoSnapshot.child("FechaPedido").getValue(String.class);
                    String fechaEntrega = pedidoSnapshot.child("FechaEntrega").getValue(String.class);

                    // Crea un objeto Pedido con la información obtenida y agregarlo a la lista
                    Pedido pedido = new Pedido(id, idCliente, precio, fechaPedido,fechaEntrega);
                    listaPedidos.add(pedido);
                }

                // Notifica al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();

                // Verifica si la lista de pedidos está vacía y muestra un Toast en consecuencia
                if (!listaPedidos.isEmpty()) {
                    txtHistorialVacio.setVisibility(View.GONE);
                }else{
                    mostrarToast("No hay pedidos realizados.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mostrarToast("No se han encontrado pedidos realizados");
            }
        });
    }

    //Método que muestra un toast personalizado
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
        // Manejamos las opciones del menú
        int id = item.getItemId();
        if (id == R.id.item_inicio) {
            // Iniciar la actividad del Carrito
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
        // Verifica si puedes volver atrás en la pila de actividades
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Si hay fragmentos en la pila, maneja el retroceso normal
            super.onBackPressed();
        } else {
            // Si no hay fragmentos en la pila, inicia la actividad Profile
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
