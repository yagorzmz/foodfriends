package com.example.foodfriends.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodfriends.Modelo.LineaPedido;
import com.example.foodfriends.Modelo.Pedido;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

public class MasVendidosActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    private ImageView iconoToolbar;
    private FirebaseDatabase firebaseDatabase;

    DatabaseReference pedidosReference;
    private List<Producto> listaTopProductos;
    private TextView txtPrimerPuesto, txtSegundoPuesto, txtTercerPuesto, txtCuartoPuesto, txtQuintoPuesto;
    private ImageView imgPrimerPuesto, imgSegundoPuesto, imgTercerPuesto, imgCuartoPuesto, imgQuintoPuesto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mas_vendidos);

        // Inicializar las vistas
        txtPrimerPuesto = findViewById(R.id.txtPrimerPuesto);
        txtSegundoPuesto = findViewById(R.id.txtSegundoPuesto);
        txtTercerPuesto = findViewById(R.id.txtTercerPuesto);
        txtCuartoPuesto = findViewById(R.id.txtCuartoPuesto);
        txtQuintoPuesto = findViewById(R.id.txtQuintoPuesto);

        imgPrimerPuesto = findViewById(R.id.imgPrimerPuesto);
        imgSegundoPuesto = findViewById(R.id.imgSegundoPuesto);
        imgTercerPuesto = findViewById(R.id.imgTercerPuesto);
        imgCuartoPuesto = findViewById(R.id.imgCuartoPuesto);
        imgQuintoPuesto = findViewById(R.id.imgQuintoPuesto);

        // Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        pedidosReference = firebaseDatabase.getReference("Pedidos");

        toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        iconoToolbar=findViewById(R.id.iconoToolbar);
        // Eliminar el título del Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Inicializar la referencia a la base de datos
        pedidosReference = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Pedidos");

        // Obtener los pedidos de los últimos 7 días
        obtenerPedidosUltimos7Dias(new OnPedidosLoadedListener() {
            @Override
            public void onPedidosLoaded(List<String> idPedidos) {
                // Llamada al método para obtener las líneas de pedido
                obtenerLineasPedidos(idPedidos, new OnLineasPedidosLoadedListener() {
                    @Override
                    public void onLineasPedidosLoaded(List<LineaPedido> lineasPedidos) {
                        // Aquí puedes utilizar la lista de lineasPedidos según tus necesidades
                        for (LineaPedido lineaPedido : lineasPedidos) {
                            // Haz algo con cada línea de pedido
                            // Por ejemplo, puedes acceder a sus propiedades como lineaPedido.getProductoId() o lineaPedido.getUnidades()
                        }
                    }
                });
            }
        });

    }
    private void mostrarTopProductos() {
        if (listaTopProductos.size() >= 1) {
            txtPrimerPuesto.setText(listaTopProductos.get(0).getNombreProducto());
            Glide.with(this).load(listaTopProductos.get(0).getUrlProducto()).into(imgPrimerPuesto);
        }

        if (listaTopProductos.size() >= 2) {
            txtSegundoPuesto.setText(listaTopProductos.get(1).getNombreProducto());
            Glide.with(this).load(listaTopProductos.get(1).getUrlProducto()).into(imgSegundoPuesto);
        }

        if (listaTopProductos.size() >= 3) {
            txtTercerPuesto.setText(listaTopProductos.get(2).getNombreProducto());
            Glide.with(this).load(listaTopProductos.get(2).getUrlProducto()).into(imgTercerPuesto);
        }

        if (listaTopProductos.size() >= 4) {
            txtCuartoPuesto.setText(listaTopProductos.get(3).getNombreProducto());
            Glide.with(this).load(listaTopProductos.get(3).getUrlProducto()).into(imgCuartoPuesto);
        }

        if (listaTopProductos.size() >= 5) {
            txtQuintoPuesto.setText(listaTopProductos.get(4).getNombreProducto());
            Glide.with(this).load(listaTopProductos.get(4).getUrlProducto()).into(imgQuintoPuesto);
        }
    }

    //Metodo que recoge y devuelve la lista de pedidos realizados en los ultimos 7 dias
    private void obtenerPedidosUltimos7Dias(final OnPedidosLoadedListener listener) {
        final List<String> idPedidos = new ArrayList<>();

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaActual = sdf.format(calendar.getTime());

        // Calcular la fecha de hace 7 días
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String fechaHace7Dias = sdf.format(calendar.getTime());

        // Realizar la consulta con un rango de fechas
        Query query = pedidosReference.orderByChild("FechaPedido").startAt(fechaHace7Dias).endAt(fechaActual);

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
    private void obtenerLineasPedidos(List<String> idPedidos, final OnLineasPedidosLoadedListener listener) {
        final List<LineaPedido> lineasPedidosUltimos7Dias = new ArrayList<>();

        // Obtener la referencia a la tabla LineasPedidos
        DatabaseReference lineasPedidosReference = FirebaseDatabase.getInstance().getReference("LineasPedidos");

        // Iterar sobre los idPedidos
        for (final String idPedido : idPedidos) {
            // Realizar la consulta para obtener las líneas de pedido para un idPedido específico
            Query query = lineasPedidosReference.orderByChild("PedidoId").equalTo(idPedido);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Iterar sobre las líneas de pedido encontradas
                    for (DataSnapshot lineaPedidoSnapshot : dataSnapshot.getChildren()) {
                        // Convertir el objeto de Firebase a un objeto LineaPedido
                        LineaPedido lineaPedido = lineaPedidoSnapshot.getValue(LineaPedido.class);
                        if (lineaPedido != null) {
                            lineasPedidosUltimos7Dias.add(lineaPedido);
                        }
                    }

                    // Notificar que las líneas de pedido han sido cargadas
                    if (listener != null) {
                        listener.onLineasPedidosLoaded(lineasPedidosUltimos7Dias);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores si es necesario
                }
            });
        }
    }

    // Interfaz para notificar cuando las líneas de pedido han sido cargadas
    public interface OnLineasPedidosLoadedListener {
        void onLineasPedidosLoaded(List<LineaPedido> lineasPedidos);
    }
}
