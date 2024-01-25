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
