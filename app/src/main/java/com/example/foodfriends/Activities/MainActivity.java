package com.example.foodfriends.Activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    // Elementos necesarios para notificaciones
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // Instancia de Firebase Authentication
    Button btnLogin, btnRegister; // Botones de inicio de sesión y registro
    EditText editEmail, editPassword; // Campos de texto para el correo electrónico y la contraseña

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprobamos si el usuario ya tiene una sesión activa
        comprobarSesion();

        // Verificamos si se ha concedido el permiso de notificación
        if (!hasNotificationPermission()) {
            // Si no se ha concedido, solicitamos el permiso
            requestNotificationPermission();
        }

        // Relacionamos los elementos de la interfaz de usuario con sus respectivos ID
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        // Configuramos el listener para el botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Al hacer clic en el botón de registro, nos dirigimos a la pantalla de registro
                iniciarRegisterActivity();
            }
        });

        // Configuramos el listener para el botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validamos los campos y procedemos al inicio de sesión si son correctos
                validarCamposYLoguear();
            }
        });
    }

    // Método que inicia la actividad de Registro
    private void iniciarRegisterActivity() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    // Método que valida los campos y procede al inicio de sesión si son correctos
    private void validarCamposYLoguear() {
        // Obtenemos el correo electrónico y la contraseña de los campos de texto
        String correo2 = editPassword.getText().toString(); // ¿Debería ser editEmail.getText().toString()?
        String password2 = editPassword.getText().toString(); // ¿Debería ser editPassword.getText().toString()?

        if (correo2.isEmpty() || password2.isEmpty()) {
            // Si el campo de correo electrónico o contraseña está vacío, mostramos un Toast de advertencia
            mostrarToast("Por favor, completa todos los campos antes de iniciar sesión.");
        } else {
            // Si los campos son válidos, procedemos al inicio de sesión
            iniciarSesion();
        }
    }

    // Método que inicia sesión utilizando Firebase Authentication
    public void iniciarSesion() {
        firebaseAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si el inicio de sesión es exitoso, manejamos la sesión
                            mostrarToast("Inicio de sesión correcto.");

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.setLogin(true, session.getUserId());

                            // Una vez iniciada la sesión, llevamos al usuario al menú.
                            Intent intentPerfil = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intentPerfil);
                        } else {
                            // Si falla el inicio de sesión, mostramos un Toast de error
                            mostrarToast("Inicio de sesión fallido.");
                        }
                    }
                });
    }

    // Método que comprueba si el usuario ya ha iniciado sesión previamente
    private void comprobarSesion() {
        SessionManager session = new SessionManager(getApplicationContext());

        // Si el usuario no ha cerrado sesión, lo llevamos directamente al menú
        if (session.isLoggedIn()) {
            int userId = session.getUserId();
            // Si el usuario ya estaba logeado, lo llevamos al menú
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
        }
    }

    // Método que verifica si se tiene el permiso de notificaciones
    private boolean hasNotificationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED);
    }

    // Método que solicita al usuario el permiso de notificación
    private void requestNotificationPermission() {
        // Solicita el permiso de notificación
        if (!hasNotificationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, NOTIFICATION_PERMISSION_REQUEST);
        }
    }

    // Manejo de resultados de solicitudes de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            // Verificamos el resultado de la solicitud de permiso de notificación
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de notificación concedido, puedes realizar acciones relacionadas con notificaciones.
            }
        }
    }

    // Método que crea un canal de notificación
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // Método que crea y muestra una notificación personalizada
    private void createNotification(int imagen, String titulo, String texto) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(imagen)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }

    // Método que muestra mensajes personalizados utilizando Toast
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}