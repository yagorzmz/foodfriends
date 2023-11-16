package com.example.foodfriends.Utilidades;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MyAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Método para establecer el estado de inicio de sesión y el ID del usuario
    public void setLogin(boolean isLoggedIn, int userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Método para verificar si el usuario está conectado
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Método para obtener el ID del usuario
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    // Método para cerrar sesión (borra todas las preferencias)
    public void logout() {
        editor.clear();
        editor.apply();
    }
}

