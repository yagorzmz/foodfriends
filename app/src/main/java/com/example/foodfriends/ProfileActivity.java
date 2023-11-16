package com.example.foodfriends;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodfriends.Utilidades.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    //Elementos
    private androidx.appcompat.widget.Toolbar toolbar;
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    TextView txtId,txtNombre,txtCorreo,txtDireccionUsuario;
    Button btnCerrarSesion,btnBorrarCuenta;
    ImageView imgEditarDireccion;


    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Enlazamos los elementos
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        imgEditarDireccion=findViewById(R.id.imgEditarDireccion);
        txtId=findViewById(R.id.txtId);
        txtNombre=findViewById(R.id.txtNombreUsuario);
        txtCorreo=findViewById(R.id.txtCorreo);
        txtDireccionUsuario=findViewById(R.id.txtDireccionUsuario);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Food Friends");


        //Si el usuario pulsa cerrar sesion
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cerramos sesion
                cerrarSesion();
            }
        });
        //Si el usuario pulsa borrar cuenta
        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Eliminamos la cuenta
                eliminarCuenta();
            }
        });
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
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

    //Metodo que obliga al usuario a editar la direccion para poder usar la aplicacion
    private void solicitarDireccion(String titulo,String texto) {

        final EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Ingresa tu dirección")
                .setMessage(texto)
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String direccion = input.getText().toString();

                        if(direccion.isEmpty()) {
                            solicitarDireccion(titulo,texto); // Volver a solicitar
                        }
                        else if(!direccion.startsWith("C/")){
                            mostrarToast("La direccion debe tener este formato: C/...");
                            solicitarDireccion(titulo,texto);
                        }
                        else if(direccion.startsWith("C/")){
                            // Guardar dirección ingresada
                            txtDireccionUsuario.setText(direccion);

                            // Actualiza la dirección en la base de datos
                            usuariosRef.child(firebaseUser.getUid()).child("DireccionUsuario").setValue(direccion);
                            dialog.dismiss();
                        }
                    }
                })
                .show();

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
                // Manejo de errores si es necesario
            }
        });
    }

    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}