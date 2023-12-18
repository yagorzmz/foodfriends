package com.example.foodfriends.Activities;

import static com.example.foodfriends.Activities.CarritoActivity.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodfriends.Modelo.LineaPedido;
import com.example.foodfriends.Modelo.LineaPedidoTemp;
import com.example.foodfriends.Modelo.Pedido;
import com.example.foodfriends.R;
import com.example.foodfriends.Utilidades.AdaptadorLineasPedidos;
import com.example.foodfriends.Utilidades.ListaLineasPedidosTempHelper;
import com.example.foodfriends.Utilidades.NotificationBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * La clase CarritoActivity muestra todos los productos añadidos al carrito
 * con sus unidades, el repcio de cada linea de pedido y el precio total del pedido
 */
public class CarritoActivity extends AppCompatActivity implements AdaptadorLineasPedidos.OnLineaPedidoChangeListener {

    //Elementos de la activity
    private static final String CHANNEL_ID = "mi_canal_de_notificaciones";
    private static final int NOTIFICATION_ID = 1;
    private static double totalConGastosEnvio;
    private androidx.appcompat.widget.Toolbar toolbar;
    static ListView listViewLineasPedido;
    private static Button btnRealizarPedido;
    static AdaptadorLineasPedidos adapter;
    static List<LineaPedidoTemp> listaLineasPedidosTemp;
    static TextView txtTotalPedido;
    static TextView txtCarritoVacio;
    ImageView iconoToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Configuración inicial de la interfaz y la barra de herramientas.
        iconoToolbar = findViewById(R.id.iconoToolbar);
        toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnRealizarPedido = findViewById(R.id.btnRealizarPedido);

        listViewLineasPedido = findViewById(R.id.listViewLineasPedido);
        listaLineasPedidosTemp = ListaLineasPedidosTempHelper.getListaLineasPedidosTemp();
        txtTotalPedido = findViewById(R.id.txtTotalPedido);
        txtCarritoVacio = findViewById(R.id.txtCarritoVacio);

        //Siempre que entremos actualizamos la situacion del carrito
        actualizarCarrito();
        // Crear el canal de notificaciones al inicio de la actividad.
        crearCanalNotificacion();

        listViewLineasPedido.setAdapter(adapter);

