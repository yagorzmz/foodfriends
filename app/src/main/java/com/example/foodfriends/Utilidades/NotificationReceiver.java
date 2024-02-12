package com.example.foodfriends.Utilidades;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.foodfriends.Activities.CarritoActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Clase NotificationReceiver
 * Esta clase es un BroadcastReceiver que recibe y maneja las acciones de las notificaciones.
 * Cuando se recibe una notificación, se realiza una acción específica, como registrar un pedido y
 * sus líneas de pedido en la actividad CarritoActivity, y luego cancelar la notificación.
 * @autor Yago Rodríguez Martínez
 * @version 1.0
 */
 public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CarritoActivity.registrarPedidoYLineasPedido();

        // Obtén el ID de la notificación desde el Intent
        int notificationId = intent.getIntExtra("notificationId", 0);

        // Crea una instancia del NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancela la notificación
        notificationManager.cancel(notificationId);
    }


}
