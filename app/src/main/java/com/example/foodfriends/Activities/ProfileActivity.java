package com.example.foodfriends.Activities;

import static com.example.foodfriends.Activities.CarritoActivity.listaLineasPedidosTemp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * La clase ProfileActivity muestra la información del usuario,
 * cambiar su direccion de envio, su foto de perfil y su
 * historial de pedidos,ademas de borrar su cuenta y cerrar sesión
 */
public class ProfileActivity extends AppCompatActivity {

    //Elementos de la activity
    private static final int PHOTO_PICKER_REQUEST_CODE = 1001;
    Uri imageUri;
    private androidx.appcompat.widget.Toolbar toolbar;
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    TextView txtId, txtNombre, txtCorreo, txtDireccionUsuario;
    Button btnCerrarSesion, btnBorrarCuenta, btnHistorial;
    ImageView imgEditarDireccion, imgPerfil;
    ImageView iconoToolbar;
    StorageReference storageReference;
    Uri uri;
    ActivityResultLauncher<Intent> resultLauncher;

    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(getApplicationContext());
        storageReference = FirebaseStorage.getInstance().getReference().child("urlPerfiles");

        // Desactivar el botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        //Enlazamos los elementos
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        btnHistorial = findViewById(R.id.btnHistorial);
        imgEditarDireccion = findViewById(R.id.imgEditarDireccion);
        txtId = findViewById(R.id.txtIdPedido);
        txtNombre = findViewById(R.id.txtNombreUsuario);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtDireccionUsuario = findViewById(R.id.txtDireccionUsuario);

        imgPerfil = findViewById(R.id.imgPerfil);

