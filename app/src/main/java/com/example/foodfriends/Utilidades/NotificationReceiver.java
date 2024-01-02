package com.example.foodfriends.Utilidades;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.foodfriends.Activities.CarritoActivity;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CarritoActivity.registrarPedidoYLineasPedido();
    }
}
