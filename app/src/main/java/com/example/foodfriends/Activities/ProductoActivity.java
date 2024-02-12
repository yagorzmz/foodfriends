package com.example.foodfriends.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.Modelo.Empresa;
import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.Modelo.Producto;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.ListaLineasPedidosTempHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * La clase ProductoActivity permite al usuario añadir el producto
 * seleccionado previamente al carrito, escogiendo la cantidad del
 * producto (max 5 unidades)
 */
public class ProductoActivity extends AppCompatActivity
{
    //Elementos de la activity
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference empresasReference;
    private static final int CANTIDAD_MINIMA = 1;
    private static final int CANTIDAD_MAXIMA =99;
    private int cantidadActual=1;
    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView txtNombreProducto;
    private TextView txtPrecio;
    private TextView txtDescripcion;
    private TextView txtCantidadProducto;
    private TextView txtNombreRestaurante;
    private ImageView imgProducto;
    Button btnAgregarAlCarrito;
    ImageButton btnSumarCantidad,btnRestarCantidad;
    ImageView iconoToolbar;
    Producto producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        //Configuración de Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        empresasReference = firebaseDatabase.getReference("Empresas");

        // Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        iconoToolbar=findViewById(R.id.iconoToolbar);
        // Inicializa las referencias a las vistas
        txtNombreProducto = findViewById(R.id.txtNombreProducto2);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtDescripcion = findViewById(R.id.txtDescripcionProducto2);
        // Crear un filtro para limitar la cantidad de caracteres
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(200);
        // Aplicar el filtro al TextView
        txtDescripcion.setFilters(filters);
        txtNombreRestaurante = findViewById(R.id.txtNombreRestaurante);
        imgProducto = findViewById(R.id.imgProducto2);
        btnAgregarAlCarrito = findViewById(R.id.btnAgregarCarrito);
        btnSumarCantidad= findViewById(R.id.btnSumarCantidad);
        btnRestarCantidad=findViewById(R.id.btnBajarCantidad);
        txtCantidadProducto=findViewById(R.id.txtCantidadProducto);

        //Metodo que incrementa las unidades escogidas del producto
        btnSumarCantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementarCantidad();
            }
        });

        //Metodo que decrementa las unidades escogidas del producto
        btnRestarCantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementarCantidad();
            }
        });
        //Metodo que añade el prodcuto al carrito
        btnAgregarAlCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la cantidad seleccionada
                int cantidadSeleccionada = Integer.valueOf(txtCantidadProducto.getText().toString());

                // Crear un objeto LineaPedidoTemp con la información del producto actual
                LineaPedidoTemp lineaPedido = new LineaPedidoTemp(
                        producto.getIdProducto(),
                        producto.getNombreProducto(),
                        producto.getPrecio(),
                        cantidadSeleccionada
                );

                // Agregar la línea de pedido a la lista
                ListaLineasPedidosTempHelper.agregarLineaPedido(lineaPedido);

                // Puedes mostrar un mensaje o realizar otras acciones aquí
                mostrarToast("Producto agregado al carrito. Cantidad: " + cantidadSeleccionada);
            }
        });

        //Uno de los dos metodos tendra un intent nulo
        recuperarIntentProducto();

        //Actualizamos la cantidad
        actualizarCantidad();
    }

    // Recuperamos la información de la empresa enviada desde la actividad anterior
    private void recuperarIntentProducto(){
        Intent intent = getIntent();
        if (intent != null)
        {
            producto=new Producto(intent.getStringExtra("idProducto"),intent.getStringExtra("nombreProducto"),
                    intent.getStringExtra("descripcion"),intent.getStringExtra("empresaId"),
                    intent.getStringExtra("urlProducto"),intent.getDoubleExtra("precio",0));
            //Si la empresa no es nula, podemos cargar los productos
            if (producto != null) {
                obtenerInformacionProducto(producto);
            }
        }
    }
    //Obtiene la info del producto seleccionado
    private void obtenerInformacionProducto(Producto producto) {
        txtNombreProducto.setText(producto.getNombreProducto());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText("Precio: " + producto.getPrecio().toString() + "€/Unidad");

        // Obtén una referencia al archivo en Firebase Storage
        String imageUrl = producto.getUrlProducto();
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        // Convierte la referencia a URL y plasma la imagen en el imageview
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Carga la imagen en el ImageView utilizando Picasso
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .placeholder(R.drawable.waiting)
                        .error(R.drawable.error)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imgProducto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar fallos al obtener la URL de descarga
                e.printStackTrace();
            }
        });
        try {
            String idEmpresa = producto.getEmpresaId();
            empresasReference.child(idEmpresa).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Obtenemos los datos de la empresa desde la base de datos
                    String nombreEmpresa = dataSnapshot.child("NombreEmpresa").getValue(String.class);
                    String localidad = dataSnapshot.child("Localidad").getValue(String.class);
                    txtNombreRestaurante.setText(nombreEmpresa+", "+localidad);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mostrarToast("No se han podido cargar las empresas. Por favor, intente entrar más tarde.");
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
    //Metodo que actualiza la cantidad elegida del producto
    private void actualizarCantidad() {
        txtCantidadProducto.setText(String.valueOf(cantidadActual));
        btnRestarCantidad.setEnabled(cantidadActual > CANTIDAD_MINIMA);
        btnSumarCantidad.setEnabled(cantidadActual < CANTIDAD_MAXIMA);
    }
    //Metodo que incrementa la cantidad del producto
    private void incrementarCantidad() {
        if (cantidadActual < CANTIDAD_MAXIMA) {
            cantidadActual++;
        }
        actualizarCantidad();
    }
    //Metodo que decrementa la cantidad del producto
    private void decrementarCantidad()
    {
        if (cantidadActual > CANTIDAD_MINIMA) {
            cantidadActual--;
            actualizarCantidad();
        }
    }

}