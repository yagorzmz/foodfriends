package com.example.foodfriends;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodfriends.Modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



public class RegisterActivity extends AppCompatActivity
{

    //Elementos de de la Activity de registro
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Button btnRegister;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Boton registrarse
        btnRegister = findViewById(R.id.btnRegister2);
        editEmail = findViewById(R.id.editEmail2);
        editPassword = findViewById(R.id.editPassword2);
        editNombre = findViewById(R.id.editNombre);

        //Cuando damos al boton de registrarse, validamos los datos y registramos al usuario
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Iniciamos el registro del usuario
                validarCamposYRegistrar();
            }
        });
    }

    //Metodo que registra un nuevo usuario en la app
    public void registrarUsuario()
    {
        firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //Validamos la direccion email
                        if (!validarEmail("miEmail@gmail.com"))
                        {
                            mostrarToast("Email no válido.");
                        }
                        else
                        {
                            //Si el registro es satisfactorio...
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                // Autenticación del usuario con FirebaseAuth
                                FirebaseAuth auth = FirebaseAuth.getInstance();

                                // Obtenemos los datos ingresados por el usuario
                                String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String nombre = editNombre.getText().toString();
                                String correo = editEmail.getText().toString();

                                //Creamos el usuario que vamos a registrar, con la direccion en blanco ya que la escribira posteriormente
                                //Establecemos por defecto una imagen de usuario, que podra cambiar
                                Usuario nuevoUsuario=new Usuario(idUsuario,nombre,correo,"");
                                //Registramos el usuario en la base de datos
                                registrarUsuarioBaseDatos(nuevoUsuario);

                            }
                            //Si ya se ha registrado con el mismo correo...
                            else
                            {
                                mostrarToast("El usuario ya ha sido registrado.");
                            }
                        }

                    }
                });
    }
    //Método que valida si el usuario al registrarse introduce una direccion email válida
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    //Método que evalua si ha escrito en todos los edittext
    private void validarCamposYRegistrar() {
        String nombre = editNombre.getText().toString().trim();
        String correo = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            // Alguno de los campos está vacío, muestra un Toast de advertencia.
            mostrarToast("Por favor, completa todos los campos antes de registrarte.");
        }
        else
        {
            registrarUsuario();
        }
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    //Metodo que registra en la base de datos el usuario
    public void registrarUsuarioBaseDatos(Usuario usuario)
    {
        // Obtener el ID del usuario
        String idUsuario = usuario.getIdUsuario();

        // Crear un mapa para contener los datos del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("NombreUsuario", usuario.getNombreUsuario());
        userData.put("Correo", usuario.getCorreo());
        userData.put("DireccionUsuario", usuario.getDireccionUsuario());

        // Registrar usuario en DB
        usuariosRef.child(idUsuario).setValue(userData);
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}