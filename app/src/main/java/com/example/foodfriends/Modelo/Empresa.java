package com.example.foodfriends.Modelo;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.net.URL;

public class Empresa implements Serializable {
    public String id;
    public String imagenRestaurante;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagenRestaurante() {
        return imagenRestaurante;
    }

    public void setImagenRestaurante(String imagenRestaurante) {
        this.imagenRestaurante = imagenRestaurante;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDireccionEmpresa() {
        return direccionEmpresa;
    }

    public void setDireccionEmpresa(String direccionEmpresa) {
        this.direccionEmpresa = direccionEmpresa;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    public String nombreEmpresa;
    public String direccionEmpresa;
    public float valoracion;
    public Long telefono;
    public String tipoComida;

    public Empresa(String id,String imagenRestaurante,String nombreEmpresa, String direccionEmpresa, float valoracion, Long telefono, String tipoComida) {
        this.id=id;
        this.imagenRestaurante=imagenRestaurante;
        this.nombreEmpresa = nombreEmpresa;
        this.direccionEmpresa = direccionEmpresa;
        this.valoracion = valoracion;
        this.telefono = telefono;
        this.tipoComida = tipoComida;
    }


}
