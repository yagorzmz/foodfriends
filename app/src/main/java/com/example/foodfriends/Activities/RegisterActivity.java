package com.example.foodfriends.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodfriends.Modelo.Usuario;
import com.example.foodfriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * La clase RegisterActivity gestiona el registro de nuevos usuarios en la aplicación.
 * Utiliza Firebase Authentication para crear cuentas de usuario y Firebase Realtime Database
 * para almacenar información adicional asociada a cada usuario.
 * Los usuarios pueden registrarse proporcionando su nombre completo, correo electrónico y contraseña.
 * El proceso de registro es manejado por Firebase, y una vez completado con éxito,
 * se almacena la información del usuario en la base de datos Firebase Realtime.
 * Esta clase se encarga de la interfaz de usuario y la lógica asociada al registro de usuarios.
 */

public class RegisterActivity extends AppCompatActivity {

    //Elementos de la Activity de registro
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Button btnRegister;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Inicialización de elementos de la interfaz
        btnRegister = findViewById(R.id.btnRegister2);
        editEmail = findViewById(R.id.editEmail2);
        editPassword = findViewById(R.id.editPassword2);
        editNombre = findViewById(R.id.editNombre);

        //Configuración del listener para el botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inicia el registro del usuario
                validarCamposYRegistrar();
            }
        });
    }

    // Método que registra un nuevo usuario en la app
    public void registrarUsuario() {
        firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        try {
                            //Validamos la dirección de email
                            if (!validarEmail("miEmail@gmail.com")) {
                                mostrarToast("Email no válido.");
                            } else {
                                //Si el registro es satisfactorio
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    //Autenticación del usuario con FirebaseAuth
                                    FirebaseAuth auth = FirebaseAuth.getInstance();

                                    //Obtenemos los datos ingresados por el usuario
                                    String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String nombre = editNombre.getText().toString();
                                    String correo = editEmail.getText().toString();

                                    //Creamos el usuario que vamos a registrar, con la dirección en blanco ya que la escribirá posteriormente
                                    //Establecemos por defecto una imagen de usuario, que podrá cambiar
                                    Usuario nuevoUsuario = new Usuario(idUsuario, nombre, correo, "");
                                    //Registramos el usuario en la base de datos
                                    registrarUsuarioBaseDatos(nuevoUsuario);

                                }
                                //Si ya se ha registrado con el mismo correo...
                                else {
                                    mostrarToast("El usuario ya ha sido registrado." +
                                            "Registrese con otro correo");
                                }
                            }
                        } catch (Exception e) {
                            mostrarToast("Error al registrar. Por favor, inténtalo de nuevo.");
                        }
                    }
                });
    }

    //Método que valida si el usuario al registrarse introduce una dirección de email válida
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    //Método que evalúa si ha escrito en todos los EditText
    private void validarCamposYRegistrar() {
        String nombre = editNombre.getText().toString().trim();
        String correo = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            //Alguno de los campos está vacío, muestra un Toast de advertencia.
            mostrarToast("Por favor, completa todos los campos antes de registrarte.");
        } else {
            registrarUsuario();
        }
    }

    //Metodo que muestra un toast personalizado
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    //Método que registra en la base de datos el usuario
    public void registrarUsuarioBaseDatos(Usuario usuario) {
        try {
            //Obtener el ID del usuario
            String idUsuario = usuario.getIdUsuario();

            //Crear un mapa para contener los datos del usuario
            Map<String, Object> userData = new HashMap<>();
            userData.put("NombreUsuario", usuario.getNombreUsuario());
            userData.put("Correo", usuario.getCorreo());
            userData.put("DireccionUsuario", usuario.getDireccionUsuario());

            //Registrar usuario en DB
            usuariosRef.child(idUsuario).setValue(userData);

            //Redirigir a la actividad de inicio después del registro
            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            mostrarToast("Error al registrar. Por favor, inténtalo de nuevo.");
        }
    }
}