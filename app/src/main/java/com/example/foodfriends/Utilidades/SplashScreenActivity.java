package com.example.foodfriends.Utilidades;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodfriends.Activities.LoginActivity;
import com.example.foodfriends.R;
/**
 * Clase SplashScreenActivity
 * Esta actividad muestra una pantalla de carga durante un período específico de tiempo y luego inicia la
 * actividad principal de la aplicación.
 * @autor Yago Rodríguez Martínez
 * @version 1.0
 */
public class SplashScreenActivity extends AppCompatActivity {

    //Duración de la pantalla de carga en milisegundos
    private static final long SPLASH_SCREEN_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //Utiliza un manejador para retrasar el inicio de la siguiente actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Inicia la actividad principal después de la duración de la pantalla de carga
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DURATION);
    }
}