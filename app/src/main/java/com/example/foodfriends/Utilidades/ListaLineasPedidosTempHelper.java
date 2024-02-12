package com.example.foodfriends.Utilidades;

import com.example.foodfriends.Modelo.LineaPedidoTemp;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase ListaLineasPedidosTempHelper
 * Esta clase sirve como un helper para gestionar una lista de líneas de pedido temporales.
 * Proporciona métodos estáticos para acceder y manipular la lista de líneas de pedido temporales.
 * @autor Yago Rodríguez Martínez
 * @version 1.0
 */
public class ListaLineasPedidosTempHelper
{
    private static List<LineaPedidoTemp> listaLineasPedidosTemp = new ArrayList<>();

    public static List<LineaPedidoTemp> getListaLineasPedidosTemp() {
        return listaLineasPedidosTemp;
    }

    public static void agregarLineaPedido(LineaPedidoTemp lineaPedidoTemp) {
        listaLineasPedidosTemp.add(lineaPedidoTemp);
    }
}
