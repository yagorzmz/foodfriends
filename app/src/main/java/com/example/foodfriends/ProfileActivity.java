package com.example.foodfriends;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodfriends.Modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    //Elementos
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private TextView txtNombreUsuario, txtCorreoDelUsuario,txtDireccion,txtId;
    Button btnCerrarSesion,btnBorrarCuenta,btnVolverInicio;
    ImageView imgEditarDireccion,imagenPerfil;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Enlazamos los elementos
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        btnVolverInicio=findViewById(R.id.btnVolverInicio);
        imgEditarDireccion=findViewById(R.id.imgEditarDireccion);
        txtNombreUsuario=findViewById(R.id.txtNombreUsuario);
        txtCorreoDelUsuario=findViewById(R.id.txtCorreo);
        txtDireccion=findViewById(R.id.txtDireccion);
        txtId=findViewById(R.id.txtId);
        imagenPerfil=findViewById(R.id.imgPerfil);

        //Boton que devuelve al usuario al inicio
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),MenuActivity.class);
                startActivity(i);

            }
        });
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
        completarPerfilUsuario();

        //Verficamos si la direccion está vacia
        if(verificarDireccion()){
            solicitarDireccion("Introduce tu direccion de envío","Debes ingresar una dirección para poder usando la app, asi sabremos donde enviarte los pedidos");
        }

    }

    //Metodo que cierra sesion y vuelve a la pantalla de Login
    private void cerrarSesion()
    {
        //Obtenemos el usuario y erramos sesion
        FirebaseAuth.getInstance().signOut();
        //Mensaje que comunica al usuario que cerramos sesion
        mostrarToast("Se ha cerrado sesión.");
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
    //Metodo que recoge la informacion de la base de datos y actualiza el perfil del usuario
    private void completarPerfilUsuario() {

        // Obtiene la instancia actual del usuario
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Obtiene el ID único del usuario actual
            String userId = user.getUid();

            // Obtiene una referencia a la base de datos en la ubicación de Europa
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Usuarios")
                    .child(userId);

            // Escucha cambios en los datos del usuario
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombre = dataSnapshot.child("NombreUsuario").getValue(String.class);
                        String correo = dataSnapshot.child("Correo").getValue(String.class);
                        String direccion = dataSnapshot.child("DireccionUsuario").getValue(String.class);
                        // Actualiza los campos en la actividad
                        txtId.setText(user.getUid());
                        txtNombreUsuario.setText(nombre);
                        txtCorreoDelUsuario.setText(correo);
                        txtDireccion.setText(direccion);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Maneja errores si es necesario
                }
            });
        }
    }

    //Metodo que verifica que si la direccion de envio esta vacia el usuario debe escribirla
    private boolean verificarDireccion()
    {
        boolean hayDireccion=true;
        if(txtDireccion.getText().equals("")){
            return false;
        }
        return hayDireccion;
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
                            return;
                        }
                        else if(!direccion.startsWith("C/")){
                            mostrarToast("La direccion debe tener este formato: C/...");
                            solicitarDireccion(titulo,texto);
                        }

                        // Guardar dirección ingresada
                        txtDireccion.setText(direccion);

                        // Actualiza la dirección en la base de datos
                        usuariosRef.child(firebaseUser.getUid()).child("DireccionUsuario").setValue(direccion);
                        dialog.dismiss();
                    }
                })
                .show();

    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}