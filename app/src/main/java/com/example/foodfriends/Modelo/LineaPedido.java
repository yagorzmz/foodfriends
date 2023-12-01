package com.example.foodfriends.Modelo;

public class LineaPedido {
    private String idLineaPedido,idPedido,idProducto;
    private int unidades;
    public LineaPedido(String idLineaPedido, String idPedido, String idProducto, int unidades) {
        this.idLineaPedido = idLineaPedido;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.unidades = unidades;
    }

    public String getIdLineaPedido() {
        return idLineaPedido;
    }

    public void setIdLineaPedido(String idLineaPedido) {
        this.idLineaPedido = idLineaPedido;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }
}
