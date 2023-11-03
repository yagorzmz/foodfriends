package com.example.foodfriends.Modelo;

public class Usuario {
    private String idUsuario;
    private String nombreUsuario;
    private String correo;
    private String direccionUsuario;
    private String urlImagenUsuario;
    public Usuario(String id, String nombre, String correo, String direccionUsuario)
    {
        this.idUsuario=id;
        this.nombreUsuario=nombre;
        this.correo=correo;
        this.direccionUsuario=direccionUsuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccionUsuario() {
        return direccionUsuario;
    }

    public void setDireccionUsuario(String direccionUsuario) {this.direccionUsuario = direccionUsuario;}

}
