package com.example.foodfriends.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodfriends.Modelo.LineaPedido;
import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.AdaptadorLineasPedidos;
import com.example.foodfriends.Utilidades.AdaptadorLineasPedidosTemp;
import com.example.foodfriends.Utilidades.ListaLineasPedidosTempHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LineasPedidosActivity extends AppCompatActivity {

    private ListView listViewLineasPedido;
    private List<LineaPedidoTemp> listaLineas;
    private AdaptadorLineasPedidos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineas_pedidos);

        // Obtén el ID del pedido de los extras del Intent
        String idPedido = getIntent().getStringExtra("ID_PEDIDO");

        listViewLineasPedido = findViewById(R.id.listviewLineas);
        listaLineas = new ArrayList<>();

        cargarLineasPedidos(idPedido, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<LineaPedidoTemp> listaLineasPedido) {
                // Inicializa el adaptador con la lista de líneas de pedido
                adapter = new AdaptadorLineasPedidos(LineasPedidosActivity.this, listaLineasPedido);

                // Asigna el adaptador a tu ListView
                listViewLineasPedido.setAdapter(adapter);
            }
        });
    }

    private void cargarLineasPedidos(String idPedido, OnDataLoadedListener listener) {
        listaLineas.clear();

        // Obtén la referencia a la base de datos
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference lineasPedidoReference = firebaseDatabase.getReference("LineasPedidos");

        // Realiza la consulta para obtener las líneas de pedido para el pedido específico
        Query query = lineasPedidoReference.orderByChild("PedidoId").equalTo(idPedido);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Itera sobre los nodos hijos para obtener la información de cada línea de pedido
                for (DataSnapshot lineaPedidoSnapshot : dataSnapshot.getChildren()) {
                    String idProducto = lineaPedidoSnapshot.child("ProductoId").getValue(String.class);
                    int unidades = lineaPedidoSnapshot.child("Unidades").getValue(Integer.class);

                    // Realiza una segunda consulta para obtener información del producto
                    DatabaseReference productosReference = firebaseDatabase.getReference("Productos");
                    productosReference.child(idProducto).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot productoSnapshot) {
                            // Obtiene el nombre y precio del producto
                            String nombreProducto = productoSnapshot.child("NombreProducto").getValue(String.class);
                            double precioProducto = productoSnapshot.child("Precio").getValue(Double.class);

                            // Crea un objeto LineaPedidoTemp con la información obtenida y agrégalo a la lista
                            LineaPedidoTemp lineaPedido = new LineaPedidoTemp(nombreProducto, precioProducto, unidades);
                            listaLineas.add(lineaPedido);
                            // Notifica al adaptador después de procesar todas las líneas de pedido
                            if (listener != null) {
                                listener.onDataLoaded(listaLineas);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejar el error al obtener la información del producto
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error al obtener las líneas de pedido
            }
        });
    }
    public interface OnDataLoadedListener {
        void onDataLoaded(List<LineaPedidoTemp> listaLineasPedido);
    }

}
