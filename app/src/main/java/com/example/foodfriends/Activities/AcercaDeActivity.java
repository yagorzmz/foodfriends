package com.example.foodfriends.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.foodfriends.R;
/**
 * Esta clase proporciona la lógica para la pantalla "Acerca de" de la aplicación, permitiendo
 * al usuario obtener información sobre el desarrollo de la aplicación y volver a la pantalla principal si lo desea.
*/
public class AcercaDeActivity extends AppCompatActivity {

    // Botón para volver a la pantalla principal
    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establecer el diseño de la interfaz de usuario para esta actividad
        setContentView(R.layout.activity_acerca_de);

        // Inicializar el botón de volver mediante su identificador en el diseño XML
        btnVolver = findViewById(R.id.btnVolverLogin);

        // Configurar un listener para el evento de clic en el botón de volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un Intent para volver a la pantalla principal (MainActivity)
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                // Iniciar la actividad correspondiente al Intent
                startActivity(i);
            }
        });
    }
}