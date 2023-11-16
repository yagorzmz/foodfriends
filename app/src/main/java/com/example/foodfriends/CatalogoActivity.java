package com.example.foodfriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.Utilidades.AdaptadorProductos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CatalogoActivity extends AppCompatActivity {
    private List<Producto> listaProductos;
    private ListView listview;
    private DatabaseReference productosRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        FirebaseApp.initializeApp(this);
        // Inicializamos la lista de productos
        listaProductos = new ArrayList<>();
        // Recuperamos datos del Intent
        Intent intent = getIntent();

        if (intent != null) {
            Empresa empresa = (Empresa) intent.getSerializableExtra("empresa");

            // Inicializamos la referencia a la base de datos de productos
            productosRef = FirebaseDatabase.getInstance().getReference("Productos");

            // Cargamos los productos asociados a la empresa
            cargarProductos(empresa);
        }

        // Creamos el adaptador
        AdaptadorProductos adaptadorProductos = new AdaptadorProductos(this, listaProductos);

        listview = findViewById(R.id.listview);

        // Establecemos el adaptador en el ListView
        listview.setAdapter(adaptadorProductos);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menuprincipal,menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.item_inicio){
            Intent i= new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
            finish();
        }
        else if(id==R.id.item_carrito){
            Intent i= new Intent(getApplicationContext(),CarritoActivity.class);
            startActivity(i);
            finish();
        }
        else if(id==R.id.item_masvendidos){
            Intent i= new Intent(getApplicationContext(), MasVendidosActivity.class);
            startActivity(i);
            finish();
        }
        else if(id==R.id.item_perfil){
            Intent i= new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            finish();
        }
        else if(id==R.id.item_acercade){
            Intent i= new Intent(getApplicationContext(), AcercaDeActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void cargarProductos(Empresa empresa) {
        if (empresa != null) {
            // Obténemos el ID de la empresa
            String empresaId = empresa.getId();

            // Creamos la consulta para obtener los productos asociados a la empresa
            productosRef.orderByChild("EmpresaId").equalTo(empresaId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaProductos.clear();

                            for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                                Producto producto = productoSnapshot.getValue(Producto.class);
                                listaProductos.add(producto);
                            }

                            // Notificamos al adaptador que los datos han cambiado
                            ((AdaptadorProductos) listview.getAdapter()).notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejar errores
                        }
                    });
        }
    }
}

