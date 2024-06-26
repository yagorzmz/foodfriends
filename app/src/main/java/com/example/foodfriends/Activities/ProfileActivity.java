package com.example.foodfriends.Activities;

import static com.example.foodfriends.Activities.CarritoActivity.listaLineasPedidosTemp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase ProfileActivity muestra la información del usuario,
 * cambiar su direccion de envio, su foto de perfil y su
 * historial de pedidos,ademas de borrar su cuenta y cerrar sesión
 */
public class ProfileActivity extends AppCompatActivity {

    //Elementos de la activity
    private boolean cuentaEliminada = false;
    private String provinciaActual;
    private String municipioActual;
    private String localidadActual;
    private String urlFotoActual;
    private static final int PHOTO_PICKER_REQUEST_CODE = 1001;
    private androidx.appcompat.widget.Toolbar toolbar;
    String urlBase = "https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference europeDatabaseReference = FirebaseDatabase.getInstance(urlBase).getReference();
    DatabaseReference usuariosRef = europeDatabaseReference.child("Usuarios");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    TextView txtNombre, txtCorreo, txtDireccionUsuario;
    Button btnCerrarSesion, btnBorrarCuenta, btnHistorial;
    ImageView imgEditarDireccion, imgPerfil;
    ImageView iconoToolbar;
    StorageReference storageReference;
    // Listas para almacenar datos de los spinners
    private Spinner spinnerProvincias;
    private Spinner spinnerMunicipios;
    private Spinner spinnerLocalidades;

    private ArrayAdapter<String> provinciasAdapter;
    private ArrayAdapter<String> municipiosAdapter;
    private ArrayAdapter<String> localidadesAdapter;

    private List<String> provinciasList;
    private List<String> municipiosList;
    private List<String> localidadesList;


    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializa los Spinners
        spinnerProvincias=findViewById(R.id.spinnerProvincias);
        spinnerMunicipios=findViewById(R.id.spinnerMunicipios);
        spinnerLocalidades=findViewById(R.id.spinnerLocalidades);

        provinciasList=new ArrayList<>();
        municipiosList=new ArrayList<>();
        localidadesList=new ArrayList<>();


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
        txtNombre = findViewById(R.id.txtNombreUsuario);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtDireccionUsuario = findViewById(R.id.txtDireccionUsuario);

        imgPerfil = findViewById(R.id.imgPerfil);

        iconoToolbar = findViewById(R.id.iconoToolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

                if (listaLineasPedidosTemp != null) {
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
        //Cargamos las provincias,municipios y localidades
        cargarUbicaciones();


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

                    provinciaActual = dataSnapshot.child("Provincia").getValue(String.class);
                    municipioActual = dataSnapshot.child("Municipio").getValue(String.class);
                    localidadActual = dataSnapshot.child("Localidad").getValue(String.class);

                    urlFotoActual=urlFotoPerfil;

                    // Establece los valores en los TextViews
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

    //Metodo que carga las provincias,municipios y localidades de la Base de datos de Firebase
    private void cargarUbicaciones() {
        //Completamos el perfil con la info del usuario
        completarPerfil(firebaseUser.getUid());

        provinciasList.clear();

        DatabaseReference ref = europeDatabaseReference.child("Provincias");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot provinciaSnapshot: dataSnapshot.getChildren()) {
                    String provinciaName = provinciaSnapshot.getKey();
                    provinciasList.add(provinciaName);

                    // Verifica si la provincia obtenida coincide con la provincia actual y agrega primero esa provincia
                    if (provinciaName.equals(provinciaActual)) {
                        provinciasList.remove(provinciaName);
                        provinciasList.add(0, provinciaName);
                    }
                }
                provinciasAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, provinciasList);
                spinnerProvincias.setAdapter(provinciasAdapter);
                provinciasAdapter.notifyDataSetChanged();
                spinnerProvincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String provinciaSeleccionada = parent.getItemAtPosition(position).toString();
                        cargarMunicipios(provinciaSeleccionada);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Metodo que carga los municipios de una provincia especifica
    private void cargarMunicipios(String provincia) {
        municipiosList.clear();
        DatabaseReference ref = europeDatabaseReference.child("Provincias").child(provincia).child("Municipios");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot municipioSnapshot: dataSnapshot.getChildren()) {
                    String municipioName = municipioSnapshot.getKey();
                    municipiosList.add(municipioName);
                    // Verifica si el municipio obtenido coincide con el municipio actual y agrega primero ese municipio

                    if (municipioName.equals(municipioActual)) {
                        municipiosList.remove(municipioName);
                        municipiosList.add(0, municipioName);
                    }
                }
                municipiosAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, municipiosList);
                spinnerMunicipios.setAdapter(municipiosAdapter);
                municipiosAdapter.notifyDataSetChanged();

                spinnerMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String municipioSeleccionado = parent.getItemAtPosition(position).toString();
                        cargarLocalidades(provincia,municipioSeleccionado);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action needed
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error handling
            }
        });
    }
    //Metodo que carga las localidades de un municipio especifico
    private void cargarLocalidades(String provincia,String municipio) {
        localidadesList.clear();
        DatabaseReference ref = europeDatabaseReference.child("Provincias").child(provincia).child("Municipios").child(municipio);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot localidadSnapshot: dataSnapshot.getChildren()) {
                    String localidadName = (String)localidadSnapshot.getValue();
                    localidadesList.add(localidadName);

                    if (localidadName.equals(localidadActual)) {
                        localidadesList.remove(localidadName);
                        localidadesList.add(0, localidadName);
                    }
                }
                localidadesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, localidadesList);
                spinnerLocalidades.setAdapter(localidadesAdapter);
                localidadesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error handling
            }
        });
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

                // Verificar si el archivo seleccionado es una imagen
                if (esImagen(uriImagenSeleccionada)) {
                    borrarImagenPorUrl(urlFotoActual);
                    imgPerfil.setImageURI(uriImagenSeleccionada);
                    subirImagenAlStorage(uriImagenSeleccionada);
                    storageReference.putFile(uriImagenSeleccionada)
                            .addOnSuccessListener(taskSnapshot -> {
                                //Obtenemos la URL de descarga de la imagen recién cargada
                                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    //Actualizamos el campo urlFotoPerfil en la base de datos con la nueva URL
                                    actualizarUrlFotoPerfilEnBaseDeDatos(uri.toString());

                                    //Actualizamos urlFotoActual con la nueva URL
                                    urlFotoActual = uri.toString();
                                }).addOnFailureListener(exception -> {
                                    exception.printStackTrace();
                                });

                            })
                            .addOnFailureListener(exception -> {
                                //Manejamos los errores durante la carga de la imagen
                                mostrarToast("Error al subir la imagen");
                                exception.printStackTrace();
                            });
                } else {
                    mostrarToast("Por favor, selecciona una imagen");
                }

        }
    }
    // Método para verificar si el archivo seleccionado es una imagen
    private boolean esImagen(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("image");
    }


    //Metodo que actuliza la foto de perfil del usuario
    private void actualizarUrlFotoPerfilEnBaseDeDatos(String urlFotoPerfil) {
        //Obténemos el ID del usuario actual
        String idUsuario = firebaseUser.getUid();

        //Actualizamos el campo urlFotoPerfil en la base de datos
        usuariosRef.child(idUsuario).child("urlFotoPerfil").setValue(urlFotoPerfil)
                .addOnSuccessListener(aVoid -> {
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
        storageReference = FirebaseStorage.getInstance().getReference().child("urlPerfiles").child(nombreImagen);

        // Obtenemos la URL de la imagen actual del perfil desde la base de datos
        usuariosRef.child(firebaseUser.getUid()).child("urlFotoPerfil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urlFotoPerfilActual = dataSnapshot.getValue(String.class);
                if (urlFotoPerfilActual != null && !urlFotoPerfilActual.equals("gs://foodfriendsapp-f51dc.appspot.com/urlPerfiles/perfilvacio.jpg")) {
                    // Creamos una referencia al archivo de la imagen actual del perfil en el Storage
                    StorageReference referenciaImagenActual = FirebaseStorage.getInstance().getReferenceFromUrl(urlFotoPerfilActual);

                    // Eliminamos la imagen actual del perfil del Storage
                    referenciaImagenActual.delete().addOnSuccessListener(aVoid -> {
                        // La imagen actual del perfil ha sido eliminada correctamente o no existía.
                        // Procedemos a subir la nueva imagen del perfil al Storage
                        storageReference.putFile(uriImagen)
                                .addOnSuccessListener(taskSnapshot -> {
                                    //Obtenemos la URL de descarga de la nueva imagen del perfil
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
                                    //Manejamos los errores durante la carga de la nueva imagen del perfil
                                    mostrarToast("Error al subir la nueva imagen del perfil al Storage");
                                    innerException.printStackTrace();
                                });
                    }).addOnFailureListener(exception -> {
                        //Manejamos los errores al intentar eliminar la imagen actual del perfil
                        mostrarToast("Error al eliminar la imagen actual del perfil del Storage");
                        exception.printStackTrace();
                    });
                } else {
                    // No hay una imagen actual del perfil en la base de datos.
                    // Procedemos a subir la nueva imagen del perfil al Storage
                    storageReference.putFile(uriImagen)
                            .addOnSuccessListener(taskSnapshot -> {
                                //Obtenemos la URL de descarga de la nueva imagen del perfil
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
                                //Manejamos los errores durante la carga de la nueva imagen del perfil
                                mostrarToast("Error al subir la nueva imagen del perfil al Storage");
                                innerException.printStackTrace();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejamos el error si la lectura de la URL de la imagen actual del perfil falla
                mostrarToast("Error al leer la URL de la imagen actual del perfil en la base de datos");
                databaseError.toException().printStackTrace();
            }
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

    //Metodo que elimina la cuenta del usuario
    public void eliminarCuenta() {
        cuentaEliminada = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar cuenta");
        builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer. Ten en cuenta que, aunque tu cuenta se eliminará, la información de tus pedidos realizados seguirá formando parte de la empresa y no se borrará.");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el id del usuario
                String idUsuario = firebaseUser.getUid();
                // Borrar la cuenta y reiniciar la aplicación
                borrarUsuario(idUsuario);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
    //Método que borra al usuario mediante su id de la base de datos y de la autenticacion
    public void borrarUsuario(String idUsuario) {
        DatabaseReference usuarioAEliminarRef = usuariosRef.child(idUsuario);
        borrarImagenPorUrl(urlFotoActual);

        usuarioAEliminarRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mostrarToast("Cuenta eliminada");
                                limpiarDatosYReiniciar(); // Reiniciar la aplicación
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
    //Metodo que reinicia la app
    private void limpiarDatosYReiniciar() {
        // Limpiar todos los datos de la aplicación
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();

        // Reiniciar la aplicación
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        System.exit(0); // Cierra la aplicación completamente
    }


    //Método que obliga al usuario a editar la direccion para poder usar la aplicacion
    public void solicitarDireccion(String titulo, String texto) {
        final EditText input = new EditText(this);

        // Configuramos el filtro para limitar la longitud del texto
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(46);
        input.setFilters(filters);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa tu dirección")
                .setMessage(texto)
                .setView(input)
                .setPositiveButton("OK", null) // Setting null here to override default behavior
                .setCancelable(false); // Make the dialog non-cancelable

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String direccion = input.getText().toString();

                        if (direccion.isEmpty()) {
                            mostrarToast("Por favor, ingresa tu dirección.");
                        } else if (!direccion.startsWith("C/")) {
                            mostrarToast("La dirección debe tener este formato: C/...");
                        } else {
                            // Guardar dirección ingresada
                            txtDireccionUsuario.setText(direccion);

                            // Actualiza la dirección en la base de datos
                            usuariosRef.child(firebaseUser.getUid()).child("DireccionUsuario").setValue(direccion);

                            // Cerrar el diálogo
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarUbicacionEnBaseDeDatos();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Cuando el usuario vuelve a la actividad, establece isDeletingAccount en false
        cuentaEliminada = false;
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
    //Meotodo que guarda la ubicacion en la base de datos del usuario
    private void guardarUbicacionEnBaseDeDatos() {
        if (!cuentaEliminada) {
            // Obtener los valores seleccionados de los spinners
            String provinciaSeleccionada = spinnerProvincias.getSelectedItem().toString();
            String municipioSeleccionado = spinnerMunicipios.getSelectedItem().toString();
            String localidadSeleccionada = spinnerLocalidades.getSelectedItem().toString();

            // Actualizar la información en la base de datos del usuario actual
            DatabaseReference usuarioRef = usuariosRef.child(firebaseUser.getUid());
            usuarioRef.child("Provincia").setValue(provinciaSeleccionada);
            usuarioRef.child("Municipio").setValue(municipioSeleccionado);
            usuarioRef.child("Localidad").setValue(localidadSeleccionada);
        }
    }
    //Metodo que borra la imagen de perfil del storage
    public void borrarImagenPorUrl(String url) {
        if(!url.equals("gs://foodfriendsapp-f51dc.appspot.com/urlPerfiles/perfilvacio.jpg")){
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // La imagen se eliminó con éxito del almacenamiento
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Hubo un error al eliminar la imagen del almacenamiento
                }
            });
        }
    }

}