        // Configuramos el listener para el botón de realizar pedido.
        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manejarBotonRealizarPedido();
            }
        });
    }
    //Método que maneja la interacción con el botón realizar pedido
    private void manejarBotonRealizarPedido() {
        if (listaLineasPedidosTemp.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No hay productos para realizar el pedido", Toast.LENGTH_SHORT).show();
        } else {
            // Mostramos un diálogo de confirmación antes de realizar el pedido.
            mostrarDialogoRealizarPedido();
        }
    }


    //Método que comprueba el estado del carrito, si esta vacio yo contiene lineas de pedido
    private void actualizarCarrito() {
        // Comprobamos si el carrito está vacío.
        if (listaLineasPedidosTemp.isEmpty()) {
            // Si está vacío, mostramos un mensaje y ocultamos elementos.
            txtCarritoVacio.setVisibility(View.VISIBLE);
            listViewLineasPedido.setVisibility(View.GONE);
            btnRealizarPedido.setVisibility(View.GONE);
            txtTotalPedido.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Carrito vacio",Toast.LENGTH_SHORT).show();
        } else {
            // Si hay elementos en el carrito, configuramos el adaptador y mostramos la lista.
            adapter = new AdaptadorLineasPedidos(this, listaLineasPedidosTemp, this);
            txtCarritoVacio.setVisibility(View.GONE);
            listViewLineasPedido.setAdapter(adapter);
        }
    }

    //Método que va calculando el precio a pagar del pedido
    private void actualizarTotal() {
        double totalSinIVA = 0.0;

        // Calcular el total sin IVA sumando los precios de cada línea
        for (LineaPedidoTemp lineaPedido : listaLineasPedidosTemp) {
            totalSinIVA += lineaPedido.calcularPrecioTotal();
        }

        double totalIVA = totalSinIVA * 0.21;
        double totalConIVA = totalSinIVA + totalIVA;

        //Calcular el envío según las reglas especificadas
        //Dependiendo del coste del pedido establecemos un gasto de envío
        double costeEnvio = 0.0;
        if (totalSinIVA < 10) {
            costeEnvio = 3.5;
        } else if (totalSinIVA >= 10 && totalSinIVA <= 20) {
            costeEnvio = 2.5;
        } else if (totalSinIVA > 20) {
            costeEnvio = 1.5;
        }

        //Precio final del pedido
        totalConGastosEnvio = totalConIVA + costeEnvio;

        //Crear el mensaje con el formato deseado
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Precio pedido sin IVA = ").append(formatearPrecio(totalSinIVA)).append("€\n");
        mensaje.append("IVA adicional = ").append(formatearPrecio(totalIVA)).append("€\n");
        mensaje.append("Coste de envío = ").append(formatearPrecio(costeEnvio)).append("€\n");
        mensaje.append("Total pedido = ").append(formatearPrecio(totalConGastosEnvio)).append("€");

        //Mostrar el mensaje
        TextView txtTotal = findViewById(R.id.txtTotalPedido);
        txtTotal.setText(mensaje.toString());
    }

    //Inflamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        // Deshabilita el ítem del menú correspondiente a esta actividad
        MenuItem item = menu.findItem(R.id.item_carrito);
        if (item != null) {
            item.setEnabled(false);
        }
        return true;
    }

    //Recogemos los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar las opciones del menú
        int id = item.getItemId();
        if (id == R.id.item_inicio) {
            // Iniciar la actividad de Inicio
            Intent i = new Intent(getApplicationContext(), InicioActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_masvendidos) {
            // Iniciar la actividad de Más Vendidos
            Intent i = new Intent(getApplicationContext(), MasVendidosActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_perfil) {
            // Iniciar la actividad de Perfil
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.item_acercade) {
            // Iniciar la actividad Acerca de
            Intent i = new Intent(getApplicationContext(), AcercaDeActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //Método para formatear el precio
    private String formatearPrecio(double precio) {
        DecimalFormat formato = new DecimalFormat("#.##");
        return formato.format(precio);
    }

    @Override
    public void onLineaPedidoChanged() {
        actualizarTotal();
    }

    @Override
    public void onEliminarLineaPedido(int position) {
        // Implementar la lógica para eliminar la línea de pedido en la posición indicada
        listaLineasPedidosTemp.remove(position);

        // Actualizar el total después de eliminar una línea de pedido
        actualizarTotal();

        // Verificar si la lista está vacía después de la eliminación
        if (listaLineasPedidosTemp.isEmpty()) {
            carritoVacio();
        } else {
            // La lista no está vacía, configura el adaptador y muestra los elementos
            listViewLineasPedido.setAdapter(adapter);
            btnRealizarPedido.setVisibility(View.VISIBLE);
        }

        //Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();
    }

    //Método que cuando la lista está vacía, muestra el TextView "CARRITO VACIO" y oculta otros elementos
    private static void carritoVacio() {
        txtCarritoVacio.setVisibility(View.VISIBLE);
        listViewLineasPedido.setVisibility(View.GONE);
        btnRealizarPedido.setVisibility(View.GONE);
        txtTotalPedido.setVisibility(View.GONE);
    }

    //Método que muestra al usuario un dialogo para que elija el tiempo en que quiere
    //recibir el pedido
    private void mostrarOpcionesTiempoEntrega() {
        // Obtener la hora actual del dispositivo
        Calendar horaActual = Calendar.getInstance();
        int horaActualInt = horaActual.get(Calendar.HOUR_OF_DAY);
        int minutoActual = horaActual.get(Calendar.MINUTE);

        // Calcular las opciones de tiempo según las especificaciones dadas
        List<String> opcionesTiempo = new ArrayList<>();
        opcionesTiempo.add(obtenerTiempoFormateado(horaActualInt, minutoActual + 1)); // Primera opción
        opcionesTiempo.add(obtenerTiempoFormateado(horaActualInt, minutoActual + 45)); // Segunda opción
        opcionesTiempo.add(obtenerTiempoFormateado(horaActualInt + 1, minutoActual)); // Tercera opción
        opcionesTiempo.add(obtenerTiempoFormateado(horaActualInt + 1, minutoActual + 15)); // Cuarta opción
        opcionesTiempo.add(obtenerTiempoFormateado(horaActualInt + 1, minutoActual + 30)); // Quinta opción

        // Crear y configurar el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el tiempo de entrega")
                .setItems(opcionesTiempo.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long milisegundos = 0;
                        switch (which) {
                            case 0:
                                milisegundos = convertirMinutosAMilisegundos(30);
                                break;
                            case 1:
                                milisegundos = convertirMinutosAMilisegundos(45);
                                break;
                            case 2:
                                milisegundos = convertirMinutosAMilisegundos(60);
                                break;
                            case 3:
                                milisegundos = convertirMinutosAMilisegundos(75);
                                break;
                            case 4:
                                milisegundos = convertirMinutosAMilisegundos(90);
                                break;
                        }
                        //Mostramos un dialogo para avisar al usuario de que le pedido se esta realizando
                        mostrarDialogoEnMarcha();
                        //Programamos el tiempo de recibo de la notificacion para confirmar el pedido
                        programarNotificacionDespuesDe(milisegundos);
                    }
                });
        builder.create().show();
    }

    //Mostramos un cuadro de dialogo para preguntar si realmente quiere hacer el pedido
    private void mostrarDialogoRealizarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación de Pedido")
                .setMessage("¿Estás seguro de que deseas realizar el pedido?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // El usuario ha confirmado, mostrar opciones de tiempo y programar la notificación
                        mostrarOpcionesTiempoEntrega();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // El usuario ha cancelado, no realizar ninguna acción
                        dialog.dismiss();
                    }
                })
                .show();
    }

    //Método que programa cuando enviar la notificacion de confirmacion de entrega de pedido
    private void programarNotificacionDespuesDe(long milisegundos) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarNotificacion();
            }
        }, milisegundos);
    }

    //Método que crea el canal de las notificaciones
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Confirmacion de entrega de pedido";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Haz click aqui para confirmar la entrega de tu pedido");

            // Registrar el canal con el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Método que muestra la notificacion de confirmacion al usuario
    private void mostrarNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Confirmacion de entrega de pedido";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Haz click aquí para confirmar la entrega de tu pedido");

            // Registrar el canal con el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // Agregar un mensaje de registro (Log) para verificar si la notificación se muestra
            Log.d("Notificacion", "Mostrando notificación");

            IntentFilter filter = new IntentFilter("com.example.foodfriends.ACCION_NOTIFICACION");
            NotificationBroadcastReceiver receiver = new NotificationBroadcastReceiver();
            registerReceiver(receiver, filter);

            // Crear un intent para enviar una difusión (broadcast) cuando se haga clic en la notificación
            Intent broadcastIntent = new Intent("com.example.foodfriends.ACCION_NOTIFICACION");
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            // Crea y muestra la notificación con un PendingIntent para la acción de clic
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notificacion) // Reemplaza ic_notificacion con el nombre de tu icono de notificación
                    .setContentTitle("Confirmacion de entrega de pedido")
                    .setContentText("Haz click aquí para confirmar la entrega del pedido.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(clickPendingIntent)  // Configura el PendingIntent para la acción de clic
                    .setAutoCancel(true);  // Hace que la notificación se cierre automáticamente al hacer clic en ella

            notificationManager.notify(1, builder.build());
        }
    }

    //Método que pasa minutos a milisegundos
    public static long convertirMinutosAMilisegundos(int minutos) {
        return minutos * 60 * 1000L;
    }

    //Método para obtener la hora en formato HH:mm
    private String obtenerTiempoFormateado(int hora, int minuto) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);

        SimpleDateFormat formato = new SimpleDateFormat("HH:mm");
        return formato.format(cal.getTime());
    }

    //Método que registra el pedido y las lineas del pedido una vez confirmado el pedido
    public static void registrarPedidoYLineasPedido() {
        // Limpiar la lista de líneas de pedidos
        listaLineasPedidosTemp.clear();

        // Configuración de Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference lineaspedidosReference = firebaseDatabase.getReference("LineasPedidos");
        DatabaseReference pedidosReference = firebaseDatabase.getReference("Pedidos");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuarioFirebase = mAuth.getCurrentUser();

        //Obtenemos una referencia a la base de datos de Firebase
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        //Generar un nuevo ID para el pedido utilizando push() de Firebase
        String nuevoPedidoId = databaseReference.child("Pedidos").push().getKey();
        String fechaPedido = obtenerFechaActual();
        String clienteId = usuarioFirebase.getUid();
        double precioTotal = totalConGastosEnvio;

        //Creamos un mapa para contener los datos del pedido
        Map<String, Object> pedidoData = new HashMap<>();
        pedidoData.put("FechaPedido", fechaPedido);
        pedidoData.put("PrecioTotal", precioTotal);
        pedidoData.put("ClienteId", clienteId);

        //Registramos pedido en DB
        pedidosReference.child(nuevoPedidoId).setValue(pedidoData);

        //Registramos cada línea de pedido en la base de datos
        for (LineaPedidoTemp lineaPedidoTemp : listaLineasPedidosTemp) {
            String idLineaPedido = lineaspedidosReference.push().getKey();
            // Crear un mapa para contener los datos de la línea de pedido
            Map<String, Object> lineaData = new HashMap<>();
            lineaData.put("PedidoId", nuevoPedidoId);
            lineaData.put("ProductoId", lineaPedidoTemp.getIdProducto());
            lineaData.put("Unidades", lineaPedidoTemp.getUnidades());

            //Registramos línea en DB
            lineaspedidosReference.child(idLineaPedido).setValue(lineaData);

            //Actualizamos el campo NumeroVentas del producto
            actualizarNumeroVentas(lineaPedidoTemp.getIdProducto());
        }

        //Limpiamos la lista y notificar al adaptador después de procesar todas las líneas de pedido
        listaLineasPedidosTemp.clear();
        adapter.notifyDataSetChanged();
        carritoVacio();
    }

    //Muestra el dialogo al usuario de que el pedido se esta realizando y deberá
    //confirmar su entrega
    private void mostrarDialogoEnMarcha() {
        // Crear y configurar el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pedido en marcha")
                .setMessage("Su pedido está en camino. Recibirá una notificación para confirmar la entrega.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Puedes agregar acciones adicionales si es necesario
                    }
                });
        builder.create().show();
    }

    //Método que suma el numero de ventas de cada producto en la base de datos
    private static void actualizarNumeroVentas(String productoId) {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://foodfriendsapp-f51dc-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference productosReference = firebaseDatabase.getReference("Productos");

            //Obtenemos el valor actual de NumeroVentas
            productosReference.child(productoId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //Obtenemos el valor actual de NumeroVentas
                        Long numeroVentasActual = (Long) snapshot.child("NumeroVentas").getValue();

                        //Incrementamos el valor de NumeroVentas
                        long nuevoNumeroVentas = numeroVentasActual + 1;

                        //Actualizamos el campo NumeroVentas en la base de datos
                        productosReference.child(productoId).child("NumeroVentas").setValue(nuevoNumeroVentas);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(adapter.getContext(), "Ha ocurrido un error en el calculo del precio", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(adapter.getContext(), "Ha ocurrido un error en el calculo del precio", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para obtener la fecha actual en un formato específico (puedes ajustar esto según tus necesidades)
    private static String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}