        iconoToolbar = findViewById(R.id.iconoToolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Metodo para cambiar la foto de perfil
        // Configuración del OnClickListener para el ImageView
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
            }
        });

        //Metodo quie abre el historial de pedidos del usuario
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HistorialActivity.class);
                startActivity(i);
                finish();
            }
        });
        //Metodo que cierra sesión al usuario
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listaLineasPedidosTemp!=null){
                    listaLineasPedidosTemp.clear();
                }
                //Cerramos sesion
                cerrarSesion();
            }
        });
        //Metodo que borra la cuenta del usuario
        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Eliminamos la cuenta
                eliminarCuenta();
            }
        });
        //Metodo que permite cambiar la direccion del usuario
        imgEditarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarDireccion("Edita tu direccion", "Introduce tu nueva direccion de envio");
            }
        });
        //Actualizamos el perfil del usuario
        completarPerfil(firebaseUser.getUid());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        // Deshabilita el ítem del menú correspondiente a esta actividad
        MenuItem item = menu.findItem(R.id.item_perfil);
        if (item != null) {
            item.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_inicio) {
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
            finish(); // Cierra la actividad actual
            return true;
        } else if (id == R.id.item_carrito) {
            Intent i = new Intent(getApplicationContext(), CarritoActivity.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.item_masvendidos) {
            Intent i = new Intent(getApplicationContext(), MasVendidosActivity.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.item_acercade) {
            Intent i = new Intent(getApplicationContext(), AcercaDeActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            mostrarToast("No has seleccionado la imagen");
            return;
        }
        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_CODE:
                Uri uriImagenSeleccionada = data.getData();
                imgPerfil.setImageURI(uriImagenSeleccionada);
                subirImagenAlStorage(uriImagenSeleccionada);
                storageReference.putFile(uriImagenSeleccionada)
                        .addOnSuccessListener(taskSnapshot -> {
                            //La imagen se ha subido exitosamente
                            mostrarToast("Imagen subida con éxito");

                            //Obtenemos la URL de descarga de la imagen recién cargada
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                //Actualizamos el campo urlFotoPerfil en la base de datos con la nueva URL
                                actualizarUrlFotoPerfilEnBaseDeDatos(uri.toString());
                            }).addOnFailureListener(exception -> {
                                //Manejamos los errores al obtener la URL de descarga
                                mostrarToast("Error al obtener la URL de descarga");
                                exception.printStackTrace();
                            });

                        })
                        .addOnFailureListener(exception -> {
                            //Manejamos los errores durante la carga de la imagen
                            mostrarToast("Error al subir la imagen");
                            exception.printStackTrace();
                        });
        }
    }

    private void actualizarUrlFotoPerfilEnBaseDeDatos(String urlFotoPerfil) {
        //Obténemos el ID del usuario actual
        String idUsuario = firebaseUser.getUid();

        //Actualizamos el campo urlFotoPerfil en la base de datos
        usuariosRef.child(idUsuario).child("urlFotoPerfil").setValue(urlFotoPerfil)
                .addOnSuccessListener(aVoid -> {
                    mostrarToast("Se ha actualizado la foto con exito");
                })
                .addOnFailureListener(e -> {
                    mostrarToast("Error al actualizar la URL de la foto de perfil en la base de datos");
                    e.printStackTrace();
                });
    }

    //Metodo que sube la imagen escogida de la galeria al storage de firebase
    private void subirImagenAlStorage(Uri uriImagen) {
        //Obtenemos el nombre del archivo original a partir de la URI
        String nombreImagen = obtenerNombreArchivoDesdeUri(uriImagen);

        if (nombreImagen == null) {
            //Manejamos el caso en que no se pueda obtener el nombre del archivo
            mostrarToast("No se pudo obtener el nombre del archivo");
            return;
        }

        //Creamos una referencia en el Storage con el mismo nombre que el archivo original
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("urlPerfiles").child(nombreImagen);

        //Antes de subir la nueva imagen, verificamos si la imagen ya existe en el Storage
        storageReference.getDownloadUrl().addOnSuccessListener(existingUri -> {
            actualizarUrlFotoPerfilEnBaseDeDatos(existingUri.toString());
        }).addOnFailureListener(exception -> {
            // La imagen no existe, procedemos a subirla
            storageReference.putFile(uriImagen)
                    .addOnSuccessListener(taskSnapshot -> {

                        //Obtenemos la URL de descarga de la imagen recién cargada
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            //Actualizamos el campo urlFotoPerfil en la base de datos con la nueva URL
                            actualizarUrlFotoPerfilEnBaseDeDatos(uri.toString());
                        }).addOnFailureListener(innerException -> {
                            //Manejamos los errores al obtener la URL de descarga
                            mostrarToast("Error al obtener la URL de descarga");
                            innerException.printStackTrace();
                        });

                    })
                    .addOnFailureListener(innerException -> {
                        //Manejamos los errores durante la carga de la imagen
                        mostrarToast("Error al subir la imagen al Storage");
                        innerException.printStackTrace();
                    });
        });
    }

    // Método que obtiene el nombre de archivo de una imagen a partir de su URI
    private String obtenerNombreArchivoDesdeUri(Uri uri) {
        String nombreArchivo = null;
        Cursor cursor = null;
        try {
            //Definimos la proyección para obtener el nombre del archivo desde el proveedor de contenidos
            String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};

            //Realizamos una consulta al proveedor de contenidos para obtener el nombre del archivo
            cursor = getContentResolver().query(uri, projection, null, null, null);

            //Verificamos si se obtuvo un resultado y mover el cursor al primer elemento
            if (cursor != null && cursor.moveToFirst()) {
                //Obtenemos el índice de la columna del nombre del archivo
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

                //Obtenemos el nombre del archivo desde el cursor
                nombreArchivo = cursor.getString(columnIndex);
            }
        } finally {
            //Cerramos el cursor para liberar recursos
            if (cursor != null) {
                cursor.close();
            }
        }

        //Devolvemos el nombre del archivo o null si no se pudo obtener
        return nombreArchivo;
    }

    // Método que cierra sesión y vuelve a la pantalla de Login
    private void cerrarSesion() {
        // Obtenemos el usuario y cerramos sesión
        FirebaseAuth.getInstance().signOut();

        // Mensaje que comunica al usuario que cerramos sesión
        mostrarToast("Se ha cerrado sesión.");

        // Cuando el usuario cierra sesión
        SessionManager session = new SessionManager(getApplicationContext());
        session.logout();

        // Volvemos a la pantalla de login y limpiamos la pila de actividades
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Inicia la actividad de inicio de sesión antes de cerrar la actividad actual
        startActivity(intent);
        finish();  // Cierra la actividad actual después de que la nueva actividad se haya iniciado
    }

    //Método que elimina la cuenta del usuario.
    public void eliminarCuenta() {
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
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
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
    public void borrarUsuario(String idUsuario) {
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
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // El usuario se eliminó con éxito de Firebase Authentication
                                mostrarToast("Usuario eliminado de la autenticación");
                                finish(); // Cierra la actividad después de eliminar el usuario
                            } else {
                                mostrarToast("Error al eliminar el usuario de la autenticación");
                            }
                        }
                    });
                } else {
                    mostrarToast("Error al eliminar el usuario de la base de datos");
                }
            }
        });
    }

    //Método que obliga al usuario a editar la direccion para poder usar la aplicacion
    public void solicitarDireccion(String titulo, String texto) {
        final EditText input = new EditText(this);

        // Configuramos el filtro para limitar la longitud del texto
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(46);
        input.setFilters(filters);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Ingresa tu dirección")
                .setMessage(texto)
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String direccion = input.getText().toString();

                        if (direccion.isEmpty()) {
                            mostrarToast("Por favor, ingresa tu dirección.");
                            solicitarDireccion(titulo, texto); // Volver a solicitar
                        } else if (!direccion.startsWith("C/")) {
                            mostrarToast("La dirección debe tener este formato: C/...");
                            solicitarDireccion(titulo, texto);
                        } else if (direccion.startsWith("C/")) {
                            // Guardar dirección ingresada
                            txtDireccionUsuario.setText(direccion);

                            // Actualiza la dirección en la base de datos
                            usuariosRef.child(firebaseUser.getUid()).child("DireccionUsuario").setValue(direccion);
                        }
                    }
                })
                .show();

        // Desactivar el botón Atrás y el gesto de deslizar para salir de la actividad hasta que se ingrese una dirección
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        // Capturar el evento de cierre del cuadro de diálogo
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Verificar si se ingresó una dirección antes de cerrar el cuadro de diálogo
                if (txtDireccionUsuario.getText().toString().isEmpty()) {
                    solicitarDireccion(titulo, texto);
                } else {
                    // Habilitar el botón Atrás y el gesto de deslizar para salir de la actividad
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Metodo que completa el perfil del usuario, obteniendo los datos de la base de datos
    private void completarPerfil(String idUsuario) {
        DatabaseReference usuarioRef = usuariosRef.child(idUsuario);
        // Agrega un ValueEventListener para escuchar los cambios en los datos del usuario
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recupera los valores de los campos del usuario
                    String nombre = dataSnapshot.child("NombreUsuario").getValue(String.class);
                    String correo = dataSnapshot.child("Correo").getValue(String.class);
                    String direccion = dataSnapshot.child("DireccionUsuario").getValue(String.class);
                    String urlFotoPerfil = dataSnapshot.child("urlFotoPerfil").getValue(String.class);

                    // Establece los valores en los TextViews
                    txtId.setText(idUsuario);
                    txtNombre.setText(nombre);
                    txtCorreo.setText(correo);

                    // Carga la imagen de perfil desde Firebase Storage y la establece en el ImageView
                    if (urlFotoPerfil != null && !urlFotoPerfil.isEmpty()) {
                        cargarImagenPerfil(urlFotoPerfil);
                    } else {
                        mostrarToast("No se ha podido cargar la foto de perfil");
                    }

                    if (direccion == null || direccion.isEmpty()) {
                        // Si la dirección está en blanco o es nula, llama al método solicitarDireccion
                        solicitarDireccion("Aviso Direccion", "Debes introducir una direccion para poder " +
                                "enviarte los pedidos. La dirección debe empezar por C/");
                    } else {
                        txtDireccionUsuario.setText(direccion);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error al cargar los datos del perfil", databaseError.toException());
            }
        });
    }

    // Método para cargar la imagen de perfil desde Firebase Storage y establecerla en el ImageView con Glide
    private void cargarImagenPerfil(String urlFotoPerfil) {
        //Obténemos una referencia al archivo en Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlFotoPerfil);

        //Convierte la referencia a URL y plasma la imagen en el imageview
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Carga la imagen en el ImageView utilizando Picasso
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .placeholder(R.drawable.waiting)
                        .error(R.drawable.error)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // Utiliza caché solo para recursos decodificados
                        .into(imgPerfil);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar fallos al obtener la URL de descarga
                e.printStackTrace();
            }
        });
    }

    //Metodo que muestra mensajes personalizados
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}