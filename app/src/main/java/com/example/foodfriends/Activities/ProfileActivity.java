package com.example.foodfriends.Activities;

import static com.example.foodfriends.Activities.CarritoActivity.listaLineasPedidosTemp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * La clase ProfileActivity muestra la información del usuario,
 * cambiar su direccion de envio, su foto de perfil y su
 * historial de pedidos,ademas de borrar su cuenta y cerrar sesión
 */
public class ProfileActivity extends AppCompatActivity {

    //Elementos de la activity
    private androidx.appcompat.widget.Toolbar toolbar;
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    TextView txtId,txtNombre,txtCorreo,txtDireccionUsuario;
    Button btnCerrarSesion,btnBorrarCuenta,btnHistorial;
    ImageView imgEditarDireccion,imgPerfil;
    ImageView iconoToolbar;
    StorageReference storageReference;
    Uri image;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(imgPerfil);
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(getApplicationContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        // Desactivar el botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        //Enlazamos los elementos
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        btnHistorial = findViewById(R.id.btnHistorial);
        imgEditarDireccion=findViewById(R.id.imgEditarDireccion);
        txtId=findViewById(R.id.txtIdPedido);
        txtNombre=findViewById(R.id.txtNombreUsuario);
        txtCorreo=findViewById(R.id.txtCorreo);
        txtDireccionUsuario=findViewById(R.id.txtDireccionUsuario);

        imgPerfil=findViewById(R.id.imgPerfil);

        iconoToolbar=findViewById(R.id.iconoToolbar);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Metodo quie abre el hsitorial de pedidos del usuario
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),HistorialActivity.class);
                startActivity(i);
                finish();
            }
        });
        //Metodo que cierra sesión al usuario
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cerramos sesion
                cerrarSesion();
                // Limpiar o reiniciar la lista de lineasPedidosTemp
                listaLineasPedidosTemp.clear();
            }
        });
        //Metodo que borra la cuenta del usuario
        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Eliminamos la cuenta
                eliminarCuenta();
            }
        });
        //Metodo que permite cambiar la direccion del usuario
        imgEditarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarDireccion("Edita tu direccion","Introduce tu nueva direccion de envio");
            }
        });
        //Actualizamos el perfil del usuario
        completarPerfil(firebaseUser.getUid());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        // Deshabilita el ítem del menú correspondiente a esta actividad
        MenuItem item = menu.findItem(R.id.item_perfil);
        if (item != null) {
            item.setEnabled(false);
        }

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
        else if(id==R.id.item_acercade){
            Intent i= new Intent(getApplicationContext(), AcercaDeActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    //Metodo que cierra sesion y vuelve a la pantalla de Login
    private void cerrarSesion()
    {
        //Obtenemos el usuario y erramos sesion
        FirebaseAuth.getInstance().signOut();

        //Mensaje que comunica al usuario que cerramos sesion
        mostrarToast("Se ha cerrado sesión.");

        // Cuando el usuario cierra sesión
        SessionManager session = new SessionManager(getApplicationContext());
        session.logout();

        //Volvemos a la pantalla de login
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    //Método que elimina la cuenta del usuario.
    public void eliminarCuenta()
    {
        // Mostrar un cuadro de diálogo de confirmación antes de eliminar la cuenta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar cuenta");
        builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Obtenemos el id del usuario
                String idUsuario = firebaseUser.getUid();
                //Borra la cuenta y vuelve al Login
                borrarUsuario(idUsuario);
                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cerrar el cuadro de diálogo
                dialog.dismiss();
            }
        });

        builder.show();

    }

    //Método que borra al usuario mediante su id de la base de datos y de la autenticacion
    public void borrarUsuario(String idUsuario)
    {
        // Obtener la referencia específica al usuario que se desea borrar
        DatabaseReference usuarioAEliminarRef = usuariosRef.child(idUsuario);

        // Eliminar el usuario de la base de datos
        usuarioAEliminarRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // El usuario se eliminó con éxito de la base de datos
                    mostrarToast("Usuario eliminado de la base de datos");

                    // Ahora, eliminar el usuario de Firebase Authentication
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                // El usuario se eliminó con éxito de Firebase Authentication
                                mostrarToast("Usuario eliminado de la autenticación");
                                finish(); // Cierra la actividad después de eliminar el usuario
                            }
                            else
                            {
                                mostrarToast("Error al eliminar el usuario de la autenticación");
                            }
                        }
                    });
                }
                else
                {
                    mostrarToast( "Error al eliminar el usuario de la base de datos");
                }
            }
        });
    }

    //Método que obliga al usuario a editar la direccion para poder usar la aplicacion
    public void solicitarDireccion(String titulo, String texto) {
        final EditText input = new EditText(this);

        // Configuramos el filtro para limitar la longitud del texto
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(46);
        input.setFilters(filters);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Ingresa tu dirección")
                .setMessage(texto)
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String direccion = input.getText().toString();

                        if (direccion.isEmpty()) {
                            mostrarToast("Por favor, ingresa tu dirección.");
                            solicitarDireccion(titulo, texto); // Volver a solicitar
                        } else if (!direccion.startsWith("C/")) {
                            mostrarToast("La dirección debe tener este formato: C/...");
                            solicitarDireccion(titulo, texto);
                        } else if (direccion.startsWith("C/")) {
                            // Guardar dirección ingresada
                            txtDireccionUsuario.setText(direccion);

                            // Actualiza la dirección en la base de datos
                            usuariosRef.child(firebaseUser.getUid()).child("DireccionUsuario").setValue(direccion);
                        }
                    }
                })
                .show();

        // Desactivar el botón Atrás y el gesto de deslizar para salir de la actividad hasta que se ingrese una dirección
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        // Capturar el evento de cierre del cuadro de diálogo
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Verificar si se ingresó una dirección antes de cerrar el cuadro de diálogo
                if (txtDireccionUsuario.getText().toString().isEmpty()) {
                    solicitarDireccion(titulo, texto);
                } else {
                    // Habilitar el botón Atrás y el gesto de deslizar para salir de la actividad
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Metodo que completa el perfil del usuario, obteniendo los datos de la base de datos
    private void completarPerfil(String idUsuario){
        DatabaseReference usuarioRef = usuariosRef.child(idUsuario);
        // Agrega un ValueEventListener para escuchar los cambios en los datos del usuario
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recupera los valores de los campos del usuario
                    String nombre = dataSnapshot.child("NombreUsuario").getValue(String.class);
                    String correo = dataSnapshot.child("Correo").getValue(String.class);
                    String direccion = dataSnapshot.child("DireccionUsuario").getValue(String.class);

                    // Establece los valores en los TextViews
                    txtId.setText(idUsuario);
                    txtNombre.setText(nombre);
                    txtCorreo.setText(correo);

                    if (direccion == null || direccion.isEmpty()) {
                        // Si la dirección está en blanco o es nula, llama al método solicitarDireccion
                        solicitarDireccion("Aviso Direccion","Debes introducir una direccion para poder " +
                                "enviarte los pedidos. La dirección debe empezar por C/");
                    } else {
                        txtDireccionUsuario.setText(direccion);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mostrarToast("No se ha podido completar el perfil");
            }
        });
    }

    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}