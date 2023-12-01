package com.example.foodfriends.Utilidades;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.foodfriends.Activities.CarritoActivity;
import com.example.foodfriends.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Cerrar la primera notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        // Mostrar una nueva notificación con el mensaje "Pedido confirmado"
        mostrarNotificacionPedidoConfirmado(context);
        CarritoActivity.registrarPedidoYLineasPedido();
    }

    private void mostrarNotificacionPedidoConfirmado(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pedido Confirmado";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID_PEDIDO_CONFIRMADO", name, importance);
            channel.setDescription("Pedido confirmado");

            // Registrar el canal con el sistema
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            // Crear y muestra la segunda notificación con el mensaje "Pedido confirmado"
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID_PEDIDO_CONFIRMADO")
                    .setSmallIcon(R.drawable.iconoff) // Reemplaza ic_notificacion_confirmada con el nombre de tu icono de notificación
                    .setContentTitle("Pedido Confirmado")
                    .setContentText("La entrega del pedido ha sido confirmada.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);  // Hace que la notificación se cierre automáticamente al hacer clic en ella

            notificationManager.notify(2, builder.build());
        }
    }
}