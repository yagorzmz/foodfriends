package com.example.foodfriends;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.PendingIntent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    //Elementos
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button btnLogin, btnRegister, btnAcercade;
    EditText editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprobamos si mantiene la sesión activa
        comprobarSesion();

        // Verifica si ya se tiene el permiso de notificación
        if (!hasNotificationPermission()) {
            // Solicita el permiso que falta
            requestNotificationPermission();
        }

        // Relacionamos los elementos de la interfaz de usuario
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnAcercade = findViewById(R.id.btnAcercade);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        // Método que al hacer clic en el botón de registrarse, lleva al usuario a la pantalla de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarRegisterActivity();
            }
        });

        // Botón de inicio de sesión, comprobamos si el usuario existe
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validamos si se ha escrito algo en los campos de inicio de sesión
                validarCamposYLoguear();
            }
        });

        // Botón que lleva al usuario a la página "Acerca de"
        btnAcercade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AcercaDeActivity.class);
                startActivity(i);
            }
        });
    }

    // Método que nos lleva a la pantalla de Registro
    private void iniciarRegisterActivity() {
        // Iniciamos la activity de registro
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    // Método que valida los campos y, si son correctos, inicia sesión
    private void validarCamposYLoguear() {
        String correo2 = editPassword.getText().toString(); // Debería ser editEmail.getText().toString()?
        String password2 = editPassword.getText().toString(); // Debería ser editPassword.getText().toString()?

        if (correo2.isEmpty() || password2.isEmpty()) {
            // El campo de nombre de usuario o contraseña está vacío, muestra un Toast de advertencia.
            mostrarToast("Por favor, completa todos los campos antes de iniciar sesión.");
        } else {
            iniciarSesion();
        }
    }

    // Método que inicia sesión al usuario si los campos son válidos
    public void iniciarSesion() {
        firebaseAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mostrarToast("Inicio de sesión correcto.");

                            // Cuando el usuario inicia sesión, se maneja la sesión
                            SessionManager session = new SessionManager(getApplicationContext());
                            session.setLogin(true, session.getUserId());

                            // Una vez iniciada la sesión, llevamos al usuario al menú.
                            Intent intentPerfil = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intentPerfil);
                        } else {
                            // Si falla el inicio de sesión
                            mostrarToast("Inicio de sesión fallido.");
                        }
                    }
                });
    }


    //Metodo que comprueba si el usuario no ha cerrado sesión, llevandolo directamente al menu
    //sin necesidad de volver a iniciar sesion
    private void comprobarSesion() {
        SessionManager session = new SessionManager(getApplicationContext());

        //Si el usuario no ha cerrado sesión le llevamos al menu
        if (session.isLoggedIn()) {
            int userId = session.getUserId();
            //Si el usuario ya estaba logeado se manda al menu
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        }
    }

    //Metodo que comprueba si tiene el permiso de notificaciones
    private boolean hasNotificationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED);
    }

    //Metodo que pide al usuario el permiso de notificacion
    private void requestNotificationPermission() {
        // Solicita el permiso de notificación
        if (!hasNotificationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, NOTIFICATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            // Verifica el resultado de la solicitud de permiso de notificación
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de notificación concedido, puedes realizar acciones relacionadas con notificaciones.
            }
        }
    }

    //Metodo que crea un canal de notificacion
    private void crearCanalNotificaion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    //Metodo que crea una notificacion personalizada
    private void createNotification(int imagen,String titulo,String texto) {
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

    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}