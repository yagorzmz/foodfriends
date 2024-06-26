package com.example.foodfriends.Modelo;

public class Usuario {
    private String idUsuario;
    private String nombreUsuario;
    private String correo;
    private String provinciaUsuario;
    private String municipioUsuario;
    private String localidadUsuario;
    private String direccionUsuario;
    private String urlImagenUsuario;
    public Usuario(String id, String nombre, String correo, String direccionUsuario,String urlImagenUsuario)
    {
        this.idUsuario=id;
        this.nombreUsuario=nombre;
        this.correo=correo;
        this.direccionUsuario=direccionUsuario;
        this.urlImagenUsuario=urlImagenUsuario;
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

    public String getUrlImagenUsuario() {
        return urlImagenUsuario;
    }

    public void setUrlImagenUsuario(String urlImagenUsuario) {
        this.urlImagenUsuario = urlImagenUsuario;
    }

    public String getProvinciaUsuario() {
        return provinciaUsuario;
    }

    public String getMunicipioUsuario() {
        return municipioUsuario;
    }

    public String getLocalidadUsuario() {
        return localidadUsuario;
    }
}
