package com.example.foodfriends.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.LineaPedido;
import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.AdaptadorLineasPedidos;
import com.example.foodfriends.Utilidades.ListaLineasPedidosTempHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CarritoActivity extends AppCompatActivity implements AdaptadorLineasPedidos.OnLineaPedidoChangeListener
{


    private androidx.appcompat.widget.Toolbar toolbar;
    ListView listViewLineasPedido;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference lineaspedidosReference;
    private Button btnRealizarPedido;
    AdaptadorLineasPedidos adapter;
    List<LineaPedido> listaLineas;
    List<LineaPedidoTemp> listaLineasPedidosTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Food Friends");
        btnRealizarPedido=findViewById(R.id.btnRealizarPedido);

        listViewLineasPedido=findViewById(R.id.listViewLineasPedido);
        // Obtener la lista de líneas de pedido
        listaLineasPedidosTemp = ListaLineasPedidosTempHelper.getListaLineasPedidosTemp();

        // Crear el adaptador
        adapter = new AdaptadorLineasPedidos(this, listaLineasPedidosTemp, this);

        // Asegúrate de que el ID coincida con tu ListView
        listViewLineasPedido.setAdapter(adapter);


    }
    private void actualizarTotal() {
        Double total = 0.0;

        // Calcular el total sumando los precios de cada línea
        for (LineaPedidoTemp lineaPedido : listaLineasPedidosTemp) {
            total += (lineaPedido.getPrecioProducto() * lineaPedido.getUnidades());
        }

        String totalFormateado = formatearPrecio(total);

        // Actualizar el TextView que muestra el total
        TextView txtTotal = findViewById(R.id.txtTotalPedido);
        txtTotal.setText("Precio total del pedido: " + String.valueOf(totalFormateado) + "€");
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

    // Método para formatear el precio
    private String formatearPrecio(double precio) {
        DecimalFormat formato = new DecimalFormat("#.##");
        return formato.format(precio);
    }
    @Override
    public void onLineaPedidoChanged() {
        actualizarTotal();
    }
    @Override
    public void onEliminarLineaPedido(int position) {
        // Implementar la lógica para eliminar la línea de pedido en la posición indicada
        listaLineasPedidosTemp.remove(position);

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();

        // Actualizar el total después de eliminar una línea de pedido
        actualizarTotal();
    }

}