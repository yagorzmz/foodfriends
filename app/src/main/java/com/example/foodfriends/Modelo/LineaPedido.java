package com.example.foodfriends.Modelo;

import java.io.Serializable;

public class LineaPedido implements Serializable
{
    public LineaPedido(String idProducto, String idPedido, int unidades) {
        this.idProducto = idProducto;
        this.idPedido = idPedido;
        this.unidades = unidades;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    private String idProducto;
    private String idPedido;
    private int unidades;



}
