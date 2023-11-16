package com.example.foodfriends.Modelo;

public class Producto
{
    private String nombreProducto,descripcion,empresaId;
    private String urlProducto;
    private float precio;

    public Producto(String nombreProducto, String descripcion, String empresaId, String urlProducto, float precio) {
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.empresaId = empresaId;
        this.urlProducto = urlProducto;
        this.precio = precio;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getUrlProducto() {
        return urlProducto;
    }

    public void setUrlProducto(String urlProducto) {
        this.urlProducto = urlProducto;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

}
