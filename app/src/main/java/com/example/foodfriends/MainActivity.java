package com.example.foodfriends;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button btnLogin,btnRegister,btnAcercade;
    EditText editEmail,editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Relacionamos los elementos
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);
        btnAcercade=findViewById(R.id.btnAcercade);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);

        //Método que al hacer click en el boton de registrarse, lleva al usuario a la pantalla de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                inicarRegisterActivity();
            }
        });
        //Boton iniciar sesion, comprobamos si el usuario existe
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validamos si ha escrito algo en el login
                validarCamposYLoguear();
            }
        });
        //Boton que lleva al usuario a la pagina de acerca de
        btnAcercade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),AcercaDeActivity.class);
                startActivity(i);
            }
        });
    }

    private void inicarRegisterActivity() {
        //Iniciamos la activity de registro
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    //Metodo que valida los campos y si son correctos logea
    private void validarCamposYLoguear()
    {
        String correo2 = editPassword.getText().toString();
        String password2 = editPassword.getText().toString();

        if (correo2.isEmpty() || password2.isEmpty()) {
            // El campo de nombre de usuario o contraseña está vacío, muestra un Toast de advertencia.
            mostrarToast("Por favor, completa todos los campos antes de iniciar sesión.");
        }else{
            iniciarSesion();
        }
    }
    //Método que inicia sesion al usuario si los campos son validos
    public void iniciarSesion()
    {
        firebaseAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            mostrarToast("Inicio de sesion correcto.");
                            //Una vez iniciado sesión, llevamos al usuario al menu.
                            Intent intentPerfil=new Intent(getApplicationContext(),MenuActivity.class);
                            startActivity(intentPerfil);
                        }
                        else
                        {
                            // Si falla el inicio de sesion
                            mostrarToast("Inicio de sesion fallido.");
                        }
                    }
                });
    }
    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}