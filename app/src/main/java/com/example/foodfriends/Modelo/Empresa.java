package com.example.foodfriends.Modelo;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.net.URL;

public class Empresa implements Serializable {
    public String id;
    public String imagenRestaurante;
    public String nombreEmpresa;
    public String provincia;
    public String municipio;
    public String localidad;
    public String direccionEmpresa;
    public Long telefono;
    public String tipoComida;
    public Empresa(String id,String imagenRestaurante,String nombreEmpresa, String direccionEmpresa,
                    Long telefono, String tipoComida,String provincia,String municipio,String localidad) {
        this.id=id;
        this.imagenRestaurante=imagenRestaurante;
        this.nombreEmpresa = nombreEmpresa;
        this.direccionEmpresa = direccionEmpresa;
        this.telefono = telefono;
        this.tipoComida = tipoComida;
        this.provincia=provincia;
        this.municipio=municipio;
        this.localidad=localidad;
    }

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

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
}
