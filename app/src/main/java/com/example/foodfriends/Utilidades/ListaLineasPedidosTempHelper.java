package com.example.foodfriends.Utilidades;

import com.example.foodfriends.Modelo.LineaPedidoTemp;

import java.util.ArrayList;
import java.util.List;

public class ListaLineasPedidosTempHelper
{
    private static List<LineaPedidoTemp> listaLineasPedidosTemp = new ArrayList<>();

    public static List<LineaPedidoTemp> getListaLineasPedidosTemp() {
        return listaLineasPedidosTemp;
    }

    public static void agregarLineaPedido(LineaPedidoTemp lineaPedidoTemp) {
        listaLineasPedidosTemp.add(lineaPedidoTemp);
    }

    public static void limpiarListaLineasPedidosTemp() {
        listaLineasPedidosTemp.clear();
    }
}
