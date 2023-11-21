package com.example.foodfriends.Utilidades;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodfriends.Activities.MainActivity;
import com.example.foodfriends.R;

public class SplashScreenActivity extends AppCompatActivity {

    // Duración de la pantalla de carga en milisegundos
    private static final long SPLASH_SCREEN_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Utiliza un manejador para retrasar el inicio de la siguiente actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Inicia la actividad principal después de la duración de la pantalla de carga
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        }, SPLASH_SCREEN_DURATION);
    }
}