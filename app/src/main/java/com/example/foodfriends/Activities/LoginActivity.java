package com.example.foodfriends.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * La clase LoginActivity gestiona el inicio de sesión de los usuarios mediante
 * el correo y la contraseña en la aplicación.
 * Utiliza Firebase Authentication para verificar las cuentas de usuario.
 * Los usuarios no registrados podrán acceder a la opcion de registrarse.
 */
public class LoginActivity extends AppCompatActivity {

    //Elementos de la activity
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button btnLogin, btnRegister;
    EditText editEmail, editPassword;
    TextView txtNoTeHasRegistrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Comprobamos si el usuario ya tiene una sesión activa
        comprobarSesion();

        //Verificamos si se ha concedido el permiso de notificación
        if (!tienePermisoNotificacion()) {
            // Si no se ha concedido, solicitamos el permiso
            pedirPermisoNotificacion();
        }

        //Relacionamos los elementos de la interfaz de usuario con sus respectivos ID
        btnLogin = findViewById(R.id.btnLogin);
        txtNoTeHasRegistrado=findViewById(R.id.txNoTeHasRegistrado);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        //Configuramos el listener para el botón de registro
        txtNoTeHasRegistrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Al hacer click en el botón de registro, nos dirigimos a la pantalla de registro
                iniciarRegisterActivity();
            }
        });

        //Configuramos el listener para el botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validamos los campos y procedemos al inicio de sesión si son correctos
                validarCamposYLoguear();
            }
        });
    }

    //Método que inicia la actividad de Registro
    private void iniciarRegisterActivity() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    //Método que valida los campos y procede al inicio de sesión si son correctos
    private void validarCamposYLoguear() {
        // Obtenemos el correo electrónico y la contraseña de los campos de texto
        String correo2 = editPassword.getText().toString();
        String password2 = editPassword.getText().toString();

        // Si el campo de correo electrónico o contraseña está vacío, mostramos un Toast de advertencia
        if (correo2.isEmpty() || password2.isEmpty()) {
            mostrarToast("Por favor, completa todos los campos antes de iniciar sesión.");
        } else {
            //Si los campos son válidos, procedemos al inicio de sesión
            iniciarSesion();
        }
    }

    //Método que inicia sesión utilizando Firebase Authentication
    public void iniciarSesion() {
        firebaseAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Si el inicio de sesión es exitoso, manejamos la sesión
                            mostrarToast("Inicio de sesión correcto.");

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.setLogin(true, session.getUserId());

                            //Una vez iniciada la sesión, llevamos al usuario al menú
                            Intent intentPerfil = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intentPerfil);
                        } else {
                            //Si falla el inicio de sesión, mostramos un Toast de error
                            mostrarToast("Inicio de sesión fallido.");
                        }
                    }
                });
    }

    //Método que comprueba si el usuario ya ha iniciado sesión previamente
    private void comprobarSesion() {
        SessionManager session = new SessionManager(getApplicationContext());

        //Si el usuario no ha cerrado sesión, lo llevamos directamente al menú
        if (session.isLoggedIn()) {
            int userId = session.getUserId();
            // Si el usuario ya estaba logeado, lo llevamos al menú
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
        }
    }

    //Método que verifica si se tiene el permiso de notificaciones
    private boolean tienePermisoNotificacion() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED);
    }

    //Método que solicita al usuario el permiso de notificación
    private void pedirPermisoNotificacion() {
        //Solicita el permiso de notificación
        if (!tienePermisoNotificacion()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, NOTIFICATION_PERMISSION_REQUEST);
        }
    }

    //Método que muestra mensajes personalizados utilizando Toast
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}