package com.example.foodfriends.Modelo;

public class Pedido {
    private String idPedido,idCliente;
    private double precioTotal;
    private String fechaPedido;

    public Pedido(String idPedido, String idCliente, double precioTotal, String fechaPedido) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.precioTotal = precioTotal;
        this.fechaPedido = fechaPedido;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
}
